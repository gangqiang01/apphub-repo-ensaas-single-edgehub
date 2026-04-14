package com.m2m.management.service;

import com.m2m.management.entity.Product;

import java.util.List;


public interface IProductService {

    long countByName(String productname);

    boolean add(Product product);

    boolean update(Product product);

    Product get(String productname);

    List<Product> getAll();
    List<Product> getProductByKeywords(String keywords);

    long count();

    long count(String keywords);

    boolean delete(String productname);
}
