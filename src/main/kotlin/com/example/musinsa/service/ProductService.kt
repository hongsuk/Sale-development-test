package com.example.musinsa.service

import com.example.musinsa.dto.*
import com.example.musinsa.entity.Product
import com.example.musinsa.repository.BrandRepository
import com.example.musinsa.repository.CategoryRepository
import com.example.musinsa.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductService @Autowired constructor(private val productRepository: ProductRepository,
                                            private val categoryRepository: CategoryRepository,
                                            private val brandRepository: BrandRepository) {
    fun addProduct(productDTO: ProductDTO): ProductDTO {
        val category = categoryRepository.findByName(productDTO.categoryName)
            ?: throw NoSuchElementException("Category not found: ${productDTO.categoryName}")
        val brand = brandRepository.findByName(productDTO.brandName)
            ?: throw NoSuchElementException("Brand not found: ${productDTO.brandName}")

        val product = Product(
            price = productDTO.price,
            category = category,
            brand = brand
        )
        val savedProduct = productRepository.save(product)
        return savedProduct.toDTO()
    }

    fun updateProduct(id: Long, productDTO: ProductDTO): ProductDTO {
        val existingProduct = productRepository.findById(id.toInt())
            .orElseThrow { NoSuchElementException("Product not found: $id") }

        val category = categoryRepository.findByName(productDTO.categoryName)
            ?: throw NoSuchElementException("Category not found: ${productDTO.categoryName}")
        val brand = brandRepository.findByName(productDTO.brandName)
            ?: throw NoSuchElementException("Brand not found: ${productDTO.brandName}")

        existingProduct.apply {
            price = productDTO.price
            this.category = category
            this.brand = brand
        }

        val updatedProduct = productRepository.save(existingProduct)
        return updatedProduct.toDTO()
    }

    fun deleteProduct(id: Long) {
        if (!productRepository.existsById(id.toInt())) {
            throw NoSuchElementException("Product not found: $id")
        }
        productRepository.deleteById(id.toInt())
    }

    fun getLowestPriceSummaryByCategory(): CategoryMinPriceSummaryDTO {
        val products = productRepository.findLowestPriceByCategory()
        val totalPrice = productRepository.calculateTotalMinPrice()

        return CategoryMinPriceSummaryDTO(products, totalPrice)
    }

    fun getLowestPriceByBrand(): LowestPriceByBrandSummaryDTO {
        val lowestTotalPriceByBrand = productRepository.findBrandWithLowestTotalPrice()
        val products = productRepository.findProductByBrandId(lowestTotalPriceByBrand.brandId)
        val totalPrice = products.sumOf { it.price }

        return LowestPriceByBrandSummaryDTO(
            lowestPrice = LowestPriceByBrandDTO(
                brand = lowestTotalPriceByBrand.brandName,
                categories = products.map { CategoryPriceDTO(it.category.name, it.price) },
                totalPrice = totalPrice
            )
        )
    }


    fun getCategoryPriceRange(categoryName: String): CategoryPriceRangeDTO {
        val category = categoryRepository.findByName(categoryName)
            ?: throw NoSuchElementException("Category not found: $categoryName")
        val categoryId = category.id
            ?: throw IllegalStateException("Category found but has no ID: $categoryName")

        val lowestPrice = productRepository.findLowestPriceProductByCategory(categoryId)
        val highestPrice = productRepository.findHighestPriceProductByCategory(categoryId)

        return CategoryPriceRangeDTO(
            category = category.name,
            lowestPrice = ProductPriceDTO(lowestPrice.brandName, lowestPrice.price),
            highestPrice = ProductPriceDTO(highestPrice.brandName, highestPrice.price)
        )
    }
}