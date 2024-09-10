package com.example.musinsa.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name="brands")
class Brand (
    @Column(name="name")
    var name:String,
    @Column(name="created_at")
    @CreationTimestamp
    var createdAt : LocalDateTime = LocalDateTime.now(),
    @Column(name="updated_at")
    @UpdateTimestamp
    var updatedAt : LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long?=null
}