package com.example.musinsa.repository

import com.example.musinsa.dto.BrandTotalPriceDTO
import com.example.musinsa.dto.CategoryMinPriceDTO
import com.example.musinsa.dto.ProductPriceDTO
import com.example.musinsa.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Int> {
    @Query("""
        SELECT new com.example.musinsa.dto.CategoryMinPriceDTO(
            p.category.name,
            p.brand.name,
            p.price
        )
        FROM Product p
        WHERE p.id = (
            SELECT MIN(p2.id)
            FROM Product p2
            WHERE p2.category = p.category
            AND p2.price = (
                SELECT MIN(p3.price)
                FROM Product p3
                WHERE p3.category = p.category
            )
        )
        ORDER BY p.category.id
    """)
    fun findLowestPriceByCategory(): List<CategoryMinPriceDTO>


    @Query("""
        SELECT COALESCE(SUM(p.price), 0)
        FROM Product p
        WHERE p.id = (
            SELECT MIN(p2.id)
            FROM Product p2
            WHERE p2.category = p.category
            AND p2.price = (
                SELECT MIN(p3.price)
                FROM Product p3
                WHERE p3.category = p.category
            )
        )
    """)
    fun calculateTotalMinPrice(): Int


    @Query("""
        SELECT new com.example.musinsa.dto.BrandTotalPriceDTO(
            p.brand.id,
            p.brand.name,
            CAST(SUM(p.price) AS int)
        )
        FROM Product p
        GROUP BY p.brand
        HAVING SUM(p.price) = (
            SELECT MIN(subquery.total)
            FROM (
                SELECT SUM(p2.price) as total
                FROM Product p2
                GROUP BY p2.brand
            ) subquery
        )
    """)
    fun findBrandWithLowestTotalPrice(): BrandTotalPriceDTO

    fun findProductByBrandId(brandId: Long): List<Product>

    @Query("""
        SELECT new com.example.musinsa.dto.ProductPriceDTO(
            p.brand.name, 
            p.price
        )
        FROM Product p
        WHERE p.category.id = :categoryId
        AND p.price = (
            SELECT MIN(p2.price)
            FROM Product p2
            WHERE p2.category.id = :categoryId
        )
    """)
    fun findLowestPriceProductByCategory(@Param("categoryId") categoryId: Long): ProductPriceDTO

    @Query("""
        SELECT new com.example.musinsa.dto.ProductPriceDTO(
            p.brand.name, 
            p.price
        )
        FROM Product p
        WHERE p.category.id = :categoryId
        AND p.price = (
            SELECT MAX(p2.price)
            FROM Product p2
            WHERE p2.category.id = :categoryId
        )
    """)
    fun findHighestPriceProductByCategory(@Param("categoryId") categoryId: Long): ProductPriceDTO

}

