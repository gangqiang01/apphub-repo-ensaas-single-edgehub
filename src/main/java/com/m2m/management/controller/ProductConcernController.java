package com.m2m.management.controller;


import com.alibaba.fastjson.JSONObject;
import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductConcern;
import com.m2m.management.entity.User;
import com.m2m.management.former.Response;
import com.m2m.management.service.IProductConcernService;
import com.m2m.management.service.IProductService;
import com.m2m.management.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
public class ProductConcernController {

    @Autowired
    IUserService userService;

    @Autowired
    IProductService productService;

    @Autowired
    IProductConcernService productConcernService;

    @RequestMapping(value = "/productConcern/{username}", method = RequestMethod.GET)
    public ResponseEntity<Product> getProduct(@PathVariable("username") String username) {
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<ProductConcern> productConcerns = productConcernService.getProductConcernByUser(user);
        long count = productConcernService.countByUser(user);
        if(productConcerns != null){
            return new ResponseEntity(Response.success(productConcerns, count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/addProductConcern", method = RequestMethod.POST)
    public ResponseEntity<Void> addProductConcern(@RequestBody JSONObject json) {
        String productname = json.getString("productname");
        String username = json.getString("username");
        if(productname == null || productname.equals("") || username == null || username.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = userService.get(username);
        Product product = productService.get(productname);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (product == null){
            return new ResponseEntity(Response.error("Product name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ProductConcern pc = productConcernService.get(productname, user);
        if(pc != null){
            return new ResponseEntity(Response.error("Product Concern is already exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ProductConcern productConcern = new ProductConcern();
        productConcern.setProductname(productname);
        productConcern.setUser(user);
        if(productConcernService.add(productConcern)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Insert Product Concern to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/deleteProductConcern", method = RequestMethod.POST)
    public ResponseEntity<Void> deleteProductConcern(@RequestBody JSONObject json) {
        String productname = json.getString("productname");
        String username = json.getString("username");
        if(productname == null || productname.equals("") || username == null || username.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = userService.get(username);
        Product product = productService.get(productname);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (product == null){
            return new ResponseEntity(Response.error("Product name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ProductConcern pc = productConcernService.get(productname, user);
        if(pc == null){
            return new ResponseEntity(Response.error("Product Concern is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(productConcernService.deleteByPcid(pc.getPcid())){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
