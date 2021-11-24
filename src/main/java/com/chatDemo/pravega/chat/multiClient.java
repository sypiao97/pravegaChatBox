package com.chatDemo.pravega.chat;

import java.util.ArrayList;
import java.util.List;

public class multiClient {
    // upload#D:\ideaProj\testFile.txt
    public static void main(String[] args) throws Exception {
        if(args == null||args.length<2){
            System.out.println("At least two clients are required");
            System.exit(1);
        }
        String selfName = args[0].trim();
        //add the remaining args as peer in a list
        int argsNum = args.length;
        List<String> otherMembers = new ArrayList<>();
        for(int i=0;i<argsNum-1;i++){
            String peerName = args[i+1].trim();
            otherMembers.add(peerName);
        }
        Chat charRoom = new Chat(selfName, otherMembers);
        charRoom.startChat();
    }
}
