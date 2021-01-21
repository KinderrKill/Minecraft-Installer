package fr.kinderrkill.launcher.utils;

import fr.kinderrkill.launcher.LauncherPanel;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static void launchMinecraft() {
        setUpLauncherProfile();
        MinecraftLauncherFinder.launchMinecraftLauncher();
    }

    private static void setUpLauncherProfile() {
        final String minecraftDirectory = OSHelper.getOS().getMinecraftDirectory();
        File profilesFile = new File(minecraftDirectory, "launcher_profiles.json");
        if (!profilesFile.exists()) {
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(profilesFile);
            byte[] data = new byte[(int) profilesFile.length()];
            fis.read(data);
            fis.close();


            String json = new String(data, StandardCharsets.UTF_8);
            JSONObject profilesJson = new JSONObject(json);

            JSONObject profiles = (JSONObject) profilesJson.get("profiles");
            JSONObject newProfile = new JSONObject();
            newProfile.put("name", Utils.CLIENT_FILE_NAME + " V." + Utils.getOnlineGameVersion());
            newProfile.put("icon", Utils.MINECRAFT_PROFIL_ICON);
            newProfile.put("lastVersionId", Utils.CLIENT_FILE_NAME);
            newProfile.put("useHopperCrashService", false);

            profiles.put(Utils.CLIENT_FILE_NAME, newProfile);

            fis.close();
            FileWriter fw = new FileWriter(profilesFile);
            fw.write(profilesJson.toString(4));
            fw.close();

        } catch (ClassCastException | IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        initFilesToDownload();
        startDownloadThread();
    }

    private void initFilesToDownload() {
        final String minecraftDirectory = OSHelper.getOS().getMinecraftDirectory();

        File versionDirectory = new File(minecraftDirectory, "versions");
        if (!versionDirectory.exists()) {
            versionDirectory.mkdir();
        }

        File clientDestination = new File(versionDirectory, Utils.CLIENT_FILE_NAME);
        if (!clientDestination.exists()) {
            clientDestination.mkdir();
        }

        filesToDownload.add(new File(clientDestination, Utils.CLIENT_FILE_NAME + ".json"));
        filesToDownload.add(new File(clientDestination, Utils.CLIENT_FILE_NAME + ".jar"));

        if (!Utils.RESOURCE_PACK_NAME.equalsIgnoreCase("null")) {
            File resourcePackDestination = new File(minecraftDirectory, "resourcepacks");
            if (!resourcePackDestination.exists()) {
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

        //DISPLAY SIZE
        double bytesTotal = downloadTotalSize;
        double kilobytesTotal = (bytesTotal / 1024);
        double megabytesTotal = (kilobytesTotal / 1024);
        String totalMOToDisplay = String.format("%.2f", megabytesTotal);

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

                    double bytesActual = totalDownload;
                    double kilobytesActual = (bytesActual / 1024);
                    double megabytesActual = (kilobytesActual / 1024);
                    String actualMOToDisplay = String.format("%.2f", megabytesActual);

                    panel.getProgressBar().setValue(totalDownload * 100 / downloadTotalSize);
                    panel
                            .getProgressLabel()
                            .setText(
                                    "Téléchargement des "
                                            + filesToDownload.size()
                                            + " fichiers : " + actualMOToDisplay + "mo / " + totalMOToDisplay + "mo ("
                                            + (totalDownload * 100 / downloadTotalSize)
                                            + " %)");

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
        Thread t =
                new Thread(
                        new Runnable() {
                            public void run() {
                                if (needUpdate()) {
                                    precalculateDownloadSize();
                                    if (!downloadFilesToDownload()) {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Erreur lors du téléchargement du client " + Utils.CLIENT_TITLE,
                                                "Erreur " + Utils.CLIENT_TITLE,
                                                JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        saveGameVersion();
                                    }
                                }
                                panel.getStartButton().setEnabled(true);
                                launchMinecraft();
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
            if (!versionDirectory.exists()) {
                versionDirectory.mkdir();
            }

            File clientDestination = new File(versionDirectory, Utils.CLIENT_FILE_NAME);
            if (!clientDestination.exists()) {
                clientDestination.mkdir();
            }

            PrintWriter writer = new PrintWriter(clientDestination + "/" + Utils.CLIENT_VERSION_NAME + ".txt", "UTF-8");
            writer.println(Utils.getOnlineGameVersion());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
