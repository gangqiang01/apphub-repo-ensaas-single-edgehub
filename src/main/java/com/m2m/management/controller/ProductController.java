package com.m2m.management.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.internal.org.bouncycastle.util.encoders.Base64Encoder;
import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductConcern;
import com.m2m.management.entity.ProductItem;
import com.m2m.management.entity.User;
import com.m2m.management.former.Response;
import com.m2m.management.service.IProductConcernService;
import com.m2m.management.service.IProductItemService;
import com.m2m.management.service.IProductService;
import com.m2m.management.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class ProductController {

    @Autowired
    IProductService productService;

    @Autowired
    IProductItemService productItemService;

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    IUserService userService;

    @Autowired
    IProductConcernService productConcernService;

    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public ResponseEntity<List<Product>> listAllProducts(@RequestParam(name="keywords", required = false, defaultValue ="") String keywords) {
        List<Product> products = productService.getProductByKeywords(keywords);
        long count  = productService.count(keywords);
        if(products != null){
            return new ResponseEntity(Response.success(products, count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/product/portrait", method = RequestMethod.POST)
    public ResponseEntity<Void> createProductPortrait(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("productname") String productname) throws IOException {
        Product product = productService.get(productname);
        if(product != null){
            byte[] b = file.getBytes();
            product.setPortrait(b);
            if(productService.update(product)){
                return new ResponseEntity(Response.success(product), HttpStatus.OK);
            }else{
                return new ResponseEntity(Response.error("Insert product portrait to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/product/{productname}", method = RequestMethod.GET)
    public ResponseEntity<Product> getProduct(@PathVariable("productname") String productname) {
        Product product = productService.get(productname);
        if(product != null){
            return new ResponseEntity(Response.success(product), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    public ResponseEntity<Void> createProduct(@RequestBody JSONObject json) throws Exception {
        String productname = json.getString("productname");
        String description = json.getString("description");
        String presentation = json.getString("presentation");
        JSONArray associatedApp = json.getJSONArray("associatedApp");
        JSONArray associatedOs = json.getJSONArray("associatedOs");
        JSONArray associatedPackage = json.getJSONArray("associatedPackage");
        JSONArray associatedFile = json.getJSONArray("associatedFile");
        if(productname == null || productname.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Product pro= productService.get(productname);
        if(pro == null){
            Product product = new Product();
            product.setProductname(productname);
            product.setDescription(description);
            product.setPresentation(presentation);
            product.setTs(new Date().getTime());
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = txManager.getTransaction(def);
            try{
                if(productService.add(product)){
                    Product p = productService.get(productname);
                    if(associatedApp != null && associatedApp.size() > 0){
                        for(int i = 0; i < associatedApp.size(); i ++){
                            ProductItem productItem = new ProductItem();
                            productItem.setFilename(associatedApp.getJSONObject(i).getString("filename"));
                            productItem.setAppname(associatedApp.getJSONObject(i).getString("pkgname"));
                            productItem.setVersion(associatedApp.getJSONObject(i).getString("versionname"));
                            productItem.setType("app");
                            productItem.setApptype("app");
                            productItem.setProduct(p);
                            productItemService.add(productItem);
                        }
                    }
                    if(associatedPackage != null && associatedPackage.size() > 0){
                        for(int i = 0; i < associatedPackage.size(); i ++){
                            ProductItem productItem = new ProductItem();
                            productItem.setFilename(associatedPackage.getJSONObject(i).getString("filename"));
                            productItem.setAppname(associatedPackage.getJSONObject(i).getString("productname"));
                            productItem.setVersion(associatedPackage.getJSONObject(i).getString("version"));
                            productItem.setType("package");
                            productItem.setApptype(associatedPackage.getJSONObject(i).getString("type"));
                            productItem.setProduct(p);
                            productItemService.add(productItem);
                        }
                    }
                    if(associatedOs != null && associatedOs.size() > 0){
                        for(int i = 0; i < associatedOs.size(); i ++){
                            ProductItem productItem = new ProductItem();
                            productItem.setAppname(associatedOs.getJSONObject(i).getString("boardname"));
                            productItem.setVersion(associatedOs.getJSONObject(i).getString("versionname"));
                            productItem.setType("os");
                            productItem.setApptype(associatedOs.getJSONObject(i).getString("os"));
                            productItem.setProduct(p);
                            productItemService.add(productItem);
                        }
                    }
                    if(associatedFile != null && associatedFile.size() > 0){
                        for(int i = 0; i < associatedFile.size(); i ++){
                            ProductItem productItem = new ProductItem();
                            productItem.setAppname(associatedFile.getJSONObject(i).getString("filename"));
                            productItem.setFilename(associatedFile.getJSONObject(i).getString("filename"));
                            productItem.setVersion(associatedFile.getJSONObject(i).getString("format"));
                            productItem.setType("file");
                            productItem.setApptype(associatedFile.getJSONObject(i).getString("type"));
                            productItem.setProduct(p);
                            productItemService.add(productItem);
                        }
                    }
                    txManager.commit(status);
                    return new ResponseEntity(Response.success(p), HttpStatus.OK);

                }else{
                    return new ResponseEntity(Response.error("Insert Product to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }catch (Exception e){
                txManager.rollback(status);
                e.printStackTrace();
                return new ResponseEntity(Response.error("Insert ProductItem to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else {
            return new ResponseEntity(Response.error("Product is already exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/product/byname", method = RequestMethod.POST)
    public ResponseEntity<Void> updateProduct(@RequestBody JSONObject json) throws Exception {
        String oldProductname = json.getString("oldProductname");
        String productname = json.getString("productname");
        String description = json.getString("description");
        String presentation = json.getString("presentation");
        JSONArray associatedApp = json.getJSONArray("associatedApp");
        JSONArray associatedOs = json.getJSONArray("associatedOs");
        JSONArray associatedPackage = json.getJSONArray("associatedPackage");
        JSONArray associatedFile = json.getJSONArray("associatedFile");
        if(productname == null || productname.equals("") || description == null || description.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Product pro= productService.get(oldProductname);
        List<ProductConcern> productConcern = productConcernService.getProductConcernByProductname(productname);
        if(pro != null){
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = txManager.getTransaction(def);
            try{
                if(productItemService.delete(pro)){
                    pro.setProductname(productname);
                    pro.setDescription(description);
                    pro.setPresentation(presentation);
                    if(productService.update(pro) && productConcernService.updateProductConcernByProductname(productname, oldProductname)){
                        Product p = productService.get(productname);
                            if(associatedApp != null && associatedApp.size() > 0){
                                for(int i = 0; i < associatedApp.size(); i ++){
                                    ProductItem productItem = new ProductItem();
                                    productItem.setFilename(associatedApp.getJSONObject(i).getString("filename"));
                                    productItem.setAppname(associatedApp.getJSONObject(i).getString("pkgname"));
                                    productItem.setVersion(associatedApp.getJSONObject(i).getString("versionname"));
                                    productItem.setType("app");
                                    productItem.setApptype("app");
                                    productItem.setProduct(p);
                                    productItemService.add(productItem);
                                }
                            }
                            if(associatedPackage != null && associatedPackage.size() > 0){
                                for(int i = 0; i < associatedPackage.size(); i ++){
                                    ProductItem productItem = new ProductItem();
                                    productItem.setFilename(associatedPackage.getJSONObject(i).getString("filename"));
                                    productItem.setAppname(associatedPackage.getJSONObject(i).getString("productname"));
                                    productItem.setVersion(associatedPackage.getJSONObject(i).getString("version"));
                                    productItem.setType("package");
                                    productItem.setApptype(associatedPackage.getJSONObject(i).getString("type"));
                                    productItem.setProduct(p);
                                    productItemService.add(productItem);
                                }
                            }
                            if(associatedOs != null && associatedOs.size() > 0){
                                for(int i = 0; i < associatedOs.size(); i ++){
                                    ProductItem productItem = new ProductItem();
                                    productItem.setAppname(associatedOs.getJSONObject(i).getString("boardname"));
                                    productItem.setVersion(associatedOs.getJSONObject(i).getString("versionname"));
                                    productItem.setType("os");
                                    productItem.setApptype(associatedOs.getJSONObject(i).getString("os"));
                                    productItem.setProduct(p);
                                    productItemService.add(productItem);
                                }
                            }
                            if(associatedFile != null && associatedFile.size() > 0){
                                for(int i = 0; i < associatedFile.size(); i ++){
                                    ProductItem productItem = new ProductItem();
                                    productItem.setVersion(associatedFile.getJSONObject(i).getString("format"));
                                    productItem.setFilename(associatedFile.getJSONObject(i).getString("filename"));
                                    productItem.setAppname(associatedFile.getJSONObject(i).getString("filename"));
                                    productItem.setType("file");
                                    productItem.setApptype(associatedFile.getJSONObject(i).getString("type"));
                                    productItem.setProduct(p);
                                    productItemService.add(productItem);
                                }
                            }
                            txManager.commit(status);
                            return new ResponseEntity(Response.success(p), HttpStatus.OK);

                        } else{
                            return new ResponseEntity(Response.error("Update Product to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }else {
                    return new ResponseEntity(Response.error("Update ProductItem to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }catch (Exception e){
                txManager.rollback(status);
                e.printStackTrace();
                return new ResponseEntity(Response.error("Update ProductItem to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else {
            return new ResponseEntity(Response.error("Product is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/product/{productname}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteProduct(@PathVariable("productname") String productname) {
        Product product = productService.get(productname);
        List<ProductConcern> productConcerns = productConcernService.getProductConcernByProductname(productname);
        if(product != null){
            if(productConcerns != null){
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                TransactionStatus status = txManager.getTransaction(def);
                try {
                    if(productConcernService.deleteProductConcernByProductname(productname)){
                        if(productService.delete(productname)) {
                            txManager.commit(status);
                            return new ResponseEntity(Response.success(), HttpStatus.OK);
                        }else{
                            txManager.rollback(status);
                            return new ResponseEntity(Response.error("Delete Product error"), HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }else{
                        txManager.rollback(status);
                        return new ResponseEntity(Response.error("Delete Product Concern error"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }catch (Exception e){
                    txManager.rollback(status);
                    e.printStackTrace();
                }
            }else{
                if(productService.delete(productname)) {
                    return new ResponseEntity(Response.success(), HttpStatus.OK);
                }else{
                    return new ResponseEntity(Response.error("Delete Product error"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }else{
            return new ResponseEntity(Response.error("Product is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
