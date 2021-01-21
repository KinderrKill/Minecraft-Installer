package fr.kinderrkill.launcher.utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class MinecraftLauncherFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return (f.getName().endsWith(".exe") ||
                f.getName().endsWith(".jar") ||
                f.getName().endsWith(".app") ||
                f.isDirectory());
    }

    @Override
    public String getDescription() {
        return "Minecraft launcher file";
    }

}
