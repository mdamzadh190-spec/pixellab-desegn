package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "design_projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val ratioName: String,
    val backgroundJson: String,
    val layersJson: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val previewColor: Long = 0xFF1E2230
)
