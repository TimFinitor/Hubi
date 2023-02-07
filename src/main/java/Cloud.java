import java.awt.image.BufferedImage;

public class Cloud extends Sprite {
    private static final long serialVersionUID = 1L;
    final int SPEED = 20;

    public Cloud(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
        super(i, x, y, delay, p);

        if ((int) (Math.random() * 2) < 1) {
            setHorizontalSpeed(-SPEED);
        }
        else {
            setHorizontalSpeed(SPEED);
        }
    }

    @Override
    public void doLogic(long delta) {
        super.doLogic(delta);

        if (getHorizontalSpeed() > 0 && getX() > parent.getWidth()) {
            x = -getWidth();
        }
        if (getHorizontalSpeed() < 0 && (getX() + getWidth() < 0)) {
            x = parent.getWidth() + getWidth();
        }
    }

    @Override
    public boolean collidedWith(Sprite s) {
        if (this.intersects(s)) {
            if (s instanceof Rocket) {
                if (s.getHorizontalSpeed() == 100.0) {
                    if (getHorizontalSpeed() != -100.0 - 1 && getHorizontalSpeed() != 100.0 + 1) {
                        setHorizontalSpeed(getHorizontalSpeed() - 0.25);

                    }
                }
                if (s.getHorizontalSpeed() == -100.0) {
                    if (getHorizontalSpeed() != -100.0 - 1 && getHorizontalSpeed() != 100.0 + 1) {
                        setHorizontalSpeed(getHorizontalSpeed() + 0.25);
                    }
                }
            }
            return true;
        }
        return false;
    }
}