package dev;

import dev.player.Player;
import mapUtilities.Door;
import mapUtilities.StaticObjects;
import mapUtilities.Wall;

import java.util.ArrayList;

public class Screen {

    private StaticObjects[][] map;
    private int screenWidth, screenHeight;
    private ArrayList<Textures> textures;

    public Screen(StaticObjects[][] map, ArrayList<Textures> textures, int screenWidth, int screenHeight) {
        this.map = map;
        this.textures = textures;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void update(Player player, int[] pixels) {

        for (int y = 0; y < screenHeight; y++) {
            double rayDirX0 = player.getDirX() - player.getPlaneX();
            double rayDirX1 = player.getDirX() + player.getPlaneX();
            double rayDirY0 = player.getDirY() - player.getPlaneY();
            double rayDirY1 = player.getDirY() + player.getPlaneY();

            int actualY = y - screenHeight / 2;

            double cameraPosV = 0.5 * screenHeight;
            double rowDistance = cameraPosV / actualY;

            double floorStepX = rowDistance * (rayDirX1 - rayDirX0) / screenWidth;
            double floorStepY = rowDistance * (rayDirY1 - rayDirY0) / screenWidth;

            double floorX = player.getPosX() + rowDistance * rayDirX0;
            double floorY = player.getPosY() + rowDistance * rayDirY0;
            int texX, texY;

            for (int x = 0; x < screenWidth; ++x) {
                int cellX = (int) floorX;
                int cellY = (int) floorY;

                int texSize = Textures.basicFloor.getSIZE();

                int color;
                if ((x + y * screenWidth) < pixels.length / 2) {
                    // Ceiling
                    texX = (int) (texSize * (floorX - 2 * player.getPosX() - cellX)) & (texSize - 1);
                    texY = (int) (texSize * (floorY - 2 * player.getPosY() - cellY)) & (texSize - 1);
                    color = Textures.basicCeiling.getPixels()[texSize * texY + texX];
                    pixels[x + y * screenWidth] = color;
                } else {
                    // Floor
                    texX = (int) (texSize * (floorX - cellX)) & (texSize - 1);
                    texY = (int) (texSize * (floorY - cellY)) & (texSize - 1);
                    color = Textures.basicFloor.getPixels()[texSize * texY + texX];
                    pixels[x + y * screenWidth] = color;
                }

                floorX += floorStepX;
                floorY += floorStepY;
            }
        }

        for (int x = 0; x < screenWidth; x++) {
            double cameraX = (2 * x) / ((double) screenWidth) - 1;
            double rayDirX = player.getDirX() + player.getPlaneX() * cameraX;
            double rayDirY = player.getDirY() + player.getPlaneY() * cameraX;

            int mapX = (int) player.getPosX();
            int mapY = (int) player.getPosY();

            double sideDistX, sideDistY;
            double rayLength;
            int stepX, stepY;

            // deltaDist calculated after simplification
            double deltaDistX = Math.abs(1 / rayDirX);
            double deltaDistY = Math.abs(1 / rayDirY);

            boolean rayHit = false;
            int wallSide = 0; //V or H
            int wallHeight;

            if (rayDirX >= 0) {
                stepX = 1;
                sideDistX = (1 - (player.getPosX() - mapX)) * deltaDistX;
            } else {
                stepX = -1;
                sideDistX = (player.getPosX() - mapX) * deltaDistX;
            }
            if (rayDirY >= 0) {
                stepY = 1;
                sideDistY = (1 - (player.getPosY() - mapY)) * deltaDistY;
            } else {
                stepY = -1;
                sideDistY = (player.getPosY() - mapY) * deltaDistY;
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

                if (map[mapX][mapY] != null) {
                    rayHit = true;
                }
            }

            if (wallSide == 0) {
                rayLength = Math.abs((mapX - player.getPosX() + ((double) (1 - stepX)) / 2) / rayDirX);
            } else {
                rayLength = Math.abs((mapY - player.getPosY() + ((double) (1 - stepY)) / 2) / rayDirY);
            }

            if (rayLength > 0) {
                wallHeight = Math.abs((int) (screenHeight / rayLength));
            } else {
                wallHeight = screenHeight;
            }
            // Its when you dont see whole wall
            int drawStart = -wallHeight / 2 + screenHeight / 2;
            int drawEnd = wallHeight / 2 + screenHeight / 2;

            if (drawStart < 0) {
                drawStart = 0;
            }
            if (drawEnd >= screenHeight) {
                drawEnd = screenHeight - 1;
            }

            int texNum = -1;

            if (map[mapX][mapY] != null) {
                if (map[mapX][mapY] instanceof Wall) {
                    texNum = 0;
                } else if (map[mapX][mapY] instanceof Door) {
                    texNum = 1;
                }
            }

            double wallHitPos;

            if (wallSide == 1) {
                wallHitPos = (player.getPosX() + ((mapY - player.getPosY() + ((double) (1 - stepY) / 2)) / rayDirY) * rayDirX);
            } else {
                wallHitPos = (player.getPosY() + ((mapX - player.getPosX() + ((double) (1 - stepX) / 2)) / rayDirX) * rayDirY);
            }

            wallHitPos -= Math.floor(wallHitPos);

            int textureX = (int) (wallHitPos * (textures.get(texNum).getSIZE()));

            if ((wallSide == 0 && rayDirX > 0) || (wallSide == 1 && rayDirY < 0)) {
                textureX = textures.get(texNum).getSIZE() - textureX - 1;
            }

            for (int y = drawStart; y < drawEnd; y++) {
                int textureY = (((2 * y - screenHeight + wallHeight) << 6) / wallHeight) / 2;
                int color;

                color = textures.get(texNum).getPixels()[textureX + (textureY * textures.get(texNum).getSIZE())];
                pixels[x + y * screenWidth] = color;
            }
        }
    }
}