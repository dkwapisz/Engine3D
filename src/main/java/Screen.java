import java.util.ArrayList;

public class Screen {
    private int[][] map;
    private int mapWidth, mapHeight, width, height;
    private ArrayList<Textures> textures;

    public Screen(int[][] map, int mapWidth, int mapHeight, ArrayList<Textures> textures, int width, int height) {
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.textures = textures;
        this.width = width;
        this.height = height;
    }

    public void update(PlayerHandling playerHandling, int[] pixels) {

        for (int y = 0; y < height; y++) {
            double rayDirX0 = playerHandling.getDirX() - playerHandling.getPlaneX();
            double rayDirX1 = playerHandling.getDirX() + playerHandling.getPlaneX();
            double rayDirY0 = playerHandling.getDirY() - playerHandling.getPlaneY();
            double rayDirY1 = playerHandling.getDirY() + playerHandling.getPlaneY();

            int actualY = y - height / 2;

            double cameraPosV = 0.5 * height;
            double rowDistance = cameraPosV / actualY;

            double floorStepX = rowDistance * (rayDirX1 - rayDirX0) / width;
            double floorStepY = rowDistance * (rayDirY1 - rayDirY0) / width;

            double floorX = playerHandling.getPosX() + rowDistance * rayDirX0;
            double floorY = playerHandling.getPosY() + rowDistance * rayDirY0;

            for (int x = 0; x < width; ++x) {
                int cellX = (int) floorX;
                int cellY = (int) floorY;

                int texNum = 1; // tekstura znajduję się na miejscu 1 w ArrayList
                int texSize = Textures.basicFloor.getSIZE();
                int texX = (int) (texSize * (floorX - cellX)) & (texSize - 1);
                int texY = (int) (texSize * (floorY - cellY)) & (texSize - 1);

                floorX += floorStepX;
                floorY += floorStepY;

                int color;
                if ((x + y * width) > pixels.length / 2) {
                    // Floor
                    color = Textures.basicFloor.getPixels()[texSize * texY + texX];
                    pixels[x + y * width] = color;
                } else {
                    // Ceiling
                    color = Textures.basicCeiling.getPixels()[texSize * texY + texX];
                    pixels[x + y * width] = color;
                }
            }
        }

        for (int x = 0; x < width; x++) {
            double cameraX = (2 * x) / ((double) width) - 1;
            double rayDirX = playerHandling.getDirX() + playerHandling.getPlaneX() * cameraX;
            double rayDirY = playerHandling.getDirY() + playerHandling.getPlaneY() * cameraX;

            int mapX = (int) playerHandling.getPosX();
            int mapY = (int) playerHandling.getPosY();

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
                sideDistX = (1 - (playerHandling.getPosX() - mapX)) * deltaDistX;
            } else {
                stepX = -1;
                sideDistX = (playerHandling.getPosX() - mapX) * deltaDistX;
            }
            if (rayDirY >= 0) {
                stepY = 1;
                sideDistY = (1 - (playerHandling.getPosY() - mapY)) * deltaDistY;
            } else {
                stepY = -1;
                sideDistY = (playerHandling.getPosY() - mapY) * deltaDistY;
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
                rayLength = Math.abs((mapX - playerHandling.getPosX() + ((double) (1 - stepX)) / 2) / rayDirX);
            } else {
                rayLength = Math.abs((mapY - playerHandling.getPosY() + ((double) (1 - stepY)) / 2) / rayDirY);
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

            int texNum = 0; // tekstura znajduję się na miejscu 0 w ArrayList
            double wallHitPos;

            if (wallSide == 1) {
                wallHitPos = (playerHandling.getPosX() + ((mapY - playerHandling.getPosY() + ((double) (1 - stepY) / 2)) / rayDirY) * rayDirX);
            } else {
                wallHitPos = (playerHandling.getPosY() + ((mapX - playerHandling.getPosX() + ((double) (1 - stepX) / 2)) / rayDirX) * rayDirY);
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
                    color = (textures.get(texNum).getPixels()[textureX + (textureY * textures.get(texNum).getSIZE())]);
                }
                pixels[x + y * (width)] = color;
            }
        }
    }
}