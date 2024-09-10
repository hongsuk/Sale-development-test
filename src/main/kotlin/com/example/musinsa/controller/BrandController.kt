package com.example.musinsa.controller

import com.example.musinsa.dto.BrandDTO
import com.example.musinsa.service.BrandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/brands")
class BrandController @Autowired constructor(
    val brandService: BrandService) {

    @PostMapping
    fun addBrand(@RequestBody brandDTO: BrandDTO): ResponseEntity<BrandDTO> {
        val createdBrand = brandService.addBrand(brandDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand)
    }

    @PutMapping("/{id}")
    fun updateBrand(@PathVariable id: Long, @RequestBody brandDTO: BrandDTO): ResponseEntity<BrandDTO> {
        val updatedBrand = brandService.updateBrand(id, brandDTO)
        return ResponseEntity.ok(updatedBrand)
    }

    @DeleteMapping("/{id}")
    fun deleteBrand(@PathVariable id: Long): ResponseEntity<Unit> {
        brandService.deleteBrand(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}")
    fun getBrand(@PathVariable id: Long): ResponseEntity<BrandDTO> {
        val brand = brandService.getBrandById(id)
        return ResponseEntity.ok(brand)
    }

    @GetMapping
    fun getAllBrands(): ResponseEntity<List<BrandDTO>> {
        val brands = brandService.getAllBrands()
        return ResponseEntity.ok(brands)
    }

}