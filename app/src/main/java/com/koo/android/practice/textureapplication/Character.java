package com.koo.android.practice.textureapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by koo on 2015/08/17.
 */
public class Character implements DrawableItem {

    private int[] imageIds = {
            R.drawable.st_std_l_1,
            R.drawable.st_std_l_2
    };

    public enum EVENTS {
        NOTHING,
        MOVERIGHT,
        MOVELEFT,
        JUMPRIGHT,
        JUMPLEFT
    }

    private EVENTS event = EVENTS.NOTHING;

    public int srcCWidth, srcCHeight;//元画像サイズ
    public int destCWidth, destCHeight;//出力画像サイズ

    public int displayWidth, displayHeight;
    private Bitmap img, img1, img2;

    private int division = 10;
    private int displayCharacterWidth;
    private int retio;
    //ジャンプ処理
    boolean jflag = false;
    int y_def = 360;
    int speed = 20;//ジャンプのスピード20くらいがいいかも
    float jf = 0.8f;//高さ調整など
    int y = y_def, y_prev = 0, y_temp = 0, y_move = 0;

    private int baseLeft = 0;
    private int baseBottom = 100;
    private int baseBottom_temp = 0;

    private int startX = -1;
    private int endX = 0;
    private int moveRangeX = 0;
    private int actionRange = 0;

    public Character(View view, int width, int height) {
        Resources r = view.getResources();
        img1 = BitmapFactory.decodeResource(r, imageIds[0]);
        img2 = BitmapFactory.decodeResource(r, imageIds[1]);
        img = img1;

        //元画像サイズ取得
        srcCWidth = img1.getWidth();
        srcCHeight = img1.getHeight();

        //ディスプレイサイズ取得
        displayWidth = width;
        displayHeight = height;

        //出力画像サイズ計算
        destCWidth = width / division;
        retio = srcCWidth / destCWidth;
        destCHeight = srcCHeight / retio;

        moveRangeX = destCWidth;//移動距離
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {

        doAction();
        Rect src = new Rect(0, 0, srcCWidth, srcCHeight);
        Rect dst = new Rect(getLeft(), getTop(), getRight(), getBottom());

        paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(dst, paint);

        canvas.drawBitmap(getImg(), src, dst, null);//character
        this.dst = dst;
    }

    private int rebound = 5;
    private boolean directionY = true;//true 下向き false 上向き

    private void doAction() {
        if (event == EVENTS.NOTHING && !directionY) {
            event = pollEvent();
        }
        switch (event) {
            case MOVERIGHT:
                if (isIntersect) {
                    event = EVENTS.NOTHING;
                    startX = -1;
                    baseLeft -= rebound;
                    break;
                } else {
                    directionY = true;
                }
                if (startX < 0) {
                    startX = getLeft();
                    endX = startX + moveRangeX;
                }

                if (getLeft() >= endX) {
                    event = EVENTS.NOTHING;
                    startX = -1;
                }
                baseLeft++;
                break;
            case MOVELEFT:
                if (isIntersect) {
                    event = EVENTS.NOTHING;
                    startX = -1;
                    baseLeft++;
                    break;
                } else {
                    directionY = true;
                }
                if (startX < 0) {
                    startX = getLeft();
                    endX = startX - moveRangeX;
                }

                if (getLeft() <= endX) {
                    startX = -1;
                    event = EVENTS.NOTHING;
                }
                baseLeft--;
                break;
            case JUMPRIGHT:
                if (isIntersect && startX >= 0) {
                    event = EVENTS.NOTHING;
                    startX = -1;
                    baseLeft--;
                    baseBottom += rebound;
                    initJumpParam();
                    break;
                }
                if (startX < 0) {
                    startX = getLeft();
                    endX = startX + moveRangeX;
                    baseBottom_temp = baseBottom;
                }

                if (getLeft() >= endX) {
                    event = EVENTS.NOTHING;
                    startX = -1;
                }
                baseBottom = getJp() + baseBottom_temp;
                baseLeft++;
                break;
            default:
                event = EVENTS.NOTHING;
                startX = -1;
                if (!isIntersect) {
                    if (directionY) {
//                        baseBottom--;
                        baseBottom-=3;
                    }
                } else {
                    baseBottom++;
                    directionY = false;
                }
                break;
        }
    }

    private Queue<EVENTS> queue = new LinkedList<EVENTS>();

    public boolean addEvent(EVENTS e) {
        return (queue.offer(e));
    }

    private EVENTS pollEvent() {
        EVENTS e = queue.poll();
        return (e == null) ? EVENTS.NOTHING : e;
    }

    public void skipEvent() {
        event = EVENTS.NOTHING;
    }

    public int getTop() {
        return displayHeight - destCHeight - baseBottom;
    }

    public int getLeft() {
        return baseLeft;
    }

    public int getRight() {
        return getLeft() + destCWidth;

    }

    public int getBottom() {
        return displayHeight - baseBottom;
    }

    public void changeImg() {
        if (img == img1) {
            img = img2;
        } else {
            img = img1;
        }
    }

    public Bitmap getImg() {
        return img;
    }

    private Rect dst;
    private boolean isIntersect = false;

    public boolean intersects(Rect r) {
        isIntersect = this.dst.intersect(r);
        return isIntersect;
    }

    private int getJp() {
        //ジャンプ処理
        if (jflag == true) {
            y_temp = y;
            y += (y - y_prev) + 1;
            y_prev = y_temp;
        }
        y_move = y_def - y;
        int jp = (int) (y_move * jf);

        if (jflag == false) {
            jflag = true;
            y_prev = y;
            y = y - speed;
        }
        return jp;
    }

    private void initJumpParam() {
        y = y_def;
        y_prev = 0;
        y_temp = 0;
        y_move = 0;
        jflag = false;
    }
}
