package com.example.musinsa.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name="products")
class Product (
    @Column(name="price")
    var price : Int,
    @Column(name="created_at")
    var createdAt : LocalDateTime = LocalDateTime.now(),
    @Column(name="updated_at")
    var updatedAt : LocalDateTime = LocalDateTime.now(),
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var category: Category,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var brand: Brand
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long?=null
}