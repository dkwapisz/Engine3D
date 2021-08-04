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

    public View2D(int[][] map, Camera camera) {
        this.map = map;
        this.camera = camera;

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
        g2d.fill(new Ellipse2D.Double(camera.getPosX() * 40 + 50, camera.getPosY() * 40 + 50, 10, 10));
    }

    private void drawRay(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        double vertexLeftX = camera.getPosX() + camera.getDirX() - camera.getPlaneX();
        double vertexLeftY = camera.getPosY() + camera.getDirY() - camera.getPlaneY();
        double vertexMiddleX = camera.getPosX() + camera.getDirX();
        double vertexMiddleY = camera.getPosY() + camera.getDirY();
        double vertexRightX = camera.getPosX() + camera.getDirX() + camera.getPlaneX();
        double vertexRightY = camera.getPosY() + camera.getDirY() + camera.getPlaneY();

        g2d.draw(new Line2D.Double(camera.getPosX() * 40 + 50, camera.getPosY() * 40 + 50, vertexLeftX  * 40 + 50, vertexLeftY * 40 + 50));
        g2d.draw(new Line2D.Double(camera.getPosX() * 40 + 50, camera.getPosY() * 40 + 50, vertexMiddleX  * 40 + 50, vertexMiddleY * 40 + 50));
        g2d.draw(new Line2D.Double(camera.getPosX() * 40 + 50, camera.getPosY() * 40 + 50, vertexRightX  * 40 + 50, vertexRightY * 40 + 50));

    }

    @Override
    public void paintComponent(Graphics g) {
        drawGrid(g);
        drawPlayer(g);
        drawRay(g);
    }

}
