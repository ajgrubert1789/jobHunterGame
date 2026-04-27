package main;

import entities.Player;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class GameWindow {

    JFrame jFrame;
    private GamePanel gamePanel;
    public GameWindow(GamePanel gamePanel) {
        this.jFrame = new JFrame();

        this.gamePanel = gamePanel;

        jFrame.setLocationRelativeTo(null);

        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(gamePanel);
        // --- FULLSCREEN MODE ---
        jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize window
        jFrame.setUndecorated(true);                    // Remove borders
        jFrame.setResizable(false);                     // Lock size
        // ------------------------

//        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }
        });

    }
}
