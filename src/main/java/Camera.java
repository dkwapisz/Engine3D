import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Camera implements KeyListener {

    private double posX, posY, dirX, dirY, planeX, planeY;
    private boolean left, right, forward, backward, rotLeft, rotRight;
    private final double MOVE_SPEED = .04;
    private final double ROTATION_SPEED = .045;
    private View2D view2D;

    public Camera(double posX, double posY, double dirX, double dirY, double planeX, double planeY) {
        this.posX = posX;
        this.posY = posY;
        this.dirX = dirX;
        this.dirY = dirY;
        this.planeX = planeX;
        this.planeY = planeY;
    }

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
    }

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
    }

    public void update(int[][] map) {
        if (forward) {
            if (map[(int) (posX + dirX * MOVE_SPEED)][(int) posY] == 0) {
                posX += dirX * MOVE_SPEED;
            }
            if (map[(int) posX][(int) (posY + dirY * MOVE_SPEED)] == 0) {
                posY += dirY * MOVE_SPEED;
            }
        }
        if (backward) {
            if (map[(int) (posX - dirX * MOVE_SPEED)][(int) posY] == 0) {
                posX -= dirX * MOVE_SPEED;
            }
            if (map[(int) posX][(int) (posY - dirY * MOVE_SPEED)] == 0) {
                posY -= dirY * MOVE_SPEED;
            }
        }
        if (right) {
            // right move
        }
        if (left) {
            // left move
        }
        if (rotRight) {
            double oldDirX = dirX;
            double oldPlaneX = planeX;
            dirX = dirX * Math.cos(-ROTATION_SPEED) - dirY * Math.sin(-ROTATION_SPEED);
            dirY = oldDirX * Math.sin(-ROTATION_SPEED) + dirY * Math.cos(-ROTATION_SPEED);
            planeX = planeX * Math.cos(-ROTATION_SPEED) - planeY * Math.sin(-ROTATION_SPEED);
            planeY = oldPlaneX * Math.sin(-ROTATION_SPEED) + planeY * Math.cos(-ROTATION_SPEED);
        }
        if (rotLeft) {
            double oldDirX = dirX;
            double oldPlaneX = planeX;
            dirX = dirX * Math.cos(ROTATION_SPEED) - dirY * Math.sin(ROTATION_SPEED);
            dirY = oldDirX * Math.sin(ROTATION_SPEED) + dirY * Math.cos(ROTATION_SPEED);
            planeX = planeX * Math.cos(ROTATION_SPEED) - planeY * Math.sin(ROTATION_SPEED);
            planeY = oldPlaneX * Math.sin(ROTATION_SPEED) + planeY * Math.cos(ROTATION_SPEED);
        }
    }

    public void keyTyped(KeyEvent arg0) {
        //nothing here
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

    public double getMOVE_SPEED() {
        return MOVE_SPEED;
    }
    public double getROTATION_SPEED() {
        return ROTATION_SPEED;
    }

    public void setRefreshing(View2D view2D) {
        this.view2D = view2D;
    }
}