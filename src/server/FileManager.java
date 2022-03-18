package server;

import java.io.*;
import java.util.HashMap;

public class FileManager {
    public static String DATA_DIR = System.getProperty("user.dir") + File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator;
    public static String ID_DIR = System.getProperty("user.dir") + File.separator + "src" + File.separator + "server" + File.separator + "register" + File.separator;
    protected static HashMap<Integer, String> idRegister = new HashMap<>();

    protected static boolean saveFile(String name, byte[] fileContent) {
        File file = new File(DATA_DIR, name);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileContent);
            fos.close();
            int id = generateNewId(fileContent.length, name.length());
            idRegister.put(id, name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    protected static byte[] loadFile(String name) throws IOException {
        if (idRegister.containsValue(name)) {
            File file = new File(DATA_DIR + name);
        } else if (idRegister.containsKey(Integer.parseInt(name))){
            name = idRegister.get(name);
        }  else if (!idRegister.containsValue(name) && !idRegister.containsKey(name)) {
            return null;
        }
        FileInputStream fis = new FileInputStream(DATA_DIR + name);
        byte[] content = fis.readAllBytes();
        fis.close();
        return content;
    }

    protected static boolean deleteFile(String name) {
        if (idRegister.containsKey(Integer.parseInt(name))){
            name = getNamebyId(Integer.parseInt(name));
        }  else if (!idRegister.containsValue(name) && !idRegister.containsKey(name)) {
            return false;
        }
        File file = new File(DATA_DIR + name);
        idRegister.values().remove(name);
        return file.delete();
    }

    protected static int generateNewId(int l, int n) {
        return l + n + 7;
    }

    protected static void loadIdRegister() {
        File register = new File(ID_DIR);
        if(!register.exists()) {
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(register);
            ObjectInputStream ois = new ObjectInputStream(fis);
            idRegister = (HashMap<Integer, String>) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load Indexes from file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void saveIdRegister() {
        File register = new File(ID_DIR);
        try{
            FileOutputStream fos = new FileOutputStream(register);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(idRegister);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static String getNamebyId(int id) {
        return idRegister.get(id);
    }
}
