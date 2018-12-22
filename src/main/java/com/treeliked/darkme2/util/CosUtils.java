package com.treeliked.darkme2.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * cos 对象存储工具类
 *
 * @author lqs2
 * @date 2018-12-15, Sat
 */
@Component
public class CosUtils {


    /**
     * cos keyId
     */
    private static String secretId;

    /**
     * cos keySecret
     */
    private static String secretKey;

    /**
     * cos region
     */
    private static String region;

    /**
     * cos bucket name1
     */
    public static String bucketName1;

    /**
     * cos bucket name2
     */
    public static String bucketName60;



    /**
     * 单例client
     **/
    private static class SingletonInstanceClass {
        private static COSClient client = new COSClient(new BasicCOSCredentials(secretId, secretKey), new ClientConfig(new Region(region)));
    }

    public static COSClient getInstance() {
        return SingletonInstanceClass.client;
    }


    @Value("${cos.secret.id}")
    public void setAccessKeyId(String secretId) {
        CosUtils.secretId = secretId;
    }

    @Value("${cos.secret.key}")
    public void setAccessKeySecret(String secretKey) {
        CosUtils.secretKey = secretKey;
    }

    @Value("${cos.bucket.region}")
    public void setEndpoint(String region) {
        CosUtils.region = region;
    }

    @Value("${cos.bucket.name.b1}")
    public void setBucketName1(String bucketName) {
        CosUtils.bucketName1 = bucketName;
    }

    @Value("${cos.bucket.name.b60}")
    public void setBucketName60(String bucketName) {
        CosUtils.bucketName60 = bucketName;
    }

//    /**
//     * @param filename bucket key
//     * @param len      文件大小
//     * @param bytes    文件
//     */
//
//    public static boolean uploadFile(String filename, long len, byte[] bytes) {
//
//        cosClient cosClient = getInstance();
//
//        // 创建上传文件请求
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, new ByteArrayInputStream(bytes));
//        // 进度条
//        PutObjectProgressListener progressListener = new PutObjectProgressListener(len);
//
//        putObjectRequest.withProgressListener(progressListener);
//
//        // 执行上传
//        cosClient.putObject(putObjectRequest);
//
//        return progressListener.isSucceed();
//    }

//    /**
//     * 获取文件流
//     *
//     * @param bucketKey object key
//     */
//    public static InputStream getObjectStream(String bucketKey) {
//
//        cosClient client = getInstance();
//        boolean exist = client.doesObjectExist(bucketName, bucketKey);
//        if (exist) {
//            cosObject obj = client.getObject(bucketName, bucketKey);
//            return obj.getObjectContent();
//        }
//        return null;
//    }


    /**
     * 删除指定的文件
     *
     * @param key obj key
     */
    public static void dropFile(String key, String  bucketName) {
        getInstance().deleteObject(bucketName, key);
    }
}
