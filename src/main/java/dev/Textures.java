package dev;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Textures {
    private int[] pixels;
    private String location;
    private final int SIZE;

    public Textures(String location, int SIZE) {
        this.location = location;
        this.SIZE = SIZE;
        this.pixels = new int[SIZE * SIZE];
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
    public int getSIZE() {
        return SIZE;
    }

    public static Textures basicWall = new Textures("src/main/resources/graphics/basicWall.png", 256);
    public static Textures basicDoor = new Textures("src/main/resources/graphics/basicDoor.png", 256);
    public static Textures buttonWall = new Textures("src/main/resources/graphics/buttonWall.png", 256);
    public static Textures buttonWallActivated = new Textures("src/main/resources/graphics/buttonWallActivated.png", 256);

    public static Textures basicFloor = new Textures("src/main/resources/graphics/basicFloor.png", 256);
    public static Textures exitFloor = new Textures("src/main/resources/graphics/exitFloor.png", 256);

    public static Textures basicCeiling = new Textures("src/main/resources/graphics/basicCeiling.png", 256);
}