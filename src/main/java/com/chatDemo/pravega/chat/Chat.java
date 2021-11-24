package com.chatDemo.pravega.chat;

import io.pravega.client.stream.EventRead;
import io.pravega.client.stream.EventStreamReader;
import io.pravega.client.stream.EventStreamWriter;

import java.util.List;
import java.util.Scanner;

import static com.chatDemo.pravega.chat.FileUtil.byte2File;
import static com.chatDemo.pravega.chat.Reader.*;
import static com.chatDemo.pravega.chat.Writer.*;

public class Chat {
    private EventStreamReader<message> reader;
    private EventStreamReader<message> historyReader;
    private EventStreamWriter<message> writer;

    private Scanner in;
    String selfUser;
    List<String> peerUser;
    public Chat(String selfUser, List<String> peerUser) throws Exception {
        // todo stream name may need be taken as an unique id?
        createStream("tcp://127.0.0.1:9090","dell","demo");
        writer = getWriter("tcp://127.0.0.1:9090", "dell", "demo");
        String conversation = "test-conversation";

        createReaderGroup("tcp://127.0.0.1:9090", "dell", "demo", selfUser);
        reader = createReader("tcp://127.0.0.1:9090", "dell", conversation, selfUser);

        //since we need to read from the beginning of the chat record, we need a unique groupname
        // todo timestamp to be improved
        long timeStamp = System.currentTimeMillis();
        createReaderGroup("tcp://127.0.0.1:9090", "dell", "demo", String.valueOf(timeStamp));
        historyReader = createReader("tcp://127.0.0.1:9090", "dell", conversation, String.valueOf(timeStamp));

        in = new Scanner(System.in);
        this.selfUser = selfUser;
        this.peerUser = peerUser;
        System.out.println("You:" + selfUser);
        for(int i=0;i<peerUser.size();i++){
            System.out.println("Peer:" + peerUser.get(i));
        }
        System.out.println("============================================");
    }

    public void startChat() {
        // print the history chat record
        while(true){
            EventRead<message> event = this.historyReader.readNextEvent(500);
            if(event.getEvent()!=null){
                message msg = event.getEvent();
                if(msg.getType()==STRINGTYPE){
                    System.out.println(msg.getSrc()+":"+new String(msg.getContent()));
                }
                if(msg.getType()==FILETYPE){
                    // if the file is sent by the current user, don't need to download it.
                    if(msg.getSrc().equals(selfUser)){
                        System.out.println("Success sending file: "+msg.getFileName());
                    }
                    else{
                        System.out.println("Success receive file: "+msg.getFileName()+" from "+msg.getSrc());
                        byte2File(msg.getContent(), "D:\\ideaProj\\files\\", msg.getFileName());
                    }
                }
            }
            // after transverse all the record, break the loop
            else{
                break;
            }
        }
        //close the history reader.
        historyReader.close();
        try{
            Reader r = new Reader(this.reader, this.selfUser);
            Thread t1 = new Thread(r);
            t1.start();
            while(true){
                System.out.print("YOU(" + this.selfUser + "):");
                String toBeSent = in.nextLine();
                if(toBeSent.trim().length()!=0){
                    message msg = new message(this.selfUser, toBeSent);
                    writeData(writer, msg);
                }
                if(toBeSent.equals("bye")){
                    //give an interrupt signal to the reader instance
                    r.setInterrupt();
                    // wait for the reader to be interrupted
                    t1.join();
                    writer.close();
                    System.exit(0);
                }
            }
        }
        catch (Exception e){
            // may not happen
            reader.close();
            writer.close();
            e.printStackTrace();
        }
    }
}
