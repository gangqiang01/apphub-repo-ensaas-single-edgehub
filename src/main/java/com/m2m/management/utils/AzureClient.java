package com.m2m.management.utils;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;

import java.io.*;
import java.net.URISyntaxException;


public class AzureClient {
    private CloudBlobContainer container = null;
    private static AzureClient instance = null;
    private AzureClient(String connectionString,String containername){

        try {
            System.out.println(connectionString);
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
            CloudBlobClient serviceClient = storageAccount.createCloudBlobClient();

            // Container name must be lower case.
            container = serviceClient.getContainerReference(containername);
            container.createIfNotExists();
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

    public static AzureClient getInstance(String connectionString, String containername) {
        if (instance == null) {
            synchronized (AzureClient.class) {
                if (instance == null) {
                    instance = new AzureClient(connectionString, containername);
                }
            }
        }
        return instance;
    }

    public static void clearInstance(){
        instance = null;
    }


    public void listContainers() {
        for (ListBlobItem blobItem : container.listBlobs()) {
            System.out.println(blobItem.getUri());
        }
    }


    public boolean deleteContainer() {
        boolean res = false;
        try{
            res = container.deleteIfExists();
            return true;
        }catch (StorageException e){
            e.printStackTrace();
        }

        return res;
    }
    public boolean deleteBlob(String key) {
        boolean res = false;
        try {
            CloudBlockBlob blob = container.getBlockBlobReference(key);
            return blob.deleteIfExists();

        }catch (URISyntaxException e) {
            e.printStackTrace();

        } catch (StorageException e) {
            e.printStackTrace();

        }
        return res;
    }

    public boolean uploadFilePut(String path, String key) {
        boolean res = false;
        try {
            CloudBlockBlob blob = container.getBlockBlobReference(key);
            File sourceFile = new File(path);
            if(!sourceFile.exists()){
                System.out.println("File not exist: path="+path);
                return false;
            }
            FileInputStream sourceStream = new FileInputStream(sourceFile);
            blob.upload(sourceStream, sourceFile.length());
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
        return res;
    }

    public boolean downloadFileFromBucket(String key,String targetFilePath){
        boolean res = false;
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
        return res;
    }

}