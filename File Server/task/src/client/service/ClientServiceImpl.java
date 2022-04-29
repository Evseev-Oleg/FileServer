package client.service;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientServiceImpl implements ClientService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final Scanner scanner = new Scanner(System.in);
    private final String dirPath = System.getProperty("user.dir")
            + File.separator + "File Server" + File.separator + "task" + File.separator
            + "src" + File.separator + "client" + File.separator + "data" + File.separator;
//  private final String dirPath = System.getProperty("user.dir") + File.separator +
//          "src" + File.separator + "client" + File.separator + "data" + File.separator;

    @Override
    public void get(DataInputStream input, DataOutputStream output) throws IOException {
        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
        String wayName = scanner.nextLine();
        if ("1".equals(wayName)) {
            System.out.print("Enter name of the file: ");
            String fileName = scanner.nextLine();
            System.out.println("The request was sent.");
            output.writeUTF("GET " + fileName);
            String sostoyanie = input.readUTF();
            if ("200".equals(sostoyanie)) {
                int length = input.readInt();
                byte[] message = new byte[length];
                executorService.submit(() -> {
                    try {
                        input.readFully(message, 0, message.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                executorService.shutdown();
                saveFail(message);
                System.out.println("File saved on the hard drive!");
            } else {
                System.out.println("The response says that this file is not found!");
            }
        } else if ("2".equals(wayName)) {
            System.out.print("Enter id: ");
            String id = scanner.nextLine();
            System.out.println("The request was sent.");
            output.writeUTF("GET " + id + " id");
            String sostoyanie = input.readUTF();
            if ("200".equals(sostoyanie)) {
                int length = input.readInt();
                byte[] message = new byte[length];
                executorService.submit(() -> {
                    try {
                        input.readFully(message, 0, message.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                try {
                    executorService.awaitTermination(1, TimeUnit.SECONDS);
                    executorService.shutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                saveFail(message);
                System.out.println("File saved on the hard drive!");
            } else {
                System.out.println("The response says that this file is not found!");
            }
        } else {
            get(input, output);
        }
    }

    @Override
    public void put(DataInputStream input, DataOutputStream output) throws IOException, InterruptedException {
        System.out.print("Enter name of the file: ");
        String fileName = scanner.nextLine();
        String[] fileNameArr = fileName.split("\\.");
        String formatFile = fileNameArr[1];
        try (BufferedInputStream inputStream = new BufferedInputStream(
                new FileInputStream(dirPath + fileName)
        )) {
            byte[] bytes = inputStream.readAllBytes();
            System.out.print("Enter name of the file to be saved on server: ");
            String fileNameForServer = scanner.nextLine();
            output.writeUTF("PUT " + "MyNewFile " + formatFile + " " + fileNameForServer);
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
            System.out.println("The request was sent.");
            String response = input.readUTF();
            String[] responseArr = response.split(" ");
            switch (responseArr[0]) {
                case "200": {
                    System.out.println("Response says that file is saved! ID = " + responseArr[1]);
                    break;
                }
                case "403": {
                    System.out.println("The response says that creating the file was" +
                            " forbidden!");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            String[] checkStr = fileName.split("\\.");
            if (checkStr.length == 1 || !"txt".equals(checkStr[1])) {
                System.out.println("The file cannot be saved!");
            } else {
                System.out.print("Enter file content: ");
                String content = scanner.nextLine();
                System.out.print("Enter name of the file to be saved on server: ");
                String newFileName = scanner.nextLine();
                if ("".equals(newFileName)) {
                    output.writeUTF("PUT " + "notName" + " " + content);
                } else {
                    output.writeUTF("PUT " + newFileName + " " + content);
                }
                System.out.println("The request was sent.");
                String response = input.readUTF();
                String[] responseArr = response.split(" ");
                switch (responseArr[0]) {
                    case "200": {
                        System.out.println("Response says that file is saved! ID = " + responseArr[1]);
                        break;
                    }
                    case "403": {
                        System.out.println("The response says that creating the file was" +
                                " forbidden!");
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void delete(DataInputStream input, DataOutputStream output) throws IOException {
        System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id): ");
        String str = scanner.nextLine();
        if ("1".equals(str)) {
            System.out.print("Enter name of the file: ");
            String fileName = scanner.nextLine();
            System.out.println("The request was sent.");
            output.writeUTF("DELETE " + fileName);
            String sostoyanie = input.readUTF();
            if ("200".equals(sostoyanie)) {
                System.out.println("The response says that this file was deleted successfully!");
            } else {
                System.out.println("The response says that this file is not found!");
            }
        } else if ("2".equals(str)) {
            System.out.print("Enter id: ");
            String id = scanner.nextLine();
            System.out.println("The request was sent.");
            output.writeUTF("DELETE " + id + " id");
            String sostoyanie = input.readUTF();
            if ("200".equals(sostoyanie)) {
                System.out.println("The response says that this file was deleted successfully");
            } else {
                System.out.println("The response says that this file is not found!");
            }
        } else {
            delete(input, output);
        }
    }

    private void saveFail(byte[] message) {
        System.out.print("The file was downloaded! Specify a name for it: ");
        String fileNameSave = scanner.nextLine();
        String fileWay = dirPath + fileNameSave;
        try (BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(fileWay))) {
            outputStream.write(message);
        } catch (Exception e) {
            System.out.println("Enter a different file name!");
            saveFail(message);
        }
    }
}
