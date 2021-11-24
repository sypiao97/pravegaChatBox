package com.chatDemo.pravega.chat;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.EventStreamWriterImpl;
import io.pravega.client.stream.impl.JavaSerializer;

import java.io.Serializable;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
public class Writer{
    public static<T> void writeData(EventStreamWriter<T> writer, T msg) throws Exception {
        CompletableFuture<Void> future = writer.writeEvent(msg);
        future.get();
    }

    public static<T extends Serializable> EventStreamWriter<T> getWriter(String url, String scope, String stream) throws Exception {
        URI uri = new URI(url);
        ClientConfig build = ClientConfig.builder().controllerURI(uri).build();
        EventStreamClientFactory streamClientFactory = EventStreamClientFactory.withScope(scope, build);
        EventWriterConfig writerConfig = EventWriterConfig.builder().build();
        return streamClientFactory.createEventWriter(stream, new JavaSerializer<T>(), writerConfig);
    }


    public static void createStream(String url, String scope, String stream)throws  Exception{
        URI uri = new URI(url);
        StreamManager streamManager = StreamManager.create(uri);
        streamManager.createScope(scope);

        StreamConfiguration build = StreamConfiguration.builder().build();
        streamManager.createStream(scope,stream,build);
    }
//    public static void main(String[] args) throws Exception {
//        createStream("tcp://127.0.0.1:9090","dell","demo");
//        EventStreamWriter<String> writer = getWriter("tcp://127.0.0.1:9090", "dell", "demo");
//        writeData(writer,"EDG niubi");
//        System.out.println("write data successfully");
//        writer.close();
//    }
}
