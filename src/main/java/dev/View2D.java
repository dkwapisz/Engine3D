package dev;

import dev.player.Player;
import mapUtilities.Door;
import mapUtilities.StaticObjects;
import mapUtilities.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class View2D extends JPanel {

    private final StaticObjects[][] map;
    private final Player player;
    private final Screen screen;
    private static JFrame view2Dframe;

    public View2D(StaticObjects[][] map, Player player, Screen screen) {
        this.map = map;
        this.player = player;
        this.screen = screen;
        view2Dframe = new JFrame("2D Preview");
        view2Dframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        view2Dframe.setResizable(false);
        view2Dframe.add(this);
        view2Dframe.setSize(617, 640);
        view2Dframe.setTitle("2D preview");
        view2Dframe.setLocationRelativeTo(null);
        view2Dframe.setVisible(false);
        view2Dframe.addKeyListener(player.getControls());
    }

    private void drawGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == null) {
                    g2d.setColor(Color.WHITE);
                } else if (map[x][y] instanceof Wall) {
                    g2d.setColor(Color.BLACK);
                } else if (map[x][y] instanceof Door) {
                    g2d.setColor(Color.BLUE);
                }
                g2d.fill(new Rectangle2D.Double(x * 40, y * 40, 40, 40));
                g2d.setColor(Color.DARK_GRAY);
                g2d.draw(new Rectangle2D.Double(x * 40, y * 40, 40, 40));
            }
        }
    }

    private void drawPlayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.fill(new Ellipse2D.Double(player.getPosX() * 40 - 5, player.getPosY() * 40 - 5, 10, 10));
    }

    private void drawRay(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);

        for (int i = 0; i < calculateRays(67).length; i++) {
            g2d.draw(new Line2D.Double(player.getPosX() * 40, player.getPosY() * 40, calculateRays(67)[i][0]  * 40, calculateRays(67)[i][1] * 40));
        }



    }

    private double[][] calculateRays(int rayNumber) {
        double[][] rays = new double[rayNumber][2];
        double dirX, dirY, vertexX, vertexY;
        int arrayIter = 0;

        for (int i = -((rayNumber - 1)/2); i <= (rayNumber - 1)/2; i++) { // copy of algorithms from screen calculations
            dirX = player.getDirX() * Math.cos(Math.toRadians(i)) - player.getDirY() * Math.sin(Math.toRadians(i));
            dirY = player.getDirX() * Math.sin(Math.toRadians(i)) + player.getDirY() * Math.cos(Math.toRadians(i));

            int mapX = (int) player.getPosX();
            int mapY = (int) player.getPosY();

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
                sideDistX = (player.getPosX() - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - player.getPosX()) * deltaDistX;
            }
            if (dirY < 0) {
                stepY = -1;
                sideDistY = (player.getPosY() - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - player.getPosY()) * deltaDistY;
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
                if (map[mapX][mapY] != null) {
                    hit = 1;
                }
            }

            if (side == 0) {
                perpWallDist = (mapX - player.getPosX() + (double) (1 - stepX) / 2) / dirX;
            } else {
                perpWallDist = (mapY - player.getPosY() + (double) (1 - stepY) / 2) / dirY;
            }

            vertexX = player.getPosX() + dirX * perpWallDist;
            vertexY = player.getPosY() + dirY * perpWallDist;

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

    public static JFrame getView2Dframe() {
        return view2Dframe;
    }
}
