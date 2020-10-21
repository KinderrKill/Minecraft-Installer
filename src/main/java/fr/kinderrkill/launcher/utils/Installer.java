package fr.kinderrkill.launcher.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.kinderrkill.launcher.LauncherPanel;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Installer {

    private final LauncherPanel panel;

    private final List<File> filesToDownload;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS'Z'", Locale.FRANCE);

    private int downloadTotalSize;

    public Installer(LauncherPanel panel) {
        this.panel = panel;
        this.filesToDownload = new ArrayList<>();
    }

    public void start() {
        initFilesToDownload();
        startDownloadThread();
    }

    private void initFilesToDownload() {
        final String minecraftDirectory = OSHelper.getOS().getMinecraftDirectory();

        File versionDirectory = new File(minecraftDirectory, "versions");
        if(!versionDirectory.exists()) {
            versionDirectory.mkdir();
        }

        File clientDestination = new File(versionDirectory, Utils.CLIENT_FILE_NAME);
        if(!clientDestination.exists()) {
            clientDestination.mkdir();
        }

        filesToDownload.add(new File(clientDestination, Utils.CLIENT_FILE_NAME + ".json"));
        filesToDownload.add(new File(clientDestination, Utils.CLIENT_FILE_NAME + ".jar"));

        if(!Utils.RESOURCE_PACK_NAME.equalsIgnoreCase("null")) {
            File resourcePackDestination = new File(minecraftDirectory, "resourcepacks");
            if(!resourcePackDestination.exists()) {
                resourcePackDestination.mkdir();
            }
            filesToDownload.add(new File(resourcePackDestination, Utils.RESOURCE_PACK_NAME + ".zip"));
        }
    }

    private void precalculateDownloadSize() {
        int totalSize = 1;
        for (File file : filesToDownload) {
            try {
                URL urlDowndloadFile = new URL(Utils.DOWNLOAD_URL + file.getName());
                URLConnection httpHeadRequest;
                httpHeadRequest = urlDowndloadFile.openConnection();
                httpHeadRequest.setDefaultUseCaches(false);
                if (httpHeadRequest instanceof HttpURLConnection) {
                    ((HttpURLConnection) httpHeadRequest).setRequestMethod("HEAD");
                }
                totalSize += httpHeadRequest.getContentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        downloadTotalSize = totalSize;
    }

    private boolean downloadFilesToDownload() {
        int totalDownload = 0;
        int bufferSize = 65536;
        int fileDownloaded = 0;

        byte[] buffer = new byte[bufferSize];
        for (File file : filesToDownload) {
            try {
                URL urlDowndloadFile = new URL(Utils.DOWNLOAD_URL + file.getName());
                InputStream is = urlDowndloadFile.openStream();
                FileOutputStream destination = new FileOutputStream(file);
                int readed = is.read(buffer, 0, bufferSize);
                while (readed >= 0) {
                    destination.write(buffer, 0, readed);
                    totalDownload += readed;
                    panel.getProgressBar().setValue(totalDownload * 100 / downloadTotalSize);
                    panel.getProgressLabel().setText("Téléchargement des " + filesToDownload.size()  + " fichiers : " + (totalDownload * 100 / downloadTotalSize) + " %");
                    readed = is.read(buffer, 0, bufferSize);
                }
                destination.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        panel.getProgressBar().setValue(100);
        panel.getProgressLabel().setText("Téléchargement des fichiers terminés !");
        return true;
    }

    private void startDownloadThread() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                if (needUpdate()) {
                    precalculateDownloadSize();
                    if (!downloadFilesToDownload()) {
                        JOptionPane.showMessageDialog(null, "Erreur lors du téléchargement du client " + Utils.CLIENT_TITLE, "Erreur " + Utils.CLIENT_TITLE, JOptionPane.ERROR_MESSAGE);
                    } else {
                        saveGameVersion();
                    }
                }
                panel.getStartButton().setEnabled(true);
                launchMinecraftLauncher();
            }
        });
        t.start();
    }

    private boolean needUpdate() {
        int localGameVersion = Integer.parseInt(Utils.getLocalGameVersion());
        int onlineGameVersion = Integer.parseInt(Utils.getOnlineGameVersion());

        return localGameVersion < onlineGameVersion;
    }

    private void saveGameVersion() {
        try {
            final String minecraftDirectory = OSHelper.getOS().getMinecraftDirectory();

            File versionDirectory = new File(minecraftDirectory, "versions");
            if(!versionDirectory.exists()) {
                versionDirectory.mkdir();
            }

            File clientDestination = new File(versionDirectory, Utils.CLIENT_FILE_NAME);
            if(!clientDestination.exists()) {
                clientDestination.mkdir();
            }

            PrintWriter writer = new PrintWriter(clientDestination + "/" + Utils.CLIENT_VERSION_NAME + ".txt", "UTF-8");
            writer.println(Utils.getOnlineGameVersion());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void launchMinecraftLauncher() {
        final String minecraftDirectory = OSHelper.getOS().getMinecraftDirectory();
        try {
            final String installDate = sdf.format(new Date());
            final JsonObject newProfile = new JsonObject();
            newProfile.addProperty("name", Utils.CLIENT_FILE_NAME + " V.");
            newProfile.addProperty("type", "custom");
            newProfile.addProperty("created", installDate);
            newProfile.addProperty("lastUsed", installDate);
            newProfile.addProperty("icon", Utils.MINECRAFT_PROFIL_ICON);
            newProfile.addProperty("lastVersionId", Utils.CLIENT_FILE_NAME);

            final File launcherProfileFile = new File(minecraftDirectory, "/launcher_profiles.json");
            JsonObject launcherProfile = new JsonObject();
            if (launcherProfileFile.exists()) {
                launcherProfile = new JsonParser().parse(Utils.readFile(launcherProfileFile)).getAsJsonObject();
            } else {
                launcherProfile.add("profiles", new JsonObject());
            }

            launcherProfile.get("profiles").getAsJsonObject().add(Utils.CLIENT_FILE_NAME, newProfile);
            launcherProfile.addProperty("selectedProfile", Utils.CLIENT_FILE_NAME);

            String jsonToWrite = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(launcherProfile);
            Utils.writeFile(jsonToWrite, launcherProfileFile);

            MinecraftLauncherFinder.launchMinecraftLauncher();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur lors du téléchargement du client " + Utils.CLIENT_TITLE, "Erreur " + Utils.CLIENT_TITLE, JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
