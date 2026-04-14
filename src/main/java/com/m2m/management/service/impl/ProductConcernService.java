package com.m2m.management.service.impl;


import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductConcern;
import com.m2m.management.entity.User;
import com.m2m.management.repository.IProductConcernRepository;
import com.m2m.management.service.IProductConcernService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class ProductConcernService implements IProductConcernService {

    @Autowired
    IProductConcernRepository productConcernRepository;

    @Override
    public boolean add(ProductConcern productConcern) {
        try{
            productConcernRepository.save(productConcern);
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
    public ProductConcern get(String productname, User user) {
        try{
            ProductConcern productConcern = productConcernRepository.findByProductnameAndUser(productname, user);
            return productConcern;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteByPcid(long pcid) {
        try{
            productConcernRepository.deleteById(pcid);
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
    public List<ProductConcern> getProductConcernByUser(User user) {
        try{
            List<ProductConcern> productConcerns = productConcernRepository.findByUser(user);
            return productConcerns;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long countByUser(User user) {
        try{
            long count = productConcernRepository.countByUser(user);
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
    public List<ProductConcern> getProductConcernByProductname(String productname) {
        try{
            List<ProductConcern> productConcerns = productConcernRepository.findByProductname(productname);
            return productConcerns;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long countByProductname(String productname) {
        try{
            long count = productConcernRepository.countByProductname(productname);
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
    public boolean deleteProductConcernByProductname(String productname) {
        try{
            productConcernRepository.deleteByProductname(productname);
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
    public boolean updateProductConcernByProductname(String productname, String oldProductname) {
        try{
            productConcernRepository.updateByProductname(productname, oldProductname);
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
