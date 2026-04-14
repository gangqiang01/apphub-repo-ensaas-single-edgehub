package com.m2m.management.service;

import com.m2m.management.entity.ProductImage;

import java.util.List;

public interface IProductImageService {

    boolean add(ProductImage productImage);

    List<ProductImage> getAll();

    ProductImage getById(long id);

    boolean deleteById(long id);
}
