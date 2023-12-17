package Tutorial;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

public class Main {

    private static JFrame currentFrame;
    private static JPanel customBox;
    private static int boxX = 10;
    private static int boxY = 450;
    private static int boxWidth = 25;
    private static int boxHeight = 25;
    private static int gravity = 1;
    private static int jumpStrength = -15;
    private static int doubleJumpStrength = -12;
    private static int ySpeed = 0;
    private static int xSpeed = 0;
    private static int jumpsRemaining = 2;
    private static int groundLevel = 475;
    private static boolean isSoundOn = false; // Track whether the sound is on or off

    public static void main(String[] args) {
        currentFrame = new JFrame("Hello World");
        currentFrame.setSize(500, 500);
        currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        currentFrame.setLayout(new BorderLayout());

        BGColor(Color.BLUE);

        customBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLUE);
                g.fillRect(boxX, boxY, boxWidth, boxHeight);

                // Draw the ground
                g.setColor(Color.GREEN);
                g.fillRect(0, groundLevel, currentFrame.getWidth(), currentFrame.getHeight() - groundLevel);
            }
        };

        currentFrame.add(customBox, BorderLayout.CENTER);

        currentFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    xSpeed = -5;
                    stopJumpSound(); // Turn off the sound when moving left
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    xSpeed = 5;
                    stopJumpSound(); // Turn off the sound when moving right
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && jumpsRemaining > 0) {
                    if (boxY == groundLevel - boxHeight) {
                        // Perform a regular jump
                        ySpeed = jumpStrength;
                        jumpsRemaining--;
                        if (!isSoundOn) {
                            playJumpSound();
                            isSoundOn = true; // Turn on the sound
                        }
                    } else if (jumpsRemaining == 1 && isSoundOn) {
                        // Perform a double jump
                        ySpeed = doubleJumpStrength;
                        jumpsRemaining--;
                        stopJumpSound(); // Turn off the sound for double jump
                        isSoundOn = false; // Turn off the sound
                    }
                }
            }



            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    xSpeed = 0;
                }
            }
        });

        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                customBox.repaint();
            }
        });

        currentFrame.setFocusable(true);
        currentFrame.requestFocus();

        currentFrame.setVisible(true);
        timer.start();
    }

    private static void BGColor(Color boxColor) {
        currentFrame.getContentPane().setBackground(boxColor);
    }

    private static void update() {
        // Apply gravity
        ySpeed += gravity;

        // Update position
        boxX += xSpeed;
        boxY += ySpeed;

        // Keep the box within the bounds of the frame
        boxX = Math.max(0, Math.min(boxX, currentFrame.getWidth() - boxWidth));
        boxY = Math.max(0, Math.min(boxY, groundLevel - boxHeight));

        // Check for collision with the ground
        if (boxY >= groundLevel - boxHeight) {
            boxY = groundLevel - boxHeight;
            ySpeed = 0;
            jumpsRemaining = 2; // Reset jumps when grounded
        }
    }

    private static void playJumpSound() {
        new Thread(() -> {
            try {
                File soundFile = new File("jump.wav");
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
