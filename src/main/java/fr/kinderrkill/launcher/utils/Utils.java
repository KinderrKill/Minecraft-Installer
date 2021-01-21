package fr.kinderrkill.launcher.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Utils {

    //Links
    public static String BASE_URL = Config.get("BASE_URL");
    public static String DOWNLOAD_URL = Config.get("DOWNLOAD_URL");

    //Resources
    public static String CLIENT_TITLE = Config.get("CLIENT_TITLE");
    public static String CLIENT_FILE_NAME = Config.get("CLIENT_FILE_NAME");
    public static String CLIENT_VERSION_NAME = Config.get("CLIENT_VERSION_NAME");

    public static String RESOURCE_PACK_NAME = Config.get("RESOURCE_PACK_NAME");

    public static String MINECRAFT_PROFIL_ICON = Config.get("MINECRAFT_PROFIL_ICON");

    public static String gameVersionNumber = null;

    public static String getOnlineGameVersion() {
        String onlineGameVersion = null;
        try {
            InputStream stream = getResourceFromUrl(DOWNLOAD_URL + CLIENT_VERSION_NAME + ".txt");
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader buffReader = new BufferedReader(reader);
            onlineGameVersion = buffReader.readLine();
            buffReader.close();
            reader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return onlineGameVersion;
    }

    public static String getLocalGameVersion() {
        final String minecraftDirectory = OSHelper.getOS().getMinecraftDirectory();

        File versionDirectory = new File(minecraftDirectory, "versions");
        if (!versionDirectory.exists()) {
            versionDirectory.mkdir();
        }

        File clientDestination = new File(versionDirectory, Utils.CLIENT_FILE_NAME);
        if (!clientDestination.exists()) {
            clientDestination.mkdir();
        }

        File localVersion = new File(clientDestination, Utils.CLIENT_VERSION_NAME + ".txt");
        if (localVersion.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(localVersion));
                gameVersionNumber = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gameVersionNumber = "0";
        }
        return gameVersionNumber;
    }

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

    public static String readFile(final File fileIn) throws IOException {
        final FileReader fileReader = new FileReader(fileIn);
        final BufferedReader buffReader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String currLine;
        while ((currLine = buffReader.readLine()) != null && !currLine.startsWith("#")) {
            sb.append(currLine);
        }

        buffReader.close();
        fileReader.close();

        return sb.toString();
    }

    public static void writeFile(String text, File file) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        writer.println(text);
        writer.close();
    }

}
