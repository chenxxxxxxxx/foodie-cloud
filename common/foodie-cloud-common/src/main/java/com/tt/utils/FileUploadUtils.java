package com.tt.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Create By Lv.QingYu in 2020/4/6
 */
@Slf4j
public class FileUploadUtils {

    private static final String endpoint = "oss-cn-beijing.aliyuncs.com";
    private static final String accessKeyId = "LTAI7Qq2AbkhI7Bj";
    private static final String accessKeySecret = "gCxsFFYjFzMbjbm1FZ760lC9vrCZcp";

    private static OSS ossClient;
    private static void getInstance(){
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    private static boolean isExistsBucketName(String bucketName){
        return ossClient.doesBucketExist(bucketName);
    }

    /**
     * @MethodName: uploadFile
     * @Description: OSS单文件上传
     * @param file
     * @param fileType 文件后缀
     * @return String 文件地址
     */
    public static String uploadFile(File file, String fileType, String bucketName, String picLocation){
        //文件名，根据UUID来
        String fileName = picLocation+ UUID.randomUUID().toString().toUpperCase().replace("-", "")+"."+fileType;
        return putObject(file,fileType,fileName,bucketName);
    }

    /**
     * @MethodName: uploadFile
     * @Description: OSS单文件上传
     * @param input
     * @param fileType 文件后缀
     * @param fileName 文件名称
     * @param bucketName  bucketName
     * @param picLocation 图片保存路径前缀
     * @return String 文件地址
     */
    public static String uploadFile(InputStream input, String fileName, String fileType, String bucketName, String picLocation){
        //文件名，根据UUID来
        fileName = picLocation+fileName+ UUID.randomUUID().toString().toUpperCase().replace("-", "")+"."+fileType;
        return putObject(input,fileType,fileName,bucketName);
    }

    public static String uploadFile(InputStream input, String fileType, String bucketName, String picLocation){
        //文件名，根据UUID来
        String fileName = picLocation+ UUID.randomUUID().toString().toUpperCase().replace("-", "")+"."+fileType;
        return putObject(input,fileType,fileName,bucketName);
    }

    /**
     * @MethodName: updateFile
     * @Description: 更新文件:只更新内容，不更新文件名和文件地址。
     *      (因为地址没变，可能存在浏览器原数据缓存，不能及时加载新数据，例如图片更新，请注意)
     * @param file
     * @param fileType
     * @param oldUrl
     * @return String
     */
    public static String updateFile(File file, String fileType, String oldUrl, String bucketName){
        String fileName = getFileName(oldUrl);
        if(fileName==null){ return null;}
        return putObject(file,fileType,fileName,bucketName);
    }

    public static String updateFile(InputStream input, String fileType, String oldUrl, String bucketName){
        String fileName = getFileName(oldUrl);
        if(fileName==null){ return null;}
        return putObject(input,fileType,fileName,bucketName);
    }

    /**
     * @MethodName: replaceFile
     * @Description: 替换文件:删除原文件并上传新文件，文件名和地址同时替换
     *      解决原数据缓存问题，只要更新了地址，就能重新加载数据)
     * @param file
     * @param fileType 文件后缀
     * @param oldUrl 需要删除的文件地址
     * @param bucketName  bucketName
     * @param picLocation 图片保存路径前缀
     * @return String 文件地址
     */
    public static String replaceFile(File file, String fileType, String oldUrl, String bucketName, String picLocation){
        //先删除原文件
        boolean flag = deleteFile(oldUrl);
        if(!flag){
            //更改文件的过期时间，让他到期自动删除。
        }
        return uploadFile(file, fileType,bucketName,picLocation);
    }

    /**
     * @MethodName: deleteFile
     * @Description: 单文件删除
     * @param fileUrl 需要删除的文件url
     * @return boolean 是否删除成功
     */
    public static boolean deleteFile(String fileUrl){
        //根据url获取bucketName
        String bucketName = FileUploadUtils.getBucketName(fileUrl);
        //根据url获取fileName
        String fileName = FileUploadUtils.getFileName(fileUrl);
        if(bucketName==null||fileName==null){ return false;}
        try {
            getInstance();
            if(!FileUploadUtils.isExistsBucketName(bucketName)){
                ossClient.createBucket(bucketName);
            }
            GenericRequest request = new DeleteObjectsRequest(bucketName).withKey(fileName);
            ossClient.deleteObject(request);
        } catch (Exception oe) {
            oe.printStackTrace();
            return false;
        } finally {
            ossClient.shutdown();
        }
        return true;
    }

    /**
     * @MethodName: batchDeleteFiles
     * @Description: 批量文件删除(较快)：适用于相同endPoint和BucketName
     * @param fileUrls 需要删除的文件url集合
     * @return int 成功删除的个数
     */
    public static int deleteFile(List<String> fileUrls){
        //成功删除的个数
        int deleteCount = 0;
        //根据url获取bucketName
        String bucketName = FileUploadUtils.getBucketName(fileUrls.get(0));
        //根据url获取fileName
        List<String> fileNames = FileUploadUtils.getFileName(fileUrls);
        if(bucketName==null||fileNames.size()<=0){ return 0;}
        try {
            getInstance();
            //判断bucketName是否存在，如果不存在则创建
            if(!FileUploadUtils.isExistsBucketName(bucketName)){
                ossClient.createBucket(bucketName);
            }
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName).withKeys(fileNames);
            DeleteObjectsResult result = ossClient.deleteObjects(request);
            deleteCount = result.getDeletedObjects().size();
        } catch (OSSException oe) {
            oe.printStackTrace();
            throw new RuntimeException("OSS服务异常:", oe);
        } catch (ClientException ce) {
            ce.printStackTrace();
            throw new RuntimeException("OSS客户端异常:", ce);
        } finally {
            ossClient.shutdown();
        }
        return deleteCount;
    }

