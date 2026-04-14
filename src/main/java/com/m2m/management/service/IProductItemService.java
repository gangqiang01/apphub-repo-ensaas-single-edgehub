package com.m2m.management.service;

import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductItem;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public interface IProductItemService {

    boolean add(ProductItem productItem);

    List<ProductItem> getAll();

    long count();

    List<ProductItem> getProductItems(Product product);

    long countByProduct(Product product);

    boolean delete(Product product);

    ProductItem get(long piid);

    List<ProductItem> getProductItemByAppnameAndApptype(String appname, String apptype);

}
