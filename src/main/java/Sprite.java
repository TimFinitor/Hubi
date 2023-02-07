import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Sprite extends Rectangle2D.Double implements Drawable, Movable {

    private static final long serialVersionUID = 1L;
    protected double dx;
    protected double dy;
    int loop_from;
    int loop_to;
    long delay;
    long animation = 0;
    GamePanel parent;
    BufferedImage[] pics;
    int currentpic = 0;
    boolean remove;

    public Sprite(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
        pics = i;
        this.x = x;
        this.y = y;
        this.delay = delay;
        this.width = pics[0].getWidth();
        this.height = pics[0].getHeight();
        parent = p;
        loop_from = 0;
        loop_to = pics.length - 1;
    }

    public void drawObjects(Graphics g) {
        g.drawImage(getCurrentPic(), (int) x, (int) y, null);
    }

    public void doLogic(long delta) {
        animation += (delta / 1000000);
        if (animation > delay) {
            animation = 0;
            computeAnimation();
        }
    }

    public void move(long delta) {

        if (dx != 0) {
            x += dx * (delta / 1e9);
        }
        if (dy != 0) {
            y += dy * (delta / 1e9);
        }
    }

    public boolean checkOpaqueColorCollisions(Sprite s) { //Pixelgenaue Hitbox-Abfrage
        Rectangle2D.Double cut = (Double) this.createIntersection(s);
        if ((cut.width < 1) || (cut.height < 1))
            return false;

        Rectangle2D.Double sub_me = getSubRec(this, cut);
        Rectangle2D.Double sub_him = getSubRec(s, cut);

        BufferedImage img_me = getCurrentPic().getSubimage((int) sub_me.x, (int) sub_me.y, (int) sub_me.width, (int) sub_me.height);
        BufferedImage img_him = s.getCurrentPic().getSubimage((int) sub_him.x, (int) sub_him.y, (int) sub_him.width, (int) sub_him.height);

        for (int i = 0; i < img_me.getWidth(); i++) {
            for (int n = 0; n < img_him.getHeight(); n++) {

                int rgb1 = img_me.getRGB(i, n);
                int rgb2 = img_him.getRGB(i, n);

                if (isOpaque(rgb1) && isOpaque(rgb2)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected BufferedImage getCurrentPic() {
        return pics[currentpic];
    }

    protected Rectangle2D.Double getSubRec(Rectangle2D.Double source, Rectangle2D.Double part) {
        Rectangle2D.Double sub = new Rectangle2D.Double();

        if (source.x > part.x) {
            sub.x = 0;
        }
        else {
            sub.y = part.y - source.y;
        }
        sub.width = part.width;
        sub.height = part.height;
        return sub;
    }

    protected boolean isOpaque(int rgb) {
        int alpha = (rgb >> 24) & 0xff;
        return alpha != 0;
    }

    public double getHorizontalSpeed() {
        return dx;
    }

    public void setHorizontalSpeed(double dx) {
        this.dx = dx;
    }

    public double getVerticalSpeed() {
        return dy;
    }

    public void setVerticalSpeed(double dy) {
        this.dy = dy;
    }

    public abstract boolean collidedWith(Sprite s);

    private void computeAnimation() {
        currentpic++;

        if (currentpic > loop_to) {
            currentpic = loop_from;
        }
    }

    public void setLoop(int from, int to) {
        loop_from = from;
        loop_to = to;
        currentpic = from;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
