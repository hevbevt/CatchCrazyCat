package com.jikexueyuan.duan.catchcrazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;

/** Created by Duan on 2015/12/9.
 */
public class Playground extends SurfaceView implements View.OnTouchListener{

    private static int width;
    private static final int COL = 10;
    private static final int ROW = 10;
    private static final int BLOCK = 10;//默认添加的路障数量

    private Dot matrix[][] = new Dot[ROW][COL];
    private Dot cat;

    public Playground(Context context) {
        super(context);
        getHolder().addCallback(callback);
        matrix = new Dot[ROW][COL];
        for (int i = 0; i < ROW ; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Dot(j, i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }

    private Dot getDot(int x, int y) {
        return matrix[y][x];
    }

    private boolean isAtEdge(Dot d) {
        if (d.getX() * d.getY() == 0 || d.getX() + 1 == COL || d.getY() + 1 == ROW) {
            return true;
        }
        return false;
    }

    private Dot getNeighbor(Dot dot, int dir) {
        switch (dir) {
            case 1:
                return getDot(dot.getX() - 1, dot.getY());
            case 2:
                if (dot.getY() % 2 == 0){
                    return getDot(dot.getX() - 1, dot.getY() - 1);
                } else {
                    return getDot(dot.getX(), dot.getY() - 1);
                }

            case 3:
                if (dot.getY() % 2 == 0){
                   return getDot(dot.getX(), dot.getY() - 1);
                } else {
                   return getDot(dot.getX() + 1, dot.getY() - 1);
                }
            case 4:
                return getDot(dot.getX() + 1, dot.getY());

            case 5:
                if (dot.getY() % 2 == 0){
                  return getDot(dot.getX(), dot.getY() + 1);
                } else {
                  return   getDot(dot.getX() + 1, dot.getY() + 1);
                }

            case 6:
                if (dot.getY() % 2 == 0){
                    return getDot(dot.getX() - 1, dot.getY() + 1);
                } else {
                    return getDot(dot.getX(), dot.getY() + 1);
                }
            default:
        }
        return null;
    }

    private int getDistance(Dot dot, int dir) {
        int distance = 0;
        Dot ori = dot, next;
        while (true){
            next = getNeighbor(ori, dir);
            if (next.getStatus() == Dot.STATUS_ON) {
                return distance * -1;
            }

            if (isAtEdge(next)) {
                distance++;
                return distance;
            }
            distance++;
            ori = next;
        }
    }

    private void moveTo(Dot dot) {
        dot.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
        cat.setXY(dot.getX(), dot.getY());
    }

    private void move() {
        if (isAtEdge(cat)) {
            lose();
            return;
        }
        Vector<Dot> avaliable = new Vector<>();
        for (int i = 1; i < 7; i++) {
            Dot n = getNeighbor(cat, i);
            if (n.getStatus() == Dot.STATUS_OFF) {
                avaliable.add(n);
            }
        }
        if (avaliable.size() == 0) {
            win();
        } else if (avaliable.size() == 1){
            moveTo(avaliable.get(0));
        } else {
            int t = (int) ((Math.random() * 1000) % avaliable.size());
            moveTo(avaliable.get(t));
        }
    }

    private void lose() {
        Toast.makeText(getContext(), "Lose", Toast.LENGTH_LONG).show();
        initGame();
    }

    private void win() {
        Toast.makeText(getContext(), "Win", Toast.LENGTH_LONG).show();
        initGame();
    }
    private void reDraw() {
        Canvas c = getHolder().lockCanvas();
        c.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        paint.setFlags(paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < ROW; i++) {
            int offset = 0;
            if (i % 2 != 0) {
                offset = width / 2;
            }
            for (int j = 0; j < COL; j++) {
                Dot one = getDot(j, i);
                switch (one.getStatus()) {
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0XFFFFAA00);
                        break;
                    default:
                        break;
                }
                c.drawOval(new RectF(one.getX() * width + offset, one.getY() * width,
                        (one.getX() + 1) * width + offset, (one.getY() + 1) * width), paint);
            }
        }
        getHolder().unlockCanvasAndPost(c);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            reDraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Playground.width = width / (COL + 1);
            reDraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //Toast.makeText(getContext(),event.getX() + ":" + event.getY(), Toast.LENGTH_SHORT).show();
            int x, y;
            y = (int) (event.getY() / width);
            if (y % 2 == 0) {
                x = (int) (event.getX() / width);
            } else {
                x = (int) ((event.getX() - width / 2) / width) ;
            }
            if (x + 1 > COL || y + 1 > ROW) {
                for (int i = 1; i < 7; i++) {
                    System.out.println(i + "@" + getDistance(cat, i));
                }
            }else if (getDot(x, y).getStatus() == Dot.STATUS_OFF){
                getDot(x, y).setStatus(Dot.STATUS_ON);
                move();
            }
            reDraw();
        }
        return true;
    }

    private void initGame() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j].setStatus(Dot.STATUS_OFF);
            }
        }
        cat = new Dot(4, 5);
        getDot(4, 5).setStatus(Dot.STATUS_IN);
        for (int i = 0; i < BLOCK; ){
            int x = (int)(Math.random() * 1000) % COL;
            int y = (int)(Math.random() * 1000) % ROW;
            if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
                getDot(x, y).setStatus(Dot.STATUS_ON);
                i++;
            }
        }
    }
}
