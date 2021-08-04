import java.util.ArrayList;
import java.awt.Color;

public class Screen {
    public int[][] map;
    public int mapWidth, mapHeight, width, height;
    public ArrayList<Textures> textures;

    public Screen(int[][] map, int mapWidth, int mapHeight, ArrayList<Textures> textures, int width, int height) {
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.textures = textures;
        this.width = width;
        this.height = height;
    }

    public void update(Camera camera, int[] pixels) {

        for (int i = 0; i < pixels.length; i++) {
            // Paint ceiling
            if (pixels[i] != Color.DARK_GRAY.getRGB() && i < pixels.length/2) {
                pixels[i] = Color.DARK_GRAY.getRGB();
            }
            // Paint floor
            if (pixels[i] != Color.GRAY.getRGB() && i >= pixels.length/2) {
                pixels[i] = Color.GRAY.getRGB();
            }
        }

        for (int x = 0; x < width; x++) {
            double cameraX = (2 * x) / ((double) width) - 1;
            double rayDirX = camera.getDirX() + camera.getPlaneX() * cameraX;
            double rayDirY = camera.getDirY() + camera.getPlaneY() * cameraX;

            int mapX = (int) camera.getPosX();
            int mapY = (int) camera.getPosY();

            double sideDistX, sideDistY;
            double rayLength;
            int stepX, stepY;

            // deltaDist calculated after simplification
            double deltaDistX = Math.abs(1/rayDirX);
            double deltaDistY = Math.abs(1/rayDirY);

            boolean rayHit = false;
            int wallSide = 0; //V or H
            int wallHeight;

            if (rayDirX >= 0) {
                stepX = 1;
                sideDistX = (1 - (camera.getPosX() - mapX)) * deltaDistX;
            } else {
                stepX = -1;
                sideDistX = (camera.getPosX() - mapX) * deltaDistX;
            }
            if (rayDirY >= 0) {
                stepY = 1;
                sideDistY = (1 - (camera.getPosY() - mapY)) * deltaDistY;
            } else {
                stepY = -1;
                sideDistY = (camera.getPosY() - mapY) * deltaDistY;
            }

            while (!rayHit) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    wallSide = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    wallSide = 1;
                }

                if (map[mapX][mapY] > 0) {
                    rayHit = true;
                }
            }

            if (wallSide == 0) {
                rayLength = Math.abs((mapX - camera.getPosX() + ((double) (1 - stepX)) / 2) / rayDirX);
            } else {
                rayLength = Math.abs((mapY - camera.getPosY() + ((double) (1 - stepY)) / 2) / rayDirY);
            }

            if (rayLength > 0) {
                wallHeight = Math.abs((int) (height / rayLength));
            } else {
                wallHeight = height;
            }

            int drawStart = -wallHeight/2 + height/2;
            int drawEnd = wallHeight/2 + height/2;

            if (drawStart < 0) {
                drawStart = 0;
            }
            if (drawEnd >= height) {
                drawEnd = height - 1;
            }

            int texNum = map[mapX][mapY] - 1;
            double wallHitPos;

            if (wallSide == 1) {
                wallHitPos = (camera.getPosX() + ((mapY - camera.getPosY() + ((double) (1 - stepY) / 2)) / rayDirY) * rayDirX);
            } else {
                wallHitPos = (camera.getPosY() + ((mapX - camera.getPosX() + ((double) (1 - stepX) / 2)) / rayDirX) * rayDirY);
            }

            wallHitPos -= Math.floor(wallHitPos);

            int textureX = (int) (wallHitPos * (textures.get(texNum).getSIZE()));

            if ((wallSide == 0 && rayDirX > 0) || (wallSide == 1 && rayDirY < 0)) {
                textureX = textures.get(texNum).getSIZE() - textureX - 1;
            }

            for (int y = drawStart; y < drawEnd; y++) {
                int textureY = (((2*y - height + wallHeight) << 6) / wallHeight) / 2;
                int color;

                if (wallSide == 0) {
                    color = textures.get(texNum).getPixels()[textureX + (textureY * textures.get(texNum).getSIZE())];
                } else {
                    color = (textures.get(texNum).getPixels()[textureX + (textureY * textures.get(texNum).getSIZE())] >> 1) & 8355711;
                }
                pixels[x + y*(width)] = color;
            }
        }
    }
}