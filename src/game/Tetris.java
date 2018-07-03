package game;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Point;
import java.awt.Color;
import java.awt.TexturePaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Project: TetrisCloneAttempt
 * Author: Evan Smith
 * Date: 4/8/18
 * Purpose: A fun game of Tetris
 */

class Tetris extends JPanel {

    // 3 dimensional array for storing each tetramino shape-blueprint, and for each rotation
    private final Point[][][] Tetraminos = {
        // I-piece
        {
            {new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0)},
            {new Point(1,0), new Point(1,1), new Point(1,2), new Point(1,3)},
            {new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0)},
            {new Point(1,0), new Point(1,1), new Point(1,2), new Point(1,3)}

        },
        // J-piece
        {
            {new Point(0,0), new Point(1,0), new Point(2,0), new Point(2,1)},
            {new Point(0,2), new Point(1,2), new Point(1,1), new Point(1,0)},
            {new Point(0,0), new Point(0,1), new Point(1,1), new Point(2,1)},
            {new Point(1,2), new Point(1,1), new Point(1,0), new Point(2,0)}
        },
        // L-piece
        {
            {new Point(0,1), new Point(0,0), new Point(1,0), new Point(2,0)},
            {new Point(0,0), new Point(1,0), new Point(1,1), new Point(1,2)},
            {new Point(0,1), new Point(1,1), new Point(2,1), new Point(2,0)},
            {new Point(1,0), new Point(1,1), new Point(1,2), new Point(2,2)}
        },
        // O-piece
        {
            {new Point(1,0), new Point(1,1), new Point(2,0), new Point(2,1)},
            {new Point(1,0), new Point(1,1), new Point(2,0), new Point(2,1)},
            {new Point(1,0), new Point(1,1), new Point(2,0), new Point(2,1)},
            {new Point(1,0), new Point(1,1), new Point(2,0), new Point(2,1)}
        },
        // S-piece
        {
            {new Point(0,1), new Point(1,1), new Point(1,0), new Point(2,0)},
            {new Point(1,0), new Point(1,1), new Point(2,1), new Point(2,2)},
            {new Point(0,1), new Point(1,1), new Point(1,0), new Point(2,0)},
            {new Point(1,0), new Point(1,1), new Point(2,1), new Point(2,2)}
        },
        // Z-piece
        {
            {new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,1)},
            {new Point(1,2), new Point(1,1), new Point(2,1), new Point(2,0)},
            {new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,1)},
            {new Point(1,2), new Point(1,1), new Point(2,1), new Point(2,0)}
        },
        // T-piece
        {
            {new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,0)},
            {new Point(0,1), new Point(1,0), new Point(1,1), new Point(1,2)},
            {new Point(0,1), new Point(1,1), new Point(1,0), new Point(2,1)},
            {new Point(1,0), new Point(1,1), new Point(1,2), new Point(2,1)}
        }
    };

    // Standard colors for tetraminos
    private final Color[] tetraminoColors = {
        new Color(250,201,1), // orange for I piece
        new Color(204,0,255), // purple for J piece
        new Color(0,0,255), // blue for L piece
        new Color(249,15,1), // red for O piece
        new Color(102,204,255), // light blue for S piece
        new Color(72,255,0), // green for Z piece
        new Color(255,255,0) // yellow for T piece
    };

    // Light versions of colors for tetramino beveled edges
    private final Color[] tetraminoLightColors = {
        new Color(250,221,100), // light orange for I piece
        new Color(204,100,255), // light purple for J piece
        new Color(0,100,255), // lighter blue for L piece
        new Color(249,75,50), // light red for O piece
        new Color(132,234,255), // light blue for S piece
        new Color(181,255,184), // light green for Z piece
        new Color(255,255,180) // light yellow for T piece
    };

    // Dark versions of colors for tetramino beveled edges
    private final Color[] tetraminoDarkColors = {
        new Color(200,151,1), // dark orange for I piece
        new Color(154,0,185), // dark purple for J piece
        new Color(0,0,135), // dark blue for L piece
        new Color(160,8,0), // dark red for O piece
        new Color(82,154,205), // darker blue for S piece
        new Color(52,185,0), // dark green for Z piece
        new Color(205,205,0) // dark yellow for T piece
    };

    // Fields used to establish colors and background textures
    private final Color BACK_LIGHT_BLUE = new Color(221, 238, 255);
    private final Color WELL_INIT_COLOR = new Color(200, 222, 235);
