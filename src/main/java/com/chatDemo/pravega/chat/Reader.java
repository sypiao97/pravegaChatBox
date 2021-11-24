package com.chatDemo.pravega.chat;

import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.stream.*;
import io.pravega.client.stream.impl.JavaSerializer;

import static com.chatDemo.pravega.chat.FileUtil.*;

import java.io.Serializable;
import java.net.URI;

public class Reader implements Runnable{
    public final static int STRINGTYPE = 1;
    public final static int FILETYPE = 2;
    private EventStreamReader<message> reader;
    private boolean interrupt = false;
    private String user;
    private boolean printHistory;
    public Reader(EventStreamReader<message> reader, String src){
        this.reader = reader;
        this.user = src;
    }
    public void run(){
        while(true){
            EventRead<message> event = this.reader.readNextEvent(500);
            if(this.interrupt){
                reader.close();
                break;
            }
            if(event.getEvent()!=null){
                message msg = event.getEvent();
                if(!msg.getSrc().equals(this.user)){
                    if(msg.getType()==STRINGTYPE){
                        System.out.println();
                        System.out.println(msg.getSrc()+":"+new String(msg.getContent()));
                        System.out.print("YOU("+this.user+"):");
                    }
                    if(msg.getType()==FILETYPE){
                        System.out.println();
                        System.out.println("Success receive file: "+msg.getFileName()+" from "+msg.getSrc());
                        byte2File(msg.getContent(), "D:\\ideaProj\\files\\", msg.getFileName());
                        System.out.print("YOU("+this.user+"):");
                    }
                }
            }
        }
    }
    public void setInterrupt(){
        this.interrupt = true;
    }

    public static void createReaderGroup(String url, String scope, String stream, String groupName) throws Exception {
        URI uri = new URI(url);
        ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope(scope, uri);
        ReaderGroupConfig build = ReaderGroupConfig.builder().stream(scope + "/" + stream).build();
        readerGroupManager.createReaderGroup(groupName, build);
    }

    public static<T extends Serializable> EventStreamReader<T> createReader(String url, String scope, String readerId, String groupName) throws Exception {
        URI uri = new URI(url);
        ClientConfig build = ClientConfig.builder().controllerURI(uri).build();
        EventStreamClientFactory streamClientFactory = EventStreamClientFactory.withScope(scope, build);
        ReaderConfig build1 = ReaderConfig.builder().build();
        EventStreamReader<T> reader = streamClientFactory.createReader(readerId, groupName, new JavaSerializer<T>(), build1);
        return reader;
    }

    public static void readData(EventStreamReader<String> reader) {
        while (true) {
            EventRead<String> event = reader.readNextEvent(1000);
            if (event.getEvent() == null) {
                System.out.println("No more event");
                break;
            }
            System.out.println(event.getEvent());
        }

    }

//    public static void main(String[] args) throws Exception {
//        createReaderGroup("tcp://127.0.0.1:9090", "dell", "demo", "dellemc");
//        EventStreamReader<String> reader = createReader("tcp://127.0.0.1:9090", "dell", "demo", "dellemc");
//        readData(reader);
//    }
}

