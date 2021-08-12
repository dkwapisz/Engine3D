package dev.player;

import dev.Game;
import mapUtilities.ButtonWall;
import mapUtilities.doors.BasicDoor;
import mapUtilities.StaticObjects;

import java.awt.*;


public class Player {

    private StaticObjects[][] map;
    private double posX, posY, dirX, dirY, planeX, planeY;
    private final Controls controls;
    private final double MOVE_SPEED = 0.04;
    private final double ROTATION_SPEED = 0.045;
    private int lastMouseMove = MouseInfo.getPointerInfo().getLocation().x;

    public Player(double posX, double posY, double dirX, double dirY, double planeX, double planeY, StaticObjects[][] map) {
        this.posX = posX;
        this.posY = posY;
        this.dirX = dirX;
        this.dirY = dirY;
        this.planeX = planeX;
        this.planeY = planeY;
        this.map = map;
        controls = new Controls();
    }

    public void movementUpdate() {
        if (controls.isForward()) {
            if (map[(int) (posX + dirX * MOVE_SPEED)][(int) posY] == null ||
                checkIfDoorOpen((int) (posX + dirX * MOVE_SPEED), (int) posY)) {
                posX += dirX * MOVE_SPEED;
            }
            if (map[(int) posX][(int) (posY + dirY * MOVE_SPEED)] == null ||
                checkIfDoorOpen((int) posX, (int) (posY + dirY * MOVE_SPEED))) {
                posY += dirY * MOVE_SPEED;
            }
        }
        if (controls.isBackward()) {
            if (map[(int) (posX - dirX * MOVE_SPEED)][(int) posY] == null ||
                checkIfDoorOpen((int) (posX - dirX * MOVE_SPEED), (int) posY)) {
                posX -= dirX * MOVE_SPEED;
            }
            if (map[(int) posX][(int) (posY - dirY * MOVE_SPEED)] == null ||
                checkIfDoorOpen((int) posX, (int) (posY - dirY * MOVE_SPEED))) {
                posY -= dirY * MOVE_SPEED;
            }
        }
        if (controls.isRight()) {
            //TODO Crash when going to corners
            if (map[(int) (posX + (dirX * Math.cos(-Math.PI / 2) - dirY * Math.sin(-Math.PI / 2)) * MOVE_SPEED)][(int) posY] == null ||
                checkIfDoorOpen((int) (posX + (dirX * Math.cos(-Math.PI / 2) - dirY * Math.sin(-Math.PI / 2)) * MOVE_SPEED), (int) posY)) {
                posX += (dirX * Math.cos(-Math.PI / 2) - dirY * Math.sin(-Math.PI / 2)) * MOVE_SPEED;
            }
            if (map[(int) posX][(int) (posY + (dirX * Math.sin(-Math.PI / 2) + dirY * Math.cos(-Math.PI / 2)) * MOVE_SPEED)] == null ||
                checkIfDoorOpen((int) posX, (int) (posY + (dirX * Math.sin(-Math.PI / 2) + dirY * Math.cos(-Math.PI / 2)) * MOVE_SPEED))) {
                posY += (dirX * Math.sin(-Math.PI / 2) + dirY * Math.cos(-Math.PI / 2)) * MOVE_SPEED;
            }
        }
        if (controls.isLeft()) {
            if (map[(int) (posX + (dirX * Math.cos(Math.PI / 2) - dirY * Math.sin(Math.PI / 2)) * MOVE_SPEED)][(int) posY] == null ||
                checkIfDoorOpen((int) (posX + (dirX * Math.cos(Math.PI / 2) - dirY * Math.sin(Math.PI / 2)) * MOVE_SPEED), (int) posY)) {
                posX += (dirX * Math.cos(Math.PI / 2) - dirY * Math.sin(Math.PI / 2)) * MOVE_SPEED;
            }
            if (map[(int) posX][(int) (posY + (dirX * Math.sin(Math.PI / 2) + dirY * Math.cos(Math.PI / 2)) * MOVE_SPEED)] == null ||
                checkIfDoorOpen((int) posX, (int) (posY + (dirX * Math.sin(Math.PI / 2) + dirY * Math.cos(Math.PI / 2)) * MOVE_SPEED))) {
                posY += (dirX * Math.sin(Math.PI / 2) + dirY * Math.cos(Math.PI / 2)) * MOVE_SPEED;
            }

        }
        if (controls.isRotRight()) {
            double oldDirX = dirX;
            double oldPlaneX = planeX;
            dirX = dirX * Math.cos(-ROTATION_SPEED) - dirY * Math.sin(-ROTATION_SPEED);
            dirY = oldDirX * Math.sin(-ROTATION_SPEED) + dirY * Math.cos(-ROTATION_SPEED);
            planeX = planeX * Math.cos(-ROTATION_SPEED) - planeY * Math.sin(-ROTATION_SPEED);
            planeY = oldPlaneX * Math.sin(-ROTATION_SPEED) + planeY * Math.cos(-ROTATION_SPEED);
        }
        if (controls.isRotLeft()) {
            double oldDirX = dirX;
            double oldPlaneX = planeX;
            dirX = dirX * Math.cos(ROTATION_SPEED) - dirY * Math.sin(ROTATION_SPEED);
            dirY = oldDirX * Math.sin(ROTATION_SPEED) + dirY * Math.cos(ROTATION_SPEED);
            planeX = planeX * Math.cos(ROTATION_SPEED) - planeY * Math.sin(ROTATION_SPEED);
            planeY = oldPlaneX * Math.sin(ROTATION_SPEED) + planeY * Math.cos(ROTATION_SPEED);
        }
        //TODO Mouse movement
//        if (lastMouseMove != controls.ifMouseMoved()) {
//            if (lastMouseMove < controls.ifMouseMoved()) {
//                double oldDirX = dirX;
//                double oldPlaneX = planeX;
//                dirX = dirX * Math.cos(-ROTATION_SPEED) - dirY * Math.sin(-ROTATION_SPEED);
//                dirY = oldDirX * Math.sin(-ROTATION_SPEED) + dirY * Math.cos(-ROTATION_SPEED);
//                planeX = planeX * Math.cos(-ROTATION_SPEED) - planeY * Math.sin(-ROTATION_SPEED);
//                planeY = oldPlaneX * Math.sin(-ROTATION_SPEED) + planeY * Math.cos(-ROTATION_SPEED);
//            }
//            if (lastMouseMove > controls.ifMouseMoved()) {
//                double oldDirX = dirX;
//                double oldPlaneX = planeX;
//                dirX = dirX * Math.cos(ROTATION_SPEED) - dirY * Math.sin(ROTATION_SPEED);
//                dirY = oldDirX * Math.sin(ROTATION_SPEED) + dirY * Math.cos(ROTATION_SPEED);
//                planeX = planeX * Math.cos(ROTATION_SPEED) - planeY * Math.sin(ROTATION_SPEED);
//                planeY = oldPlaneX * Math.sin(ROTATION_SPEED) + planeY * Math.cos(ROTATION_SPEED);
//            }
//        }

        if (controls.isOpenDoor()) {
            useAction();
        }
    }

