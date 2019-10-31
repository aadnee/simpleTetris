package com.example.tetrisproject;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


public class TetrisThread extends Thread {
    private final static int sleepInterval = 500;
    private SurfaceHolder surfaceHolder;
    private Board tetrisBoard;
    private boolean run = false;
    private boolean pause = false;

    public TetrisThread(SurfaceHolder holder, Board tetrisBoard) {
        this.tetrisBoard = tetrisBoard;
        this.surfaceHolder = holder;
    }

    @Override
    public void run() {
        Canvas c;
        while (isRunning()) {
            c=null;
            try {
                c=surfaceHolder.lockCanvas(null);
                synchronized(surfaceHolder) {
                    tetrisBoard.draw(c);
                }
            } finally {
                if (c!=null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
            mySleep(sleepInterval);
        }
    }
    public void mySleep(int length) {
        try {
            sleep(length);
        }
        catch (InterruptedException e) {
            Log.i(e.toString(),"mySleep interrupted");
        }
    }
    public void setRunning(boolean run) {
        this.run=run;
    }
    public boolean isRunning() {
        return run;
    }
    public void setPaused(boolean p) {
        synchronized(surfaceHolder) {
            pause = p;
        }
    }
    public boolean isPaused() {
        synchronized(surfaceHolder) {
            return pause;
        }
    }
}
