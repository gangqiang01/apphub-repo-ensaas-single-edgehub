package com.m2m.management.utils;

/**
 * Created by root on 11/2/20.
 */

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

/**
 * 负责文件下载的类
 */
public class DownloadThreadTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadThreadTask.class);

    /**
     * 待下载的文件
     */
    private String url = null;

    /**
     * 本地文件名
     */
    private String targetpath = null;

    /**
     * 偏移量
     */
    private long offset = 0;

    /**
     * 分配给本线程的下载字节数
     */
    private long length = 0;

    private CountDownLatch end;
    private CloseableHttpClient httpClient;
    private HttpContext context;

    public DownloadThreadTask(String url, String targetpath, long offset, long length, CountDownLatch end, CloseableHttpClient httpClient) {
        this.url = url;
        this.targetpath = targetpath;
        this.offset = offset;
        this.length = length;
        this.end = end;
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
    }

    public void run() {
        try {
            HttpGet httpGet = new HttpGet(this.url);
            httpGet.addHeader("Range", "bytes=" + this.offset + "-" + (this.offset + this.length - 1));
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
            byte[] buff = new byte[1024];
            int bytesRead;
            File newFile = new File(targetpath);
            RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
            while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
                raf.seek(this.offset);
                raf.write(buff, 0, bytesRead);
                this.offset = this.offset + bytesRead;
            }
            raf.close();
            bis.close();
        } catch (ClientProtocolException e) {
            e.getStackTrace();
            new File(this.targetpath).delete();
        } catch (IOException e) {
            e.getStackTrace();
            new File(this.targetpath).delete();
        } finally {
            end.countDown();
        }
    }
}