package com.chatDemo.pravega.chat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class FileUtil {

    // transfer a file to byte array
    public static byte[] file2Byte(String filePath){
        ByteArrayOutputStream bos=null;
        BufferedInputStream in=null;
        try{
            File file=new File(filePath);
            if(!file.exists()){
                throw new FileNotFoundException("file not exists");
            }
            bos=new ByteArrayOutputStream((int)file.length());
            in=new BufferedInputStream(new FileInputStream(file));
            int buf_size=1024;
            byte[] buffer=new byte[buf_size];
            int len=0;
            while(-1 != (len=in.read(buffer,0,buf_size))){
                bos.write(buffer,0,len);
            }
            return bos.toByteArray();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
        finally{
            try{
                if(in!=null){
                    in.close();
                }
                if(bos!=null){
                    bos.close();
                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    // transfer a byte array to a file
    public static void byte2File(byte[] bfile,String filePath,String fileName){
        BufferedOutputStream bos=null;
        FileOutputStream fos=null;
        File file=null;
        try{
            File dir=new File(filePath);
            if(!dir.exists() && !dir.isDirectory()){
                dir.mkdirs();
            }
            file=new File(filePath+fileName);
            fos=new FileOutputStream(file);
            bos=new BufferedOutputStream(fos);
            bos.write(bfile);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        finally{
            try{
                if(bos != null){
                    bos.close();
                }
                if(fos != null){
                    fos.close();
                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}