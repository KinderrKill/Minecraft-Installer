package fr.kinderrkill.launcher.utils;

import fr.kinderrkill.launcher.LauncherPanel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

    public void run() {
        initFilesToDowload();
        startDownloadThread();
    }

    private void initFilesToDowload() {
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
    }

    private void precalculateDownloadSize() {
        int totalSize = 1; // Not zero, so no divide by 0 after.
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
                    panel.getProgressLabel().setText("Téléchargement des fichiers : " + (totalDownload * 100 / downloadTotalSize) + " %");
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
                boolean needUpdate = true;

                if (needUpdate) {
                    precalculateDownloadSize();
                    if (!downloadFilesToDownload()) {
                        //mainWindow.onFailUpdate("FailedDownloadingMinefieldFiles");
                        return;
                    }
                    //if (!settingUpLauncherProfile()) {
                        //mainWindow.onFailUpdate("FailedSettingUpLauncherProfile");
                      //  return;
                    //}
                    //saveClientVersion();
                }
                System.out.println("FINISH DOWLOADING !");
                panel.getStartButton().setEnabled(true);
               // runMinecraftLauncher();
            }
        });
        t.start();
    }
}
