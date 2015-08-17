package com.koo.android.practice.textureapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by koo on 2015/08/17.
 */
public class Character implements DrawableItem {

    private int[] imageIds = {
            R.drawable.st_std_l_1,
            R.drawable.st_std_l_2
    };

    public int srcCWidth, srcCHeight;//元画像サイズ
    public int destCWidth, destCHeight;//出力画像サイズ

    public int displayWidth, displayHeight;
    private Bitmap img, img1, img2;

    private int division = 8;
    private int displayCharacterWidth;
    private int retio;
    //ジャンプ処理
    boolean jflag = false;
    int y_def = 360;
    int speed = 20;//ジャンプのスピード20くらいがいいかも
    float jf = 0.8f;//高さ調整など
    int y = y_def, y_prev = 0, y_temp = 0, y_move = 0;

    public boolean moveRightFlg = false;
    private int baseLeft = 0;

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

    }

    @Override
    public void draw(Canvas canvas, Paint paint) {

        if(moveRightFlg) {

        }

        Rect src = new Rect(0, 0, srcCWidth, srcCHeight);
        Rect dst = new Rect(getLeft(), getTop(), getRight(), getBottom());

        canvas.drawBitmap(getImg(), src, dst, null);//character
    }

    private int getTop() {
        return displayHeight - destCHeight;
    }

    private int getLeft() {
        return 0;
    }

    private int getRight() {
        return getLeft() + destCWidth;

    }

    private int getBottom() {
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

}