//    private TexturePaint backgroundCells;
    private TexturePaint tpLight;
    private TexturePaint tpDark;
    private TexturePaint startControls;
    private TexturePaint pauseControls;

    // Fields used for tetramino logic and tetramino/well appearance
    private Point pieceOrigin;
    private int currentPiece;
    private int previewPiece;
    private int rotation;
    private ArrayList<Integer> nextPieces = new ArrayList<>();
    private ArrayList<Integer> shufflePieces = new ArrayList<>();
    private Boolean[][] well;
    private Color[][] wellLook;

    // Field used for thread synchronization
    private final Object lock = new Object();

    // Boolean fields used for controlling flow
    private boolean isAlive = true;
    private boolean paused = false;
    private boolean quitting = false;
    private boolean gameOver = false;
    private boolean soundIsOn = true;
    private boolean isStarted = false;
    private boolean isStalled = false;
    private boolean isSlammed = false;
    private boolean flashing = false;
    private final boolean hasExited = false;

    // Fields to store highlighting effect state
    private boolean levelDownHighlight = false;
    private boolean levelUpHighlight = false;
    private boolean pauseHighlight = false;
    private boolean quitHighlight = false;
    private boolean soundHighlight = false;
    private boolean midGameRestartHighlight = false;
    private boolean restartHighlight = false;
    private boolean exitHighlight = false;
    private boolean viewScoresHighlight = false;

    // Fields to store highlighting effect locations
    private Rectangle levelDownRect;
    private Rectangle levelUpRect;
    private Rectangle pauseRect;
    private Rectangle quitRect;
    private Rectangle soundRect;
    private Rectangle midGameRestartRect;
    private Rectangle restartRect;
    private Rectangle exitRect;
    private Rectangle viewScoresRect;

    // Fields used to store game-play stats
    private long score = 0;
    private int level = 1;
    private int clearedLines = 0;
    private int numCleared = 0;
    private int[] flashRows = {0, 0, 0, 0};

    // Tetris instance for use in main method
    private static Tetris game;

    // Different size fonts for use
    private Font sSBig = new Font("SansSerif", Font.BOLD, 18);
    private Font sSSmall = new Font("SansSerif", Font.BOLD, 15);
    private Font sSExtraSmall = new Font("SansSerif", Font.BOLD, 10);

    // Files for storing and passing data to high scores webpage
    private File dateFile = new File("/Library/WebServer/Documents/leaderboardFiles/dates.txt");
    private File timeFile = new File("/Library/WebServer/Documents/leaderboardFiles/times.txt");
    private File scoreFile = new File("/Library/WebServer/Documents/leaderboardFiles/scores.txt");
    private File plainScoreFile = new File("/Library/WebServer/Documents/leaderboardFiles/plainScores.txt");
    private File levelFile = new File("/Library/WebServer/Documents/leaderboardFiles/levels.txt");

    private Tetris() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (lowerLevelClicked(x, y)) {
                    level -= 1;
                    repaint();
                }
                else if (higherLevelClicked(x, y)) {
                    level += 1;
                    repaint();
                }
                else if (soundClicked(x, y)) {
                    if (soundIsOn) {
                        soundIsOn = false;
                        SoundUtils.stopPlaying();
                    }
                    else {
                        soundIsOn = true;
                        SoundUtils.startPlaying();
                    }
                    repaint();
                }
                else if (pauseClicked(x, y)) {
                    pauseActions();
                }
                else if (quitClicked(x, y)) {
                    quitActions();
                }
                else if (midGameRestartClicked(x, y)) {
                    gameOver = true;
                    isAlive = false;
                    SoundUtils.stopPlaying();
                    restartActions(true);
                }
                else if (restartClicked(x, y)) {
                    restartActions(false);
                }
                else if (exitClicked(x, y)) {
                    System.exit(0);
                }
                else if (viewScoresClicked(x, y)) {
                    viewHighscores();
                }
            }//end mousePressed

            @Override
            public void mouseReleased(MouseEvent e) {
                //nothing
            }
        });

        // Initialization of highlight location rectangles
        levelDownRect = new Rectangle(175, 100, 20, 20);
        levelUpRect = new Rectangle(285, 100, 20, 20);
        pauseRect = new Rectangle(300, 300, 70, 15);
        quitRect = new Rectangle(400, 300, 65, 15);
        soundRect = new Rectangle(320, 340, 30, 30);
        midGameRestartRect = new Rectangle(400, 350, 80, 15);
        restartRect = new Rectangle(55, 210, 90, 15);
        exitRect = new Rectangle(175, 210, 50, 15);
        viewScoresRect = new Rectangle(55, 250, 205, 20);

        tpLight = createLightBackground();
        tpDark = createDarkBackground();
        startControls = createStartControls();
        pauseControls = createPauseControls();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                levelDownHighlight = levelDownRect.contains(e.getPoint());
                levelUpHighlight = levelUpRect.contains(e.getPoint());
                pauseHighlight = pauseRect.contains(e.getPoint());
                quitHighlight = quitRect.contains(e.getPoint());
                soundHighlight = soundRect.contains(e.getPoint());
                midGameRestartHighlight = midGameRestartRect.contains(e.getPoint());
                restartHighlight = restartRect.contains(e.getPoint());
                exitHighlight = exitRect.contains(e.getPoint());
                viewScoresHighlight = viewScoresRect.contains(e.getPoint());
                repaint();
            }
        });
    }//end constructor

    private boolean lowerLevelClicked(int x, int y) {
        return (!isStarted && level > 1 && x >= 175 && x <= 195
            && y >= 100 && y <= 120);
    }

    private boolean higherLevelClicked(int x, int y) {
        return (!isStarted && level < 10 && x >= 285 && x <= 305
            && y >= 100 && y <= 120);
    }

    private boolean soundClicked(int x, int y) {
        return (isStarted && !paused && !quitting && x >= 320
            && x <= 350 && y >= 340 && y <= 370);
    }

    private boolean pauseClicked(int x, int y) {
        return (isStarted && !quitting && !gameOver && x >= 300
            && x <= 370 && y >= 300 && y <= 315);
    }

    private boolean quitClicked(int x, int y) {
        return (isStarted && !paused && !gameOver && x >= 400
            && x <= 465 && y >= 300 && y <= 315);
    }

    private boolean midGameRestartClicked(int x, int y) {
        return (isStarted && !paused && !quitting && !gameOver
            && x >= 400 && x <= 480 && y >= 350 && y <= 365);
    }

    private boolean restartClicked(int x, int y) {
        return (gameOver && x >= 55 && x <= 145 && y >= 210 && y <= 225);
    }

    private boolean exitClicked(int x, int y) {
        return (gameOver && x >= 175 && x <= 225 && y >= 210 && y <= 225);
    }

    private boolean viewScoresClicked(int x, int y) {
        return (gameOver && x >= 55 && x <= 260 && y >= 250 && y <= 270);
    }

    private void resetFields() {

        paused = false;
        quitting = false;
        gameOver = false;
        isStarted = false;
        isAlive = true;
        isStalled = false;
        isSlammed = false;
        flashing = false;
        score = 0;
        level = 1;
        clearedLines = 0;
        SoundUtils.resetSongSpeed();
        SoundUtils.resetStartNote();

    }//end method resetFields


    // Creates a border around the well and initializes the dropping piece
    private void init() {

        well = new Boolean[12][20];
        wellLook = new Color[12][20];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 20; j++) {
                well[i][j] = i != 0 && i != 11 && j != 19;
                wellLook[i][j] = WELL_INIT_COLOR;
            }
        }

        newPiece();

    }//end method init

    // Put a new, random piece into the dropping position
    private void newPiece() {

        pieceOrigin = new Point(4, 1);
        rotation = 0;
        if ((nextPieces.isEmpty()) || (nextPieces.size() == 1)) {
            Collections.addAll(shufflePieces, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(shufflePieces);
            nextPieces.addAll(shufflePieces);
        }
        currentPiece = nextPieces.get(0);
        previewPiece = nextPieces.get(1);

        nextPieces.remove(0);

        if (!isValidMove(pieceOrigin.x, pieceOrigin.y+1, rotation)) {
            gameOverActions();
        }

    }//end method newPiece

    private void slide(int direction) {

        if (isValidMove(pieceOrigin.x+direction, pieceOrigin.y, rotation)) {
            pieceOrigin.x += direction;
        }
        repaint();

    }//end method slide

    private void rotate() {

        if (isValidMove(pieceOrigin.x, pieceOrigin.y, rotation+1)) {
            rotation++;
        }

        if (rotation == 4) {
            rotation = 0;
        }
        repaint();

    }//end method rotate

    private void slamPiece() {
        isSlammed = true;
        for (int i = pieceOrigin.y; i < 18; i++) {
            if (isValidMove(pieceOrigin.x, pieceOrigin.y+1, rotation)) {
                pieceOrigin.y += 1;
            }
            else {
                lockPiece();
                break;
            }
        }
        repaint();

    }//end method slamPiece

    private void dropPiece() {

        if (isValidMove(pieceOrigin.x, pieceOrigin.y+1, rotation)) {
            pieceOrigin.y += 1;
        }
        else {
            lockPiece();
        }
        repaint();

    }//end method dropPiece

    private void lockPiece() {

        for (Point p: Tetraminos[currentPiece][rotation]) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = false;
            wellLook[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
        }
        repaint();
        if (!gameOver) {
            score += pieceOrigin.y+1;
        }
        clearLines();
        if (!gameOver) {
            newPiece();
        }

    }//end method lockPiece

    private void clearLines() {

        boolean gap;
        numCleared = 0;
        for (int j = 18; j >= 0; j--) {
            gap = false;
            for (int i = 0; i < 12; i++) {
                if (well[i][j]) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                isStalled = true;
                flashing = true;
                clearedLines++;
                flashRows[numCleared] = j-numCleared;
                numCleared++;
                deleteLine(j);
                j += 1;
                if (clearedLines % 10 == 0) {
                    level++;
                    if (level <= 10 && (level % 2 == 0)) {
                        SoundUtils.setSongSpeed();
                    }
                }
            }
        }//end checking for complete lines to clear

        switch (numCleared) {
            case 0:
                break;
            case 1:
                score += 40*level;
                break;
            case 2:
                score += 100*level;
                break;
            case 3:
                score += 300*level;
                break;
            case 4:
                score += 1200*level;
                break;
        }//end score adjustment switch

    }//end method clearLines

    private void deleteLine(int rowNum) {

        for (int j = rowNum-1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                wellLook[i][j+1] = wellLook[i][j];
                well[i][j+1] = well[i][j];

            }
        }

    }//end method deleteLine

    private boolean isValidMove(int x, int y, int r) {

        if (r == 4) {
            r = 0;
        }
        if (x == -1) {
            return false;
        }

        for (Point p: Tetraminos[currentPiece][r]) {
            if (!well[p.x + x][p.y + y]) {
                return false;
            }
        }
        return true;

    }//end method isValidMove

    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;
        FontMetrics fm = g2.getFontMetrics();

        // Paint the background
        g2.setPaint(tpLight);
        g2.fillRect(0,0, 510, 575); // filling background

        // Paint the well
        g2.setPaint(tpDark);
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 19; j++) {
                g2.fillRect(25*i, 25*j, 25, 25);
            }
        }

        // Draw all the cell borders
        drawCells(g2);

        // Draw the preview piece
        drawPreviewPiece(g2);

        // Draw fixed pieces at bottom of well
        drawFixedPieces(g2);

        // Draw the level, lines, and score
        drawData(g2, fm);

        // Draw the currently falling piece
        drawPiece(g2);

        // Draw game over screen if game is over
        if (gameOver) {
            drawGameOverScreen(g2, fm);
        }

        // Draw start screen if game not yet started
        if (!isStarted) {
            drawStartScreen(g2);
        }

        // Draw pause screen if game is paused
        if (paused) {
            drawPauseScreen(g2);
        }

        // Draw quitting screen if attempt to quit
        if (quitting) {
            drawQuitScreen(g2);
        }

        if (flashing) {
            g2.setPaint(Color.GRAY);
            for (int i = 0; i < numCleared; i++) {
                g2.drawRect(28, flashRows[i] * 25, 244, 23);
            }
        }

    }//end method paintComponent

    private void drawPreviewPiece(Graphics2D g2) {

        // Display the next piece
        g2.setColor(tetraminoColors[previewPiece]);
        for (Point p : Tetraminos[previewPiece][0]) {
            g2.fillRect((p.x + 13) * 25,
                (p.y + 2) * 25,
                25, 25);
        }
        for (Point p : Tetraminos[previewPiece][0]) {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect((p.x + 13) * 25,
                (p.y + 2) * 25,
                25, 25);
            g2.setColor(tetraminoDarkColors[previewPiece]);
            g2.setStroke(new BasicStroke(4));
            g2.drawLine((p.x + 13) * 25 + 3,
                (p.y + 2) * 25 + 22,
                (p.x + 13) * 25 + 22,
                (p.y + 2) * 25 + 22);
            g2.drawLine((p.x + 13) * 25 +3,
                (p.y + 2) * 25 + 22,
                (p.x + 13) * 25 + 3,
                (p.y + 2) * 25 + 3);
            g2.setColor(tetraminoLightColors[previewPiece]);
            g2.setStroke(new BasicStroke(4));
            drawTriangle((p.x + 13) * 25 + 1, (p.x + 13) * 25 + 5, (p.x + 13) * 25 + 5,
                (p.y + 2) * 25 + 1, (p.y + 2) * 25 + 1, (p.y + 2) * 25 + 5, g2);
            g2.drawLine((p.x + 13) * 25 + 7,
                (p.y + 2) * 25 + 3,
                (p.x + 13) * 25 + 22,
                (p.y + 2) * 25 + 3);
            drawTriangle((p.x + 13) * 25 + 20, (p.x + 13) * 25 + 24, (p.x + 13) * 25 + 24,
                (p.y + 2) * 25 + 20, (p.y + 2) * 25 + 20, (p.y + 2) * 25 + 24, g2);
            g2.drawLine((p.x + 13) * 25 + 22,
                (p.y + 2) * 25 + 18,
                (p.x + 13) * 25 + 22,
                (p.y + 2) * 25 + 3);
        }

    }//end method drawPreviewPiece

    private void drawFixedPieces(Graphics2D g2) {

        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 19; j++) {
                if (!wellLook[i][j].equals(WELL_INIT_COLOR)) {
                    Color cr = wellLook[i][j];
                    int redValue = cr.getRed();
                    int greenValue = cr.getGreen();
                    int blueValue = cr.getBlue();

                    Color storeColor = new Color(redValue, greenValue, blueValue);

                    int storePiece = 0;
                    for (int k = 0; k < 7; k++) {
                        if (tetraminoColors[k].getRGB() == storeColor.getRGB()) {
                            storePiece = k;
                        }
                    }
                    g2.setPaint(cr);
                    g2.fillRect(25*i, 25*j, 25, 25);

                    drawFixedBevel(g2, i, j, storePiece);
                }
            }
        }

    }//end method drawFallingPiece

    private TexturePaint createLightBackground() {

        BufferedImage bi = new BufferedImage(7, 7, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();
        big.setColor(new Color(201, 218, 242));
        big.fillRect(0, 0, 7, 7);
        big.setColor(new Color(225, 230, 234));
        big.drawLine(0, 0, 7, 7);

        Rectangle r = new Rectangle(0, 0, 7, 7);
        return new TexturePaint(bi, r);

    }//end method createLightBackground

    private TexturePaint createDarkBackground() {

        BufferedImage bi = new BufferedImage(7, 7, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();
        big.setColor(new Color(189, 206, 228));
        big.fillRect(0, 0, 7, 7);
        big.setColor(new Color(207, 211, 214));
        big.drawLine(0, 0, 7, 7);

        Rectangle r = new Rectangle(0, 0, 7, 7);
        return new TexturePaint(bi, r);

    }//end method createDarkBackground


    private TexturePaint createStartControls() {

        BufferedImage bi = new BufferedImage(510, 420, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();

        // Draw controls
        big.setPaint(BACK_LIGHT_BLUE);
        big.fillRect(0, 0, 510, 420);

        big.setPaint(Color.LIGHT_GRAY);

        big.fillRect(170, 250, 40, 20);
        big.fillRect(220, 250, 40, 20);
        big.fillRect(270, 250, 40, 20);
        big.fillRect(220, 220, 40, 20);
        big.fillRect(150, 310, 180, 25);

        big.setPaint(Color.GRAY);

        big.drawRect(170, 250, 40, 20);
        big.drawRect(220, 250, 40, 20);
        big.drawRect(270, 250, 40, 20);
        big.drawRect(220, 220, 40, 20);
        big.drawRect(150, 310, 180, 25);

        drawTriangle(194, 182, 194, 255, 260, 265, big);
        drawTriangle(234, 240, 246, 256, 264, 256, big);
        drawTriangle(284, 296, 284, 255, 260, 265, big);
        drawTriangle(234, 240, 246, 234, 225, 234, big);

        big.setFont(sSSmall);
        big.drawString("LEFT", 122, 265);
        big.drawString("DOWN", 215, 290);
        big.drawString("RIGHT", 320, 265);
        big.drawString("ROTATE", 210, 212);
        big.drawString("SPACE", 215, 327);
        big.drawString("HARD DROP", 193, 355);

        big.setFont(sSBig);
        big.drawString("P", 50, 400);
        big.drawString("Q", 210, 400);
        big.drawString("S", 372, 400);

        big.setFont(sSSmall);
        big.drawString(": PAUSE", 62, 399);
        big.drawString(": QUIT", 225, 399);
        big.drawString(": SOUND", 383, 399);

        Rectangle r = new Rectangle(0, 0, 510, 420);
        return new TexturePaint(bi, r);

    }//end method createStartControls

    private TexturePaint createPauseControls() {

        BufferedImage bi = new BufferedImage(275, 425, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();

        // Draw controls
        big.setPaint(BACK_LIGHT_BLUE);
        big.fillRect(0, 0, 275, 425);

        big.setPaint(Color.LIGHT_GRAY);

        big.fillRect(76, 250, 40, 20);
        big.fillRect(126, 250, 40, 20);
        big.fillRect(176, 250, 40, 20);
        big.fillRect(126, 220, 40, 20);
        big.fillRect(56, 310, 180, 25);

        big.setPaint(Color.GRAY);

        big.drawRect(76, 250, 40, 20);
        big.drawRect(126, 250, 40, 20);
        big.drawRect(176, 250, 40, 20);
        big.drawRect(126, 220, 40, 20);
        big.drawRect(56, 310, 180, 25);

        drawTriangle(100, 90, 100, 255, 260, 265, big);
        drawTriangle(139, 145, 151, 256, 264, 256, big);
        drawTriangle(190, 199, 190, 255, 260, 265, big);
        drawTriangle(139, 145, 151, 234, 225, 234, big);

        big.setFont(sSExtraSmall);
        big.drawString("LEFT", 42, 265);
        big.drawString("DOWN", 129, 290);
        big.drawString("RIGHT", 224, 265);
        big.drawString("ROTATE", 125, 212);
        big.drawString("SPACE", 126, 327);
        big.drawString("HARD DROP", 116, 355);

        big.drawString("P:", 56, 399);
        big.drawString("Q:", 116, 399);
        big.drawString("S:", 176, 399);

        big.drawString("PAUSE", 68, 399);
        big.drawString("QUIT", 131, 399);
        big.drawString("SOUND", 189, 399);

        Rectangle r = new Rectangle(0, 0, 275, 425);
        return new TexturePaint(bi, r);

    }//end method createPauseControls

    private void drawStartScreen(Graphics2D g2) {

        g2.setFont(sSBig);
        g2.setPaint(BACK_LIGHT_BLUE);
        g2.fillRect(0, 0, 12*25+210, 23*25);

        g2.setPaint(Color.GRAY);
        g2.drawString("CHOOSE LEVEL :", 165, 60);
        if (level == 1) {
            g2.setPaint(BACK_LIGHT_BLUE);
        }
        else if (levelDownHighlight) {
            g2.setPaint(Color.BLUE);
        }
        drawTriangle(195, 175, 195, 100, 110, 120, g2);
        g2.setPaint(Color.GRAY);
        g2.drawString("" + level, 232, 115);
        if (level == 10) {
            g2.setPaint(BACK_LIGHT_BLUE);
        }
        else if (levelUpHighlight) {
            g2.setPaint(Color.BLUE);
        }
        drawTriangle(285, 305, 285, 100, 110, 120, g2);

        g2.setPaint(startControls);
        g2.fillRect(0, 170, 510, 420);

        g2.setPaint(Color.GRAY);
        g2.setFont(sSBig);
        g2.drawString("PRESS ENTER TO START", 130, 500);

    }//end method drawStartScreen

    private void drawTriangle(int x1, int x2, int x3, int y1, int y2, int y3, Graphics2D g2) {

        int[] xPoints = {x1, x2, x3};
        int[] yPoints = {y1, y2, y3};

        g2.fillPolygon(xPoints, yPoints, 3);

    }//end method drawTriangle

    private void drawPauseScreen(Graphics2D g2) {

        g2.setPaint(new Color(221, 238, 255));
        g2.fillRect(27, 75, 247, 350);

        g2.setPaint(Color.GRAY);
        g2.drawLine(28, 75, 272, 75);
        g2.drawLine(28, 425, 272, 425);

        g2.drawString("PAUSED", 110, 120);

        g2.setPaint(pauseControls);
        g2.fillRect(30, 130, 235, 275);

        g2.setPaint(Color.GRAY);
    }//end method drawPauseScreen

    private void drawQuitScreen(Graphics2D g2) {

        g2.setPaint(new Color(221, 238, 255));
        g2.fillRect(27, 75, 247, 350);

        g2.setPaint(Color.GRAY);
        g2.drawLine(28, 75, 272, 75);
        g2.drawLine(28, 425, 272, 425);

        g2.drawString("QUIT GAME?", 95, 120);
        g2.drawString("Y FOR YES", 35, 160);
        g2.drawString("N FOR NO", 175, 160);

    }//end method drawQuitScreen

    private void drawGameOverScreen(Graphics2D g2, FontMetrics fm) {

        g2.setPaint(new Color(221, 238, 255));
        g2.fillRect(27, 75, 247, 350);

        g2.setPaint(Color.GRAY);
        g2.drawLine(29, 75, 272, 75);
        g2.drawLine(29, 425, 272, 425);

        g2.setPaint(Color.RED);
        g2.drawString("GAME OVER", 95, 120);
        g2.setPaint(Color.BLACK);
        g2.drawString("FINAL SCORE", 35, 180);
        String scoreTotalString = "" + score;
        g2.drawString(scoreTotalString, 235 - fm.stringWidth(scoreTotalString), 180);

        g2.setPaint(Color.LIGHT_GRAY);
        g2.fillRect(55, 210, 12, 12);
        g2.setPaint(Color.BLACK);
        if (restartHighlight) {
            g2.setPaint(Color.BLUE);
        }
        g2.drawRect(55, 210, 12, 12);
        g2.drawString("RESTART", 70, 222);

        g2.setPaint(Color.LIGHT_GRAY);
        g2.fillRect(175, 210, 12, 12);
        g2.setPaint(Color.BLACK);
        if (exitHighlight) {
            g2.setPaint(Color.BLUE);
        }
        g2.drawRect(175, 210, 12, 12);
        g2.drawString("QUIT", 190, 222);

        g2.setPaint(Color.BLACK);
        if (viewScoresHighlight) {
            g2.setPaint(Color.BLUE);
        }
        drawTriangle(55, 70, 55, 250, 260, 270, g2);
        g2.drawString("VIEW HIGH SCORES", 80, 267);

    }//end method drawGameOverScreen

    private void drawCells(Graphics2D g2) {

        // draw the well
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(4));

        g2.drawRect(24, 25, 252, 452);

        // draw the preview box
        g2.setPaint(BACK_LIGHT_BLUE);
        g2.fillRect(290, 25, 170, 85);

        g2.setColor(Color.BLACK);
        g2.drawRect(290, 25, 170, 85);

        // draw the stats boxes
        g2.setPaint(BACK_LIGHT_BLUE);
        g2.fillRect(290, 125, 200, 135);
        g2.setColor(Color.BLACK);
        g2.drawRect(290, 125, 200, 135);
        g2.drawLine(290, 170, 490, 170);
        g2.drawLine(290, 215, 490, 215);

    }//end method drawCells

    private void drawData(Graphics2D g2, FontMetrics fm) {

        g2.setColor(Color.BLACK);
        g2.setFont(sSBig);

        g2.drawString("LEVEL", 300, 155);
        String levelTotalString = "" + level;
        g2.drawString(levelTotalString, 440 - fm.stringWidth(levelTotalString), 155);

        g2.drawString("LINES", 300, 200);
        String lineTotalString = "" + clearedLines;
        g2.drawString(lineTotalString, 440 - fm.stringWidth(lineTotalString), 200);

        g2.drawString("SCORE", 300, 245);
        String scoreTotalString = "" + score;
        g2.drawString(scoreTotalString, 440 - fm.stringWidth(scoreTotalString), 245);

        if (!quitting) {
            g2.setPaint(Color.LIGHT_GRAY);
            g2.fillRect(300, 300, 12, 12);
            g2.setPaint(Color.BLACK);
            if (pauseHighlight) {
                g2.setPaint(Color.BLUE);
            }
            g2.drawRect(300, 300, 12, 12);
            if (paused) {
                g2.drawString("RESUME", 316, 313);
            }
            else {
                g2.drawString("PAUSE", 316, 313);
            }
        }

        if (!paused) {
            g2.setPaint(Color.LIGHT_GRAY);
            g2.fillRect(400, 300, 12, 12);
            g2.setPaint(Color.BLACK);
            if (quitHighlight) {
                g2.setPaint(Color.BLUE);
            }
            g2.drawRect(400, 300, 12, 12);
            if (quitting) {
                g2.drawString("RESUME", 416, 313);
            }
            else {
                g2.drawString("QUIT", 416, 313);
            }
        }

        if (!quitting && !paused) {
            g2.setPaint(Color.LIGHT_GRAY);
            g2.fillRect(400, 350, 12, 12);
            g2.setPaint(Color.BLACK);
            if (midGameRestartHighlight) {
                g2.setPaint(Color.BLUE);
            }
            g2.drawRect(400, 350, 12, 12);
            g2.drawString("RESTART", 420, 363);
        }

        // Draw sound symbol button
        drawSoundSymbol(g2);

    }//end method drawData

    private void drawSoundSymbol(Graphics2D g2) {

        if (soundIsOn && !paused && !quitting) {
            g2.setPaint(Color.BLACK);
            if (soundHighlight) {
                g2.setPaint(Color.BLUE);
            }
            g2.fillRect(320, 350, 10, 10);
            drawTriangle(335, 320, 335, 345, 355, 365, g2);
            g2.setStroke(new BasicStroke(3));
            Arc2D arc = new Arc2D.Double(323, 347, 20, 20, -35, 80, Arc2D.OPEN);
            g2.draw(arc);
            arc = new Arc2D.Double(321, 342, 30, 30, -40, 80, Arc2D.OPEN);
            g2.draw(arc);
        }
        else if (!paused && !quitting) {
            g2.setPaint(Color.GRAY);
            if (soundHighlight) {
                g2.setPaint(Color.BLUE);
            }
            g2.fillRect(320, 350, 10, 10);
            drawTriangle(335, 320, 335, 345, 355, 365, g2);
            g2.setPaint(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            Ellipse2D circle = new Ellipse2D.Double(315, 340, 30, 30);
            g2.draw(circle);
            g2.drawLine(320, 365, 340, 345);
        }

    }//end method drawSoundSymbol

    // Draw the falling piece
    private void drawPiece(Graphics2D g2) {

        g2.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetraminos[currentPiece][rotation]) {
            g2.fillRect((p.x + pieceOrigin.x) * 25,
                (p.y + pieceOrigin.y) * 25,
                25, 25);
        }
        drawFallingBevel(g2);

    }//end method drawPiece

    private void drawFallingBevel(Graphics2D g2) {

        for (Point p : Tetraminos[currentPiece][rotation]) {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect((p.x + pieceOrigin.x) * 25,
                (p.y + pieceOrigin.y) * 25,
                25, 25);
            g2.setColor(tetraminoDarkColors[currentPiece]);
            g2.setStroke(new BasicStroke(4));
            g2.drawLine((p.x + pieceOrigin.x) * 25 + 3,
                (p.y + pieceOrigin.y) * 25 + 22,
                (p.x + pieceOrigin.x) * 25 + 22,
                (p.y + pieceOrigin.y) * 25 + 22);
            g2.drawLine((p.x + pieceOrigin.x) * 25 +3,
                (p.y + pieceOrigin.y) * 25 + 22,
                (p.x + pieceOrigin.x) * 25 + 3,
                (p.y + pieceOrigin.y) * 25 + 3);
            g2.setColor(tetraminoLightColors[currentPiece]);
            g2.setStroke(new BasicStroke(4));
            drawTriangle((p.x + pieceOrigin.x) * 25 + 1, (p.x + pieceOrigin.x) * 25 + 5,
                (p.x + pieceOrigin.x) * 25 + 5,
                (p.y + pieceOrigin.y) * 25 + 1, (p.y + pieceOrigin.y) * 25 + 1,
                (p.y + pieceOrigin.y) * 25 + 5, g2);
            g2.drawLine((p.x + pieceOrigin.x) * 25 + 7,
                (p.y + pieceOrigin.y) * 25 + 3,
                (p.x + pieceOrigin.x) * 25 + 22,
                (p.y + pieceOrigin.y) * 25 + 3);
            drawTriangle((p.x + pieceOrigin.x) * 25 + 20, (p.x + pieceOrigin.x) * 25 + 24,
                (p.x + pieceOrigin.x) * 25 + 24,
                (p.y + pieceOrigin.y) * 25 + 20, (p.y + pieceOrigin.y) * 25 + 20,
                (p.y + pieceOrigin.y) * 25 + 24, g2);
            g2.drawLine((p.x + pieceOrigin.x) * 25 + 22,
                (p.y + pieceOrigin.y) * 25 + 18,
                (p.x + pieceOrigin.x) * 25 + 22,
                (p.y + pieceOrigin.y) * 25 + 3);
        }

    }//end method drawBevel

    private void drawFixedBevel(Graphics2D g2, int i, int j, int storePiece) {

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(25*i, 25*j, 25, 25);

        g2.setColor(tetraminoDarkColors[storePiece]);
        g2.setStroke(new BasicStroke(4));
        g2.drawLine((25*i) + 3,
            (25*j) + 22,
            (25*i) + 22,
            (25*j) + 22);
        g2.drawLine((25*i) +3,
            (25*j) + 22,
            (25*i) + 3,
            (25*j) + 3);
        g2.setColor(tetraminoLightColors[storePiece]);
        g2.setStroke(new BasicStroke(4));
        drawTriangle((25*i) + 1, (25*i) + 5, (25*i) + 5,
            (25*j) + 1, (25*j) + 1, (25*j) + 5, g2);
        g2.drawLine((25*i) + 7,
            (25*j) + 3,
            (25*i) + 22,
            (25*j) + 3);
        drawTriangle((25*i) + 20, (25*i) + 24, (25*i) + 24,
            (25*j) + 20, (25*j) + 20, (25*j) + 24, g2);
        g2.drawLine((25*i) + 22,
            (25*j) + 18,
            (25*i) + 22,
            (25*j) + 3);

    }//end method drawFixedBevel

    private void allowPause() {

        synchronized(lock) {
            while(paused || quitting) {
                try {
                    lock.wait();
                } catch(InterruptedException e) {
                    // do nothing
                }
            }
        }

    }//end method allowPause


    private void pauseActions() {

        paused = !paused;

            if (paused) {
                repaint();
                SoundUtils.stopPlaying();
            }
            else if(soundIsOn) {
                SoundUtils.startPlaying();
            }

        synchronized(lock) {
            lock.notifyAll();
        }

    }//end method pauseActions

    private void soundActions() {

        if (soundIsOn) {
            soundIsOn = false;
            SoundUtils.stopPlaying();
        }
        else {
            game.soundIsOn = true;
            SoundUtils.startPlaying();
        }
        repaint();

    }//end method soundActions

    private void restartActions(boolean midGame) {

        resetFields();
        if (midGame) {
            resetGame(true);
        }
        else {
            resetGame(false);
        }
        repaint();

    }//end method restartActions

    private void quitActions() {

        quitting = !quitting;

        if (quitting) {
            repaint();
            SoundUtils.stopPlaying();
        }
        else if (soundIsOn) {
            SoundUtils.startPlaying();
        }

        synchronized(lock) {
            lock.notifyAll();
        }

    }//end method quitActions

    private void gameOverActions() {

        gameOver = true;
        isAlive = false;
        repaint();
        SoundUtils.stopPlaying();

        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        DateFormat timeFormat = new SimpleDateFormat("h:mm a");
        Date date = new Date();
        String pattern = "#,###";
        DecimalFormat df = new DecimalFormat(pattern);

        try (BufferedWriter bw1 = new BufferedWriter(new FileWriter(dateFile, true));
             BufferedWriter bw2 = new BufferedWriter(new FileWriter(timeFile, true));
             BufferedWriter bw3 = new BufferedWriter(new FileWriter(scoreFile, true));
             BufferedWriter bw4 = new BufferedWriter(new FileWriter(plainScoreFile, true));
             BufferedWriter bw5 = new BufferedWriter(new FileWriter(levelFile, true))) {

            bw1.write("" + dateFormat.format(date) + "\n");
            bw2.write("" + timeFormat.format(date) + "\n");
            bw3.write("" + df.format(score) + "\n");
            bw4.write("" + score + "\n");
            bw5.write("" + level + "\n");
        } catch (IOException e) {
            //nothing
        }

    }//end method gameOverActions

    private boolean isActive() {

        return (!paused && !quitting && isStarted && !gameOver);

    }//end method isActive

    private void viewHighscores() {

        try {
            Desktop.getDesktop().browse(new URI("http://localhost/leaderboardFiles/leaderboard.php"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }//end method viewHighScores

    private static void resetGame(boolean midGame) {

        game.init();
        if (!midGame) {
            SoundUtils.startPlaying();
        }

    }//end method resetGame

    public static void main(String[] args) {

        JFrame f = new JFrame("tEVtris");
        f.setResizable(false);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(12*25+210, 23*25);

        game = new Tetris();
        game.init();
        f.add(game);
        f.setVisible(true);
        f.setLocationRelativeTo(null);

        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                //nothing
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (game.isActive()) game.slide(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (game.isActive()) game.slide(1);
                        break;
                    case KeyEvent.VK_UP:
                        if (game.isActive()) game.rotate();
                        break;
                    case KeyEvent.VK_DOWN:
                        if (game.isActive()) game.dropPiece();
                        break;
                    case KeyEvent.VK_SPACE:
                        if (game.isActive()) game.slamPiece();
                        break;
                    case KeyEvent.VK_P:
                        if (!game.quitting && game.isStarted && !game.gameOver) game.pauseActions();
                        break;
                    case KeyEvent.VK_Y:
                        if (game.quitting) System.exit(0);
                        break;
                    case KeyEvent.VK_N:
                        if (game.quitting) game.quitActions();
                        break;
                    case KeyEvent.VK_Q:
                        if (!game.paused && game.isStarted && !game.gameOver) game.quitActions();
                        break;
                    case KeyEvent.VK_S:
                        if (game.isActive()) game.soundActions();
                        break;
                    case KeyEvent.VK_ENTER:
                        if (!game.isStarted) {
                            game.isStarted = true;
                            for (int i = 1; i < game.level; i+=2) {
                                SoundUtils.setSongSpeed();
                            }
                            SoundUtils.resetStartNote();
                            SoundUtils.startPlaying();
                        }
                        break;
                }
            }//end method keyPressed

            @Override
            public void keyReleased(KeyEvent e) {
                //nothing
            }
        });//end addKeyListener

        Thread gameFlow = new Thread(() -> {
            while (!game.hasExited) {
                if (game.isAlive && game.isStarted) {
                    game.allowPause();
                    try {
                        if (game.isStalled && game.isSlammed) {
                            Thread.sleep(800);
                        }
                        else if (game.level > 9) {
                            if (game.isStalled && !game.isSlammed) {
                                Thread.sleep(800);
                            }
                            else {
                                Thread.sleep(150);
                            }
                        }
                        else {
                            if (game.isStalled) {
                                Thread.sleep(800);
                            }
                            else {
                                Thread.sleep(1100 - game.level*100);
                            }
                        }
                        if (game.isSlammed) {
                            game.isSlammed = false;
                            game.flashing = false;
                        }
                        else if (!game.isSlammed) {
                            game.isStalled = false;
                            game.flashing = false;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!game.paused && !game.quitting) {
                        game.dropPiece();

                    }
                }
                else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });//end gameFflow

        gameFlow.start();


        Thread songThread = new Thread(() -> {

            while (!game.hasExited) {
                try {
                    if (!game.isAlive) {
                        Thread.sleep(200);
                    }
                    while (game.isAlive) {
                        if (!game.isStarted || !game.soundIsOn) {
                            Thread.sleep(200);
                        }
                        while (game.soundIsOn && game.isStarted) {
                            if (!SoundUtils.getPlaying()) {
                                Thread.sleep(200);
                            } else {
                                SoundUtils.playSong();
                            }
                        }
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });//end songThread

        songThread.start();

    }//end method main

}//end class Tetris