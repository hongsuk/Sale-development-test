package com.example.musinsa.controller

import com.example.musinsa.dto.CategoryMinPriceSummaryDTO
import com.example.musinsa.dto.CategoryPriceRangeDTO
import com.example.musinsa.dto.LowestPriceByBrandSummaryDTO
import com.example.musinsa.dto.ProductDTO
import com.example.musinsa.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController @Autowired constructor(val productService: ProductService) {
    @GetMapping("/lowest-price-by-category")
    fun getLowestPriceByCategory(): ResponseEntity<CategoryMinPriceSummaryDTO> {
        val result = productService.getLowestPriceSummaryByCategory()
        return ResponseEntity.ok(result)
    }

    @GetMapping("/lowest-price-by-brand")
    fun getLowestPriceByBrand(): ResponseEntity<LowestPriceByBrandSummaryDTO> {
        val result = productService.getLowestPriceByBrand()
        return ResponseEntity.ok(result)
    }

    @GetMapping("/price-range")
    fun getCategoryPriceRange(@RequestParam categoryName: String): ResponseEntity<CategoryPriceRangeDTO> {
        val priceRange = productService.getCategoryPriceRange(categoryName)
        return ResponseEntity.ok(priceRange)
    }

    @PostMapping
    fun addProduct(@RequestBody productDTO: ProductDTO): ResponseEntity<ProductDTO> {
        val createdProduct = productService.addProduct(productDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct)
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody productDTO: ProductDTO): ResponseEntity<ProductDTO> {
        val updatedProduct = productService.updateProduct(id, productDTO)
        return ResponseEntity.ok(updatedProduct)
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Unit> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}