package fr.kinderrkill.launcher;

import fr.kinderrkill.launcher.utils.Config;
import fr.kinderrkill.launcher.utils.Utils;

import javax.swing.*;
import java.awt.*;

public class LauncherWindow extends JFrame {

    public LauncherWindow() {
        super(Utils.CLIENT_TITLE);
    }

    public void launch() {
        Config.init();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(new Dimension(Config.width, Config.height)));
        setBackground(Color.GRAY);

        setIconImage(Utils.getResource(Config.get("LAUNCHER_ICON_TEXTURE")));

        setupPanel();

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    private void setupPanel() {
        this.setContentPane(new LauncherPanel());
    }
}
