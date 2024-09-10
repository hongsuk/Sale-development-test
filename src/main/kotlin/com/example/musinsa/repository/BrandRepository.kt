package com.example.musinsa.repository

import com.example.musinsa.entity.Brand
import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository : JpaRepository<Brand, Int> {
    fun findByName(name: String): Brand?
}