package com.koo.android.practice.textureapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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

    public enum EVENTS{
        NOTHING,
        MOVERIGHT,
        MOVELEFT
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
        destCHeight = srcCHeight /retio;

        moveRangeX = destCWidth;//移動距離
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {

        doAction();

        Rect src = new Rect(0, 0, srcCWidth, srcCHeight);
        Rect dst = new Rect(getLeft(), getTop(), getRight(), getBottom());
        canvas.drawBitmap(getImg(), src, dst, null);//character
        canvas.drawRect(dst,paint);
        this.dst = dst;
    }

    private void doAction() {
        if(event == EVENTS.NOTHING) {
            event = pollEvent();
        }
        switch (event) {
            case MOVERIGHT:
                if(startX < 0) {
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
                if(startX < 0) {
                    startX = getLeft();
                    endX = startX - moveRangeX;
                }

                if (getLeft() <= endX) {
                    event = EVENTS.NOTHING;
                    startX = -1;
                }
                baseLeft--;
                break;
        }
    }

    private Queue<EVENTS> queue = new LinkedList<EVENTS>();

    public boolean addEvent(EVENTS e) {
        return (queue.offer(e));
    }

    private EVENTS pollEvent(){
        EVENTS e = queue.poll();
        return (e == null) ? EVENTS.NOTHING : e;
    }

    public void skipEvent() {
        event = EVENTS.NOTHING;
    }

    public int getTop() {
        return displayHeight - destCHeight;
    }

    public int getLeft() {
        return baseLeft;
    }

    public int getRight() {
        return getLeft() + destCWidth;

    }

    public int getBottom() {
        return displayHeight;
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
    public boolean intersects(Rect r){
        return this.dst.intersect(r);
    }
}
