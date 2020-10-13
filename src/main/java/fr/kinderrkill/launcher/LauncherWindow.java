package fr.kinderrkill.launcher;

import fr.kinderrkill.launcher.utils.Config;
import fr.kinderrkill.launcher.utils.Utils;
import fr.theshark34.swinger.animation.Animator;

import javax.swing.*;
import java.awt.*;

public class LauncherWindow extends JFrame {

    public LauncherWindow() {
        super(Utils.CLIENT_TITLE);
    }

    public void launch() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(new Dimension(720, 480)));
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
