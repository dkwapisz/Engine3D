package dev;

import dev.player.Player;
import mapUtilities.ButtonWall;
import mapUtilities.ExitFloor;
import mapUtilities.doors.BasicDoor;
import mapUtilities.StaticObjects;
import mapUtilities.Wall;

import java.util.ArrayList;

public class Screen {

    private final StaticObjects[][] map;
    private final StaticObjects[][] mapWithoutDoor;
    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;
    private final ArrayList<Textures> textures;

    public Screen(StaticObjects[][] map, ArrayList<Textures> textures, int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        this.map = map;
        this.textures = textures;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;

        mapWithoutDoor = new StaticObjects[map.length][map[0].length];

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] instanceof BasicDoor) {
                    mapWithoutDoor[i][j] = null;
                } else {
                    mapWithoutDoor[i][j] = map[i][j];
                }
            }
        }
    }

    public void update(Player player, int[] pixels) {
        //Fixed order of calls
        //TODO Ceiling under doors -> when door is opening
        updateFloorAndCeiling(player, pixels);
        //Must call twice, first to remember walls, next to draw door correctly
        updateWalls(player, pixels, false);
        updateWalls(player, pixels, true);
    }

    public void updateFloorAndCeiling(Player player, int[] pixels) {
        for (int y = 0; y < SCREEN_HEIGHT; y++) {
            double rayDirX0 = player.getDirX() - player.getPlaneX();
            double rayDirX1 = player.getDirX() + player.getPlaneX();
            double rayDirY0 = player.getDirY() - player.getPlaneY();
            double rayDirY1 = player.getDirY() + player.getPlaneY();

            int actualY = y - SCREEN_HEIGHT / 2;

            double cameraPosV = 0.5 * SCREEN_HEIGHT;
            double rowDistance = cameraPosV / actualY;

            double floorStepX = rowDistance * (rayDirX1 - rayDirX0) / SCREEN_WIDTH;
            double floorStepY = rowDistance * (rayDirY1 - rayDirY0) / SCREEN_WIDTH;

            double floorX = player.getPosX() + rowDistance * rayDirX0;
            double floorY = player.getPosY() + rowDistance * rayDirY0;
            int texX, texY;

            for (int x = 0; x < SCREEN_WIDTH; ++x) {
                int cellX = (int) floorX;
                int cellY = (int) floorY;

                int texSize = Textures.basicFloor.getSIZE();

                int color;
                if ((x + y * SCREEN_WIDTH) < pixels.length / 2) {
                    // Ceiling
                    texX = (int) (texSize * (floorX - 2 * player.getPosX() - cellX)) & (texSize - 1);
                    texY = (int) (texSize * (floorY - 2 * player.getPosY() - cellY)) & (texSize - 1);
                    color = Textures.basicCeiling.getPixels()[texSize * texY + texX];
                } else {
                    // Floor
                    texX = (int) (texSize * (floorX - cellX)) & (texSize - 1);
                    texY = (int) (texSize * (floorY - cellY)) & (texSize - 1);
                    color = Textures.basicFloor.getPixels()[texSize * texY + texX];

                    //TODO ExitFloor
//                    if (cellX == Game.getExitPos()[0] && cellY == Game.getExitPos()[1]) {
//                        color = Textures.exitFloor.getPixels()[texSize * texY + texX];
//                    }
                }

                pixels[x + y * SCREEN_WIDTH] = color;

                floorX += floorStepX;
                floorY += floorStepY;
            }
        }
    }

    public void updateWalls(Player player, int[] pixels, boolean withDoor) {
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            double cameraX = (2 * x) / ((double) SCREEN_WIDTH) - 1;
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

                if (withDoor) {
                    if (map[mapX][mapY] != null) {
                        rayHit = true;
                    }
                } else {
                    if (mapWithoutDoor[mapX][mapY] != null) {
                        rayHit = true;
                    }
                }

            }

            if (wallSide == 0) {
                rayLength = Math.abs((mapX - player.getPosX() + ((double) (1 - stepX)) / 2) / rayDirX);
            } else {
                rayLength = Math.abs((mapY - player.getPosY() + ((double) (1 - stepY)) / 2) / rayDirY);
            }

            if (rayLength > 0) {
                wallHeight = Math.abs((int) (SCREEN_HEIGHT / rayLength));
            } else {
                wallHeight = SCREEN_HEIGHT;
            }
            // Its when you dont see whole wall
            int drawStart = -wallHeight / 2 + SCREEN_HEIGHT / 2;
            int drawEnd = wallHeight / 2 + SCREEN_HEIGHT / 2;
            // When you see whole wall
            if (drawStart < 0) {
                drawStart = 0;
            }
            if (drawEnd >= SCREEN_HEIGHT) {
                drawEnd = SCREEN_HEIGHT - 1;
            }

            if (withDoor) {
                if (map[mapX][mapY] instanceof BasicDoor && ((BasicDoor) map[mapX][mapY]).isMoving()) {
                    double tempDrawEnd = (double) wallHeight / 2 + (double) SCREEN_HEIGHT / 2;
                    double doorPercent = ((double) ((BasicDoor) map[mapX][mapY]).getDoorProgress()) / 100;
                    tempDrawEnd -= (double) wallHeight * doorPercent;
                    drawEnd = (int) tempDrawEnd;

                    if (drawEnd >= SCREEN_HEIGHT) {
                        drawEnd = SCREEN_HEIGHT - 1;
                    }
                }
            }

            int texNum;
            //TODO ButtonWall only on one side in Wall
            if (map[mapX][mapY] instanceof Wall) {
                texNum = 0;
            } else if (map[mapX][mapY] instanceof BasicDoor) {
                texNum = 1;
            } else if (map[mapX][mapY] instanceof ButtonWall && !(((ButtonWall) map[mapX][mapY]).isClicked())) {
                texNum = 2;
            } else if (map[mapX][mapY] instanceof ButtonWall && (((ButtonWall) map[mapX][mapY]).isClicked())) {
                texNum = 3;
            } else {
                texNum = 0;
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
                int textureY = (((2 * y - SCREEN_HEIGHT + wallHeight) << 8) / wallHeight) / 2;
                int color = 0;

                if (mapX < map.length - 1 && mapY < map.length - 1 && withDoor) {
                    if (map[mapX][mapY] instanceof BasicDoor && ((BasicDoor) map[mapX][mapY]).isMoving()) {
                        textureY += ((BasicDoor) map[mapX][mapY]).getDoorProgress()*2.5;
                    }
                }

                try {
                    color = textures.get(texNum).getPixels()[textureX + (textureY * textures.get(texNum).getSIZE())];
                } catch (ArrayIndexOutOfBoundsException ignored) {}

                pixels[x + y * SCREEN_WIDTH] = color;
            }
        }
    }
}