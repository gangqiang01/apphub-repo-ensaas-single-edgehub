package com.m2m.management.repository;

import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProductItemRepository extends JpaRepository<ProductItem, Long> {


    List<ProductItem> findByProduct(Product product);

    long countByProduct(Product product);

    List<ProductItem> findByAppnameAndAndApptype(String appname, String apptype);

    void deleteByProduct(Product product);
}
