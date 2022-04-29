package server;

import server.service.FileService;
import server.service.FileServiceImpl;
import server.service.IdForFiles;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
  private final FileService fileService = new FileServiceImpl();
  private static final int PORT = 23456;

  public void run() {
    try (ServerSocket server = new ServerSocket(PORT)) {
      boolean fl = true;
      System.out.println("Server started!");
      while (fl) {
        try (
                Socket socket = server.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
          String request = input.readUTF();
          String[] arrRequest = request.split(" ");
          String http = arrRequest[0];
          switch (http) {
            case "GET": {
              fileService.get(arrRequest, output);
              break;
            }
            case "PUT": {
              String answer = fileService.add(arrRequest, input);
              output.writeUTF(answer);
              break;
            }
            case "DELETE": {
              String answer = fileService.delete(arrRequest, output);
              output.writeUTF(answer);
              break;
            }
            case "exit": {
              fl = false;
              break;
            }
            default: {
              break;
            }
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
