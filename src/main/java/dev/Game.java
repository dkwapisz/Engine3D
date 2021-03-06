package dev;

import dev.player.Player;
import mapUtilities.ButtonWall;
import mapUtilities.ExitFloor;
import mapUtilities.StaticObjects;
import mapUtilities.doors.BasicDoor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Game extends JFrame implements Runnable {

    private final Thread mainThread;
    private boolean running;
    private final BufferedImage image;
    private final int[] pixels;
    private StaticObjects[][] map;
    private static Map<ButtonWall, BasicDoor> buttonGroup;
    private ArrayList<Textures> textures;
    private final Player player;
    private final Screen screen;
    private final View2D view2D;
    private int mapNumber;
    private double startPosX;
    private double startPosY;
    private static int[] exitPos = new int[2];
    private boolean mapLoading;

    public Game() {
        loadMap(false);
        mainThread = new Thread(this);
        image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        loadTextures();
        player = new Player(startPosX, startPosY, 1, 0, 0, -0.66, map);
        screen = new Screen(map, textures, 1280, 720);
        view2D = new View2D(map, player, screen);
        addKeyListener(player.getControls());
        addMouseMotionListener(player.getControls());
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Engine3D");
        setBackground(Color.black);
        setLocationRelativeTo(null);
        setVisible(true);
        start();
    }

    private void loadTextures() {
        textures = new ArrayList<>();
        textures.add(Textures.basicWall);
        textures.add(Textures.basicDoor);
        textures.add(Textures.buttonWall);
        textures.add(Textures.buttonWallActivated);
    }

    public void loadMap(boolean nextMap) {
        mapNumber++;
        map = MapReading.getMap(mapNumber);
        buttonGroup = MapReading.getButtonGroups(map, mapNumber);
        startPosX = MapReading.getStartPosX() + 0.5;
        startPosY = MapReading.getStartPosY() + 0.5;

        if (nextMap) {
            player.setNextLevelPositions(startPosX, startPosY, map);
            screen.setNextLevel(map);
            view2D.setNextLevelView(map, player, screen);
        }
    }

    private void checkExitCollision() {
        if (new Rectangle2D.Double(player.getPosX(), player.getPosY(), 0.5, 0.5).intersects(new Rectangle2D.Double(exitPos[0]+0.5, exitPos[1]+0.5, 1, 1))) {
            loadMap(true);
        }
    }

    private synchronized void start() {
        running = true;
        mainThread.start();
    }

    public synchronized void stop() {
        running = false;

        try {
            mainThread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void render() {
        BufferStrategy bufferStrategy = getBufferStrategy();

        if (bufferStrategy == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        bufferStrategy.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double nanos = 1000000000.0 / 60.0; // 60 times per sec
        double delta = 0;
        requestFocus();
        while (running) {
            long now = System.nanoTime();
            delta = delta + ((now - lastTime) / nanos);
            lastTime = now;
            while (delta >= 1) {
                screen.update(player, pixels);
                player.movementUpdate();
                checkExitCollision();
                delta--;
            }
            view2D.repaint();
            render();
        }
    }

    public static Map<ButtonWall, BasicDoor> getButtonGroup() {
        return buttonGroup;
    }
    public static int[] getExitPos() {
        return exitPos;
    }

    public static void main(String [] args) {
        new Game();
    }

}
