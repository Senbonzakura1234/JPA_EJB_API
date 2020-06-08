package com.app.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id")
    private String id;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<ProductAttribute> productAttributes;

    @Column(name = "userId")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "userId", updatable = false, insertable = false)
    private User user;

    @Column(name = "categoryId")
    private String categoryId;

    @ManyToOne
    @JoinColumn(name = "categoryId", updatable = false, insertable = false)
    private Category category;

    @NotBlank(message = "please provide a name")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "please provide a amount of product in stock")
    @Min(value = 0, message = "product in stock cannot be less then 0")
    @Column(name = "inStock", nullable = false)
    private Integer inStock;

    @NotBlank(message = "please provide a price of product")
    @Min(value = 0, message = "price cannot be less then 0")
    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "createdAt", nullable = false)
    private Long createdAt;

    @Column(name = "updatedAt", nullable = false)
    private Long updatedAt;

    @Column(name = "deletedAt")
    private Long deletedAt;

    @Column(name = "status", nullable = false)
    private StatusEnum status;

    public Product() {
        id = UUID.randomUUID().toString();
        status = StatusEnum.SHOW;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public enum StatusEnum {
        SHOW(0),
        DELETED(1);

        private final int value;

        StatusEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ProductAttribute> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(List<ProductAttribute> productAttributes) {
        this.productAttributes = productAttributes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Long deletedAt) {
        this.deletedAt = deletedAt;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}
