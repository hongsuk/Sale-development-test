package com.example.musinsa.dto

import com.example.musinsa.entity.Brand
import java.time.LocalDateTime

data class BrandDTO(
    val id: Long?,
    val name: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

fun Brand.toDTO() = BrandDTO(
    id = this.id,
    name = this.name,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)