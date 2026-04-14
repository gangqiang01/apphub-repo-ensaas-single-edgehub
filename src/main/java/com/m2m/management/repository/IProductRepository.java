package com.m2m.management.repository;

import com.m2m.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IProductRepository extends JpaRepository<Product, Long> {

    Product findByProductname(String productname);

    List<Product> findByProductnameContaining(String keywords);

    List<Product> findByProductnameContainingOrderByTsDesc(String keywords);

    long countByProductname(String productname);

    long countByProductnameContaining(String keywords);

    @Transactional
    void deleteByProductname(String productname);

}
