package client.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ClientService {
  void get(DataInputStream input, DataOutputStream output) throws IOException;
  void put(DataInputStream input, DataOutputStream output) throws IOException, InterruptedException;
  void delete(DataInputStream input, DataOutputStream output)throws IOException;
}
