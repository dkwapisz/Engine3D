package dev.player;

import dev.View2D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Player {

    private double posX, posY, dirX, dirY, planeX, planeY;
    private final Controls controls;
    private final double MOVE_SPEED = 0.04;
    private final double ROTATION_SPEED = 0.045;

    public Player(double posX, double posY, double dirX, double dirY, double planeX, double planeY) {
        this.posX = posX;
        this.posY = posY;
        this.dirX = dirX;
        this.dirY = dirY;
        this.planeX = planeX;
        this.planeY = planeY;
        controls = new Controls();
    }

    public void movementUpdate(int[][] map) {
        if (controls.isForward()) {
            if (map[(int) (posX + dirX * MOVE_SPEED)][(int) posY] == 0) {
                posX += dirX * MOVE_SPEED;
            }
            if (map[(int) posX][(int) (posY + dirY * MOVE_SPEED)] == 0) {
                posY += dirY * MOVE_SPEED;
            }
        }
        if (controls.isBackward()) {
            if (map[(int) (posX - dirX * MOVE_SPEED)][(int) posY] == 0) {
                posX -= dirX * MOVE_SPEED;
            }
            if (map[(int) posX][(int) (posY - dirY * MOVE_SPEED)] == 0) {
                posY -= dirY * MOVE_SPEED;
            }
        }
        if (controls.isRight()) {
            if (map[(int) (posX + (dirX * Math.cos(-Math.PI / 2) - dirY * Math.sin(-Math.PI / 2)) * MOVE_SPEED)][(int) posY] == 0 ||
                map[(int) posX][(int) (posY + (dirX * Math.sin(-Math.PI / 2) + dirY * Math.cos(-Math.PI / 2)) * MOVE_SPEED)] == 0) {

                posX += (dirX * Math.cos(-Math.PI / 2) - dirY * Math.sin(-Math.PI / 2)) * MOVE_SPEED;
                posY += (dirX * Math.sin(-Math.PI / 2) + dirY * Math.cos(-Math.PI / 2)) * MOVE_SPEED;
            }
        }
        if (controls.isLeft()) {
            if (map[(int) (posX + (dirX * Math.cos(Math.PI / 2) - dirY * Math.sin(Math.PI / 2)) * MOVE_SPEED)][(int) posY] == 0 ||
                map[(int) posX][(int) (posY + (dirX * Math.sin(Math.PI / 2) + dirY * Math.cos(Math.PI / 2)) * MOVE_SPEED)] == 0) {

                posX += (dirX * Math.cos(Math.PI / 2) - dirY * Math.sin(Math.PI / 2)) * MOVE_SPEED;
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