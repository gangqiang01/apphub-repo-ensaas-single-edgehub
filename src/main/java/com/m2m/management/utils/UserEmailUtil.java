package com.m2m.management.utils;

import com.alibaba.fastjson.JSONObject;
import com.m2m.management.entity.Attention;
import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductConcern;
import com.m2m.management.entity.ProductItem;
import com.m2m.management.service.IAttentionService;
import com.m2m.management.service.IProductConcernService;
import com.m2m.management.service.IProductItemService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class UserEmailUtil {

    public static String[] getUserEmail(String appname, String type, IProductItemService productItemService, IProductConcernService productConcernService, IAttentionService attentionService){
        String[] emails = null;
        try{
            Set userSet = new HashSet();
            List<Attention> attentionList = attentionService.getAttentionByNameAndType(appname, type);
            List<ProductItem> productItemList = productItemService.getProductItemByAppnameAndApptype(appname,type );
            if(attentionList != null && attentionList.size() > 0){
                for(int i = 0; i < attentionList.size(); i ++){
                    userSet.add(attentionList.get(i).getUser().getName());
                }
            }
            if(productItemList != null && productItemList.size() > 0){
                Set productSet = new HashSet();
                for(int i = 0; i < productItemList.size(); i ++) {
                    productSet.add(productItemList.get(i).getProduct());
                }
                if(productSet.size() > 0){
                    for(Object object: productSet){
                        JSONObject ob = (JSONObject) JSONObject.toJSON(object);
                        List<ProductConcern> productConcernList = productConcernService.getProductConcernByProductname(ob.getString("productname"));
                        if(productConcernList != null && productConcernList.size() > 0){
                            for(int i = 0; i < productConcernList.size(); i ++){
                                userSet.add(productConcernList.get(i).getUser().getName());
                            }
                        }
                    }
                }
            }
            if(userSet.size() > 0){
                int i = 0;
                emails = new String[userSet.size()];
                for(Object object: userSet){
                    emails[i] = object.toString();
                    i++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return emails;
        }
        return emails;
    }
}
