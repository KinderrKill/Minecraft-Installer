package fr.kinderrkill.launcher.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

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
