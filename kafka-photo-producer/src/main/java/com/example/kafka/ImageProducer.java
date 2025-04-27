package com.example.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

public class ImageProducer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties config = new Properties();
        config.load(ImageProducer.class.getClassLoader().getResourceAsStream("config.properties"));

        String bootstrapServers = config.getProperty("bootstrap.servers");
        String topic = config.getProperty("topic.name");
        String watchDir = config.getProperty("watch.folder");

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, config.getProperty("acks"));
        props.put(ProducerConfig.RETRIES_CONFIG, config.getProperty("retries"));

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Paths.get(watchDir).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        System.out.println("Monitoring folder: " + watchDir);

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    String fileName = event.context().toString();
                    File file = new File(watchDir + "/" + fileName);

                    if (isImageFile(file)) {
                        System.out.println("New image detected: " + fileName);
                        waitForFileCopy(file);
                        byte[] fileContent = FileUtils.readFileToByteArray(file);
                        String base64Image = Base64.getEncoder().encodeToString(fileContent);

                        ProducerRecord<String, String> record = new ProducerRecord<>(topic, fileName, base64Image);
                        producer.send(record, (metadata, exception) -> {
                            if (exception == null) {
                                System.out.println("Sent " + fileName + " to Kafka topic " + metadata.topic());
                            } else {
                                exception.printStackTrace();
                            }
                        });
                    }
                }
            }
            key.reset();
        }
    }

    private static boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    private static void waitForFileCopy(File file) throws InterruptedException {
        long size = -1;
        while (true) {
            long newSize = file.length();
            if (newSize == size) break;
            size = newSize;
            Thread.sleep(500);
        }
    }
}
