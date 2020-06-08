package com.app.context;

import com.app.entity.*;
import com.app.model.returnResult.DatabaseQueryResult;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.ArrayList;
import java.util.List;

@Stateless(name = "ProductEJB")
public class ProductBean {
    @PersistenceUnit
    EntityManagerFactory emf;

    public ProductBean() {
    }

    public Product getProduct(String id) {
        if(id != null && !id.isEmpty()){
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            Product product = em.find(Product.class, id);
            em.getTransaction().commit();
            em.close();
            return product.getStatus() != Product.StatusEnum.DELETED? product : null;
        }else {
            return null;
        }
    }

    public List<Product> getAllProduct(String query) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Product> list;
        if (query != null && !query.isEmpty()) {
            list = em.createQuery(
                    "SELECT c from Product c where name like :queryString and status != :queryStatus",
                    Product.class).setParameter("queryString", "%" + query + "%")
                    .setParameter("queryStatus", Product.StatusEnum.DELETED).getResultList();
        } else {
            list =  em.createQuery("SELECT c from Product c where status != :queryStatus",
                    Product.class).setParameter("queryStatus", Product.StatusEnum.DELETED)
                    .getResultList();
        }
        em.getTransaction().commit();
        em.close();
        return list;
    }

    public List<Product> getProductsOfCategory(String id) {
        List<Product> list = new ArrayList<>();
        if (id == null || id.isEmpty()) return list;
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        list = em.createQuery(
                "select c from Product  c where categoryId = :queryString and status != :queryStatus",
                Product.class)
                .setParameter("queryString", id)
                .setParameter("queryStatus", Product.StatusEnum.DELETED)
                .getResultList();
        em.getTransaction().commit();
        em.close();
        return list;
    }


    public List<Product> getProductsOfAttribute(String id) {
        List<Product> products = new ArrayList<>();

        if(id == null || id.isEmpty()) return products;

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        if(em.find(Attribute.class, id) == null) return products;

        List<ProductAttribute> list = em.createQuery(
                "select c from ProductAttribute  c where attributeId = :queryString and status != :queryStatus",
                ProductAttribute.class)
                .setParameter("queryString", id)
                .setParameter("queryStatus", ProductAttribute.StatusEnum.DELETED)
                .getResultList();

        for (ProductAttribute item: list
        ) {
            Product product = em.find(Product.class, item.getProductId());
            if(product != null){
                products.add(product);
            }
        }

        return products;
    }

    public DatabaseQueryResult addProduct(Product product, String userId,
                                          List<String> attributes,
                                          String categoryId) {
        if(product != null && userId != null &&
                categoryId != null && attributes.size() > 0){

            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            List<ProductAttribute> list = new ArrayList<>();

            if(em.find(User.class, userId) != null){
                product.setUserId(userId);
            }else {
                return new DatabaseQueryResult(
                        false,
                        "addProduct fail, User not found");
            }

            if(em.find(Category.class, categoryId) != null){
                product.setCategoryId(categoryId);
            }else {
                return new DatabaseQueryResult(
                        false,
                        "addProduct fail, Category not found");
            }

            for (String id: attributes
                 ) {
                if(em.find(Attribute.class, id) != null){
                    ProductAttribute productAttribute = new ProductAttribute();
                    productAttribute.setAttributeId(id);
                    productAttribute.setProductId(product.getId());
                    list.add(productAttribute);
                }
            }

            if(list.size() < 1) {
                em.getTransaction().commit();
                em.close();
                return new DatabaseQueryResult(
                        false,
                        "addProduct fail, Attribute not found");
            }else{
                em.persist(product);
                em.getTransaction().commit();
                em.close();
                return new DatabaseQueryResult(true, "addProduct success");
            }
        }else {
            return new DatabaseQueryResult(false, "addProduct fail, input is null");
        }
    }

    public DatabaseQueryResult updateProduct(Product product,
                                             String id, String userId,
                                             List<String> attributes,
                                             String categoryId) {
        if(product != null && id != null && !id.isEmpty() &&
                userId != null && !userId.isEmpty() && attributes.size() > 0 &&
                categoryId != null && !categoryId.isEmpty()
        ){
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Product u = em.find(Product.class, id);
            if(u != null && u.getStatus() != Product.StatusEnum.DELETED){
                List<ProductAttribute> listToAdd = new ArrayList<>();
                for (String item: attributes
                ) {
                    if(em.find(Attribute.class, item) != null){
                        ProductAttribute productAttribute = new ProductAttribute();
                        productAttribute.setProductId(id);
                        productAttribute.setAttributeId(item);
                        listToAdd.add(productAttribute);
                    }
                }
                if(listToAdd.size() < 1)  return new DatabaseQueryResult(false,
                        "updateProduct failed, Product not found");

                List<ProductAttribute> listToDel = em.createQuery(
                        "select c from ProductAttribute  c where productId = :queryString and status != :queryStatus",
                        ProductAttribute.class)
                        .setParameter("queryString", id)
                        .setParameter("queryStatus", ProductAttribute.StatusEnum.DELETED)
                        .getResultList();

                for (ProductAttribute item: listToDel
                     ) {
                    if(item != null){
                        ProductAttribute pAToDel = em.find(ProductAttribute.class, item.getId());
                        if (pAToDel != null){
                            pAToDel.setStatus(ProductAttribute.StatusEnum.DELETED);
                        }
                    }
                }

                u.setName(product.getName());
                u.setCategoryId(categoryId);
                u.setUserId(userId);
                u.setProductAttributes(product.getProductAttributes());
                u.setUser(product.getUser());
                u.setCategory(product.getCategory());
                u.setInStock(product.getInStock());
                u.setPrice(product.getPrice());
                if(product.getStatus() != Product.StatusEnum.DELETED){
                    u.setStatus(product.getStatus());
                }
                u.setUpdatedAt(System.currentTimeMillis());
                u.setProductAttributes(listToAdd);

                em.getTransaction().commit();
                em.close();
                return new DatabaseQueryResult(true,
                        "updateProduct success");
            }else {
                em.getTransaction().commit();
                em.close();
                return new DatabaseQueryResult(false,
                        "updateProduct failed, Product not found");
            }
        }else {
            return new DatabaseQueryResult(false,
                    "updateProduct failed, bad request");
        }
    }

    public DatabaseQueryResult deleteProduct(String id) {
        if(id != null && !id.isEmpty()){
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            Product u = em.find(Product.class, id);
            if(u != null && u.getStatus() != Product.StatusEnum.DELETED){
                List<ProductAttribute> listToDel = em.createQuery(
                        "select c from ProductAttribute  c where productId = :queryString and status != :queryStatus",
                        ProductAttribute.class)
                        .setParameter("queryString", id)
                        .setParameter("queryStatus", ProductAttribute.StatusEnum.DELETED)
                        .getResultList();

                for (ProductAttribute item: listToDel
                ) {
                    if(item != null){
                        ProductAttribute pAToDel = em.find(ProductAttribute.class, item.getId());
                        if (pAToDel != null){
                            pAToDel.setStatus(ProductAttribute.StatusEnum.DELETED);
                        }
                    }
                }

                u.setStatus(Product.StatusEnum.DELETED);
                u.setUpdatedAt(System.currentTimeMillis());
                u.setDeletedAt(System.currentTimeMillis());

                em.getTransaction().commit();
                em.close();
                return new DatabaseQueryResult(true,
                        "deleteProduct success");
            }else {
                em.getTransaction().commit();
                em.close();
                return new DatabaseQueryResult(false,
                        "deleteProduct failed, Product not found");
            }
        }else {
            return new DatabaseQueryResult(false,
                    "deleteProduct failed, bad request");
        }
    }
}
