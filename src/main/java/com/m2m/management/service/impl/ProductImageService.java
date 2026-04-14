package com.m2m.management.service.impl;

import com.m2m.management.entity.ProductImage;
import com.m2m.management.repository.IProductImageRepository;
import com.m2m.management.service.IProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class ProductImageService implements IProductImageService {

    @Autowired
    IProductImageRepository productImageRepository;

    @Override
    public boolean add(ProductImage productImage) {
        try{
            productImageRepository.save(productImage);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ProductImage> getAll() {
        try{
            List<ProductImage> productImages = productImageRepository.findAll();
            return productImages;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ProductImage getById(long id) {
        try{
            ProductImage productImage = productImageRepository.findById(id);
            return productImage;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteById(long id) {
        try{
            productImageRepository.deleteById(id);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