    private void useAction() {
        int playerVecX = (int) (posX + dirX);
        int playerVecY = (int) (posY + dirY);

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] instanceof BasicDoor && !((BasicDoor) map[x][y]).isButtonDoor()) {
                    if ((new Rectangle(playerVecX, playerVecY,1,1)).intersects(new Rectangle(x, y, 1, 1))) {
                        ((BasicDoor) map[x][y]).open();
                    }
                }
                if (map[x][y] instanceof ButtonWall) {
                    if ((new Rectangle(playerVecX, playerVecY,1,1)).intersects(new Rectangle(x, y, 1, 1))) {
                        Game.getButtonGroup().get((ButtonWall) map[x][y]).open();
                    }
                }
            }
        }
    }

    private boolean checkIfDoorOpen(int x, int y) {
        return (map[x][y] instanceof BasicDoor && ((BasicDoor) map[x][y]).isOpened());
    }

    public double getPosX() {
        return posX;
    }
    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }
    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getDirX() {
        return dirX;
    }
    public void setDirX(double dirX) {
        this.dirX = dirX;
    }

    public double getDirY() {
        return dirY;
    }
    public void setDirY(double dirY) {
        this.dirY = dirY;
    }

    public double getPlaneX() {
        return planeX;
    }
    public void setPlaneX(double planeX) {
        this.planeX = planeX;
    }

    public double getPlaneY() {
        return planeY;
    }
    public void setPlaneY(double planeY) {
        this.planeY = planeY;
    }

    public Controls getControls() {
        return controls;
    }
}