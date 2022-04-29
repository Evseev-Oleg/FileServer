package client;

import client.service.ClientService;
import client.service.ClientServiceImpl;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  ClientService clientService = new ClientServiceImpl();
  private final Scanner scanner = new Scanner(System.in);

  public void con() {
    String address = "127.0.0.1";
    int port = 23456;
    try (Socket socket = new Socket(InetAddress.getByName(address), port);
         DataInputStream input = new DataInputStream(socket.getInputStream());
         DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
      System.out.print("Enter action (1 - get a file, 2 - create a file," +
              " 3 - delete a file): ");
      String num = scanner.nextLine();
      switch (num) {
        case "1": {
          clientService.get(input, output);
          break;
        }
        case "2": {
          clientService.put(input, output);
          break;
        }
        case "3": {
          clientService.delete(input, output);
          break;
        }
        case "exit": {
          System.out.println("The request was sent.");
          output.writeUTF("exit");
          break;
        }
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
