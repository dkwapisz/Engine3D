package dev;

import mapUtilities.ButtonWall;
import mapUtilities.ExitFloor;
import mapUtilities.doors.BasicDoor;
import mapUtilities.StaticObjects;
import mapUtilities.Wall;
import mapUtilities.doors.Door;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapReading {

    private static final ArrayList<Integer> colorGroups = new ArrayList<>();
    private static int startPosX;
    private static int startPosY;

    public static StaticObjects[][] getMap(int mapNumber) {
        BufferedImage mapImage = loadImage("src/main/resources/maps/map" + mapNumber + ".png");
        mapImage = mapImage.getSubimage(0, 0, mapImage.getWidth(), mapImage.getHeight()/2);
        StaticObjects[][] map = new StaticObjects[mapImage.getWidth()][mapImage.getHeight()];
        int freeSpace = new Color(255, 255, 255).getRGB();
        int basicWall = new Color(0, 0, 0).getRGB();
        int basicDoor = new Color(0, 0, 255).getRGB();
        int buttonWall = new Color(0, 200, 255).getRGB();
        int startPos = new Color(255, 50, 50).getRGB();
        int exitFloor = new Color(255, 255, 0).getRGB();

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
                else if (currentPixel == exitFloor) {
                    Game.getExitPos()[0] = x;
                    Game.getExitPos()[1] = y;
                }
                else if (currentPixel == startPos) {
                    startPosX = x;
                    startPosY = y;
                }
            }
        }

        return map;
    }

    public static Map<ButtonWall, BasicDoor> getButtonGroups(StaticObjects[][] map, int mapNumber) {
        BufferedImage mapGroup = loadImage("src/main/resources/maps/map" + mapNumber + ".png");
        mapGroup = mapGroup.getSubimage(0, mapGroup.getHeight()/2, mapGroup.getWidth(), mapGroup.getHeight()/2);

        Map<ButtonWall, BasicDoor> buttonGroup = new HashMap<>();
        BasicDoor tempDoor = null;
        ButtonWall tempButton = null;
        int groupCounter = countGroups(mapGroup);

        for (int i = 0; i < groupCounter; i++) {

            for (int x = 0; x < mapGroup.getWidth(); x++) {
                for (int y = 0; y < mapGroup.getHeight(); y++) {

                    int currentPixel = mapGroup.getRGB(x, y);
                    int group1 = colorGroups.get(i);

                    if (currentPixel == group1) {
                        if (map[x][y] instanceof Door) {
                            tempDoor = (BasicDoor) map[x][y];
                            tempDoor.setButtonDoor(true);
                        } else if (map[x][y] instanceof ButtonWall) {
                            tempButton = (ButtonWall) map[x][y];
                            tempButton.setClicked(false);
                        }
                    }
                }
            }
            buttonGroup.put(tempButton, tempDoor);
        }
        return buttonGroup;
    }

    private static int countGroups(BufferedImage mapGroup) {
        int groupCounter = 0;
        for (int x = 0; x < mapGroup.getWidth(); x++) {
            for (int y = 0; y < mapGroup.getHeight(); y++) {

                int currentPixel = mapGroup.getRGB(x, y);
                int undefined1 = new Color(0,0,0).getRGB();
                int undefined2 = new Color(255,255,255).getRGB();

                if (currentPixel != undefined1 && currentPixel != undefined2 && !colorGroups.contains(currentPixel)) {
                    groupCounter++;
                    colorGroups.add(currentPixel);
                }
            }
        }
        return groupCounter;
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

    public static int getStartPosX() {
        return startPosX;
    }
    public static int getStartPosY() {
        return startPosY;
    }
}
