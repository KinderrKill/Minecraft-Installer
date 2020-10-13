package fr.kinderrkill.launcher.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileHelper {

    public static InputStream getStreamFromUrl(final String url) throws IOException {
        final URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept_Language", "en-US,q=0.5");
        connection.setDoOutput(true);
        return connection.getInputStream();
    }

    public static void deleteDirectory(File dir) {
        if(dir.exists()) {
            for (final File file : dir.listFiles()) {
                if(file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    public static String readFile(final File fileIn) throws IOException {
        final FileReader fileReader = new FileReader(fileIn);
        final BufferedReader buffReader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String currLine;
        while((currLine = buffReader.readLine()) != null && !currLine.startsWith("#")) {
            sb.append(currLine);
        }

        buffReader.close();
        fileReader.close();

        return sb.toString();
    }

    public static void writeFile(String text, File file) throws IOException {
        if(file.exists()) {
            file.delete();
        }
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        writer.println(text);
        writer.close();
    }

    public static void writeFile(InputStream input, File file) throws IOException {
        if(file.exists()) {
            file.delete();
        }
        final FileOutputStream outputStream = new FileOutputStream(file);
        final byte[] buffer = new byte[8192];
        int bytesRead;
        while((bytesRead = input.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
    }

}
