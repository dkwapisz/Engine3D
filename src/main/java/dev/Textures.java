package dev;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Textures {
    private int[] pixels;
    private String location;
    private final int SIZEX;
    private final int SIZEY;

    public Textures(String location, int sizeX, int sizeY) {
        this.location = location;
        this.SIZEX = sizeX;
        this.SIZEY = sizeY;
        this.pixels = new int[sizeX * sizeY];
        load();
    }

    private void load() {
        try {
            BufferedImage image = ImageIO.read(new File(location));
            int w = image.getWidth();
            int h = image.getHeight();
            image.getRGB(0, 0, w, h, pixels, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getPixels() {
        return pixels;
    }
    public int getSIZEX() {
        return SIZEX;
    }
    public int getSIZEY() {
        return SIZEY;
    }

    public static Textures basicWall = new Textures("src/main/resources/graphics/basicWall.png", 256, 256);
    public static Textures basicDoor = new Textures("src/main/resources/graphics/basicDoor.png", 256, 256);
    public static Textures buttonWall = new Textures("src/main/resources/graphics/buttonWall.png", 256, 256);

    public static Textures basicFloor = new Textures("src/main/resources/graphics/basicFloor.png", 256, 256);
    public static Textures basicCeiling = new Textures("src/main/resources/graphics/basicCeiling.png", 256, 256);
}