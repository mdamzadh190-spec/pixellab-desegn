package com.example.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ProjectRepository
import com.example.data.QuotesAndTemplates
import com.example.data.TemplateItem
import com.example.data.entity.ProjectEntity
import com.example.export.CanvasExporter
import com.example.model.BackgroundConfig
import com.example.model.BackgroundType
import com.example.model.CanvasRatio
import com.example.model.DesignLayer
import com.example.model.LayerType
import com.example.model.ShapeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

import com.example.model.AnimationPreset
import com.example.model.CustomCanvasDimension
import com.example.model.MeasurementUnit

enum class StudioTab(val title: String) {
    TEMPLATES("Projects"),
    TEXT("Text 'A'"),
    SHAPES("Shapes"),
    BACKGROUND("Canvas"),
    LAYERS("Layers")
}

data class StudioState(
    val projectTitle: String = "My Design",
    val currentProjectId: Long? = null,
    val canvasRatio: CanvasRatio = CanvasRatio.SQUARE,
    val customDimension: CustomCanvasDimension? = null,
    val backgroundConfig: BackgroundConfig = BackgroundConfig(),
    val layers: List<DesignLayer> = emptyList(),
    val selectedLayerId: String? = null,
    val activeTab: StudioTab = StudioTab.TEXT,
    val showGrid: Boolean = false,
    val isPlayingAnimation: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isExporting: Boolean = false,
    val exportSuccessMessage: String? = null
)

class StudioViewModel(private val repository: ProjectRepository) : ViewModel() {

    private val _state = MutableStateFlow(StudioState())
    val state: StateFlow<StudioState> = _state.asStateFlow()

    // Undo & Redo History (Snapshots of layers & background & ratio)
    private data class HistorySnapshot(
        val ratio: CanvasRatio,
        val background: BackgroundConfig,
        val layers: List<DesignLayer>
    )

    private val undoStack = mutableListOf<HistorySnapshot>()
    private val redoStack = mutableListOf<HistorySnapshot>()

