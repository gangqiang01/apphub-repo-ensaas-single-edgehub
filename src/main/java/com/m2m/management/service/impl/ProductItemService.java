package com.m2m.management.service.impl;

import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductItem;
import com.m2m.management.repository.IProductItemRepository;
import com.m2m.management.service.IProductItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class ProductItemService implements IProductItemService {

    @Autowired
    IProductItemRepository productItemRepository;

    @Override
    public boolean add(ProductItem productItem) {
        try{
            productItemRepository.save(productItem);
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
    public List<ProductItem> getAll() {
        try{
            List<ProductItem> productItems= productItemRepository.findAll();
            return productItems;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long count() {
        try{
            long count = productItemRepository.count();
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<ProductItem> getProductItems(Product product) {
        try{
            List<ProductItem> productItems= productItemRepository.findByProduct(product);
            return productItems;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long countByProduct(Product product) {
        try{
            long count = productItemRepository.countByProduct(product);
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean delete(Product product) {
        try{
            productItemRepository.deleteByProduct(product);
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
    public ProductItem get(long piid) {
        try{
            Optional<ProductItem> productItemOptional = productItemRepository.findById(piid);
            ProductItem productItem = productItemOptional.get();
            return productItem;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ProductItem> getProductItemByAppnameAndApptype(String appname, String apptype) {
        try{
            List<ProductItem> productItems = productItemRepository.findByAppnameAndAndApptype(appname, apptype);
            return productItems;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
