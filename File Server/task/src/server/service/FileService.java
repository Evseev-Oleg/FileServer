package server.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface FileService {
    String add(String[] http, DataInputStream input) throws IOException, InterruptedException;
    void get(String[] answer, DataOutputStream output) throws IOException, InterruptedException;
    String delete(String[] answer, DataOutputStream output);
}