    val savedProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Load starter template
        loadDefaultStarter()
    }

    private fun loadDefaultStarter() {
        val defaultTpl = QuotesAndTemplates.getSampleTemplates().first()
        loadTemplate(defaultTpl)
    }

    private fun saveHistoryState() {
        val current = HistorySnapshot(
            ratio = _state.value.canvasRatio,
            background = _state.value.backgroundConfig,
            layers = _state.value.layers.map { it.copy() }
        )
        undoStack.add(current)
        if (undoStack.size > 25) undoStack.removeAt(0)
        redoStack.clear()
        updateUndoRedoStatus()
    }

    private fun updateUndoRedoStatus() {
        _state.value = _state.value.copy(
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty()
        )
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val current = HistorySnapshot(
            ratio = _state.value.canvasRatio,
            background = _state.value.backgroundConfig,
            layers = _state.value.layers.map { it.copy() }
        )
        redoStack.add(current)
        val previous = undoStack.removeAt(undoStack.lastIndex)
        _state.value = _state.value.copy(
            canvasRatio = previous.ratio,
            backgroundConfig = previous.background,
            layers = previous.layers.map { it.copy() },
            selectedLayerId = null
        )
        updateUndoRedoStatus()
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val current = HistorySnapshot(
            ratio = _state.value.canvasRatio,
            background = _state.value.backgroundConfig,
            layers = _state.value.layers.map { it.copy() }
        )
        undoStack.add(current)
        val next = redoStack.removeAt(redoStack.lastIndex)
        _state.value = _state.value.copy(
            canvasRatio = next.ratio,
            backgroundConfig = next.background,
            layers = next.layers.map { it.copy() },
            selectedLayerId = null
        )
        updateUndoRedoStatus()
    }

    fun setActiveTab(tab: StudioTab) {
        _state.value = _state.value.copy(activeTab = tab)
    }

    fun toggleGrid() {
        _state.value = _state.value.copy(showGrid = !_state.value.showGrid)
    }

    fun selectLayer(id: String?) {
        _state.value = _state.value.copy(selectedLayerId = id)
        if (id != null) {
            val layer = _state.value.layers.find { it.id == id }
            if (layer != null) {
                // Auto switch tab based on layer type
                when (layer.type) {
                    LayerType.TEXT -> _state.value = _state.value.copy(activeTab = StudioTab.TEXT)
                    LayerType.SHAPE -> _state.value = _state.value.copy(activeTab = StudioTab.SHAPES)
                    else -> {}
                }
            }
        }
    }

    fun setCanvasRatio(ratio: CanvasRatio) {
        saveHistoryState()
        _state.value = _state.value.copy(canvasRatio = ratio)
    }

    fun setBackgroundConfig(config: BackgroundConfig) {
        saveHistoryState()
        _state.value = _state.value.copy(backgroundConfig = config)
    }

    // Layer Management
    fun addTextLayer(initialText: String = "PixelLab Design") {
        saveHistoryState()
        val nextZ = (_state.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        val newLayer = DesignLayer(
            id = UUID.randomUUID().toString(),
            name = "Text: ${initialText.take(12)}",
            type = LayerType.TEXT,
            text = initialText,
            textColor = 0xFFFFFFFF,
            fontSize = 32f,
            xPercent = 0.5f,
            yPercent = 0.5f,
            isBold = true,
            hasShadow = true,
            zIndex = nextZ
        )
        _state.value = _state.value.copy(
            layers = _state.value.layers + newLayer,
            selectedLayerId = newLayer.id,
            activeTab = StudioTab.TEXT
        )
    }

    fun addShapeLayer(shapeType: ShapeType = ShapeType.ROUNDED_RECT) {
        saveHistoryState()
        val nextZ = (_state.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        val newLayer = DesignLayer(
            id = UUID.randomUUID().toString(),
            name = "Shape: ${shapeType.displayName}",
            type = LayerType.SHAPE,
            shapeType = shapeType,
            fillColor = 0xFF00E5FF,
            xPercent = 0.5f,
            yPercent = 0.5f,
            zIndex = nextZ
        )
        _state.value = _state.value.copy(
            layers = _state.value.layers + newLayer,
            selectedLayerId = newLayer.id,
            activeTab = StudioTab.SHAPES
        )
    }

    fun addStickerLayer(tag: String, tint: Long = 0xFFFFB300) {
        saveHistoryState()
        val nextZ = (_state.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        val newLayer = DesignLayer(
            id = UUID.randomUUID().toString(),
            name = "Badge: $tag",
            type = LayerType.STICKER,
            stickerTag = tag,
            stickerTint = tint,
            xPercent = 0.5f,
            yPercent = 0.5f,
            zIndex = nextZ
        )
        _state.value = _state.value.copy(
            layers = _state.value.layers + newLayer,
            selectedLayerId = newLayer.id
        )
    }

    fun addImageLayer(uri: String) {
        saveHistoryState()
        val nextZ = (_state.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        val newLayer = DesignLayer(
            id = UUID.randomUUID().toString(),
            name = "Photo Layer",
            type = LayerType.IMAGE,
            imageUri = uri,
            xPercent = 0.5f,
            yPercent = 0.5f,
            zIndex = nextZ
        )
        _state.value = _state.value.copy(
            layers = _state.value.layers + newLayer,
            selectedLayerId = newLayer.id
        )
    }

    fun updateLayerPosition(id: String, xPercent: Float, yPercent: Float) {
        _state.value = _state.value.copy(
            layers = _state.value.layers.map {
                if (it.id == id) it.copy(xPercent = xPercent, yPercent = yPercent) else it
            }
        )
    }

    fun updateLayerScale(id: String, scale: Float) {
        _state.value = _state.value.copy(
            layers = _state.value.layers.map {
                if (it.id == id) it.copy(scale = scale) else it
            }
        )
    }

    fun updateLayerRotation(id: String, rotation: Float) {
        _state.value = _state.value.copy(
            layers = _state.value.layers.map {
                if (it.id == id) it.copy(rotation = rotation) else it
            }
        )
    }

    fun updateSelectedLayer(transform: (DesignLayer) -> DesignLayer) {
        val selectedId = _state.value.selectedLayerId ?: return
        saveHistoryState()
        _state.value = _state.value.copy(
            layers = _state.value.layers.map {
                if (it.id == selectedId) transform(it) else it
            }
        )
    }

    fun deleteLayer(id: String) {
        saveHistoryState()
        _state.value = _state.value.copy(
            layers = _state.value.layers.filter { it.id != id },
            selectedLayerId = if (_state.value.selectedLayerId == id) null else _state.value.selectedLayerId
        )
    }

    fun duplicateLayer(id: String) {
        val layer = _state.value.layers.find { it.id == id } ?: return
        saveHistoryState()
        val nextZ = (_state.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        val copy = layer.copyLayer().copy(zIndex = nextZ)
        _state.value = _state.value.copy(
            layers = _state.value.layers + copy,
            selectedLayerId = copy.id
        )
    }

    fun toggleLayerVisibility(id: String) {
        _state.value = _state.value.copy(
            layers = _state.value.layers.map {
                if (it.id == id) it.copy(isVisible = !it.isVisible) else it
            }
        )
    }

    fun toggleLayerLock(id: String) {
        _state.value = _state.value.copy(
            layers = _state.value.layers.map {
                if (it.id == id) it.copy(isLocked = !it.isLocked) else it
            }
        )
    }

    fun moveLayerZ(id: String, moveUp: Boolean) {
        val list = _state.value.layers.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index == -1) return
        if (moveUp && index < list.size - 1) {
            val item = list.removeAt(index)
            list.add(index + 1, item)
        } else if (!moveUp && index > 0) {
            val item = list.removeAt(index)
            list.add(index - 1, item)
        }
        // reassign zIndex
        list.forEachIndexed { i, layer -> layer.zIndex = i }
        saveHistoryState()
        _state.value = _state.value.copy(layers = list)
    }

    fun alignSelectedLayer(horizontal: Boolean, vertical: Boolean) {
        val selectedId = _state.value.selectedLayerId ?: return
        saveHistoryState()
        _state.value = _state.value.copy(
            layers = _state.value.layers.map {
                if (it.id == selectedId) {
                    it.copy(
                        xPercent = if (horizontal) 0.5f else it.xPercent,
                        yPercent = if (vertical) 0.5f else it.yPercent
                    )
                } else it
            }
        )
    }

    // Templates
    fun loadTemplate(template: TemplateItem) {
        saveHistoryState()
        _state.value = _state.value.copy(
            projectTitle = template.title,
            canvasRatio = template.ratio,
            backgroundConfig = template.backgroundConfig,
            layers = template.layers.map { it.copy() },
            selectedLayerId = null
        )
    }

    // Persistence with Room
    fun saveProject(title: String) {
        viewModelScope.launch {
            val layersArray = JSONArray()
            _state.value.layers.forEach { layersArray.put(it.toJson()) }

            val bgJson = _state.value.backgroundConfig.toJson().toString()
            val layersJson = layersArray.toString()

            val entity = ProjectEntity(
                id = _state.value.currentProjectId ?: 0,
                title = title.ifBlank { "Untitled Design" },
                ratioName = _state.value.canvasRatio.name,
                backgroundJson = bgJson,
                layersJson = layersJson,
                updatedAt = System.currentTimeMillis(),
                previewColor = _state.value.backgroundConfig.color
            )

            val newId = repository.saveProject(entity)
            _state.value = _state.value.copy(
                projectTitle = entity.title,
                currentProjectId = if (entity.id == 0L) newId else entity.id,
                exportSuccessMessage = "Project '${entity.title}' saved successfully!"
            )
        }
    }

    fun loadSavedProject(entity: ProjectEntity) {
        viewModelScope.launch {
            val ratio = try {
                CanvasRatio.valueOf(entity.ratioName)
            } catch (e: Exception) {
                CanvasRatio.SQUARE
            }
            val bg = try {
                BackgroundConfig.fromJson(JSONObject(entity.backgroundJson))
            } catch (e: Exception) {
                BackgroundConfig()
            }
            val layersList = mutableListOf<DesignLayer>()
            try {
                val array = JSONArray(entity.layersJson)
                for (i in 0 until array.length()) {
                    layersList.add(DesignLayer.fromJson(array.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            saveHistoryState()
            _state.value = _state.value.copy(
                projectTitle = entity.title,
                currentProjectId = entity.id,
                canvasRatio = ratio,
                backgroundConfig = bg,
                layers = layersList,
                selectedLayerId = null
            )
        }
    }

    fun deleteSavedProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_state.value.currentProjectId == id) {
                _state.value = _state.value.copy(currentProjectId = null)
            }
        }
    }

    // Export & Sharing
    fun exportAndShare(context: Context, isPng: Boolean = true, targetWidth: Int = 1280) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true)
            try {
                val bitmap = CanvasExporter.renderToBitmap(
                    context = context,
                    ratio = _state.value.canvasRatio,
                    background = _state.value.backgroundConfig,
                    layers = _state.value.layers,
                    targetWidth = targetWidth
                )
                val uri = CanvasExporter.saveToCache(context, bitmap, isPng)
                CanvasExporter.shareImage(context, uri)
                _state.value = _state.value.copy(isExporting = false)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(isExporting = false, exportSuccessMessage = "Export failed: ${e.localizedMessage}")
            }
        }
    }

    fun exportToGallery(context: Context, isPng: Boolean = true, targetWidth: Int = 1920) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true)
            try {
                val bitmap = CanvasExporter.renderToBitmap(
                    context = context,
                    ratio = _state.value.canvasRatio,
                    background = _state.value.backgroundConfig,
                    layers = _state.value.layers,
                    targetWidth = targetWidth
                )
                val success = CanvasExporter.saveToGallery(context, bitmap, isPng)
                _state.value = _state.value.copy(
                    isExporting = false,
                    exportSuccessMessage = if (success) "Image saved to Gallery (Pictures/PixelLab)!" else "Failed to save to Gallery."
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(isExporting = false, exportSuccessMessage = "Export error: ${e.localizedMessage}")
            }
        }
    }

    fun togglePlayAnimation() {
        _state.value = _state.value.copy(isPlayingAnimation = !_state.value.isPlayingAnimation)
    }

    fun applyAnimationToSelected(preset: AnimationPreset) {
        val selectedId = _state.value.selectedLayerId
        if (selectedId != null) {
            updateSelectedLayer { it.copy(animationPreset = preset) }
            _state.value = _state.value.copy(isPlayingAnimation = true)
        } else {
            // Apply to all layers if none selected
            saveHistoryState()
            _state.value = _state.value.copy(
                layers = _state.value.layers.map { it.copy(animationPreset = preset) },
                isPlayingAnimation = true
            )
        }
    }

    fun setCustomDimension(dimension: CustomCanvasDimension) {
        saveHistoryState()
        val customRatio = CanvasRatio.CUSTOM.apply {
            widthRatio = dimension.width
            heightRatio = dimension.height
            label = dimension.presetName ?: "${dimension.width}×${dimension.height} ${dimension.unit.symbol}"
        }
        _state.value = _state.value.copy(
            customDimension = dimension,
            canvasRatio = customRatio
        )
    }

    fun setQuickUnit(unit: MeasurementUnit) {
        val current = _state.value.customDimension ?: CustomCanvasDimension(1080f, 1080f, MeasurementUnit.PIXEL, 300)
        val (w, h) = current.convertTo(unit)
        val newDim = current.copy(
            width = w,
            height = h,
            unit = unit,
            presetName = "Custom (${String.format(java.util.Locale.US, "%.1f", w)}×${String.format(java.util.Locale.US, "%.1f", h)} ${unit.symbol})"
        )
        setCustomDimension(newDim)
    }

    fun exportToPdfAndShare(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true)
            try {
                val uri = CanvasExporter.exportToPdf(
                    context = context,
                    ratio = _state.value.canvasRatio,
                    background = _state.value.backgroundConfig,
                    layers = _state.value.layers,
                    customDimension = _state.value.customDimension
                )
                CanvasExporter.sharePdf(context, uri)
                _state.value = _state.value.copy(isExporting = false)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(isExporting = false, exportSuccessMessage = "PDF export failed: ${e.localizedMessage}")
            }
        }
    }

    fun savePdfToDocuments(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true)
            try {
                val success = CanvasExporter.savePdfToDocuments(
                    context = context,
                    ratio = _state.value.canvasRatio,
                    background = _state.value.backgroundConfig,
                    layers = _state.value.layers,
                    customDimension = _state.value.customDimension
                )
                _state.value = _state.value.copy(
                    isExporting = false,
                    exportSuccessMessage = if (success) "PDF saved to Documents/PixelLab folder!" else "Failed to save PDF to Documents."
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(isExporting = false, exportSuccessMessage = "PDF save error: ${e.localizedMessage}")
            }
        }
    }

    fun clearExportMessage() {
        _state.value = _state.value.copy(exportSuccessMessage = null)
    }
}

