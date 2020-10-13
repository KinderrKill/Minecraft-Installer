package fr.kinderrkill.launcher;

import fr.kinderrkill.launcher.utils.Config;
import fr.kinderrkill.launcher.utils.Installer;
import fr.kinderrkill.launcher.utils.Utils;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.textured.STexturedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LauncherPanel extends JPanel {

    private final int WIDTH = 720;
    private final int HEIGHT = 450;

    private final Installer installer;

    private STexturedButton startButton;
    private SColoredBar progressBar;
    private JLabel progressLabel;


    public LauncherPanel() {
        installer = new Installer(this);

        this.setLayout(null);
        this.setBackground(Swinger.TRANSPARENT);

        setupComponent();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Swinger.drawFullsizedImage(g, this, Utils.getResource(Config.get("BACKGROUND_TEXTURE")));

        int logoWidth = Integer.parseInt(Config.get("MAIN_LOGO_WIDTH"));
        int logoHeight = Integer.parseInt(Config.get("MAIN_LOGO_HEIGHT"));

        g.drawImage(Utils.getResource(Config.get("MAIN_LOGO_TEXTURE")), WIDTH/2 - (logoWidth/2), HEIGHT/2 - (logoHeight/2) - 150, logoWidth, logoHeight, this);
    }

    private void setupComponent() {
        startButton = new STexturedButton(Utils.getResource(Config.get("PLAY_BUTTON_0_TEXTURE")), Utils.getResource(Config.get("PLAY_BUTTON_1_TEXTURE")), Utils.getResource(Config.get("PLAY_BUTTON_2_TEXTURE")));
        startButton.setBounds(WIDTH/2 - (startButton.getTexture().getWidth(startButton.getParent())/2), Integer.parseInt(Config.get("PLAY_BUTTON_HEIGHT")));
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                installer.start();
                startButton.setEnabled(false);
            }
        });
        add(startButton);

        int barPosX = 720;
        int barPosY = Integer.parseInt(Config.get("BAR_POS_Y"));
        int barWidth = Integer.parseInt(Config.get("BAR_WIDTH"));
        int barHeight = Integer.parseInt(Config.get("BAR_HEIGHT"));
        progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
        progressBar.setBounds(barPosX/2-(barWidth/2), barPosY, barWidth, barHeight);
        progressBar.setMaximum(100);
        add(progressBar);

        progressLabel = new JLabel("Téléchargement des fichiers en attente...", SwingConstants.CENTER);
        progressLabel.setBounds(barPosX/2-(barWidth/2), barPosY - 2, barWidth, 20);
        progressLabel.setFont(new Font(progressLabel.getFont().getName(), Font.BOLD, 15));
        progressLabel.setForeground(Color.WHITE);
        add(progressLabel);
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
