package com.example.musinsa.service

import com.example.musinsa.dto.BrandTotalPriceDTO
import com.example.musinsa.dto.CategoryMinPriceDTO
import com.example.musinsa.dto.ProductDTO
import com.example.musinsa.dto.ProductPriceDTO
import com.example.musinsa.entity.Brand
import com.example.musinsa.entity.Category
import com.example.musinsa.entity.Product
import com.example.musinsa.repository.BrandRepository
import com.example.musinsa.repository.CategoryRepository
import com.example.musinsa.repository.ProductRepository
import io.mockk.*
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock

class ProductServiceTest {
    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @Mock
    private lateinit var brandRepository: BrandRepository

    @InjectMocks
    private lateinit var productService: ProductService

    @BeforeEach
    fun setup() {
        productRepository = mockk()
        categoryRepository = mockk()
        brandRepository = mockk()
        productService = ProductService(productRepository, categoryRepository, brandRepository)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `addProduct should create and return a new product`() {
        // Given
        val productDTO = ProductDTO(null, 1000, "Category1", "Brand1", null, null)
        val category = Category("Category1")
        val brand = Brand("Brand1")
        val savedProduct = Product(price = 1000, category = category, brand = brand).apply { id = 1L }

        every { categoryRepository.findByName("Category1") } returns category
        every { brandRepository.findByName("Brand1") } returns brand
        every { productRepository.save(any()) } returns savedProduct

        // When
        val result = productService.addProduct(productDTO)

        // Then
        assert(result.id == 1L)
        assert(result.price == 1000)
        assert(result.categoryName == "Category1")
        assert(result.brandName == "Brand1")
        verify { productRepository.save(any()) }
    }

    @Test
    fun `addProduct should throw exception when category not found`() {
        // Given
        val productDTO = ProductDTO(null, 1000, "NonExistentCategory", "Brand1", null, null)
        every { categoryRepository.findByName("NonExistentCategory") } returns null

        // When & Then
        assertThrows<NoSuchElementException> {
            productService.addProduct(productDTO)
        }
    }

    @Test
    fun `updateProduct should update and return the product`() {
        // Given
        val productId = 1L
        val updateDTO = ProductDTO(productId, 2000, "UpdatedCategory", "UpdatedBrand", null, null)
        val existingProduct = Product(price = 1000, category = Category("OldCategory"), brand = Brand("OldBrand")).apply { id = productId }
        val updatedCategory = Category("UpdatedCategory")
        val updatedBrand = Brand("UpdatedBrand")

        every { productRepository.findById(productId.toInt()) } returns java.util.Optional.of(existingProduct)
        every { categoryRepository.findByName("UpdatedCategory") } returns updatedCategory
        every { brandRepository.findByName("UpdatedBrand") } returns updatedBrand
        every { productRepository.save(any()) } returns existingProduct.apply {
            price = 2000
            category = updatedCategory
            brand = updatedBrand
        }

        // When
        val result = productService.updateProduct(productId, updateDTO)

        // Then
        assert(result.id == productId)
        assert(result.price == 2000)
        assert(result.categoryName == "UpdatedCategory")
        assert(result.brandName == "UpdatedBrand")
        verify { productRepository.save(any()) }
    }

    @Test
    fun `deleteProduct should delete the product`() {
        // Given
        val productId = 1L
        every { productRepository.existsById(productId.toInt()) } returns true
        every { productRepository.deleteById(productId.toInt()) } just Runs

        // When
        productService.deleteProduct(productId)

        // Then
        verify { productRepository.deleteById(productId.toInt()) }
    }

    @Test
    fun `deleteProduct should throw exception when product not found`() {
        // Given
        val productId = 1L
        every { productRepository.existsById(productId.toInt()) } returns false

        // When & Then
        assertThrows<NoSuchElementException> {
            productService.deleteProduct(productId)
        }
    }

    @Test
    fun `getLowestPriceSummaryByCategory should return correct summary`() {
        // Given
        val products = listOf(
            CategoryMinPriceDTO("Category1", "BrandA", 1000),
            CategoryMinPriceDTO("Category2", "BrandB", 2000)
        )
        val totalPrice = 3000

        every { productRepository.findLowestPriceByCategory() } returns products
        every { productRepository.calculateTotalMinPrice() } returns totalPrice

        // When
        val result = productService.getLowestPriceSummaryByCategory()

        // Then
        assert(result.products == products)
        assert(result.totalPrice == totalPrice)
        verify { productRepository.findLowestPriceByCategory() }
        verify { productRepository.calculateTotalMinPrice() }
    }

    @Test
    fun `getLowestPriceByBrand should return correct summary`() {
        // Given
        val lowestTotalPriceBrand = BrandTotalPriceDTO(1L, "BrandA", 3000)
        val products = listOf(
            Product(price = 1000, category = Category("Category1"), brand = Brand("BrandA")),
            Product(price = 2000, category = Category("Category2"), brand = Brand("BrandA"))
        )

        every { productRepository.findBrandWithLowestTotalPrice() } returns lowestTotalPriceBrand
        every { productRepository.findProductByBrandId(1L) } returns products

        // When
        val result = productService.getLowestPriceByBrand()

        // Then
        assert(result.lowestPrice.brand == "BrandA")
        assert(result.lowestPrice.categories.size == 2)
        assert(result.lowestPrice.totalPrice == 3000)
        verify { productRepository.findBrandWithLowestTotalPrice() }
        verify { productRepository.findProductByBrandId(1L) }
    }

    @Test
    fun `getCategoryPriceRange should return correct price range`() {
        // Given
        val categoryName = "TestCategory"
        val category = Category(categoryName).apply { id = 1L }
        val lowestPrice = ProductPriceDTO("BrandA", 1000)
        val highestPrice = ProductPriceDTO("BrandB", 5000)

        every { categoryRepository.findByName(categoryName) } returns category
        every { productRepository.findLowestPriceProductByCategory(1L) } returns lowestPrice
        every { productRepository.findHighestPriceProductByCategory(1L) } returns highestPrice

        // When
        val result = productService.getCategoryPriceRange(categoryName)

        // Then
        assert(result.category == categoryName)
        assert(result.lowestPrice.brandName == "BrandA" && result.lowestPrice.price == 1000)
        assert(result.highestPrice.brandName == "BrandB" && result.highestPrice.price == 5000)
        verify { categoryRepository.findByName(categoryName) }
        verify { productRepository.findLowestPriceProductByCategory(1L) }
        verify { productRepository.findHighestPriceProductByCategory(1L) }
    }

    @Test
    fun `getCategoryPriceRange should throw NoSuchElementException when category not found`() {
        // Given
        val nonExistentCategory = "NonExistentCategory"
        every { categoryRepository.findByName(nonExistentCategory) } returns null

        // When & Then
        assertThrows<NoSuchElementException> {
            productService.getCategoryPriceRange(nonExistentCategory)
        }
    }

    @Test
    fun `getCategoryPriceRange should throw IllegalStateException when category has no ID`() {
        // Given
        val categoryName = "CategoryWithNoId"
        val category = Category(categoryName).apply { id = null }
        every { categoryRepository.findByName(categoryName) } returns category

        // When & Then
        assertThrows<IllegalStateException> {
            productService.getCategoryPriceRange(categoryName)
        }
    }
}