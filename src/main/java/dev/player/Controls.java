package dev.player;

import dev.View2D;

import java.awt.*;
import java.awt.event.*;

public class Controls implements KeyListener, MouseMotionListener {

    private boolean left, right, forward, backward, rotLeft, rotRight, openDoor;
    private int mouseMove = MouseInfo.getPointerInfo().getLocation().x;

    @Override
    public void keyPressed(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_LEFT) {
            rotLeft = true;
        }
        if (key.getKeyCode() == KeyEvent.VK_RIGHT) {
            rotRight = true;
        }
        if (key.getKeyCode() == KeyEvent.VK_W) {
            forward = true;
        }
        if (key.getKeyCode() == KeyEvent.VK_S) {
            backward = true;
        }
        if (key.getKeyCode() == KeyEvent.VK_A) {
            left = true;
        }
        if (key.getKeyCode() == KeyEvent.VK_D) {
            right = true;
        }
        if (key.getKeyCode() == KeyEvent.VK_SPACE) {
            openDoor = true;
        }

        if (key.getKeyCode() == KeyEvent.VK_M) {
            View2D.getView2Dframe().setVisible(!View2D.getView2Dframe().isVisible());
        }
        if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_LEFT) {
            rotLeft = false;
        }
        if (key.getKeyCode() == KeyEvent.VK_RIGHT) {
            rotRight = false;
        }
        if (key.getKeyCode() == KeyEvent.VK_W) {
            forward = false;
        }
        if (key.getKeyCode() == KeyEvent.VK_S) {
            backward = false;
        }
        if (key.getKeyCode() == KeyEvent.VK_A) {
            left = false;
        }
        if (key.getKeyCode() == KeyEvent.VK_D) {
            right = false;
        }
        if (key.getKeyCode() == KeyEvent.VK_SPACE) {
            openDoor = false;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseMove = MouseInfo.getPointerInfo().getLocation().x;
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        //nothing here
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //nothing here
    }

    public int ifMouseMoved() {
        return mouseMove;
    }
    public boolean isLeft() {
        return left;
    }
    public boolean isRight() {
        return right;
    }
    public boolean isForward() {
        return forward;
    }
    public boolean isBackward() {
        return backward;
    }
    public boolean isRotLeft() {
        return rotLeft;
    }
    public boolean isRotRight() {
        return rotRight;
    }
    public boolean isOpenDoor() {
        return openDoor;
    }


}
