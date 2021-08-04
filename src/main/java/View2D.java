import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class View2D extends JFrame {

    private int[][] map;
    private Camera camera;
    private BufferedImage image;

    public View2D(int[][] map, Camera camera) {
        this.map = map;
        this.camera = camera;
        image = new BufferedImage(700, 700, BufferedImage.TYPE_INT_RGB);

        setSize(700, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("2D preview");
        setBackground(Color.LIGHT_GRAY);
        setLocationRelativeTo(null);
        setVisible(true);
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
                g2d.fill(new Rectangle2D.Double(x*40+50, y*40+50, 40, 40));
                g2d.setColor(Color.DARK_GRAY);
                g2d.draw(new Rectangle2D.Double(x*40+50, y*40+50, 40, 40));
            }
        }
    }

    private void drawPlayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.fill(new Ellipse2D.Double(camera.getPosX()*40+50, camera.getPosY()*40+50, 10, 10));
    }

    @Override
    public void update(Graphics g) {
        drawPlayer(g);
    }

    @Override
    public void paint(Graphics g) {
        drawGrid(g);
        drawPlayer(g);
    }

    public BufferedImage getImage() {
        return image;
    }
}
