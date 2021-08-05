import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class View2D extends JPanel {

    private final int[][] map;
    private final Camera camera;
    private final Screen screen;

    public View2D(int[][] map, Camera camera, Screen screen) {
        this.map = map;
        this.camera = camera;
        this.screen = screen;

        JFrame frame = new JFrame("2D Preview");
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.setTitle("2D preview");
        frame.setBackground(Color.LIGHT_GRAY);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void drawGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] != 0) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fill(new Rectangle2D.Double(x * 40 + 50, y * 40 + 50, 40, 40));
                g2d.setColor(Color.DARK_GRAY);
                g2d.draw(new Rectangle2D.Double(x * 40 + 50, y * 40 + 50, 40, 40));
            }
        }
    }

    private void drawPlayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.fill(new Ellipse2D.Double(camera.getPosX() * 40 + 50 - 5, camera.getPosY() * 40 + 50 - 5, 10, 10));
    }

    private void drawRay(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);

        for (int i = 0; i < calculateRays().length; i++) {
            g2d.draw(new Line2D.Double(camera.getPosX() * 40 + 50, camera.getPosY() * 40 + 50, calculateRays()[i][0]  * 40 + 50, calculateRays()[i][1] * 40 + 50));
        }



    }

    private double[][] calculateRays() {
        double[][] rays = new double[67][2];
        double dirX, dirY, vertexX, vertexY;
        int arrayIter = 0;

        for (int i = -33; i <= 33; i++) { // copy of algorithms from screen calculations
            dirX = camera.getDirX() * Math.cos(Math.toRadians(i)) - camera.getDirY() * Math.sin(Math.toRadians(i));
            dirY = camera.getDirX() * Math.sin(Math.toRadians(i)) + camera.getDirY() * Math.cos(Math.toRadians(i));

            int mapX = (int) camera.getPosX();
            int mapY = (int) camera.getPosY();

            double sideDistX;
            double sideDistY;

            double deltaDistX = Math.abs(1 / dirX);
            double deltaDistY = Math.abs(1 / dirY);
            double perpWallDist;

            int stepX;
            int stepY;

            int hit = 0;
            int side = 0;

            if (dirX < 0) {
                stepX = -1;
                sideDistX = (camera.getPosX() - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - camera.getPosX()) * deltaDistX;
            }
            if (dirY < 0) {
                stepY = -1;
                sideDistY = (camera.getPosY() - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - camera.getPosY()) * deltaDistY;
            }

            while (hit == 0) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                }
                else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                if (map[mapX][mapY] > 0) {
                    hit = 1;
                }
            }

            if (side == 0) {
                perpWallDist = (mapX - camera.getPosX() + (double) (1 - stepX) / 2) / dirX;
            } else {
                perpWallDist = (mapY - camera.getPosY() + (double) (1 - stepY) / 2) / dirY;
            }

            vertexX = camera.getPosX() + dirX * perpWallDist;
            vertexY = camera.getPosY() + dirY * perpWallDist;

            rays[arrayIter][0] = vertexX;
            rays[arrayIter][1] = vertexY;

            arrayIter++;
        }



        return rays;
    }

    @Override
    public void paintComponent(Graphics g) {
        drawGrid(g);
        drawPlayer(g);
        drawRay(g);
    }

}
