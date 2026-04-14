package com.m2m.management.controller;


import com.alibaba.fastjson.JSONObject;
import com.m2m.management.entity.ProductImage;
import com.m2m.management.former.Response;
import com.m2m.management.service.IProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class ProductImageController {

    @Autowired
    IProductImageService productImageService;

    @RequestMapping(value = "/product/image", method = RequestMethod.POST)
    public ResponseEntity<Void> addProductImage(@RequestBody JSONObject jsonObject){
        String image = jsonObject.getString("image");
        ProductImage productImage = new ProductImage();
        productImage.setImage(image);
        productImage.setTs(new Date().getTime());
        if(productImageService.add(productImage)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Insert product image to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/product/image", method = RequestMethod.GET)
    public ResponseEntity<List<ProductImage>> listProductImages() {
        List<ProductImage> productImages = productImageService.getAll();
        if(productImages != null){
            return new ResponseEntity(Response.success(productImages), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/product/image/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteProductImage(@PathVariable("id") long id) {
        ProductImage productImage = productImageService.getById(id);
        if(productImage != null){
            if(productImageService.deleteById(id)){
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            }else{
                return new ResponseEntity(Response.error("Delete product image error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity(Response.error("Product image is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
