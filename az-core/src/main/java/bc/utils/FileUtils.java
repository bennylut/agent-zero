/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 * @author BLutati
 */
public class FileUtils {

    public static void ensureDirectoryExists(String path) {
        File dir = new File(path);
        if (dir.exists() && !dir.isDirectory()) {
            throw new UnsupportedOperationException("cannot make file into a directory - '" + path + "'");
        }
        
        if (!dir.exists()){
            dir.mkdirs();
        }
    }

    public static void copy(String source, String destination) throws IOException{
        File s = new File(source);
        File d = new File(destination);
        
        FileInputStream ss = new FileInputStream(s);
        FileOutputStream ds = new FileOutputStream(d);
 
        FileChannel sourceFileChannel = ss.getChannel();
        FileChannel destinationFileChannel = ds.getChannel();
 
        long size = sourceFileChannel.size();
        sourceFileChannel.transferTo(0, size, destinationFileChannel);
    }

    /**
     * write the data to disk - building any directory in the given path if needed
     * @param path
     * @param fileName
     * @param data
     * @return
     * @throws IOException
     */
    public static File persist(File path, String fileName, byte[] data) throws IOException {
        path.mkdirs();
        File f = new File(path.getAbsolutePath() + "\\" + fileName);
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(data);
        fos.flush();
        fos.close();
        return f;
    }

    public static File persistText(File path, String fileName, String data) throws IOException {
        path.mkdirs();
        File f = new File(path.getAbsolutePath() + "\\" + fileName);
        f.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(data);
        bw.flush();
        bw.close();
        return f;
    }

    public static String unPersistText(File f) throws FileNotFoundException, IOException {
        return new String(unPersist(f));
    }

    public static File persistObject(File path, String fileName, Serializable ser) throws IOException {
        path.mkdirs();
        File f = new File(path.getAbsolutePath() + "\\" + fileName);
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(ser);
        os.close();
        return f;
    }

    public static <T> T unPersistObject(File f, Class<T> cls) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream(fis);
        T ret = (T) ois.readObject();
        ois.close();
        return ret;
    }

    /**
     * un persisting the given file means that this function will read this whole file to memory and
     * return its byte array.
     * @param f
     * @return
     */
    public static byte[] unPersist(File f) throws FileNotFoundException, IOException {
        byte[] ret = new byte[(int) f.length()];
        FileInputStream fis = new FileInputStream(f);
        fis.read(ret);
        fis.close();
        return ret;
    }

    /**
     * delete dir / file - everything!
     * @param dir
     * @return
     */
    public static boolean delete(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = delete(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * copy a full folder recursively
     * @param from
     * @param to
     * @throws IOException 
     */
    public static void copyFolder(final File from, final File to) throws IOException {
        assert !to.exists() || to.isDirectory();

        if (!to.exists()) {
            to.mkdirs();
        }

        if (!from.isDirectory()) {
            copy(from.getAbsolutePath(), new File(to, from.getName()).getAbsolutePath());
        } else {
            final File newDestDir = new File(to, from.getName());
            if (!newDestDir.exists() && !newDestDir.mkdir()) {
                throw new IOException("cannot write folder " + newDestDir.getAbsolutePath());
            }
            for (final File child : from.listFiles()) {
                copyFolder(child, newDestDir);
            }
        }
    }

    /**
     * dump the stream given to the given file
     * @param is
     * @param to 
     */
    public static void dump(InputStream is, File to) throws IOException {
        FileOutputStream fos = new FileOutputStream(to);
        byte[] r = new byte[1024];
        int size;
        while ((size = is.read(r)) > 0) {
            fos.write(r, 0, size);
        }

        fos.close();
    }

    /**
     * copying a resource from within the jar to external file 
     * @param pathToResource - relative to the given class
     * @param relatedClass - a class to find start finding the resource from
     * @param dest - where to put the resource
     */
    public static void copyResourceFile(String pathToResource, Class relatedClass, File dest) throws IOException {
        InputStream s = relatedClass.getResourceAsStream(pathToResource);
        dump(s, dest);
    }

    public static void extractZipFile(File zip, File destFolder) throws ZipException, IOException {
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        ZipFile zf = new ZipFile(zip);
        Enumeration<? extends ZipEntry> ents = zf.entries();

        while (ents.hasMoreElements()) {
            ZipEntry e = ents.nextElement();
            final String path = destFolder.getAbsolutePath();

            //this will assume the folders in the zip is allways stored before the files stored in them 
            if (e.isDirectory()) {
                new File(path + "/" + e.getName()).mkdirs();
            } else {
                dump(zf.getInputStream(e), new File(path + "/" + e.getName()));
            }
        }
    }

    public static void copy(File from, File to) throws IOException {
        if (from.exists()) {
            byte[] all = new byte[32768];
            int read = -1;
            InputStream is = new FileInputStream(from);
            OutputStream os = new FileOutputStream(to);

            while ((read = is.read(all)) > 0) {
                os.write(all, 0, read);
            }

            is.close();
            os.close();
        }
    }

    public static String nameWithoutExtention(File f) {
        return f.getName().substring(0, f.getName().lastIndexOf("."));
    }

    public static boolean contentCompare(File f1, File f2) {

        BufferedReader br1 = null;
        BufferedReader br2 = null;
        try {

            if (f1.exists() && f2.exists() && f1.isFile() && f2.isFile()) {

                if (f1.length() != f2.length()) {
                    return false;
                }

                br1 = new BufferedReader(new FileReader(f1));
                br2 = new BufferedReader(new FileReader(f2));

                String l1, l2;

                while ((l1 = br1.readLine()) != null) {
                    l2 = br2.readLine();
                    if (l2 == null) {
                        return false;
                    }
                    if (!l2.equals(l1)) {
                        return false;
                    }
                }

                l2 = br2.readLine();
                if (l2 == null) {
                    return true;
                }
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (br1 != null) {
                    br1.close();
                }
                if (br2 != null) {
                    br2.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }
}
