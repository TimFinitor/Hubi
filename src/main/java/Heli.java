import java.awt.image.BufferedImage;

public class Heli extends Sprite {
    private static final long serialVersionUID = 1L;
    private final BufferedImage[] r;
    private final BufferedImage[] l;

    public Heli(BufferedImage[] i, BufferedImage[] r, BufferedImage[] l, double x, double y, long delay, GamePanel p) {
        super(i, x, y, delay, p);
        this.r = r;
        this.l = l;
    }

    @Override
    public void doLogic(long delta) {
        super.doLogic(delta);

        if (getX() < 0) {
            setHorizontalSpeed(0);
            setX(0);
        }
        if (getX() + getWidth() > parent.getWidth()) {
            setX(parent.getWidth() - getWidth());
            setHorizontalSpeed(0);
        }
        if (getY() < 0) {
            setY(0);
            setVerticalSpeed(0);
        }
        if (getY() + getHeight() > parent.getHeight()) {
            setY(parent.getHeight() - getHeight());
            setVerticalSpeed(0);
        }
    }

    @Override
    protected BufferedImage getCurrentPic() {
        if (dx > 0) {
            return r[currentpic];
        }
        if (dx < 0) {
            return l[currentpic];
        }
        else {
            return super.getCurrentPic();
        }
    }

    @Override
    public boolean collidedWith(Sprite s) {
        if (remove) {
            return false;
        }
        if (this.checkOpaqueColorCollisions(s)) {
            if (s instanceof Rocket) {
                parent.createExplosion((int) getX(), (int) getY());
                parent.createExplosion((int) s.getX(), (int) s.getY());
                remove = true;
                s.remove = true;
                return true;
            }
        }
        return false;
    }
}
