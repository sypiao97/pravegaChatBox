package com.chatDemo.pravega.chat;

import java.io.*;

import static com.chatDemo.pravega.chat.FileUtil.*;

public class message implements Serializable {
    //magic numbers
    public final static int STRINGTYPE = 1;
    public final static int FILETYPE = 2;
    private int type;
    // who send this message
    private String src;
    private String fileName;
    private byte[] content;
    public message(String source, String content){
        this.src = source;
        String[] tmp = content.split("#");
        if(tmp.length==2&&tmp[0].equals("upload")){
            this.type = FILETYPE;
            // todo splitter may not work for the unix based system
            String[] parsePath = tmp[1].split("\\\\");
            //file name only be used when the message is a file
            this.fileName = parsePath[parsePath.length-1];
            this.content = file2Byte(tmp[1]);
        }
        else{
            this.type = STRINGTYPE;
            this.content = content.getBytes();
        }
    }
    public int getType(){
        return this.type;
    }
    public String getSrc(){
        return this.src;
    }
    public byte[] getContent(){
        return this.content;
    }
    public String getFileName(){
        return this.fileName;
    }
}

