package server.service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class IdForFiles implements Serializable {
    private Map<String, String> mapIdAndFileName = new HashMap<>();
      private final String dirPath = System.getProperty("user.dir")
          + File.separator + "File Server" + File.separator + "task" + File.separator
          + "src" + File.separator + "server" + File.separator + "serializable" + File.separator + "mapSerialize.data";
//    private final String dirPath = System.getProperty("user.dir") + File.separator +
//            "src" + File.separator + "server" + File.separator + "serializable" + File.separator + "mapSerialize.data";

    public IdForFiles() {
    }

    public Map<String, String> getMapIdAndFileName() {
        return mapIdAndFileName;
    }

    public void setMapIdAndFileName(Map<String, String> mapIdAndFileName) {
        this.mapIdAndFileName = mapIdAndFileName;
    }

    public Map<String, String> get() {
        return deserialization().getMapIdAndFileName();
    }

    public String add(String fileName) {
        mapIdAndFileName = deserialization().getMapIdAndFileName();
        String strId;
        while (true) {
            int id = (int) (Math.random() * 100);
            strId = String.valueOf(id);
            if (!mapIdAndFileName.containsKey(strId)) {
                mapIdAndFileName.put(strId, fileName);
                break;
            }
        }
        serialization();
        return strId;
    }

    public String delete(String fileName) {
        mapIdAndFileName = deserialization().getMapIdAndFileName();
        String[] arr = fileName.split(" ");
        if (arr.length == 1) {
            String key = null;
            for (String k : this.mapIdAndFileName.keySet()) {
                if (this.mapIdAndFileName.get(k).equals(fileName)) {
                    key = k;
                    break;
                }
            }
            mapIdAndFileName.remove(key);
            serialization();
            return "";
        } else {
            String str = mapIdAndFileName.get(arr[0]);
            mapIdAndFileName.remove(arr[0]);
            serialization();
            return str;
        }
    }

    public void serialization() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dirPath)))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IdForFiles deserialization() {
        IdForFiles idForFiles = null;
        try (ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(dirPath)))) {
            idForFiles = (IdForFiles) oos.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return idForFiles;
    }
}
