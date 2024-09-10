package com.example.musinsa.dto

import com.example.musinsa.entity.Product
import java.time.LocalDateTime

data class CategoryMinPriceDTO(
    val categoryName:String,
    val brandName:String,
    val minPrice:Int,
)

data class CategoryMinPriceSummaryDTO(
    val products: List<CategoryMinPriceDTO>,
    val totalPrice: Int
)

data class LowestPriceByBrandSummaryDTO(
    val lowestPrice: LowestPriceByBrandDTO
)
data class LowestPriceByBrandDTO(
    val brand: String,
    val categories: List<CategoryPriceDTO>,
    val totalPrice: Int
)
data class CategoryPriceDTO(
    val category: String,
    val price: Int
)

data class BrandTotalPriceDTO(
    val brandId: Long,
    val brandName: String,
    val totalPrice: Int
)

data class CategoryPriceRangeDTO(
    val category: String,
    val lowestPrice: ProductPriceDTO,
    val highestPrice: ProductPriceDTO
)

data class ProductPriceDTO(
    val brandName: String,
    val price: Int
)

data class ProductDTO(
    val id: Long?,
    val price: Int,
    val categoryName: String,
    val brandName: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

fun Product.toDTO() = ProductDTO(
    id = this.id,
    price = this.price,
    categoryName = this.category.name,
    brandName = this.brand.name,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)