import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class MapReading {

    public static int[][] getMap() {
        BufferedImage mapImage = loadImage("src/main/resources/maps/map0.png");
        int[][] map = new int[mapImage.getWidth()][mapImage.getHeight()];
        int freeSpace = new Color(255, 255, 255).getRGB();
        int basicWall = new Color(0, 0, 0).getRGB();

        for (int x = 0; x < mapImage.getWidth(); x++) {
            for (int y = 0; y < mapImage.getHeight(); y++) {

                int currentPixel = mapImage.getRGB(x, y);

                if (currentPixel == freeSpace) {
                    map[x][y] = 0;
                }
                else if (currentPixel == basicWall) {
                    map[x][y] = 1;
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
