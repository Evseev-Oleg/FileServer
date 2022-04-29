package server.service;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileServiceImpl implements FileService {
    private final IdForFiles idForFiles = new IdForFiles();
      private final String dirPath = System.getProperty("user.dir")
          + File.separator + "File Server" + File.separator + "task" + File.separator
          + "src" + File.separator + "server" + File.separator + "data" + File.separator;
//    private final String dirPath = System.getProperty("user.dir") + File.separator +
//            "src" + File.separator + "server" + File.separator + "data" + File.separator;

    @Override
    public String add(String[] arrRequest, DataInputStream input) throws IOException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        String fileName = arrRequest[1];
        if ("MyNewFile".equals(fileName)) {
            int length = input.readInt();
            byte[] message = new byte[length];
            executorService.submit(() -> {
                try {
                    input.readFully(message, 0, message.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executorService.awaitTermination(1, TimeUnit.SECONDS);
            executorService.shutdown();
            if (arrRequest.length == 4) {
                String fileWay = dirPath + arrRequest[3];
                if (new File(fileWay).isFile()) {
                    return "403";
                } else {
                    try (BufferedOutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(fileWay))) {
                        outputStream.write(message);
                        return "200 " + idForFiles.add(arrRequest[3]);
                    }
                }
            } else {
                UUID nameFile = UUID.randomUUID();
                String name = nameFile + "." + arrRequest[2];
                String fileWay = dirPath + name;
                if (new File(fileWay).isFile()) {
                    return "403";
                } else {
                    try (BufferedOutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(fileWay))) {
                        outputStream.write(message);
                        return "200 " + idForFiles.add(name);
                    }
                }
            }
        } else {
            if ("notName".equals(arrRequest[1])) {
                UUID nameFile = UUID.randomUUID();
                String name = nameFile + ".txt";
                String fileWay = dirPath + name;
                if (new File(fileWay).isFile()) {
                    return "403";
                } else {
                    try (BufferedOutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(fileWay))) {
                        StringBuilder data = new StringBuilder();
                        for (int i = 2; i < arrRequest.length; i++) {
                            data.append(arrRequest[i]);
                        }
                        String resData = data.toString();
                        byte[] bytesData = resData.getBytes();
                        outputStream.write(bytesData);
                        return "200 " + idForFiles.add(name);
                    }
                }
            } else {
                String fileWay = dirPath + arrRequest[1];
                if (new File(fileWay).isFile()) {
                    return "403";
                } else {
                    try (BufferedOutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(fileWay))) {
                        StringBuilder data = new StringBuilder();
                        for (int i = 2; i < arrRequest.length; i++) {
                            data.append(arrRequest[i]);
                        }
                        String resData = data.toString();
                        byte[] bytesData = resData.getBytes();
                        outputStream.write(bytesData);
                        return "200 " + idForFiles.add(arrRequest[1]);
                    }
                }
            }
        }
    }

    @Override
    public void get(String[] answer, DataOutputStream output) throws IOException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        if (answer.length == 2) {
            try (BufferedInputStream inputStream = new BufferedInputStream(
                    new FileInputStream(dirPath + answer[1])
            )) {
                byte[] bytes = inputStream.readAllBytes();
                output.writeUTF("200");
                output.writeInt(bytes.length);
                executorService.submit(() -> {
                    try {
                        output.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                executorService.awaitTermination(1, TimeUnit.SECONDS);
                executorService.shutdown();
            } catch (FileNotFoundException e) {
                output.writeUTF("404");
            }
        } else {
            Map<String, String> map = idForFiles.get();
            if (!map.containsKey(answer[1])) {
                output.writeUTF("404");
            } else {
                String fileName = map.get(answer[1]);
                try (BufferedInputStream inputStream = new BufferedInputStream(
                        new FileInputStream(dirPath + fileName)
                )) {
                    byte[] bytes = inputStream.readAllBytes();
                    output.writeUTF("200");
                    output.writeInt(bytes.length);
                    executorService.submit(() -> {
                        try {
                            output.write(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    executorService.awaitTermination(1, TimeUnit.SECONDS);
                    executorService.shutdown();
                }
            }
        }
    }

    @Override
    public String delete(String[] answer, DataOutputStream output) {
        if (answer.length == 2) {
            File file = new File(dirPath + answer[1]);
            if (file.delete()) {
                idForFiles.delete(answer[1]);
                return "200";
            } else {
                return "404";
            }
        } else {
            String fileName = idForFiles.delete(answer[1] + " " + answer[2]);
            File file = new File(dirPath + fileName);
            if (file.delete()) {
                return "200";
            } else {
                return "404";
            }
        }
    }
}
