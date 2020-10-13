package fr.kinderrkill.launcher.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Utils {

    //Links
    public static String BASE_URL = Config.get("BASE_URL");
    public static String DOWNLOAD_URL = Config.get("DOWNLOAD_URL");

    //Resources
    public static String CLIENT_TITLE = Config.get("CLIENT_TITLE");
    public static String CLIENT_FILE_NAME = Config.get("CLIENT_FILE_NAME");

    public static BufferedImage getResource(String resource) {
        try {
            return ImageIO.read(Utils.class.getResourceAsStream("/assets/" + resource));
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Can't load the given resource (" + resource + ") : " + e);
        }
    }

    public static InputStream getResourceFromUrl(String url) throws IOException {
        final URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept_Language", "en-US,q=0.5");
        connection.setDoOutput(true);
        return connection.getInputStream();
    }

}
