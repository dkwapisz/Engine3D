package dev;

import mapUtilities.ButtonWall;
import mapUtilities.doors.BasicDoor;
import mapUtilities.StaticObjects;
import mapUtilities.Wall;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class MapReading {

    public static StaticObjects[][] getMap() {
        BufferedImage mapImage = loadImage("src/main/resources/maps/map0.png");
        StaticObjects[][] map = new StaticObjects[mapImage.getWidth()][mapImage.getHeight()];
        int freeSpace = new Color(255, 255, 255).getRGB();
        int basicWall = new Color(0, 0, 0).getRGB();
        int basicDoor = new Color(0, 0, 255).getRGB();
        int buttonWall = new Color(0, 200, 255).getRGB();

        for (int x = 0; x < mapImage.getWidth(); x++) {
            for (int y = 0; y < mapImage.getHeight(); y++) {

                int currentPixel = mapImage.getRGB(x, y);

                if (currentPixel == freeSpace) {
                    map[x][y] = null;
                }
                else if (currentPixel == basicWall) {
                    map[x][y] = new Wall();
                }
                else if (currentPixel == basicDoor) {
                    map[x][y] = new BasicDoor();
                }
                else if (currentPixel == buttonWall) {
                    map[x][y] = new ButtonWall();
                }
            }
        }

        return map;
    }


    public static BufferedImage loadImage(String path) {
        BufferedImage imageToReturn = null;
        try {
            imageToReturn = ImageIO.read(new FileInputStream(path));
        } catch (IOException e) {
            System.out.println("Error with reading map file");
        }
        return imageToReturn;
    }
}
