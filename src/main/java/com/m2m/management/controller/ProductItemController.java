package com.m2m.management.controller;

import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.constant.AmazonConstant;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.*;
import com.m2m.management.former.Response;
import com.m2m.management.service.*;
import com.m2m.management.utils.FileUtil;
import com.m2m.management.utils.S3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@Slf4j
public class ProductItemController {

    private String pathSeparate = File.separator;
    private String buildFileName = "build.prop";

    @Autowired
    IProductItemService productItemService;

    @Autowired
    IProductService productService;

    @Autowired
    IRepoAppService repoAppsService;

    @Autowired
    IRepoBspService repoBspsService;

    @Autowired
    IRepoDockerComposeService repoDockerComposeService;

    @Autowired
    IRepoExeService repoExeService;

    @Autowired
    IRepoFileService repoFilesService;

    @Autowired
    IRepoLinuxPkgService repoLinuxPkgService;

    @Autowired
    IUserService userService;

    @Autowired
    IRepoService repoService;

    @Value("${repo.data}")
    private String repoString;

    @RequestMapping(value = "/productItem", method = RequestMethod.GET)
    public ResponseEntity<List<ProductItem>> listAllProductItems() {
        List<ProductItem> productItems = productItemService.getAll();
        long count  = productItemService.count();
        if(productItems != null){
            return new ResponseEntity(Response.success(productItems, count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/productItem/isExist/{piid}", method = RequestMethod.GET)
    public ResponseEntity<List<ProductItem>> getProductItem(@PathVariable("piid") long piid) {
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        User user = users.get(0);
        if(user == null){
            return new ResponseEntity(Response.error("User is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Storage storage = user.getStorage();
        if(storage == null){
            return new ResponseEntity(Response.error("Storage is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ProductItem productItem = productItemService.get(piid);
        if(productItem != null){
            String type = productItem.getApptype();
            if(type.equals("app")){
                String filename = productItem.getFilename();
                String versionname = productItem.getVersion();
                String packagename = productItem.getAppname();
                List<RepoApp> repoApp = repoAppsService.get(packagename,versionname ,filename , storage);
                if(repoApp == null || repoApp.size() <= 0){
                    return new ResponseEntity(Response.error("App is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(Response.success(repoApp.get(0)), HttpStatus.OK);
            }else if(type.equals("deb") || type.equals("tar")){
                String filename = productItem.getFilename();
                RepoLinuxPkg repoLinuxPkg = repoLinuxPkgService.get(storage, filename);
                if(repoLinuxPkg == null){
                    return new ResponseEntity(Response.error("RepoLinuxPkg is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(Response.success(repoLinuxPkg), HttpStatus.OK);
            }else if(type.equals("exe") || type.equals("zip")){
                String filename = productItem.getFilename();
                RepoExe repoExe = repoExeService.get(storage, filename);
                if(repoExe == null){
                    return new ResponseEntity(Response.error("RepoExe is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(Response.success(repoExe), HttpStatus.OK);
            }else if(type.equals("android") || type.equals("windows") || type.equals("linux")){
                String boardname = productItem.getAppname();
                String versionname = productItem.getVersion();
                List<RepoBsp> repoBsp = repoBspsService.get(boardname, versionname, storage, type);
                if(repoBsp == null || repoBsp.size() <= 0){
                    return new ResponseEntity(Response.error("RepoBsp is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(Response.success(repoBsp.get(0)), HttpStatus.OK);
            }else if(type.equals("Swarm") || type.equals("Compose")){
                String tag = productItem.getAppname();
                String version = productItem.getVersion();
                RepoDockerCompose repoDockerCompose = repoDockerComposeService.get(tag, version, storage);
                if(repoDockerCompose == null){
                    return new ResponseEntity(Response.error("RepoDockerCompose is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(Response.success(repoDockerCompose), HttpStatus.OK);
            }else{
                String filename = productItem.getFilename();
                RepoFile repoFile = repoFilesService.getByFilenameAndType(filename, type, storage);
                if(repoFile == null){
                    return new ResponseEntity(Response.error("RepoFile is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(Response.success(repoFile), HttpStatus.OK);
            }
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/productItem/{productname}", method = RequestMethod.GET)
    public ResponseEntity<List<ProductItem>> getProductItems(@PathVariable("productname") String productname) {
        Product product = productService.get(productname);
        if(product != null){
            List<ProductItem> productItems = productItemService.getProductItems(product);
            long count  = productItemService.countByProduct(product);
            if(productItems != null){
                return new ResponseEntity(Response.success(productItems, count), HttpStatus.OK);
            }else{
                return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/productItem/downloadApp/{piid}", method = RequestMethod.GET)
    public void  downloadByWeb(@PathVariable("piid") long piid, HttpServletResponse response){
        String separator = "#";
        File tempFile = new File(UploadConfig.path);
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
        //            set blob key in web
        S3Client s3Client = S3Client.getInstance(userService);
        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }
        ProductItem productItem = productItemService.get(piid);
        String type = productItem.getApptype();
        String s3Key = "", filename = "";
        if(type.equals("app")){
            String pkgname = productItem.getAppname();
            String versionname = productItem.getVersion();
            filename = productItem.getFilename();

            String[] repoArray = repoString.split(separator);
            String reponame = repoArray[0];
            Repo repo = repoService.get(reponame);
            s3Key = repo.getDarkname() + pathSeparate + pkgname + pathSeparate + versionname + pathSeparate + filename;

        }else if(type.equals("deb") || type.equals("tar")){
            String productname = productItem.getAppname();
            String versionname = productItem.getVersion();
            filename = productItem.getFilename();
            String[] repoArray = repoString.split(separator);
            String reponame = repoArray[2];
            Repo repo = repoService.get(reponame);
            String productnametrim = productname.replaceAll(" ", "");
            s3Key = repo.getDarkname() + pathSeparate + productnametrim + pathSeparate + versionname + pathSeparate + filename;


        }else if(type.equals("exe") || type.equals("zip")){
            String productname = productItem.getAppname();
            String versionname = productItem.getVersion();
            filename = productItem.getFilename();
            String[] repoArray = repoString.split(separator);
            String reponame = repoArray[5];
            Repo repo = repoService.get(reponame);
            String productnametrim = productname.replaceAll(" ", "");
            s3Key = repo.getDarkname() + pathSeparate + productnametrim + pathSeparate + versionname + pathSeparate + filename;

        }else if(type.equals("deploy")){
            filename = productItem.getFilename();
            String[] repoArray = repoString.split(separator);
            String reponame = repoArray[3];
            Repo repo = repoService.get(reponame);
            s3Key = repo.getDarkname() + pathSeparate + type  + pathSeparate + filename;

        }else if(type.equals("Swarm") || type.equals("Compose")){
            String productname = productItem.getAppname();
            String versionname = productItem.getVersion();
            filename = productItem.getFilename();
            String[] repoArray = repoString.split(separator);
            String reponame = repoArray[4];
            Repo repo = repoService.get(reponame);
            String sfilename = String.format("%s-%s.yaml",productname, versionname);
            s3Key =
                    repo.getDarkname() +
                            pathSeparate +productname +
                            pathSeparate +versionname+
                            pathSeparate + sfilename;

        }
        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(s3Key, os);
            System.out.println("Download the app successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/productItem/downloadApp/os/{piid}", method = RequestMethod.GET)
    public void  downloadOsByWeb(@PathVariable("piid") long piid, HttpServletResponse response){
        String separator = "#";
        ProductItem productItem = productItemService.get(piid);
        //            set blob key in web
        S3Client s3Client = S3Client.getInstance(userService);
        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }
        String type = productItem.getApptype();
        String boardname = productItem.getAppname();
        String versionname = productItem.getVersion();
        String[] repoArray = repoString.split(separator);
        String reponame = repoArray[1];
        Repo repo = repoService.get(reponame);
        String boardPath = System.getProperty("java.io.tmpdir")+pathSeparate+ boardname;
        File boardFile = new File(boardPath);
        if(!boardFile.exists())
            boardFile.mkdirs();
        String filename = "", bspSavePath = "";
        if(type.equals("android")){
            filename = boardname+".ota.zip";
            bspSavePath = repo.getDarkname() + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + filename;

        }else{
            List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
            Storage storage = users.get(0).getStorage();
            List<RepoBsp> repoBspList = repoBspsService.get(boardname, versionname, storage, type);
            RepoBsp repoBsp = repoBspsService.get(repoBspList.get(0).getrbid());
            String address = repoBsp.getAddress();
            filename = address.substring(address.lastIndexOf(pathSeparate)+1);
            bspSavePath =  address.substring(AmazonConstant.protocol.length());
        }
        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(bspSavePath, os);

            System.out.println("Download the os update pkg successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/productItem/downloadApp/md5/{piid}", method = RequestMethod.GET)
    public void  downloadMd5ByWeb(@PathVariable("piid") long piid, HttpServletResponse response){
        String separator = "#";
        //set blob key in web
        S3Client s3Client = S3Client.getInstance(userService);
        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }
        ProductItem productItem = productItemService.get(piid);
        String type = productItem.getApptype();

            String boardname = productItem.getAppname();
            String versionname = productItem.getVersion();
            String[] repoArray = repoString.split(separator);
            String reponame = repoArray[1];
            Repo repo = repoService.get(reponame);
            String boardPath = System.getProperty("java.io.tmpdir")+pathSeparate+ boardname;
            File boardFile = new File(boardPath);
            if(!boardFile.exists())
                boardFile.mkdirs();

            String filename, md5SaveKey;
            if(type.equals("android")){
                filename = boardname+".ota.zip.md5";
                String bspSavePath = repo.getDarkname() + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + filename;
                md5SaveKey = bspSavePath+".md5";
            }else{
                List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
                Storage storage = users.get(0).getStorage();
                List<RepoBsp> repoBspList = repoBspsService.get(boardname, versionname, storage, type);
                RepoBsp repoBsp = repoBspsService.get(repoBspList.get(0).getrbid());
                String address = repoBsp.getAddress();
                filename = address.substring(address.lastIndexOf(pathSeparate)+1);
                String bspSavePath =  address.substring(AmazonConstant.protocol.length());
                md5SaveKey = bspSavePath + ".md5";
            }
        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(md5SaveKey, os);

            System.out.println("Download the md5 file successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/productItem/downloadApp/buildprop/{piid}", method = RequestMethod.GET)
    public void  downloadBuildpropByWeb(@PathVariable("piid") long piid, HttpServletResponse response){
        String separator = "#";
        File tempFile = new File(UploadConfig.path);
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
        //            set blob key in web
        S3Client s3Client = S3Client.getInstance(userService);
        if(!s3Client.isBucketExit()){
            s3Client.createBucket();
        }
        ProductItem productItem = productItemService.get(piid);
        String boardname = productItem.getAppname();
        String versionname = productItem.getVersion();
        String[] repoArray = repoString.split(separator);
        String reponame = repoArray[1];
        Repo repo = repoService.get(reponame);
        String filename = boardname+"_"+buildFileName;
        String buildSaveKey = repo.getDarkname() + pathSeparate + boardname + pathSeparate + versionname + pathSeparate + buildFileName;
        try {
            String enfilename = new String(filename.getBytes(), "ISO-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+enfilename);
            OutputStream os = response.getOutputStream();
            s3Client.downloadStreamFromBucket(buildSaveKey, os);

            System.out.println("Download the builb prop file successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
