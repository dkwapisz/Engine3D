import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class View2D extends JPanel {

    private final int[][] map;
    private final PlayerHandling playerHandling;
    private final Screen screen;
    private static JFrame view2Dframe;

    public View2D(int[][] map, PlayerHandling playerHandling, Screen screen) {
        this.map = map;
        this.playerHandling = playerHandling;
        this.screen = screen;
        view2Dframe = new JFrame("2D Preview");
        view2Dframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        view2Dframe.setResizable(false);
        view2Dframe.add(this);
        view2Dframe.setSize(617, 640);
        view2Dframe.setTitle("2D preview");
        view2Dframe.setLocationRelativeTo(null);
        view2Dframe.setVisible(false);
        view2Dframe.addKeyListener(playerHandling);
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
                g2d.fill(new Rectangle2D.Double(x * 40, y * 40, 40, 40));
                g2d.setColor(Color.DARK_GRAY);
                g2d.draw(new Rectangle2D.Double(x * 40, y * 40, 40, 40));
            }
        }
    }

    private void drawPlayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.fill(new Ellipse2D.Double(playerHandling.getPosX() * 40 - 5, playerHandling.getPosY() * 40 - 5, 10, 10));
    }

    private void drawRay(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);

        for (int i = 0; i < calculateRays(67).length; i++) {
            g2d.draw(new Line2D.Double(playerHandling.getPosX() * 40, playerHandling.getPosY() * 40, calculateRays(67)[i][0]  * 40, calculateRays(67)[i][1] * 40));
        }



    }

    private double[][] calculateRays(int rayNumber) {
        double[][] rays = new double[rayNumber][2];
        double dirX, dirY, vertexX, vertexY;
        int arrayIter = 0;

        for (int i = -((rayNumber - 1)/2); i <= (rayNumber - 1)/2; i++) { // copy of algorithms from screen calculations
            dirX = playerHandling.getDirX() * Math.cos(Math.toRadians(i)) - playerHandling.getDirY() * Math.sin(Math.toRadians(i));
            dirY = playerHandling.getDirX() * Math.sin(Math.toRadians(i)) + playerHandling.getDirY() * Math.cos(Math.toRadians(i));

            int mapX = (int) playerHandling.getPosX();
            int mapY = (int) playerHandling.getPosY();

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
                sideDistX = (playerHandling.getPosX() - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - playerHandling.getPosX()) * deltaDistX;
            }
            if (dirY < 0) {
                stepY = -1;
                sideDistY = (playerHandling.getPosY() - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - playerHandling.getPosY()) * deltaDistY;
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
                perpWallDist = (mapX - playerHandling.getPosX() + (double) (1 - stepX) / 2) / dirX;
            } else {
                perpWallDist = (mapY - playerHandling.getPosY() + (double) (1 - stepY) / 2) / dirY;
            }

            vertexX = playerHandling.getPosX() + dirX * perpWallDist;
            vertexY = playerHandling.getPosY() + dirY * perpWallDist;

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
