package com.m2m.management.utils;


import java.io.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.util.StringUtils;
import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.Storage;
import com.m2m.management.entity.User;
import com.m2m.management.service.IUserService;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.core.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
public class S3Client {
    private AmazonS3         s3;
    private TransferManager     tx;
    private static S3Client instance = null;
    private static String bucketName="";

    //azure blob
    private CloudBlobContainer container = null;
    private boolean isS3 = true;
    public static final String AZURE = "azure";
    private CloudBlobClient serviceClient = null;

    private S3Client(String accessKeyId, String secretAccessKey, String endpoint, String bucketname){
        isS3 = true;
        bucketName = bucketname;
        //get property from k8s env
        ClientConfiguration config = new ClientConfiguration();
        // SDK default using https, here i using HTTP

        //##########https
        if(endpoint!= null&& endpoint.startsWith("https")){
//            System.out.println("https");
            config.withProtocol(Protocol.HTTPS);
            config.setMaxConnections(200);
            config.setConnectionTimeout(8000);
            config.setSignerOverride("S3SignerType");
            this.s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(accessKeyId,secretAccessKey )
                    )).withClientConfiguration(config)
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,"" ))
                    .withPathStyleAccessEnabled(true).build();

        }else{
//            System.out.println("http");
            config.withProtocol(Protocol.HTTP);
            config.setMaxConnections(200);
            config.setConnectionTimeout(8000);
            // SDK default using v4 sign, here using v2  sign
            config.setSignerOverride("S3SignerType");
            if(endpoint.contains("aliyun")){
                this.s3 = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(accessKeyId,secretAccessKey )
                        )).withClientConfiguration(config)
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,"" ))
                        .build();
            }else{
                this.s3 = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(accessKeyId,secretAccessKey )
                        )).withClientConfiguration(config)
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,"" ))
                        .withPathStyleAccessEnabled(true).build();
            }

        }
    }
    //azure blob
    private S3Client(String connectionString, String containername){
        bucketName = containername;
        isS3 = false;
        try {
            System.out.println("connectionString:"+ connectionString);
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
            serviceClient = storageAccount.createCloudBlobClient();

            // Container name must be lower case.
            container = serviceClient.getContainerReference(containername);
            container.createIfNotExists();
            System.out.println("Creating container: " + container.getName());
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

            // Include public access in the permissions object
            containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

            // Set the permissions on the container
            container.uploadPermissions(containerPermissions);
        }
        catch (StorageException storageException) {
            System.out.print("StorageException encountered: ");
            System.out.println(storageException.getMessage());

        }
        catch (Exception e) {
            System.out.print("Exception encountered: ");
            System.out.println(e.getMessage());
        }
    }

    public static S3Client getInstance(String accessKeyId, String secretAccessKey, String endpoint, String bucketname) {
        instance = new S3Client(accessKeyId, secretAccessKey, endpoint, bucketname);
        return instance;
    }

    public static S3Client getInstance(String accessKey, String bucketname) {
        instance = new S3Client(accessKey, bucketname);
        return instance;
    }

    public static S3Client getInstance(IUserService userService) {
        List<User> users = userService.get(UserRole.SYSTEMUSER.ordinal());
        if(users ==null){
            return null;
        }
        User user = users.get(0);
        Storage storage = user.getStorage();
        System.out.println(
                String.format("endpoint:%s##accesskey:%s##secretkey:%s##container:%s",
                        storage.getBloburl(), storage.getBlobaccesskey(), storage.getBlobsecretkey(), storage.getBlobbucket()));

        String burl = storage.getBloburl();
        String accesskey = storage.getBlobaccesskey();
        String secretkey = storage.getBlobsecretkey();
        String bucketname = storage.getBlobbucket();
        if(burl.equals(AZURE)){
            if(StringUtils.isNullOrEmpty(accesskey)
                    ||StringUtils.isNullOrEmpty(bucketname)){
                return null;
            }
            instance = new S3Client(accesskey, bucketname);
        }else{
            if(StringUtils.isNullOrEmpty(accesskey)
                    ||StringUtils.isNullOrEmpty(secretkey)
                    ||StringUtils.isNullOrEmpty(bucketname)){
                return null;
            }
            instance = new S3Client(accesskey, secretkey, burl, bucketname);
        }
        return instance;
    }

    public static S3Client getInstance(Storage storage) {
        System.out.println(
                String.format("endpoint:%s##accesskey:%s##secretkey:%s##container:%s",
                        storage.getBloburl(), storage.getBlobaccesskey(), storage.getBlobsecretkey(), storage.getBlobbucket()));

        String burl = storage.getBloburl();
        String accesskey = storage.getBlobaccesskey();
        String secretkey = storage.getBlobsecretkey();
        String bucketname = storage.getBlobbucket();
        if(burl.equals(AZURE)){
            if(StringUtils.isNullOrEmpty(accesskey)
                    ||StringUtils.isNullOrEmpty(bucketname)){
                return null;
            }
            instance = new S3Client(accesskey, bucketname);
        }else{
            if(StringUtils.isNullOrEmpty(accesskey)
                    ||StringUtils.isNullOrEmpty(secretkey)
                    ||StringUtils.isNullOrEmpty(bucketname)){
                return null;
            }
            instance = new S3Client(accesskey, secretkey, burl, bucketname);
        }
        return instance;
    }

    public static void clearInstance(){
        instance = null;
    }

    public boolean createBucket() {
        if(isS3){
            if(this.s3.doesBucketExistV2(bucketName) == true) {
                return false;
            }
//        System.out.println("creating " + bucketName + " ...");
            this.s3.createBucket(bucketName);
            System.out.println(bucketName + " has been created!");
            return true;
        }else{
            // Container name must be lower case.
            try {
                container = serviceClient.getContainerReference(bucketName);
                container.createIfNotExists();
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
    }

    public void listBuckets() {
        System.out.println("Listing buckets");
        if(isS3){
            for(Bucket bucket : this.s3.listBuckets()) {
                System.out.println(" - " + bucket.getName());
            }
        }else{
            CloudBlockBlob blob = null;
            Iterable<ListBlobItem> blobs = container.listBlobs();
// The only blob found in the container is the directory itself
            for (ListBlobItem cblob : blobs) {
                //log the current blob URI
                if (cblob instanceof CloudBlob) {  // this never happens
                    CloudBlob cloudBlob = (CloudBlob) cblob;
                    System.out.println(cloudBlob.getName()+" - " + cloudBlob.getSnapshotID());
                }
            }
        }
    }

    public boolean copyObject(String sourceKey, String destinationKey) {
        if(isS3){
            try {
                CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);
                this.s3.copyObject(copyObjRequest);
                this.s3.deleteObject(bucketName, sourceKey);
                return true;
            } catch (AmazonServiceException e) {
                e.printStackTrace();
            } catch (SdkClientException e) {
                e.printStackTrace();
            }
        }else{
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(sourceKey);
                CloudBlockBlob desBlob = container.getBlockBlobReference(destinationKey);
                desBlob.startCopy(blob);
                blob.delete();
                return true;
            }catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean isObjectExit(String key) {
        int len = key.length();
        if(isS3){
            ObjectListing objectListing = this.s3.listObjects(bucketName);
            String s = new String();
            for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                s = objectSummary.getKey();
                int slen = s.length();
                if(len == slen) {
                    int i;
                    for(i=0;i<len;i++) if(s.charAt(i) != key.charAt(i)) break;
                    if(i == len) return true;
                }
            }
        }else{
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(key);
                return blob.exists();

            }catch (URISyntaxException e) {
                e.printStackTrace();

            } catch (StorageException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
    public boolean isBucketExit(){
        if(isS3){
            return this.s3.doesBucketExistV2(bucketName);
        }else{
            if(container != null){
                try{
                    return container.exists();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return false;
    }


    public boolean deleteObject(String key) {
        if(isS3){
            if(this.s3.doesBucketExistV2(bucketName) == false) {
//            log.info(bucketName + " does not exists!");
                return false;
            }
            this.s3.deleteObject(bucketName, key);
            return true;
        }else{
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(key);
                blob.delete();
                return true;
            }catch (URISyntaxException e) {
                e.printStackTrace();

            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



    // using PUT operation and ETag is normal md5 value
    public boolean uploadFilePut(String path, String key) {
        if(isS3){
            try {
                PutObjectRequest request = new PutObjectRequest(bucketName, key, new File(path));
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("plain/text");
                metadata.addUserMetadata("x-amz-meta-title", "someTitle");
                request.setMetadata(metadata);
                this.s3.putObject(request);
                return true;
            } catch (AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.
                e.printStackTrace();
                return false;
            } catch (SdkClientException e) {
                // Amazon S3 couldn't be contacted for a response, or the client
                // couldn't parse the response from Amazon S3.
                e.printStackTrace();
                return false;
            }
        }else{
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(key);
                File file = new File(path);
                if(!file.exists()){
                    System.out.println("File not exist: path="+key);
                    return false;
                }
                FileInputStream sourceStream = new FileInputStream(file);
                blob.upload(sourceStream, file.length());
                return true;
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (URISyntaxException e) {
                e.printStackTrace();

            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean uploadFileMulPart(File file,  String key) {
        System.out.println(isS3+"##multpart file key="+key+"##bucketname="+bucketName);
        long partSize = 5 * 1024 * 1024; // Set part size to 10 MB.
        if(isS3){
            long contentLength = file.length();

            try {
                // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
                // then, after each individual part has been uploaded, pass the list of ETags to
                // the request to complete the upload.
                List<PartETag> partETags = new ArrayList<PartETag>();

                // Initiate the multipart upload.
                InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key);
                InitiateMultipartUploadResult initResponse = this.s3.initiateMultipartUpload(initRequest);

                // Upload the file parts.
                long filePosition = 0;
                for (int i = 1; filePosition < contentLength; i++) {
                    // Because the last part could be less than 5 MB, adjust the part size as needed.
                    partSize = Math.min(partSize, (contentLength - filePosition));

                    // Create the request to upload a part.
                    UploadPartRequest uploadRequest = new UploadPartRequest()
                            .withBucketName(bucketName)
                            .withKey(key)
                            .withUploadId(initResponse.getUploadId())
                            .withPartNumber(i)
                            .withFileOffset(filePosition)
                            .withFile(file)
                            .withPartSize(partSize);

                    // Upload the part and add the response's ETag to our list.
                    UploadPartResult uploadResult = this.s3.uploadPart(uploadRequest);
                    partETags.add(uploadResult.getPartETag());
                    filePosition += partSize;
                }

                // Complete the multipart upload.
                CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, key,
                        initResponse.getUploadId(), partETags);
                this.s3.completeMultipartUpload(compRequest);
                System.out.println(file.getName() + " upload succeed!");
                return true;
            } catch (AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.
                e.printStackTrace();
            } catch (SdkClientException e) {
                // Amazon S3 couldn't be contacted for a response, or the client
                // couldn't parse the response from Amazon S3.
                e.printStackTrace();
            }
            return false;

        }else{
            FileInputStream fileInputStream = null;
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(key);
                fileInputStream = new FileInputStream(file);

                ArrayList<BlockEntry> blockList = new ArrayList<BlockEntry>();
                int blockNum = 0;
                String blockId, blockIdEncoded = null;
                while(fileInputStream.available()>(partSize)){
                    blockId = String.format("%05d", blockNum);
                    blockIdEncoded = Base64.encode(blockId.getBytes());
                    blob.uploadBlock(blockIdEncoded, fileInputStream, partSize);
                    blockList.add(new BlockEntry(blockIdEncoded));
                    blockNum++;
                }
                blockId = String.format("%05d", blockNum);
                blockIdEncoded = Base64.encode(blockId.getBytes());
                blob.uploadBlock(blockIdEncoded, fileInputStream, fileInputStream.available());
                blockList.add(new BlockEntry(blockIdEncoded));
                blob.commitBlockList(blockList);
                System.out.println(file.getName() + " upload succeed!");
                return true;
            }catch (IOException e){
                e.printStackTrace();
            }catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean uploadFileMulPartByChunk(MultipartFile mfile, String key, int chunk, String md5,  PartETag[] partETags, BlockEntry[] blockEntrys) {

        File file = transferToFile(mfile);
        long partSize = file.length();
        if(isS3){
            try {
                // Initiate the multipart upload.
                InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key);
                InitiateMultipartUploadResult initResponse = this.s3.initiateMultipartUpload(initRequest);
                if(!UploadUtils.uploadIds.containsKey(md5)){
                    UploadUtils.uploadIds.put(md5, initResponse.getUploadId());
                }
                // Create the request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(key)
                        .withUploadId(UploadUtils.uploadIds.get(md5))
                        .withPartNumber(chunk+1)
                        .withFileOffset(0)
                        .withFile(file)
                        .withPartSize(partSize);
                System.out.println("partSize:"+partSize+"##chunk:"+chunk);
                UploadPartResult uploadResult = this.s3.uploadPart(uploadRequest);
                partETags[chunk] = uploadResult.getPartETag();
                UploadUtils.partETags.put(md5, partETags);
                return true;
            } catch (AmazonServiceException e) {
                e.printStackTrace();
            } catch (SdkClientException e) {
                e.printStackTrace();
            }finally {
                file.delete();
            }
            return false;

        }else{
            FileInputStream fileInputStream = null;
            CloudBlockBlob blob = null;
            try {
                blob = container.getBlockBlobReference(key);
                String blockId, blockIdEncoded = null;
                blockId = String.format("%05d", chunk);
                fileInputStream = new FileInputStream(file);
                blockIdEncoded = Base64.encode(blockId.getBytes());
                blob.uploadBlock(blockIdEncoded, fileInputStream, partSize);
                blockEntrys[chunk] = new BlockEntry(blockIdEncoded);
                UploadUtils.blockList.put(md5, blockEntrys);
                System.out.println(key + " upload succeed!");
                return true;
            }catch (IOException e){
                e.printStackTrace();
            }catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (StorageException e) {
                e.printStackTrace();
            }finally {
                try {
                    fileInputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
                file.delete();
            }
        }
        return false;
    }

    private static File transferToFile(MultipartFile multipartFile) {
        File file = null;
        try {
            String filename = UUID.randomUUID().toString();
            file=File.createTempFile(filename, ".txt");
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean uploadFileMulPartCommit(String key, String md5) {
        if(isS3){
            PartETag[] partETagArray = UploadUtils.partETags.get(md5);
            List<PartETag> partETags = Arrays.asList(partETagArray);
            try {
                CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, key, UploadUtils.uploadIds.get(md5), partETags);
                this.s3.completeMultipartUpload(compRequest);
                System.out.println(key + "commit succeed!");
                return true;
            } catch (AmazonServiceException e) {
                e.printStackTrace();
            } catch (SdkClientException e) {
                e.printStackTrace();
            } finally {
                UploadUtils.partETags.remove(md5);
                UploadUtils.uploadIds.remove(md5);
                UploadUtils.removeKey(md5);
            }
            return false;

        }else{
            BlockEntry[] blockEntrysArray = UploadUtils.blockList.get(md5);
            List<BlockEntry> blockEntrys = Arrays.asList(blockEntrysArray);
            CloudBlockBlob blob = null;
            try{
                blob = container.getBlockBlobReference(key);
                blob.commitBlockList(blockEntrys);
                return true;
            }catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (StorageException e) {
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }finally {
                UploadUtils.blockList.remove(md5);
                UploadUtils.removeKey(md5);
            }
        }
        return false;
    }

    public boolean uploadFilePut(File file, String key) {
        System.out.println(isS3+"##single file key="+key+"##bucketname="+bucketName);
        if(isS3){
            try {
                PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("plain/text");
                metadata.addUserMetadata("x-amz-meta-title", "someTitle");
                request.setMetadata(metadata);
                this.s3.putObject(request);
                return true;
            } catch (AmazonServiceException e) {
                e.printStackTrace();
                return false;
            } catch (SdkClientException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(key);
                if(!file.exists()){
                    System.out.println("File not exist: path="+key);
                    return false;
                }
                FileInputStream sourceStream = new FileInputStream(file);
                blob.upload(sourceStream, file.length());
                return true;
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (URISyntaxException e) {
                e.printStackTrace();

            } catch (StorageException e) {
                e.printStackTrace();

            }
        }
        return false;
    }

    public boolean downloadFileFromBucket(String key,String targetFilePath){
        if(isS3){
            S3Object object = this.s3.getObject(new GetObjectRequest(bucketName,key));
            if(object != null){
                InputStream input = null;
                FileOutputStream fileOutputStream = null;
                byte[] data = null;
                try {
                    input = object.getObjectContent();
                    data = new byte[4096];
                    int len = 0;
                    fileOutputStream = new FileOutputStream(targetFilePath);
                    while ((len = input.read(data)) != -1) {
                        fileOutputStream.write(data, 0, len);
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }finally{
                    if (object != null) {
                        try {
                            object.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(fileOutputStream != null){
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(input != null){
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else {
                return false;
            }
        }else{
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(key);
                File sourceFile = new File(targetFilePath);
                blob.downloadToFile(sourceFile.getAbsolutePath());
                return true;
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean downloadStreamFromBucket(String key,OutputStream outputStream){
        if(isS3){
            S3Object object = this.s3.getObject(new GetObjectRequest(bucketName,key));
            if(object != null){
                InputStream input = null;
                byte[] data = null;
                try {
                    input = object.getObjectContent();
                    data = new byte[4096];
                    int len = 0;
                    while ((len = input.read(data)) != -1) {
                        outputStream.write(data, 0, len);
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if (object != null) {
                        try {
                            object.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(outputStream != null){
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(input != null){
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else {
                return false;
            }
        }else{
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(key);
                blob.download(outputStream);
                return true;
            }catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public byte[] getFileFromBucket(String key){
        if(isS3){
            S3Object object = this.s3.getObject(new GetObjectRequest(bucketName, key));
            if(object != null){
                InputStream input = null;
                FileOutputStream fileOutputStream = null;
                try {
                    input = object.getObjectContent();
                    ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                    byte[] buff = new byte[1024];
                    int rc = 0;
                    while ((rc = input.read(buff, 0, 100)) > 0) {
                        swapStream.write(buff, 0, rc);
                    }
                    byte[] in2b = swapStream.toByteArray();
                    return in2b;


                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }finally{
                    if(fileOutputStream != null){
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(input != null){
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else{
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(key);
                byte[] result = new byte[1024*1024*2];
                blob.downloadToByteArray(result, 0);
                return result;
            }catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}