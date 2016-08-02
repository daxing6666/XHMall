package com.xinheng.util.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.xinheng.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件工具类
 */
public class FileUtils {

    private final static FileUtils instance = new FileUtils();

    /**
     * 单例对象实例
     */
    public static FileUtils getInstance(){
        return instance;
    }

    /**
     * 创建根目录(直接在手机目录下)
     */
    public boolean createRootDirectory(){
        if(SDCardUtils.getInstance().existSDCard()){
            File file = new File(Constants.ROOT_FILE_PATH);
            if(!file.exists()){
                return file.mkdirs();
            }
        }
        return false;
    }

    /**
     * 创建缓存根目录(应用卸载后会被自动删除)
     */
    public boolean createCacheRootDirectory(){
        File file = new File(Constants.CACHE_ROOT_FILE_PATH);
        if(!file.exists()){
            return file.mkdirs();
        }
        return false;
    }

    /**
     * 创建文件
     * @param fileName
     * @param isCacheDir true 在缓存目录创建文件 false 在手机目录下创建文件
     * @return
     */
    public boolean createFile(String fileName,boolean isCacheDir){

        if(TextUtils.isEmpty(fileName)){
            return false;
        }else{
            if(isCacheDir){
                if(createCacheRootDirectory()){
                    File file = new File(Constants.ROOT_FILE_PATH + File.separator + fileName);
                    if(!file.exists()){
                        try {
                            return file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        file.delete();
                        try {
                            return file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else{
                if(SDCardUtils.getInstance().existSDCard()){
                    if(createRootDirectory()){
                        File file = new File(Constants.ROOT_FILE_PATH + File.separator + fileName);
                        if(!file.exists()){
                            try {
                                return file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            file.delete();
                            try {
                                return file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    return false;
                }
            }
        }
        return false;
    }
}
