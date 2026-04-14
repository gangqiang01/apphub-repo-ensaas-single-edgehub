package com.m2m.management.service;

import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductConcern;
import com.m2m.management.entity.User;

import java.util.List;

public interface IProductConcernService {

    boolean add(ProductConcern productConcern);

    ProductConcern get(String productname, User user);

    boolean deleteByPcid(long pcid);

    List<ProductConcern> getProductConcernByUser(User user);

    long countByUser(User user);

    List<ProductConcern> getProductConcernByProductname(String productname);

    long countByProductname(String productname);

    boolean deleteProductConcernByProductname(String productname);

    boolean updateProductConcernByProductname(String productname, String oldProductname);

}
