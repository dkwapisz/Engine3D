package dev;

import dev.player.Player;
import mapUtilities.Door;
import mapUtilities.StaticObjects;
import mapUtilities.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Screen {

    private StaticObjects[][] map;
    private StaticObjects[][] mapWithoutDoor;
    private ArrayList<Integer> doorPositions;
    private int screenWidth, screenHeight;
    private ArrayList<Textures> textures;
    private double[] ZBuffer;
    double tempPlaneX, tempPlaneY, tempDirX, tempDirY;
//    double[] sprite = new double[] {4, 2};
//    int[] spriteOrder = new int[sprite.length];
//    double[] spriteDistance = new double[sprite.length];

    public Screen(StaticObjects[][] map, ArrayList<Textures> textures, int screenWidth, int screenHeight) {
        this.map = map;
        this.textures = textures;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        ZBuffer = new double[screenWidth];

        mapWithoutDoor = new StaticObjects[map.length][map[0].length];
        doorPositions = new ArrayList<>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] instanceof Door) {
                    mapWithoutDoor[i][j] = null;
                    doorPositions.add(i);
                    doorPositions.add(j);
                } else {
                    mapWithoutDoor[i][j] = map[i][j];
                }
            }
        }
    }

    public void update(Player player, int[] pixels) {
        //Fixed order of calls
        updateFloorAndCeiling(player, pixels);
        updateWalls(player, pixels);
        updateDoor(player, pixels);
    }

    public void updateFloorAndCeiling(Player player, int[] pixels) {
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
                } else {
                    // Floor
                    texX = (int) (texSize * (floorX - cellX)) & (texSize - 1);
                    texY = (int) (texSize * (floorY - cellY)) & (texSize - 1);
                    color = Textures.basicFloor.getPixels()[texSize * texY + texX];
                }

                pixels[x + y * screenWidth] = color;

                floorX += floorStepX;
                floorY += floorStepY;
            }
        }
    }

    public void updateWalls(Player player, int[] pixels) {
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

                if (mapWithoutDoor[mapX][mapY] != null) {
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
            // When you see whole wall
            if (drawStart < 0) {
                drawStart = 0;
            }
            if (drawEnd >= screenHeight) {
                drawEnd = screenHeight - 1;
            }

            int texNum = -1;

            if (mapWithoutDoor[mapX][mapY] != null) {
                if (mapWithoutDoor[mapX][mapY] instanceof Wall) {
                    texNum = 0;
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
                int textureY = (((2 * y - screenHeight + wallHeight) << 8) / wallHeight) / 2;
                int color = textures.get(texNum).getPixels()[textureX + (textureY * textures.get(texNum).getSIZE())];
                pixels[x + y * screenWidth] = color;
            }

            ZBuffer[x] = rayLength; //perpendicular distance is used
        }
    }

    public void updateDoor(Player player, int[] pixels) {
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
                if (mapX < 0 || mapX >= map.length - 1 || mapY < 0 || mapY >= map.length - 1) {
                    break;
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
            // When you see whole wall
            if (drawStart < 0) {
                drawStart = 0;
            }
            if (drawEnd >= screenHeight) {
                drawEnd = screenHeight - 1;
            }

            if (map[mapX][mapY] instanceof Door && ((Door) map[mapX][mapY]).isMoving()) {
                double tempDrawEnd = wallHeight / 2 + screenHeight / 2;
                double doorPercent = ((double) ((Door) map[mapX][mapY]).getDoorProgress()) / 100;
                tempDrawEnd -= (double) wallHeight * doorPercent;
                drawEnd = (int) tempDrawEnd;

                if (drawEnd >= screenHeight) {
                    drawEnd = screenHeight - 1;
                }
            }


            int texNum = 1;

            if (map[mapX][mapY] instanceof Wall) {
                texNum = 0;
            } else if (map[mapX][mapY] instanceof Door) {
                texNum = 1;
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
                int textureY = (((2 * y - screenHeight + wallHeight) << 8) / wallHeight) / 2;
                int color = 0;

                if (mapX >= 0 && mapX < map.length - 1 && mapY >= 0 && mapY < map.length - 1) {
                    if (map[mapX][mapY] instanceof Door && ((Door) map[mapX][mapY]).isMoving()) {
                        textureY += ((Door) map[mapX][mapY]).getDoorProgress()*2.5;
                    }
                } else {
                    break;
                }

                try {
                    color = textures.get(texNum).getPixels()[textureX + (textureY * textures.get(texNum).getSIZE())];
                } catch (ArrayIndexOutOfBoundsException ignored) {}

                pixels[x + y * screenWidth] = color;
            }

            ZBuffer[x] = rayLength; //perpendicular distance is used
        }
    }

    public void updateSprites(Player player, int[] pixels) {
        for (int i = 0; i < doorPositions.size(); i+=2) {

            double invDet = 0, transformX = 0, transformY = 0;

            double spriteX = doorPositions.get(i) + 0.5 - player.getPosX();
            double spriteY = doorPositions.get(i+1) + 0.5 - player.getPosY();

            invDet = 1 / (player.getPlaneX() * player.getDirY() - player.getDirX() * player.getPlaneY());

            transformX = invDet * (player.getDirY() * spriteX - player.getDirX() * spriteY);
            transformY = invDet * (-player.getPlaneY() * spriteX + player.getPlaneX() * spriteY);

            int spriteScreenX = (int) ((screenWidth/2) * (1 + transformX/transformY));
            int spriteHeight = Math.abs((int) (screenHeight / transformY));

            int drawStartY = -spriteHeight / 2 + screenHeight / 2;
            int drawEndY = spriteHeight / 2 + screenHeight / 2;

            if (drawStartY < 0) {
                drawStartY = 0;
            }
            if (drawEndY >= screenHeight) {
                drawEndY = screenHeight - 1;
            }

            int spriteWidth = Math.abs((int) (screenWidth / transformY));

            int drawStartX = -spriteWidth / 2 + spriteScreenX;
            int drawEndX = spriteWidth / 2 + spriteScreenX;

            if (drawStartX < 0) {
                drawStartX = 0;
            }
            if (drawEndX >= screenWidth) {
                drawEndX = screenWidth - 1;
            }

            if (map[doorPositions.get(i)][doorPositions.get(i+1)] instanceof Door && ((Door) map[doorPositions.get(i)][doorPositions.get(i+1)]).isMoving()) {
                double tempDrawEnd = spriteHeight / 2 + screenHeight / 2;
                double doorPercent = ((double) ((Door) map[doorPositions.get(i)][doorPositions.get(i + 1)]).getDoorProgress()) / 100;
                tempDrawEnd -= (double) spriteHeight * doorPercent;
                drawEndY = (int) tempDrawEnd;

                if (drawEndY >= screenHeight) {
                    drawEndY = screenHeight - 1;
                }
            }

            for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                int textureX = (int) (256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * textures.get(1).getSIZE() / spriteWidth) / 256;
                int doorProgress = ((Door) map[doorPositions.get(i)][doorPositions.get(i+1)]).getDoorProgress();
                if (transformY > 0 && stripe > 0 && stripe < screenWidth && transformY < ZBuffer[stripe]) {
                    for (int y = drawStartY; y < drawEndY; y++) {
                        int d = (y) * 256 - screenHeight * 128 + spriteHeight * 128;
                        int textureY = ((d * textures.get(1).getSIZE()) / spriteHeight) / 256;
                        int color = 0;
                        textureY += doorProgress*(2.5);
                        try {
                            color = textures.get(1).getPixels()[textureX + (textureY * textures.get(1).getSIZE())];
                        } catch (ArrayIndexOutOfBoundsException ignored) {}
                        pixels[stripe + y * screenWidth] = color;
                    }
                }
            }
        }
    }

    private double pointDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}