    /**
     * @MethodName: batchDeleteFiles
     * @Description: 批量文件删除(较慢)：适用于不同endPoint和BucketName
     * @param fileUrls 需要删除的文件url集合
     * @return int 成功删除的个数
     */
    public static int deleteFiles(List<String> fileUrls){
        int count = 0;
        for (String url : fileUrls) {
            if(deleteFile(url)){
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @MethodName: putObject
     * @Description: 上传文件
     * @param file
     * @param fileType
     * @param fileName
     * @return String
     */
    private static String putObject(File file, String fileType, String fileName, String bucketName){
        //默认null
        String url = null;
        try {
            getInstance();
            if(!FileUploadUtils.isExistsBucketName(bucketName)){
                ossClient.createBucket(bucketName);
            }
            // 创建上传Object的Metadata
            InputStream input = new FileInputStream(file);
            ObjectMetadata meta = new ObjectMetadata();
            // 设置上传内容类型
            meta.setContentType(FileUploadUtils.contentType(fileType));
            // 被下载时网页的缓存行为
            meta.setCacheControl("no-cache");
            //创建上传请求
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName,input,meta);
            ossClient.putObject(request);
            //上传成功再返回的文件路径
            url = endpoint.replaceFirst("http://","http://"+bucketName+".")+"/"+fileName;
        } catch (OSSException oe) {
            oe.printStackTrace();
            return null;
        } catch (ClientException ce) {
            ce.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            ossClient.shutdown();
        }
        return url;
    }

    /**
     * @MethodName: putObject
     * @Description: 上传文件
     * @param input
     * @param fileType
     * @param fileName
     * @return String
     */
    private static String putObject(InputStream input, String fileType, String fileName, String bucketName){
        //默认null
        String url = null;
        try {
            getInstance();
            if(!FileUploadUtils.isExistsBucketName(bucketName)){
                ossClient.createBucket(bucketName);
            }
            // 创建上传Object的Metadata
            ObjectMetadata meta = new ObjectMetadata();
            // 设置上传内容类型
            meta.setContentType(FileUploadUtils.contentType(fileType));
            // 被下载时网页的缓存行为
            meta.setCacheControl("no-cache");
            //创建上传请求
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName,input,meta);
            ossClient.putObject(request);
            //上传成功再返回的文件路径
            url = endpoint.replaceFirst("http://","http://"+bucketName+".")+"/"+fileName;
        } catch (OSSException oe) {
            oe.printStackTrace();
            return null;
        } catch (ClientException ce) {
            ce.printStackTrace();
            return null;
        }finally {
            ossClient.shutdown();
        }
        return url;
    }

    /**
     * @MethodName: contentType
     * @Description: 获取文件类型
     * @param fileType
     * @return String
     */
    private static String contentType(String fileType){
        fileType = fileType.toLowerCase();
        String contentType = "";
        switch (fileType) {
            case "bmp": contentType = "image/bmp";
                break;
            case "gif": contentType = "image/gif";
                break;
            case "png":
            case "jpeg":
            case "jpg": contentType = "image/jpeg";
                break;
            case "html":contentType = "text/html";
                break;
            case "txt": contentType = "text/plain";
                break;
            case "vsd": contentType = "application/vnd.visio";
                break;
            case "ppt":
            case "pptx":contentType = "application/vnd.ms-powerpoint";
                break;
            case "doc":
            case "docx":contentType = "application/msword";
                break;
            case "xml":contentType = "text/xml";
                break;
            case "mp4":contentType = "video/mp4";
                break;
            default: contentType = "application/octet-stream";
                break;
        }
        return contentType;
    }

    /**
     * @MethodName: getBucketName
     * @Description: 根据url获取bucketName
     * @param fileUrl 文件url
     * @return String bucketName
     */
    private static String getBucketName(String fileUrl){
        String http = "http://";
        String https = "https://";
        int httpIndex = fileUrl.indexOf(http);
        int httpsIndex = fileUrl.indexOf(https);
        int startIndex  = 0;
        if(httpIndex==-1){
            if(httpsIndex==-1){
                return null;
            }else{
                startIndex = httpsIndex+https.length();
            }
        }else{
            startIndex = httpIndex+http.length();
        }
        int endIndex = fileUrl.indexOf(".oss-");
        return fileUrl.substring(startIndex, endIndex);
    }

    /**
     *
     * @MethodName: getFileName
     * @Description: 根据url获取fileName
     * @param fileUrl 文件url
     * @return String fileName
     */
    private static String getFileName(String fileUrl){
        String str = "aliyuncs.com/";
        int beginIndex = fileUrl.indexOf(str);
        if(beginIndex==-1){ return null;}
        return fileUrl.substring(beginIndex+str.length());
    }

    /**
     *
     * @MethodName: getFileName
     * @Description: 根据url获取fileNames集合
     * @param fileUrls 文件url
     * @return List<String>  fileName集合
     */
    private static List<String> getFileName(List<String> fileUrls){
        List<String> names = new ArrayList<>();
        for (String url : fileUrls) {
            names.add(getFileName(url));
        }
        return names;
    }

    public static void getAllFileName(String bucketName,String picLocation){
        getInstance();
        if(!FileUploadUtils.isExistsBucketName(bucketName)){
            ossClient.createBucket(bucketName);
        }
        ObjectListing objectListing = ossClient.listObjects(bucketName,picLocation);
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        for(OSSObjectSummary s:sums){
            System.out.println("\t"+s.getKey());
        }
    }

    /***
     * @author: Micheal
     * @date: 2018年12月03日14:31:51
     * @param: [bucketName, objectName]
     * bucketName:为文件存在在OSS上面的文件夹，如：imgcarsrevice 车辆图片的文件夹
     * objectName：如：上传图片返回的地址为：(realUrl)http://imgcarsrevice.oss-cn-hangzhou.aliyuncs.com/imgcarsrevice/B26A4EE9573C49BD868ACFDEF0DD6F1F.jpg
     * 那么objectName为：imgcarsrevice/B26A4EE9573C49BD868ACFDEF0DD6F1F.jpg
     * @return: java.lang.String
     * @version: 1.0
     * @Description:TODO 根据具体的文件名称，返回一个加签名的临时可访问的url地址
     */
    public static String getUrlForSignAccess(String bucketName,String realUrl){
        getInstance();
        String objectName=realUrl.split("oss-cn-beijing.aliyuncs.com/")[1];
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        String str = null;
        if(ossClient != null){
            URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
            if(url != null){
                str = url.toString();
            }
        }
        return str;
    }

}
