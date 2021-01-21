package fr.kinderrkill.launcher.utils;

import java.io.File;

public enum OSHelper {

    WINDOWS("AppData" + File.separator + "Roaming" + File.separator + ".minecraft"),
    MAC("library" + File.separator + "Application Support" + File.separator + ".minecraft"),
    LINUX(".minecraft");

    private final String directory;

    private OSHelper(String directory) {
        this.directory = File.separator + directory + File.separator;
    }

    public static final OSHelper getOS() {
        final String currentOs = System.getProperty("os.name").toLowerCase();
        return currentOs.startsWith("windows") ? WINDOWS : currentOs.startsWith("mac") ? MAC : LINUX;
    }

    public String getMinecraftDirectory() {
        return System.getProperty("user.home") + directory;
    }

}
