package com.m2m.management.utils;

import com.m2m.management.former.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtil {
    public static String tmpPath = System.getProperty("java.io.tmpdir");
    private static String pathSeparate = File.separator;
    public static Boolean copyFile(File ofile,String newPath) throws IOException{

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(newPath);
            if (file.exists()) {
                System.out.println("[copyFile]["+newPath+"]"+file.getName()+" exists in repo already, delete");
                file.delete();
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            bis = new BufferedInputStream(new FileInputStream(ofile));
            bos = new BufferedOutputStream(new FileOutputStream(file));
            int len;
            byte[] bytes = new byte[8*1024];
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0 , len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static Boolean saveCustomThemeFile(MultipartFile file, String path) throws IOException{
        File leftFile = new File(path);
        leftFile.renameTo(new File(path+".bk"));
        OutputStream out = null;
        InputStream filecontent = null;
        try {
            File iconFile = new File(path);
            out = new FileOutputStream(iconFile);
            filecontent = file.getInputStream();
            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
            return false;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }
        return true;
    }

    public static Boolean copyFile(String content, String newPath) throws IOException{
        FileWriter writer = null;
        File file = new File(newPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if(new File(newPath).exists())
            new File(newPath).delete();
        try {
            writer = new FileWriter(newPath);
            writer.write(content);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static Boolean copyFile(byte[] content, String newPath){
        if(new File(newPath).exists())
            new File(newPath).delete();
        try {
            FileOutputStream fos = new FileOutputStream(newPath);
            fos.write(content);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean createDir(String path){
        File dir=new File(path);
        if(dir.exists()){
            log.info("[createDir]["+path+"]"+dir.getName()+"dir exist in repo already");
           return true;
        }
        return dir.mkdirs();
    }
    public static Boolean delFile(String path){
        File file = new File(path);
        if(file.exists() && file.isFile()){
            return file.delete();
        }else{
            System.out.println("file is not exist or not file");
            return false;
        }
    }

// delete dir and files in dir
    public static Boolean delDir(String dirPath) {
        try {
            File delFile = new File(dirPath);
            if (!delFile.exists() || !delFile.isDirectory()) {
                System.out.println("[delDir]["+dirPath+"]"+delFile.getName()+" exists in repo already");
                return false;
            }
            if(delAllFiles(dirPath)){
                return  delFile.delete();
            }else{
                System.out.println("delAllFiles error");
                return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
// delete all file in dir
    public static boolean delAllFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            }
            if (f.isDirectory()) {
                if (path.endsWith(File.separator)) {
                    delDir(path  + f.getName());
                } else {
                    delDir(path + File.separator + f.getName());
                }
            }
        }
        return true;
    }


    public static Boolean isOnlyChildDir(String path){
        File dir=new File(path);
        if(dir.exists() && dir.isDirectory() && dir.canExecute()){
            File[] files=dir.listFiles();
            if(files != null && files.length > 1){
                return false;
            }else{
                return true;
            }
        }else{
            System.out.println("[isOnlyChildDir]["+path+"]"+dir.getName()+" not exist or isnot dir");
            return false;
        }
    }

    public static boolean isOnlyChildApk(String path){
        File dir=new File(path);
        if(dir.exists() && dir.isDirectory() && dir.canExecute()){
            File[] files=dir.listFiles();

            if(files != null && files.length > 1){
                int res = 0;
                for(int i=0; i<files.length; i++){
                    if(files[i].getName().endsWith(".apk"))
                        res++;

                }
                if(res >1){
                    return false;
                }else{
                    return true;
                }
            }else{
                return true;
            }
        }else{
            System.out.println("[isOnlyChildApkDir]["+path+"]"+dir.getName()+" not exist or isnot dir");
            return false;
        }
    }

    public static boolean isEmptyDir(String path){
        File file = new File(path);
        if(file != null && file.exists() &&file.isDirectory()){
            String[] files = file.list();
            if(files.length == 0){
                return true;
            }else {
                System.out.println("[isEmptyDir]["+path+"]filelength:"+files.length);
                return false;
            }
        }else{
            System.out.println("[isEmptyDir]["+path+"]"+file.getName()+" not exist or isnot dir");
            return false;
        }

    }

    /**
     * 分块写入文件
     * @param target
     * @param targetSize
     * @param src
     * @param srcSize
     * @param chunks
     * @param chunk
     * @throws IOException
     */
    public static void writeWithBlok(String target, Long targetSize, InputStream src, Long srcSize, Integer chunks, Integer chunk) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(target,"rw");
        randomAccessFile.setLength(targetSize);
        if (chunk == chunks - 1 && chunk != 0) {
            randomAccessFile.seek(chunk * (targetSize - srcSize) / chunk);
        } else {
            randomAccessFile.seek(chunk * srcSize);
        }
        byte[] buf = new byte[1024];
        int len;
        while (-1 != (len = src.read(buf))) {
            randomAccessFile.write(buf,0,len);
        }
        randomAccessFile.close();
    }

    /**
     * 生成随机文件名
     * @return
     */
    public static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public static byte[] File2byte(File tradeFile){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }



    public static boolean deleteRepoFile(String projectPath, String version, String filenname){
        boolean isDelDir = false;
        String versionPath = projectPath+ pathSeparate+ version;
        String filePath = versionPath + pathSeparate +filenname;
        if(FileUtil.isOnlyChildDir(projectPath)){
            if(FileUtil.isOnlyChildApk(versionPath)){
                isDelDir = FileUtil.delDir(projectPath);
            }else{
                isDelDir = FileUtil.delFile(filePath);
            }
        }else{
            if(FileUtil.isOnlyChildApk(versionPath)){
                isDelDir = FileUtil.delDir(versionPath);
            }else{
                isDelDir = FileUtil.delFile(filePath);
            }
        }
        return isDelDir;
    }

    public static void zipFiles(File[] srcfile,File zipfile){
        byte[] buf=new byte[1024];
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩
            ZipOutputStream out=new ZipOutputStream(new FileOutputStream(zipfile));
            for(int i=0;i<srcfile.length;i++){
                FileInputStream in=new FileInputStream(srcfile[i]);
                out.putNextEntry(new ZipEntry(srcfile[i].getName()));
                int len;
                while((len=in.read(buf))>0){
                    out.write(buf,0,len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            System.out.println("压缩完成.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean generateImage(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) // 图像数据为空
            return false;
        if(imgStr.indexOf("base64,")>0){
            imgStr = imgStr.substring(imgStr.indexOf("base64,")+7);
        }
        System.out.println(imgStr);
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            File iconFile = new File(imgFilePath);
            if(!iconFile.getParentFile().exists()){
                iconFile.getParentFile().mkdirs();
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
