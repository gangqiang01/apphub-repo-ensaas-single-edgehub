package com.m2m.management.utils;
import com.alibaba.fastjson.JSONObject;
import com.m2m.management.entity.Storage;
import com.m2m.management.entity.User;
import com.m2m.management.constant.UserRole;
import com.m2m.management.service.IStorageService;
import com.m2m.management.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @date ：Created in 11/13/20 4:56 PM
 * @description：save blob info
 */
public class BlobUtil {
    private static final Logger LOG = LoggerFactory.getLogger(BlobUtil.class);
    private static final String BucketName = "apphub";
    public static boolean saveBlobInfo(IUserService userService, IStorageService storageService){
        if(storageService.getByType("default") != null){
            LOG.error("default blobstore exist already");
            return false;
        }
        String ServiceInstName = "blobstore";
        String endpoint=null, secretkey=null, accesskey=null;
        JSONObject vcapServices = new JSONObject().parseObject(System.getenv("ENSAAS_SERVICES"));
        try{
            if(vcapServices.getJSONArray(ServiceInstName) == null){
                LOG.error("blobstore is null");
                return false;
            }
//            LOG.info("vcapServices:"+vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).toString());
            endpoint = vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).getJSONObject("credentials").getString("externalHosts");
            secretkey = vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).getJSONObject("credentials").getString("secretKey");
            accesskey = vcapServices.getJSONArray(ServiceInstName).getJSONObject(0).getJSONObject("credentials").getString("accessKey");

            if(StringUtils.isEmpty(endpoint)|| StringUtils.isEmpty(secretkey)|| StringUtils.isEmpty(accesskey)){
                LOG.error("endpoint|secretkey|accesskey is null");
                return false;
            }

            S3Client s3Client = S3Client.getInstance(accesskey, secretkey, endpoint, BucketName);
            if(!s3Client.isBucketExit()){
                boolean res = s3Client.createBucket();
                if(res == false){
                    LOG.error("create bucket error");
                    return false;
                }
            }

            List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
            if(users == null){
               LOG.error("admin user is null");
                return false;
            }
            User user = users.get(0);
            if(user == null){
                LOG.error("admin user is null");
                return false;
            }
            if(storageService.getByType("default") == null){
                Storage storage = new Storage();
                storage.setBloburl(endpoint);
                storage.setBlobsecretkey(secretkey);
                storage.setBlobaccesskey(accesskey);
                storage.setBlobbucket(BucketName);
                storage.setType("default");
                if(storageService.getByChoose(1) == null){
                    storage.setChoose(1);
                }else{
                    storage.setChoose(0);
                }

                boolean res =  storageService.add(storage);
                if(user.getStorage() == null){
                    user.setStorage(storageService.getByType("default"));
                    if(userService.update(user)){
                        LOG.info("update user blob info success");
                    }else{
                        LOG.error("update user blob info fail");
                    }
                }
                if(res){
                    return true;
                }

            }
            return false;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

}
