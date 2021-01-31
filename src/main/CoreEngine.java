package main;

import interfaces.EngineComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class is the underlying engine that drives the game; it is used to draw information
 * from the game's state to the JFrame associated. This class is ABSTRACT as it should be extended
 * by the games main logic class (PacmanGame.java).
 *
 * @author Harry Felton - 18032692
 *
 * **
 * The following code is first-party and has been written by Harry Felton, unless otherwise stated. All code in
 * this file, falls under the projects license (LICENSE.md).
 */

public abstract class CoreEngine implements EngineComponent, MouseMotionListener, MouseListener, KeyListener {
    protected final int WINDOW_WIDTH;
    protected final int WINDOW_HEIGHT;
    protected final String WINDOW_TITLE;

    protected JFrame mainFrame;
    protected DrawPanel mainPanel;
    protected Graphics2D engineGraphics;
    protected GameTimer gameLoop;

    private boolean graphicsReady = false;
    private long currentTime = 0;
    private long previousTime = 0;

    Color black = Color.BLACK;
    Color orange = Color.ORANGE;
    Color pink = Color.PINK;
    Color red = Color.RED;
    Color purple = new Color(128, 0, 128);
    Color blue = Color.BLUE;
    Color green = Color.GREEN;
    Color yellow = Color.YELLOW;
    Color white = Color.WHITE;

    /* Core Entry Methods */

    public CoreEngine(int width, int height, String title) {
        this.WINDOW_WIDTH = width;
        this.WINDOW_HEIGHT = height;
        this.WINDOW_TITLE = title;

        this.initialiseEngine();
        SwingUtilities.invokeLater(this::initialiseFrame);
    }

    protected void initialiseFrame() {
        mainFrame = new JFrame();
        mainPanel = new DrawPanel(this);

        mainFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainFrame.setTitle(WINDOW_TITLE);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocation(200,200);

        mainPanel.setDoubleBuffered(true);
        mainPanel.addMouseListener(this);
        mainPanel.addMouseMotionListener(this);

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this::handleKeyEvent);

        Insets insets = mainFrame.getInsets();
        mainFrame.setSize(WINDOW_WIDTH + insets.left + insets.right, WINDOW_HEIGHT + insets.top + insets.bottom);
    }

    protected void engineStart() {
        this.graphicsReady = true;

        this.gameLoop.setRepeats(true);
        this.gameLoop.start();
    }

    /* Handler Methods */

    protected boolean handleKeyEvent(KeyEvent e) {
        switch (e.getID()) {
            case KeyEvent.KEY_PRESSED -> this.keyPressed(e);
            case KeyEvent.KEY_RELEASED -> this.keyReleased(e);
            case KeyEvent.KEY_TYPED -> this.keyTyped(e);
        }

        return false;
    }

    /* Accessory Methods */
    protected void initialiseEngine() {
        this.gameLoop = new GameTimer(30, e -> {
            double a = measureTime();
            update(a / 1000.);
            mainPanel.repaint();
        });
    }

    protected long measureTime() {
        currentTime = System.currentTimeMillis();
        if(previousTime == 0) {
            // Not called before, measurement will be zero for first call
            previousTime = currentTime;
        }

        long measured = currentTime - previousTime;
        previousTime = currentTime;
        return measured;
    }


    /* Sub-classes */
    protected class DrawPanel extends JPanel {
        CoreEngine engine;
        DrawPanel(CoreEngine e) {
            super();

            this.engine = e;
        }

        @Override
        public void paintComponent(Graphics g) {
            engineGraphics = (Graphics2D)g;
            engineGraphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

            if(graphicsReady) {
                this.engine.paintComponent();
            }
        }
    }

    protected class GameTimer extends Timer {
        protected GameTimer(int framerate, ActionListener listener) {
            super(1000/framerate, listener);
        }
    }

    /* Required, but useless methods */
    // Can be selectively overridden if needed by the main game class.
    @Override
    public void keyPressed(KeyEvent event) {}
    @Override
    public void keyReleased(KeyEvent event) {}
    @Override
    public void keyTyped(KeyEvent event) {}
    @Override
    public void mouseClicked(MouseEvent event) {}
    @Override
    public void mousePressed(MouseEvent event) {}
    @Override
    public void mouseReleased(MouseEvent event) {}
    @Override
    public void mouseEntered(MouseEvent event) {}
    @Override
    public void mouseExited(MouseEvent event) {}
    @Override
    public void mouseMoved(MouseEvent event) {}
    @Override
    public void mouseDragged(MouseEvent event) {}
}
