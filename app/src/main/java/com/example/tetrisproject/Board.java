package com.example.tetrisproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Color;
import java.util.Random;

public class Board extends SurfaceView implements SurfaceHolder.Callback {

    private int boardWidth;
    private int boardHeight;
    private int xOffset; // Brikkeforflytning fra X-aksen (venstre side av brettet = x0, positiv akse mot høyre)
    private int yOffset; // Brikkeforflytning fra Y-aksen (toppen av brettet = y0, positiv akse nedover)
    private int XSTEP; // Pikselbredde for hver kloss
    private int YSTEP; // Pikselhøyde for hver kloss
    private int XMAX = 10; // Bredde
    private int YMAX = 16; // Høyde
    private int[][] grid = new int[YMAX][XMAX]; // Standard tetris-oppbygning på 10x16 blokker
    public TetrisThread tetrisThread;
    private boolean pieceFalling = false;
    private int[][] piecePosition;
    private int pieceColor;
    private final int MOVE_RIGHT = 1;
    private final int MOVE_LEFT = 2;

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        tetrisThread = new TetrisThread(holder, this);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.DKGRAY);
        if (!tetrisThread.isPaused()) {
            if (!pieceFalling) {
                drawPiece(canvas);
            }
        }
        updatePiece(canvas, tetrisThread.isPaused());
        updateBoard(canvas);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int touchPosX = (int) event.getX(0);
            int touchPosY = (int) event.getY(0);
            if (tetrisThread.isPaused()) return true;
            if (touchPosY > (boardHeight / 3) * 2) rotate();
            else if (touchPosX < boardWidth / 2 && xOffset > 0 && checkCollision(MOVE_LEFT)) {
                xOffset--;
            } else if (touchPosX > boardWidth / 2 && xOffset < XMAX-piecePosition[0].length && checkCollision(MOVE_RIGHT)){
                xOffset++;
            }
        }return true;
    }

    private boolean checkCollision(int rightleft) {
        switch (rightleft) {
            case MOVE_RIGHT:
                for (int row = 0; row < piecePosition.length; row++) {
                    for (int col = 0; col < piecePosition[row].length; col++) {
                        if(piecePosition[row][col] != 0){
                            if (xOffset + col < grid[0].length - 1 && grid[yOffset+row][xOffset+col+1] == 0) {
                                Log.i("Testlog", "Ledig plass høyre");
                                return true;
                            }else return false;
                        }
                    }
                }
                break;
            case MOVE_LEFT:
                for (int row = 0; row < piecePosition.length; row++) {
                    for (int col = 0; col < piecePosition[row].length; col++) {
                        if(piecePosition[row][col] != 0){
                            if (xOffset + col > 0 && grid[yOffset+row][xOffset+col-1] == 0){
                                Log.i("Testlog", "Ledig plass venstre");
                                return true;
                            }else return false;
                        }
                    }
                }
                break;
            default:
                return true;
        }return false;
    }

    public void rotate() {
        if(tetrisThread.isPaused()) return;
        int height = piecePosition.length;
        int width = piecePosition[0].length;
        int[][] rotate = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rotate[i][j] = piecePosition[height-j-1][i];
            }
        }
        while(xOffset + rotate[0].length > 10){
            xOffset--;
        }
        if((yOffset+rotate.length < 16))
        piecePosition = rotate;
    }

    private void updatePiece(Canvas canvas, boolean isPaused) {
        Paint piecePaint = new Paint();
        piecePaint.setColor(pieceColor);
        if (pieceFalling) {
            for (int row = 0; row < piecePosition.length; row++) {
                for (int col = 0; col < piecePosition[row].length; col++) {
                    if (piecePosition[row][col] != 0) {
                        if (yOffset + row < grid.length) {
                            if (grid[yOffset + row][xOffset + col] != 0) {//Sjekker om det er ledig plass under brikken
                                savePiece();
                                pieceFalling = false;
                                break;
                            }
                        }
                        if ((yOffset + (row)) >= grid.length) {
                            savePiece();
                            pieceFalling = false;
                            break;
                        }
                        canvas.drawRect(xOffset * XSTEP + col * XSTEP, yOffset * YSTEP + row * YSTEP, xOffset * XSTEP + (col + 1) * XSTEP, yOffset * YSTEP + (row + 1) * YSTEP, piecePaint);
                    }
                }
            }
            if (pieceFalling && !isPaused) yOffset++;
        }
    }

    private void savePiece() {
        for (int row = 0; row < piecePosition.length; row++) {
            for (int col = 0; col < piecePosition[row].length; col++) {
                if (piecePosition[row][col] != 0) {
                    grid[yOffset + row - 1][xOffset + col] = piecePosition[row][col];
                }
            }
        }
        xOffset = 0;
        yOffset = 0;
    }

    private void updateBoard(Canvas canvas) {
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.RED);
        int[] colors = {Color.BLACK, Color.rgb(139, 0, 139), Color.YELLOW, Color.rgb(255, 165, 0), Color.BLUE, Color.GREEN, Color.RED, Color.CYAN};

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] != 0) {
                    gridPaint.setColor(colors[grid[row][col]]);
                    gridPaint.setStyle(Paint.Style.FILL);
                    canvas.drawRect(col * XSTEP, row * YSTEP, (col + 1) * XSTEP, (row + 1) * YSTEP, gridPaint);
                }
                gridPaint.setStyle(Paint.Style.STROKE);
                gridPaint.setColor(Color.BLACK);
                canvas.drawRect(col * XSTEP, row * YSTEP, (col + 1) * XSTEP, (row + 1) * YSTEP, gridPaint);
            }
        }
    }

    public void drawPiece(Canvas canvas) {
        Paint piecePaint = new Paint();
        Random rnd = new Random();
        int pieceInt = rnd.nextInt(7);
        int[][] piece;
        piecePaint.setColor(Color.CYAN);
        switch (pieceInt) {
            case 0: // T-piece
                piece = new int[][]{{0, 1, 0}, {1, 1, 1}}; // 2x3
                pieceColor = Color.rgb(139, 0, 139);
                xOffset = 4;
                break;
            case 1: //O-piece
                piece = new int[][]{{2, 2}, {2, 2}}; // 2x2
                pieceColor = Color.YELLOW;
                xOffset = 4;
                break;
            case 2: //L-piece
                piece = new int[][]{{0, 0, 3}, {3, 3, 3}}; // 3x3
                pieceColor = Color.rgb(255, 165, 0);
                xOffset = 4;
                break;
            case 3: //J-piece
                piece = new int[][]{{4, 0, 0}, {4, 4, 4}}; // 3x3
                pieceColor = Color.BLUE;
                xOffset = 4;
                break;
            case 4: //S-piece
                piece = new int[][]{{0, 5, 5}, {5, 5, 0}}; // 3x3
                pieceColor = Color.GREEN;
                xOffset = 4;
                break;
            case 5: //Z-piece
                piece = new int[][]{{6, 6, 0}, {0, 6, 6}}; // 3x3
                pieceColor = Color.RED;
                xOffset = 4;
                break;
            case 6: //I-piece
                piece = new int[][]{{7}, {7}, {7}, {7}}; // 3x3
                pieceColor = Color.CYAN;
                xOffset = 4;
                break;
            default:
                piece = new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
                pieceColor = Color.WHITE;
        }
        piecePaint.setColor(pieceColor);
        for (int row = 0; row < piece.length; row++) {
            for (int col = 0; col < piece[row].length; col++) {
                if (piece[row][col] != 0) {
                    canvas.drawRect(xOffset * XSTEP + col * XSTEP, row * YSTEP, xOffset * XSTEP + col * XSTEP + XSTEP, row * YSTEP + YSTEP, piecePaint);
                }
            }
        }
        piecePosition = piece;
        pieceFalling = true;
    }

    public TetrisThread getThread() {
        return tetrisThread;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        tetrisThread.setRunning(true);
        tetrisThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        boardHeight = height;
        boardWidth = width;
        XSTEP = width / XMAX;
        YSTEP = height / YMAX;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        tetrisThread.setRunning(false);
        while (retry) {
            try {
                tetrisThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}