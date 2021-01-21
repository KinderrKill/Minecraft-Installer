package fr.kinderrkill.launcher;

import fr.kinderrkill.launcher.utils.*;
import fr.kinderrkill.launcher.utils.Config.Alignment;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.textured.STexturedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class LauncherPanel extends JPanel {

    private static LauncherPanel instance;

    private final Installer installer;

    private STexturedButton startButton;
    private SColoredBar progressBar;
    private JLabel progressLabel;

    private File minecraftLauncher;
    private JLabel minecraftLauncherPath;

    public LauncherPanel() {
        instance = this;
        installer = new Installer(this);

        MinecraftLauncherFinder.startSearch(this);

        this.setLayout(null);
        this.setBackground(Swinger.TRANSPARENT);

        setupComponent();

        loadLauncherData();
    }

    public static LauncherPanel getInstance() {
        return instance;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Swinger.drawFullsizedImage(g, this, Utils.getResource(Config.get("BACKGROUND_TEXTURE")));

        int logoWidth = Config.getRelativeInt("MAIN_LOGO_WIDTH");
        int logoHeight = Config.getRelativeInt("MAIN_LOGO_HEIGHT");
        int logoPosX = Config.getRelativeInt("MAIN_LOGO_POS_X");
        int logoPosY = Config.getRelativeInt("MAIN_LOGO_POS_Y");

        g.drawImage(
                Utils.getResource(Config.get("MAIN_LOGO_TEXTURE")),
                logoPosX - (logoWidth / 2),
                logoPosY,
                logoWidth,
                logoHeight,
                this);
    }

    private void setupComponent() {
        startButton =
                new STexturedButton(
                        Utils.getResource(Config.get("PLAY_BUTTON_0_TEXTURE")),
                        Utils.getResource(Config.get("PLAY_BUTTON_1_TEXTURE")),
                        Utils.getResource(Config.get("PLAY_BUTTON_2_TEXTURE")));
        Alignment startButtonAlign = Config.getAlignment("PLAY_BUTTON_ALIGN");
        switch (startButtonAlign) {
            case LEFT:
                startButton.setBounds(
                        Config.getRelativeInt("PLAY_BUTTON_POS_X"),
                        Config.getRelativeInt("PLAY_BUTTON_POS_Y"),
                        Config.getRelativeInt("PLAY_BUTTON_WIDTH"),
                        Config.getRelativeInt("PLAY_BUTTON_HEIGHT"));
                break;
            case CENTER:
                startButton.setBounds(
                        Config.getRelativeInt("PLAY_BUTTON_POS_X")
                                - Config.getRelativeInt("PLAY_BUTTON_WIDTH") / 2,
                        Config.getRelativeInt("PLAY_BUTTON_POS_Y"),
                        Config.getRelativeInt("PLAY_BUTTON_WIDTH"),
                        Config.getRelativeInt("PLAY_BUTTON_HEIGHT"));
                break;
            case RIGHT:
                startButton.setBounds(
                        Config.getRelativeInt("PLAY_BUTTON_POS_X") - Config.getRelativeInt("PLAY_BUTTON_WIDTH"),
                        Config.getRelativeInt("PLAY_BUTTON_POS_Y"),
                        Config.getRelativeInt("PLAY_BUTTON_WIDTH"),
                        Config.getRelativeInt("PLAY_BUTTON_HEIGHT"));
                break;

            default:
                startButton.setBounds(
                        Config.getRelativeInt("PLAY_BUTTON_POS_X"),
                        Config.getRelativeInt("PLAY_BUTTON_POS_Y"),
                        Config.getRelativeInt("PLAY_BUTTON_WIDTH"),
                        Config.getRelativeInt("PLAY_BUTTON_HEIGHT"));
                break;
        }
        startButton.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        installer.start();
                        startButton.setEnabled(false);
                    }
                });
        add(startButton);

        int barPosX = Config.getRelativeInt("BAR_POS_X");
        int barPosY = Config.getRelativeInt("BAR_POS_Y");
        int barWidth = Config.getRelativeInt("BAR_WIDTH");
        int barHeight = Config.getRelativeInt("BAR_HEIGHT");
        Alignment barAlign = Config.getAlignment("BAR_ALIGN");
        progressBar = new SColoredBar(Config.getColor("BAR_COLOR_PRIMARY"), Config.getColor("BAR_COLOR_SECONDARY"));

        switch (barAlign) {
            case LEFT:
                progressBar.setBounds(barPosX, barPosY, barWidth, barHeight);
                break;
            case CENTER:
                progressBar.setBounds(barPosX - barWidth / 2, barPosY, barWidth, barHeight);
                break;
            case RIGHT:
                progressBar.setBounds(barPosX - barWidth, barPosY, barWidth, barHeight);
                break;

            default:
                progressBar.setBounds(barPosX, barPosY, barWidth, barHeight);
                break;
        }
        progressBar.setMaximum(100);

        progressLabel =
                new JLabel(
                        Utils.CLIENT_TITLE + " - Version " + Utils.getOnlineGameVersion(),
                        SwingConstants.CENTER);
        switch (barAlign) {
            case LEFT:
                progressLabel.setBounds(barPosX, barPosY - 2, barWidth, 20);
                break;
            case CENTER:
                progressLabel.setBounds(barPosX - barWidth / 2, barPosY - 2, barWidth, 20);
                break;
            case RIGHT:
                progressLabel.setBounds(barPosX - barWidth, barPosY - 2, barWidth, 20);
                break;

            default:
                progressLabel.setBounds(barPosX, barPosY - 2, barWidth, 20);
                break;
        }
        progressLabel.setFont(new Font(progressLabel.getFont().getName(), Font.BOLD, 15));
        progressLabel.setForeground(Config.getColor("BAR_COLOR_TEXT"));
        add(progressLabel);
        add(progressBar);

        int pathY = Config.getRelativeInt("MINECRAFT_PATH_POS_Y");
        minecraftLauncherPath = new JLabel("", SwingConstants.CENTER);
        minecraftLauncherPath.setBounds(0, pathY, Config.width, 40);
        minecraftLauncherPath.setFont(
                new Font(minecraftLauncherPath.getFont().getName(), Font.BOLD, 15));
        minecraftLauncherPath.setForeground(Color.WHITE);
        minecraftLauncherPath.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        minecraftLauncherPath.setForeground(Color.YELLOW);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        minecraftLauncherPath.setForeground(Color.WHITE);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        askMinecraftLauncher();
                    }
                });
        add(minecraftLauncherPath);

        displayMinecraftLauncherPath();
    }

    private void askMinecraftLauncher() {
        if (minecraftLauncher == null || !minecraftLauncher.exists()) {
            if (!MinecraftLauncherFinder.searching) {
                minecraftLauncher = MinecraftLauncherFinder.getMinecraftLauncherPath();
                if (minecraftLauncher != null && minecraftLauncher.exists()) {
                    displayMinecraftLauncherPath();
                    return;
                }
            }
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Définir l'emplacement du Launcher Minecraft");
        fc.setFileFilter(new MinecraftLauncherFilter());
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            minecraftLauncher = fc.getSelectedFile();
        }
        displayMinecraftLauncherPath();
    }

    public void updateMinecraftLauncher() {
        if (minecraftLauncher == null || !minecraftLauncher.exists()) {
            if (!MinecraftLauncherFinder.searching) {
                minecraftLauncher = MinecraftLauncherFinder.getMinecraftLauncherPath();
            }
        }
    }

    public void displayMinecraftLauncherPath() {
        if (MinecraftLauncherFinder.searching) {
            minecraftLauncherPath.setText(
                    "<html><center>"
                            + "<u>Launcher Minecraft :</u>"
                            + "<br>"
                            + "<FONT SIZE=\"3\">> Recherche en cours...</FONT>"
                            + "</center></html>");
        } else if (minecraftLauncher == null) {
            minecraftLauncherPath.setText(
                    "<html><center>"
                            + "<u>Launcher Minecraft (Cliquer pour définir) :</u>"
                            + "<br>"
                            + "<FONT SIZE=\"3\">> Introuvable...</FONT>"
                            + "</center></html>");
        } else {
            minecraftLauncherPath.setText(
                    "<html><center>"
                            + "<u>Launcher Minecraft :</u>"
                            + "<br>"
                            + "<FONT SIZE=\"3\">"
                            + minecraftLauncher
                            + "</FONT>"
                            + "</center></html>");
            saveLauncherData();
        }
    }

    public File getMinecraftLauncher() {
        if (minecraftLauncher == null) {
            askMinecraftLauncher();
            if (minecraftLauncher == null) {
                return null;
            }
        }
        if (!minecraftLauncher.exists()) {
            askMinecraftLauncher();
        } else {
            return minecraftLauncher;
        }
        return null;
    }

    public void saveLauncherData() {
        File clientDirectory = new File(OSHelper.getOS().getMinecraftDirectory());
        if (!clientDirectory.exists()) {
            clientDirectory.mkdir();
        }

        try {
            File launcherProperties =
                    new File(clientDirectory, Utils.CLIENT_FILE_NAME.toLowerCase() + "-installer.properties");
            Properties properties = new Properties();

            if (minecraftLauncher != null && minecraftLauncher.exists()) {
                properties.setProperty("minecraftLauncher", minecraftLauncher.toString());
            }

            FileOutputStream fos = new FileOutputStream(launcherProperties);
            properties.store(fos, null);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLauncherData() {
        File clientDirectory = new File(OSHelper.getOS().getMinecraftDirectory());
        if (!clientDirectory.exists()) {
            return;
        }

        try {
            File launcherProperties =
                    new File(clientDirectory, Utils.CLIENT_FILE_NAME.toLowerCase() + "-installer.properties");
            if (!launcherProperties.exists()) {
                return;
            }

            FileInputStream fis = new FileInputStream(launcherProperties);
            Properties properties = new Properties();
            properties.load(fis);

            minecraftLauncher = null;
            String minecraftLauncherPath = properties.getProperty("minecraftLauncher");
            if (minecraftLauncher != null) {
                minecraftLauncher = new File(minecraftLauncherPath);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SColoredBar getProgressBar() {
        return progressBar;
    }

    public STexturedButton getStartButton() {
        return startButton;
    }

    public JLabel getProgressLabel() {
        return progressLabel;
    }
}