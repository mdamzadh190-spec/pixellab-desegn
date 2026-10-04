package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ShapeType
import com.example.ui.canvas.DesignCanvas
import com.example.ui.components.ButtonTableDialog
import com.example.ui.components.CanvasDimensionDialog
import com.example.ui.components.CanvasToolsPanel
import com.example.ui.components.ExportDialog
import com.example.ui.components.LayersPanel
import com.example.ui.components.ShapeToolsPanel
import com.example.ui.components.StudioBottomNav
import com.example.ui.components.StudioTopBar
import com.example.ui.components.TemplatesAndQuotesSheet
import com.example.ui.components.TextToolsPanel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.viewmodel.StudioTab
import com.example.viewmodel.StudioViewModel

@Composable
fun StudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showExportDialog by remember { mutableStateOf(false) }
    var showTemplatesSheet by remember { mutableStateOf(false) }
    var showButtonTable by remember { mutableStateOf(false) }
    var showDimensionDialog by remember { mutableStateOf(false) }

    // Modern Android Photo Picker for layer image insertion
    val photoLayerPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addImageLayer(uri.toString())
        }
    }

    LaunchedEffect(state.exportSuccessMessage) {
        state.exportSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearExportMessage()
        }
    }

    val selectedLayer = state.layers.find { it.id == state.selectedLayerId }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StudioTopBar(
                projectTitle = state.projectTitle,
                canUndo = state.canUndo,
                canRedo = state.canRedo,
                showGrid = state.showGrid,
                isPlayingAnimation = state.isPlayingAnimation,
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onToggleGrid = { viewModel.toggleGrid() },
                onToggleAnimation = { viewModel.togglePlayAnimation() },
                onOpenButtonTable = { showButtonTable = true },
                onSaveProject = { title -> viewModel.saveProject(title) },
                onOpenExport = { showExportDialog = true },
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        },
        bottomBar = {
            Column {
                // Active Editor Tool Panel above the bottom tabs
                when (state.activeTab) {
                    StudioTab.TEMPLATES -> {
                        LaunchedEffect(Unit) {
                            showTemplatesSheet = true
                        }
                    }
                    StudioTab.TEXT -> {
                        TextToolsPanel(
                            selectedLayer = selectedLayer,
                            onAddText = { viewModel.addTextLayer() },
                            onOpenQuotes = { showTemplatesSheet = true },
                            onUpdateLayer = { transform -> viewModel.updateSelectedLayer(transform) },
                            onDuplicate = { selectedLayer?.let { viewModel.duplicateLayer(it.id) } },
                            onDelete = { selectedLayer?.let { viewModel.deleteLayer(it.id) } },
                            onCenterAlign = { viewModel.alignSelectedLayer(horizontal = true, vertical = true) }
                        )
                    }
                    StudioTab.SHAPES -> {
                        ShapeToolsPanel(
                            selectedLayer = selectedLayer,
                            onAddShape = { shape -> viewModel.addShapeLayer(shape) },
                            onAddSticker = { tag, tint -> viewModel.addStickerLayer(tag, tint) },
                            onUpdateLayer = { transform -> viewModel.updateSelectedLayer(transform) },
                            onDuplicate = { selectedLayer?.let { viewModel.duplicateLayer(it.id) } },
                            onDelete = { selectedLayer?.let { viewModel.deleteLayer(it.id) } },
                            onCenterAlign = { viewModel.alignSelectedLayer(horizontal = true, vertical = true) }
                        )
                    }
                    StudioTab.BACKGROUND -> {
                        CanvasToolsPanel(
                            currentRatio = state.canvasRatio,
                            customDimension = state.customDimension,
                            currentBackground = state.backgroundConfig,
                            onRatioChanged = { ratio -> viewModel.setCanvasRatio(ratio) },
                            onOpenDimensionDialog = { showDimensionDialog = true },
                            onBackgroundChanged = { bg -> viewModel.setBackgroundConfig(bg) }
                        )
                    }
                    StudioTab.LAYERS -> {
                        LayersPanel(
                            layers = state.layers,
                            selectedLayerId = state.selectedLayerId,
                            onSelectLayer = { id -> viewModel.selectLayer(id) },
                            onToggleVisibility = { id -> viewModel.toggleLayerVisibility(id) },
                            onToggleLock = { id -> viewModel.toggleLayerLock(id) },
                            onMoveLayer = { id, up -> viewModel.moveLayerZ(id, up) },
                            onDuplicateLayer = { id -> viewModel.duplicateLayer(id) },
                            onDeleteLayer = { id -> viewModel.deleteLayer(id) }
                        )
                    }
                }

                Divider(color = DarkBorder, thickness = 0.5.dp)

                StudioBottomNav(
                    activeTab = state.activeTab,
                    onTabSelected = { tab ->
                        viewModel.setActiveTab(tab)
                        if (tab == StudioTab.TEMPLATES) {
                            showTemplatesSheet = true
                        }
                    }
                )
            }
        },
        containerColor = DarkBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            DesignCanvas(
                ratio = state.canvasRatio,
                customDimension = state.customDimension,
                background = state.backgroundConfig,
                layers = state.layers,
                selectedLayerId = state.selectedLayerId,
                showGrid = state.showGrid,
                isPlayingAnimation = state.isPlayingAnimation,
                onSelectLayer = { id -> viewModel.selectLayer(id) },
                onUpdateLayerPosition = { id, x, y -> viewModel.updateLayerPosition(id, x, y) },
                onUpdateLayerScale = { id, scale -> viewModel.updateLayerScale(id, scale) },
                onUpdateLayerRotation = { id, rot -> viewModel.updateLayerRotation(id, rot) },
                onDeleteLayer = { id -> viewModel.deleteLayer(id) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // Button Table Modal (বাটন টেবিল)
    if (showButtonTable) {
        ButtonTableDialog(
            onAddText = { viewModel.addTextLayer() },
            onOpenQuotes = { showTemplatesSheet = true },
            onAddShape = { shape -> viewModel.addShapeLayer(shape) },
            onAddSticker = { tag -> viewModel.addStickerLayer(tag) },
            onOpenDimensionDialog = { showDimensionDialog = true },
            onQuickUnitSet = { unit -> viewModel.setQuickUnit(unit) },
            onQuickPresetSet = { preset -> viewModel.setCustomDimension(preset) },
            onApplyAnimationToSelected = { preset -> viewModel.applyAnimationToSelected(preset) },
            onTogglePlayAnimation = { viewModel.togglePlayAnimation() },
            isPlayingAnimation = state.isPlayingAnimation,
            onOpenPdfExport = { showExportDialog = true },
            onOpenImageExport = { showExportDialog = true },
            onDismiss = { showButtonTable = false }
        )
    }

    // Canvas Dimension Dialog (inch, pixel, cm, mm)
    if (showDimensionDialog) {
        CanvasDimensionDialog(
            initialDimension = state.customDimension,
            onApplyDimension = { dim -> viewModel.setCustomDimension(dim) },
            onDismiss = { showDimensionDialog = false }
        )
    }

    // Templates and Quotes Bottom Sheet
    if (showTemplatesSheet) {
        TemplatesAndQuotesSheet(
            savedProjects = savedProjects,
            onSelectTemplate = { tpl -> viewModel.loadTemplate(tpl) },
            onSelectQuote = { quoteText -> viewModel.addTextLayer(quoteText) },
            onLoadSavedProject = { proj -> viewModel.loadSavedProject(proj) },
            onDeleteSavedProject = { id -> viewModel.deleteSavedProject(id) },
            onDismiss = { showTemplatesSheet = false }
        )
    }

    // Export Dialog (PDF, PNG, JPEG)
    if (showExportDialog) {
        ExportDialog(
            isExporting = state.isExporting,
            customDimension = state.customDimension,
            onSaveToGallery = { isPng, width ->
                viewModel.exportToGallery(context, isPng, width)
            },
            onShare = { isPng, width ->
                viewModel.exportAndShare(context, isPng, width)
            },
            onSavePdf = {
                viewModel.savePdfToDocuments(context)
            },
            onSharePdf = {
                viewModel.exportToPdfAndShare(context)
            },
            onDismiss = { showExportDialog = false }
        )
    }
}
