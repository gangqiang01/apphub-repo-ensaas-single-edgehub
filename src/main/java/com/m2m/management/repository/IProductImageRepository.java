package com.m2m.management.repository;

import com.m2m.management.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductImageRepository extends JpaRepository<ProductImage, Long> {

    ProductImage findById(long id);

}
