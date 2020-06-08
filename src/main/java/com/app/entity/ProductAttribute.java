package com.app.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "productAttribute")
public class ProductAttribute {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "productId")
    private String productId;

    @ManyToOne
    @JoinColumn(name = "productId", updatable = false, insertable = false)
    private Product product;

    @Column(name = "attributeId")
    private String attributeId;

    @ManyToOne
    @JoinColumn(name = "attributeId", updatable = false, insertable = false)
    private Attribute attribute;

    @Column(name = "createdAt", nullable = false)
    private Long createdAt;

    @Column(name = "updatedAt", nullable = false)
    private Long updatedAt;

    @Column(name = "status", nullable = false)
    private StatusEnum status;

    public ProductAttribute() {
        id = UUID.randomUUID().toString();
        status = StatusEnum.SHOW;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public enum StatusEnum {
        SHOW (0),
        DELETED (1);

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
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

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}
