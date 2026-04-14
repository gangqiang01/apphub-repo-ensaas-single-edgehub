package com.m2m.management.utils;

import com.alibaba.fastjson.JSONObject;
import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.CloudRepoStatus;
import com.m2m.management.entity.User;
import com.m2m.management.former.Response;
import com.m2m.management.restful.CloudRepoStatusManager;
import com.m2m.management.service.ICloudRepoStatusService;
import com.m2m.management.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.OutputStream;
import java.util.List;


@Slf4j
public class CloudFileDownload {
    private static final Logger LOG = LoggerFactory.getLogger(CloudFileDownload.class);

    private ICloudRepoStatusService cloudRepoStatusService;
    private IUserService userService;
    private CloudRepoStatusManager cloudRepoStatusManager;

    private String endpoint = null;
    private String secretkey = null;
    private String accesskey = null;
    private String bucket = null;
    private int errorcode = SyncErrorCode.NOERROR.ordinal();
    private int status = StatusType.BUILDING.ordinal();
    private static CloudFileDownload instance = null;

    private CloudFileDownload(
            CloudRepoStatusManager cloudRepoStatusManager,
            IUserService userService,
            ICloudRepoStatusService cloudRepoStatusService){
        this.cloudRepoStatusService = cloudRepoStatusService;
        this.userService = userService;
        this.cloudRepoStatusManager = cloudRepoStatusManager;
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        User user = users.get(0);
        if(org.springframework.util.StringUtils.isEmpty(user.getCloudurl())){
            errorcode = SyncErrorCode.CLOUDURLEMPTY.ordinal();
            status = StatusType.FAILED.ordinal();
        }else{
            String path = "/configMap";
            ResponseEntity<String> responseEntity = cloudRepoStatusManager.doGet(path);
            if(responseEntity != null){
                String cresp = responseEntity.getBody();
                if(cresp == null){
                    errorcode = SyncErrorCode.CLOUDURLERROR.ordinal();
                    status = StatusType.FAILED.ordinal();
                }
                log.info("responce:"+cresp);
                JSONObject cappJson = JSONObject.parseObject(cresp);

                if(cappJson.getString("status").equals("success")){
                    JSONObject cloudConfigInfo = cappJson.getJSONObject("data");
                    endpoint = cloudConfigInfo.getString("endpoint");
                    accesskey = cloudConfigInfo.getString("accesskey");
                    secretkey = cloudConfigInfo.getString("secretkey");
                    bucket = cloudConfigInfo.getString("bucket");
                }else{
                    errorcode = SyncErrorCode.CLOUDURLERROR.ordinal();
                    status = StatusType.FAILED.ordinal();
                }


            }else{
                errorcode = SyncErrorCode.CLOUDURLERROR.ordinal();
                status = StatusType.FAILED.ordinal();
            }
        }

    }

    public static CloudFileDownload getInstance(
            CloudRepoStatusManager cloudRepoStatusManager,
            IUserService userService,
            ICloudRepoStatusService cloudRepoStatusService
    ) {
        return new CloudFileDownload(cloudRepoStatusManager, userService, cloudRepoStatusService);
    }

    public static void clearInstance(){
        instance = null;
    }

    public boolean filedownload(
            String downloadpath,
            String filename,
            String dpname,
            String pkgname,
            String version,
            String type
    ) {
        OutputStream output = null;
        String savePath = String.format("%s/%s", UploadConfig.path, downloadpath);
        if(status == StatusType.BUILDING.ordinal()){
            File dir = new File(savePath).getParentFile();
            if (!dir.exists()) {
                if(!dir.mkdirs()){
                    errorcode = SyncErrorCode.DIRERROR.ordinal();
                    status = StatusType.FAILED.ordinal();
                }
            }
            File file = new File(savePath);
            if(file.exists()) {
                if(!file.delete()){
                    errorcode = SyncErrorCode.FILEEXIST.ordinal();
                    status = StatusType.FAILED.ordinal();
                }
            }
        }


        if(status == StatusType.BUILDING.ordinal()){
            try {
                LOG.info(String.format("downloadPath:%s#filename:%s#dpname:%s#pkgname:%s#version:%s#type:%s",
                        downloadpath,
                        filename,
                        dpname,
                        pkgname,
                        version,
                        type));


                boolean ret = false;
                S3Client client = null;
                if(endpoint.equals(S3Client.AZURE)){
                    log.info("azure");
                    client = S3Client.getInstance(accesskey, bucket);
                }else{
                    log.info("s3");
                    client = S3Client.getInstance(accesskey, secretkey, endpoint, bucket);
                }

                for (int i = 3; i > 0; i--) {
                    ret = client.downloadFileFromBucket(downloadpath, savePath);
                    if(ret){
                        log.info("download file success, filename="+filename);
                        break;
                    }
                }

                if(!ret){
                    log.info("download file failed, filename="+ filename);
                    errorcode = SyncErrorCode.DOENLOADERROR.ordinal();
                    status = StatusType.FAILED.ordinal();
                }
            }catch (Exception ex) {
                LOG.error("Download " + downloadpath + " Failed!");
                errorcode = SyncErrorCode.DOENLOADERROR.ordinal();
                status = StatusType.FAILED.ordinal();
                ex.printStackTrace();
            } finally {
                LOG.info("download file finished");
                if (output != null) {
                    try {
                        output.close();
                    } catch (Exception ex) {
                       ex.printStackTrace();
                    }
                }
            }
        }

        if(!new File(savePath).exists()){
            errorcode = SyncErrorCode.DOENLOADERROR.ordinal();
            status = StatusType.FAILED.ordinal();
        }
        CloudRepoStatus cloudRepoStatus1 = this.cloudRepoStatusService.getByDpname(dpname, type, pkgname, version);
        cloudRepoStatus1.setErrorcode(errorcode);
        cloudRepoStatus1.setStatus(status);
        if(this.cloudRepoStatusService.update(cloudRepoStatus1)&&
                        status == StatusType.BUILDING.ordinal()){
            return true;
        }else{
            return false;
        }
    }

    public boolean download(
            String downloadpath,
            String filename
    ) {
        OutputStream output = null;
        String url;

        String savePath = String.format("%s/%s", UploadConfig.path, downloadpath);
        File dir = new File(savePath).getParentFile();
        if (!dir.exists()) {
            if(!dir.mkdirs()){
                LOG.error("Parent dir not exist: filename="+filename);
            }
        }
        File file = new File(savePath);
        if(file.exists()) {
            if(!file.delete()){
                LOG.error("File exist: filename="+filename);
            }
        }

        try {
            LOG.info(String.format("downloadPath:%s#filename:%s",
                    downloadpath,
                    filename));
            boolean ret = false;
            S3Client client = null;
            if(endpoint.equals(S3Client.AZURE)){
                client = S3Client.getInstance(accesskey, bucket);
            }else{
                client = S3Client.getInstance(accesskey, secretkey, endpoint, bucket);
            }
            for (int i = 3; i > 0; i--) {
                if (!ret) {
                    ret = client.downloadFileFromBucket(downloadpath, savePath);
                }
            }
            if(ret){
                return true;
            }
        }catch (Exception ex) {
            LOG.error("Download " + downloadpath + " Failed!");
            ex.printStackTrace();
        } finally {
            LOG.info("download file finished");
            if (output != null) {
                try {
                    output.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return false;
    }
}
