package com.example.musinsa.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name="categories")
class Category (
    @Column(name="name")
    var name:String,
    @Column(name="created_at")
    var createdAt : LocalDateTime = LocalDateTime.now(),
    @Column(name="updated_at")
    var updatedAt : LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long?=null
}