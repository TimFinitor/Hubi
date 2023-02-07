import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ListIterator;
import java.util.Vector;

public class GamePanel extends JPanel implements Runnable, KeyListener, ActionListener {
    private static final long serialVersionUID = 1L;
    JFrame frame;
    long delta = 0;
    long last = 0;
    long fps = 0;
    long gameover = 0;

    Heli copter;
    Vector<Sprite> actors;
    Vector<Sprite> painter;

    boolean up;
    boolean down;
    boolean left;
    boolean right;
    boolean started;
    int speed = 100;
    boolean sound = false;

    javax.swing.Timer timer; //Neuer Timer für Raketen

    BufferedImage[] rocket;
    BufferedImage[] explosion;
    BufferedImage background;

    SoundLib soundLib;

    public GamePanel(int w, int h) {
        this.setPreferredSize(new Dimension(w, h));
        this.setBackground(Color.BLUE);
        frame = new JFrame("Hubi Hubi Schraub Schraub");
        frame.setLocation(100, 100);
        background = loadPics("background.jpg", 1)[0];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);
        Thread th = new Thread(this); //Neuer Thread
        th.start();
    }

    public static void main(String[] args) { //Main Methode
        new GamePanel(800, 600);     //Neues GamePanel wird erstellt
    }

    private void doInitializations() { //Initialisierung aller Objekte

        last = System.nanoTime();
        gameover = 0;
        rocket = loadPics("rocket.gif", 8); //Bild von Rakete
        explosion = loadPics("explosion.gif", 5);
        BufferedImage[] heli = loadPics("heli.gif", 4);
        BufferedImage[] helir = loadPics("helir.gif", 4);
        BufferedImage[] helil = loadPics("helil.gif", 4);
        copter = new Heli(heli, helir, helil, 400, 300, 100, this);
        actors = new Vector<>(); //Neuer Vector/Dynamisches Array
        painter = new Vector<>();
        actors.add(copter);
        soundLib = new SoundLib();
        soundLib.loadSound("bumm", "boom.wav");
        soundLib.loadSound("rocket", "rocket_start.wav");
        soundLib.loadSound("heli", "heli.wav");
        createClouds();
        timer = new Timer(3000, this); //Timer wird auf 3 Sek gesetzt
        timer.start(); //Timer wird gestartet
        started = true; //Nach dem Hinzufügen aller Objekte wird started auf true gesetzt
    }

    public void createExplosion(int x, int y) {
        ListIterator<Sprite> it = actors.listIterator();
        it.add(new Explosion(explosion, x, y, 100, this));
        if (sound) {
            soundLib.playSound("bumm");
        }
    }

    @Override
    public void run() {
        while (frame.isVisible()) {
            computerDelta();
            if (isStarted()) {
                checkKeys();
                doLogic();
                moveObjects();
                cloneVectors();
            }
            repaint();

            try {
                Thread.sleep(10);
            }
            catch (InterruptedException ignored) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void cloneVectors() {
        painter = (Vector<Sprite>) actors.clone();
    }

    private void computerDelta() {
        delta = System.nanoTime() - last;
        last = System.nanoTime();
        fps = ((long) 1e9) / delta;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.drawImage(background, 0, 0, this);
        g.drawString("FPS: " + fps, 20, 10);

        if (!started) {
            return;
        }
        for (Sprite r : painter) {
            r.drawObjects(g);
        }
    }

    private void moveObjects() {

        for (Sprite r : actors) {
            r.move(delta);
        }
    }

    private void doLogic() {

        for (ListIterator<Sprite> it = actors.listIterator(); it.hasNext(); ) {
            Sprite r = it.next();
            r.doLogic(delta);

            if (r.remove) {
                it.remove();
            }
        }
        for (int i = 0; i < actors.size(); i++) {
            for (int n = i + 1; n < actors.size(); n++) {
                for (int m = i + 1; m < actors.size(); m++) {
                    Sprite s1 = actors.elementAt(i);
                    Sprite s2 = actors.elementAt(n);
                    Sprite s3 = actors.elementAt(m);
                    s1.collidedWith(s2);
                    s3.collidedWith(s1);
                }
            }
        }
        if (copter.remove && gameover == 0) {
            gameover = System.currentTimeMillis();
        }
        if (gameover > 0) {
            if (System.currentTimeMillis() - gameover > 3000) {
                stopGame();
            }
        }
    }

    public void stopGame() {
        timer.stop();
        setStarted(false);
        soundLib.stopLoopingSound();
    }

    private BufferedImage[] loadPics(String path, int pics) { //Methode um Bilder zu laden
        BufferedImage[] anim = new BufferedImage[pics];
        BufferedImage source = null;
        URL pic_url = getClass().getClassLoader().getResource(path);
        try {
            assert pic_url != null;
            source = ImageIO.read(pic_url);
        }
        catch (IOException ignored) {
        }

        for (int x = 0; x < pics; x++) {
            assert source != null;
            anim[x] = source.getSubimage(x * source.getWidth() / pics, 0, source.getWidth() / pics, source.getHeight());
        }
        return anim;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!isStarted()) {
                startGame();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (isStarted()) {
                stopGame();
            }
            else
                frame.dispose();
        }
        if (e.getKeyCode() == KeyEvent.VK_M) {
            if (sound) {
                soundLib.stopLoopingSound();
            }
            else {
                soundLib.loopSound("heli");
            }
            sound = !sound;
        }
    }

    private void checkKeys() {
        if (up) {
            copter.setVerticalSpeed(-speed);
        }
        if (down) {
            copter.setVerticalSpeed(speed);
        }
        if (right) {

            copter.setHorizontalSpeed(speed);
        }
        if (left) {
            copter.setHorizontalSpeed(-speed);
        }
        if (!up && !down) {
            copter.setVerticalSpeed(0);
        }
        if (!left && !right) {
            copter.setHorizontalSpeed(0);
        }
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    private void createClouds() {
        BufferedImage[] bi = loadPics("cloud.gif", 1);
        for (int y = 10; y < getHeight(); y += 50) {
            int x = (int) (Math.random() * getWidth());
            Cloud cloud = new Cloud(bi, x, y, 1000, this);
            actors.add(cloud);
        }
    }

    private void startGame() {
        doInitializations();
        setStarted(true);
        soundLib.loopSound("heli");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted() && e.getSource().equals(timer)) {
            createRocket();
        }
    }

    private void createRocket() {
        int x;
        int y = (int) (Math.random() * getHeight());
        int hori = (int) (Math.random() * 2);

        if (hori == 0) {
            x = -30;
        }
        else {
            x = getWidth() + 30;
        }

        Rocket rock = new Rocket(rocket, x, y, 100, this);
        if (x < 0) {
            rock.setHorizontalSpeed(100);
        }
        else {
            rock.setHorizontalSpeed(-100);
        }

        ListIterator<Sprite> it = actors.listIterator();
        it.add(rock);
        if (sound) {
            soundLib.playSound("rocket");
        }
    }
}

