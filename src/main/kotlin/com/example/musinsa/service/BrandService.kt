package com.example.musinsa.service

import com.example.musinsa.dto.BrandDTO
import com.example.musinsa.dto.toDTO
import com.example.musinsa.entity.Brand
import com.example.musinsa.repository.BrandRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BrandService @Autowired constructor(
    private val brandRepository: BrandRepository) {

    fun addBrand(brandDTO: BrandDTO): BrandDTO {
        val brand = Brand(
            name = brandDTO.name
        )
        val savedBrand = brandRepository.save(brand)
        return savedBrand.toDTO()
    }

    fun updateBrand(id: Long, brandDTO: BrandDTO): BrandDTO {
        val existingBrand = brandRepository.findById(id.toInt())
            .orElseThrow { NoSuchElementException("Brand not found: $id") }

        existingBrand.name = brandDTO.name
        val updatedBrand = brandRepository.save(existingBrand)
        return updatedBrand.toDTO()
    }

    fun deleteBrand(id: Long) {
        if (!brandRepository.existsById(id.toInt())) {
            throw NoSuchElementException("Brand not found: $id")
        }
        brandRepository.deleteById(id.toInt())
    }

    fun getBrandById(id: Long): BrandDTO {
        val brand = brandRepository.findById(id.toInt())
            .orElseThrow { NoSuchElementException("Brand not found: $id") }
        return brand.toDTO()
    }

    fun getAllBrands(): List<BrandDTO> {
        return brandRepository.findAll().map { it.toDTO() }
    }
}