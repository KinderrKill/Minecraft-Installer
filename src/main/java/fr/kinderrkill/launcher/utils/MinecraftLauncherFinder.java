package fr.kinderrkill.launcher.utils;

import fr.kinderrkill.launcher.LauncherPanel;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MinecraftLauncherFinder {
    private static final String[] exeMd5 = new String[]{
            "350df398c57c56ed744306de95a4e451", // MinecraftLauncher.exe from Minecraft.msi
            "3389f0c8717e7438bfb0b03498756f27", // An old version
            "3c166bae84553d4cb27af8abdc61712d", // New (downloaded in may 2014)
            "7C710AE7EF7A774B30E04C3867F9B96E"
    };
    private static final String[] jarMd5 = new String[]{
            "85273e24404cc6865805f951487b8a1e", // New (downloaded in may 2014)
    };
    private static final int BUFFER_SIZE = 3 * 1024 * 1024;
    private static final byte[] buffer = new byte[BUFFER_SIZE];
    public static boolean searching = true;
    private static String launcherPath = null;
    private static MessageDigest messageDigest = null;

    public static File getMinecraftLauncherPath() {
        if (launcherPath == null)
            return null;

        return new File(launcherPath);
    }

    public static void startSearch(final LauncherPanel launcherMainPanel) {
        String path = searchMinecraftLauncher();
        searching = false;
        if (path != null) {
            launcherPath = path;
        }

        if (launcherMainPanel != null) {
            launcherMainPanel.updateMinecraftLauncher();
        }
    }

    private static String searchMinecraftLauncher() {
        if (OSHelper.getOS() == OSHelper.MAC) {
            String applicationPath = "/Applications";
            File applicationFile = new File(applicationPath);
            File minecraftAppFile = new File(applicationFile, "/Minecraft.app");
            File contentFile = new File(minecraftAppFile, "/Contents");
            File resourcesAppFile = new File(contentFile, "/Resources");
            File javaFile = new File(resourcesAppFile, "/Java");
            File bootstrapFile = new File(javaFile, "Bootstrap.jar");
            if (bootstrapFile.exists()) {
                return bootstrapFile.toString();
            }
            if (applicationPath != null) {
                String found = SearchInDirectory(applicationPath);
                if (found != null)
                    return found;
            }
        }
        if (OSHelper.getOS() == OSHelper.WINDOWS) {
            String programFile = System.getenv("ProgramFiles(X86)");
            if (programFile != null) {
                File pFile = new File(programFile);
                File mpFile = new File(pFile, "Minecraft Launcher");
                if (!mpFile.exists()) {
                    mpFile = new File(pFile, "Minecraft");
                }
                File launcher = new File(mpFile, "MinecraftLauncher.exe");
                if (launcher.exists()) {
                    return launcher.toString();
                }
                String found = SearchInDirectory(mpFile.getAbsolutePath());
                if (found != null)
                    return found;
            }
        }
        String UserDirectoryPath = System.getProperty("user.dir");
        if (UserDirectoryPath != null) {
            String found = SearchInDirectory(UserDirectoryPath);
            if (found != null)
                return found;
        }
        String homeDirectoryPath = System.getProperty("user.home");
        if (homeDirectoryPath != null) {
            String found = SearchInDirectory(homeDirectoryPath);
            if (found != null)
                return found;
        }
        String desktopDirectoryPath = System.getProperty("user.home") + System.getProperty("file.separator")
                + "Desktop";
        if (desktopDirectoryPath != null) {
            String found = SearchInDirectory(desktopDirectoryPath);
            if (found != null)
                return found;
        }
        String downloadDirectoryPath = System.getProperty("user.home") + System.getProperty("file.separator")
                + "Downloads";
        if (downloadDirectoryPath != null) {
            String found = SearchInDirectory(downloadDirectoryPath);
            if (found != null)
                return found;
        }

        return null;
    }

    public static void launchMinecraftLauncher() {
        Runtime runtime = Runtime.getRuntime();
        try {
            File pathToMinecraftLauncher = LauncherPanel.getInstance().getMinecraftLauncher();
            if (pathToMinecraftLauncher != null) {
                File launcher = pathToMinecraftLauncher;
                if (pathToMinecraftLauncher.getName().endsWith(".app")) {
                    launcher = new File(pathToMinecraftLauncher, "Contents" + File.separator + "Resources"
                            + File.separator + "Java" + File.separator + "Bootstrap.jar");
                }

                if (launcher.canExecute() && !launcher.toString().endsWith(".jar")) {
                    runtime.exec(new String[]{
                            launcher.toString(), "--workDir", OSHelper.getOS().getMinecraftDirectory()
                    });
                } else {
                    runtime.exec(new String[]{
                            "java", "-jar", launcher.toString(), "--workDir", OSHelper.getOS().getMinecraftDirectory()
                    });
                }
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "L'installeur peut lancer automatiquement le launcher Minecraft si celui-ci est défini"
                                + "\n\nExemple pour " + OSHelper.getOS() + " :    "
                                + (OSHelper.getOS().equals(OSHelper.WINDOWS) ? "Minecraft Launcher.exe" : OSHelper.getOS().equals(OSHelper.LINUX) ? "Minecraft Launcher.jar" : "Minecraft Launcher.app")
                                + "\n"
                        , "Launcher Minecraft non détecté !", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors du lancement du launcher Minecraft."
                    , "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean checkMd5(String currentFileMd5) {
        for (String s : jarMd5) {
            if (s.equals(currentFileMd5))
                return true;
        }
        if (OSHelper.getOS() == OSHelper.WINDOWS) {
            for (String s : exeMd5) {
                if (s.equals(currentFileMd5))
                    return true;
            }
        }
        return false;
    }

    private static String computeMd5(File file) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            int pos = 0;
            int readed = fis.read(buffer, 0, BUFFER_SIZE);
            while (readed > 0) {
                pos += readed;
                readed = fis.read(buffer, pos, BUFFER_SIZE - pos);
            }
            fis.close();
            messageDigest.reset();
            messageDigest.update(buffer, 0, pos);
            String currentFileMd5 = new BigInteger(1,
                    messageDigest.digest()).toString(16);
            while (currentFileMd5.length() < 32) {
                currentFileMd5 = "0" + currentFileMd5;
            }
            return currentFileMd5;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }

    private static String SearchInDirectory(String directoryPath) {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e1) {
            return null;
        }

        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            for (String f : directory.list()) {
                File currentFile = new File(directory, f);
                if (currentFile.isDirectory()) {
                    if (currentFile.toString().endsWith(".app")) {
                        File bootstrapFile = new File(currentFile, "Contents" + File.separator + "Resources"
                                + File.separator + "Java" + File.separator + "Bootstrap.jar");
                        if (bootstrapFile.exists()) {
                            String currentFileMd5 = computeMd5(bootstrapFile);
                            if (checkMd5(currentFileMd5)) {
                                return currentFile.toString();
                            }
                        }
                    }
                    continue;
                }
                if (!currentFile.canRead()) {
                    continue;
                }
                long size = currentFile.length();
                if (size > BUFFER_SIZE || size < 16) {
                    continue;
                }

                String currentFileMd5 = computeMd5(currentFile);
                if (checkMd5(currentFileMd5)) {
                    return currentFile.toString();
                }
            }
        }
        return null;
    }
}
