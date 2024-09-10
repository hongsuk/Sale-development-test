package com.example.musinsa.service

import com.example.musinsa.dto.BrandDTO
import com.example.musinsa.entity.Brand
import com.example.musinsa.repository.BrandRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

class BrandServiceTest {
    private lateinit var brandRepository: BrandRepository
    private lateinit var brandService: BrandService

    @BeforeEach
    fun setup() {
        brandRepository = mockk()
        brandService = BrandService(brandRepository)
    }

    @Test
    fun `addBrand should create and return a new brand`() {
        // Given
        val brandDTO = BrandDTO(null, "NewBrand", null, null)
        val savedBrand = Brand("NewBrand").apply {
            id = 1L
            createdAt = LocalDateTime.now()
        }

        every { brandRepository.save(any()) } returns savedBrand

        // When
        val result = brandService.addBrand(brandDTO)

        // Then
        assert(result.id == 1L)
        assert(result.name == "NewBrand")
        assert(result.createdAt != null)
        verify { brandRepository.save(any()) }
    }

    @Test
    fun `updateBrand should update and return the brand`() {
        // Given
        val brandId = 1L
        val updateDTO = BrandDTO(brandId, "UpdatedBrand", null, null)
        val existingBrand = Brand("OldBrand").apply {
            id = brandId
            createdAt = LocalDateTime.now().minusDays(1)
        }

        every { brandRepository.findById(brandId.toInt()) } returns Optional.of(existingBrand)
        every { brandRepository.save(any()) } answers {
            val savedBrand = firstArg<Brand>()
            savedBrand.apply {
                name = "UpdatedBrand"
                // createdAt은 변경되지 않음
            }
        }

        // When
        val result = brandService.updateBrand(brandId, updateDTO)

        // Then
        assert(result.id == brandId)
        assert(result.name == "UpdatedBrand")
        assert(result.createdAt == existingBrand.createdAt)
        verify { brandRepository.findById(brandId.toInt()) }
        verify { brandRepository.save(any()) }
    }

    @Test
    fun `updateBrand should throw NoSuchElementException when brand not found`() {
        // Given
        val brandId = 1L
        val updateDTO = BrandDTO(brandId, "UpdatedBrand", null, null)

        every { brandRepository.findById(brandId.toInt()) } returns Optional.empty()

        // When & Then
        assertThrows<NoSuchElementException> {
            brandService.updateBrand(brandId, updateDTO)
        }
    }

    @Test
    fun `deleteBrand should delete the brand`() {
        // Given
        val brandId = 1L
        every { brandRepository.existsById(brandId.toInt()) } returns true
        every { brandRepository.deleteById(brandId.toInt()) } just Runs

        // When
        brandService.deleteBrand(brandId)

        // Then
        verify { brandRepository.existsById(brandId.toInt()) }
        verify { brandRepository.deleteById(brandId.toInt()) }
    }

    @Test
    fun `deleteBrand should throw NoSuchElementException when brand not found`() {
        // Given
        val brandId = 1L
        every { brandRepository.existsById(brandId.toInt()) } returns false

        // When & Then
        assertThrows<NoSuchElementException> {
            brandService.deleteBrand(brandId)
        }
    }

    @Test
    fun `getBrandById should return the brand when found`() {
        // Given
        val brandId = 1L
        val brand = Brand("TestBrand").apply {
            id = brandId
            createdAt = LocalDateTime.now()
        }

        every { brandRepository.findById(brandId.toInt()) } returns Optional.of(brand)

        // When
        val result = brandService.getBrandById(brandId)

        // Then
        assert(result.id == brandId)
        assert(result.name == "TestBrand")
        assert(result.createdAt == brand.createdAt)
        verify { brandRepository.findById(brandId.toInt()) }
    }

    @Test
    fun `getBrandById should throw NoSuchElementException when brand not found`() {
        // Given
        val brandId = 1L
        every { brandRepository.findById(brandId.toInt()) } returns Optional.empty()

        // When & Then
        assertThrows<NoSuchElementException> {
            brandService.getBrandById(brandId)
        }
    }

    @Test
    fun `getAllBrands should return list of all brands`() {
        // Given
        val brands = listOf(
            Brand("Brand1").apply { id = 1L; createdAt = LocalDateTime.now() },
            Brand("Brand2").apply { id = 2L; createdAt = LocalDateTime.now() }
        )

        every { brandRepository.findAll() } returns brands

        // When
        val result = brandService.getAllBrands()

        // Then
        assert(result.size == 2)
        assert(result[0].name == "Brand1")
        assert(result[1].name == "Brand2")
        verify { brandRepository.findAll() }
    }
}