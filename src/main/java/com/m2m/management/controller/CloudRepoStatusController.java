package com.m2m.management.controller;

import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.CloudRepoStatus;
import com.m2m.management.entity.Storage;
import com.m2m.management.entity.User;
import com.m2m.management.former.Response;
import com.m2m.management.service.ICloudRepoStatusService;

import com.m2m.management.service.impl.UserService;
import com.m2m.management.utils.CloudFileDownload;
import com.m2m.management.utils.StatusType;
import com.m2m.management.utils.SyncErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * @date ：Created in 9/3/20 1:51 PM
 * @description：cloud repo status controller
 */

@RestController
@Slf4j
public class CloudRepoStatusController {
    @Autowired
    private ICloudRepoStatusService cloudRepoStatusService;

    @Autowired
    private UserService userService;

    private final int INSTALL_TIMEOUT = 60*60*1000;//1 hours = 3600*1000 ms
    @RequestMapping(value = "/cloudstatusByPage", method = RequestMethod.GET)
    public ResponseEntity<List<CloudRepoStatus>> getCloudRepoStatuss(
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit,
            @RequestParam(name="type") String type) {
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        Storage storage = users.get(0).getStorage();
        List<CloudRepoStatus> cloudRepoStatus = cloudRepoStatusService.getAllByPage(storage, type, keywords, currentpage-1, limit);
        //lazy-check and update
        Date dt = new Date();
        Long timeStamp = dt.getTime();
        for (CloudRepoStatus cloudRepoStatus1: cloudRepoStatus) {
            if (cloudRepoStatus1.getStatus() == StatusType.BUILDING.ordinal()&&
                    cloudRepoStatus1.getTs() != null &&
                    timeStamp - cloudRepoStatus1.getTs() > INSTALL_TIMEOUT) {
                cloudRepoStatus1.setStatus(StatusType.FAILED.ordinal());
                cloudRepoStatus1.setErrorcode(SyncErrorCode.TIMEOUT.ordinal());
                cloudRepoStatusService.update(cloudRepoStatus1);
            }
        }
        long count  = -1;
        count = cloudRepoStatusService.count(storage, type, keywords);
        if(cloudRepoStatus == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            return new ResponseEntity(Response.success(cloudRepoStatus, count), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/cloudstatus", method = RequestMethod.POST)
    public ResponseEntity<Void> createCloudRepoStatuss(
            @RequestParam("filename") String filename,
            @RequestParam("pkgname") String pkgname,
            @RequestParam("version") String version,
            @RequestParam("type") String type,
            @RequestParam("dpname") String dpname
    ){
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        Storage storage = users.get(0).getStorage();
        CloudRepoStatus cloudRepoStatus = new CloudRepoStatus();
        cloudRepoStatus.setFilename(filename);
        cloudRepoStatus.setDpname(dpname);
        cloudRepoStatus.setVersion(version);
        cloudRepoStatus.setPkgname(pkgname);
        cloudRepoStatus.setType(type);
        cloudRepoStatus.setStorage(storage);
        if(cloudRepoStatusService.add(cloudRepoStatus)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else {
            return new ResponseEntity(Response.error("Add app to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @RequestMapping(value = "/cloudstatus/batchDelete", method = RequestMethod.POST)
    public ResponseEntity<String> batchDeleteApp( @RequestBody CloudRepoStatus pcloudRepoStatus){
        long[] cidArray = pcloudRepoStatus.getCidArray();
        Boolean isDelete = false;
        for(int i = 0; i< cidArray.length; i++){
            isDelete = cloudRepoStatusService.delete(cidArray[i]);
        }

        if(isDelete){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/cloudstatus/{cid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteCloudRepoStatussById(@PathVariable("cid") long cid){
        if(cloudRepoStatusService.delete(cid)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/cloudstatus", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllCloudRepoStatuss(){
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        Storage storage = users.get(0).getStorage();
        if(cloudRepoStatusService.deleteAll(storage)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/cloudstatusByType/{type}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllCloudRepoStatuss(@PathVariable("type") String type){
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        Storage storage = users.get(0).getStorage();
        if(cloudRepoStatusService.deleteByType(storage, type)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("App is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
