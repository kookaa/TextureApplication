package com.koo.android.practice.textureapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by koo on 2015/08/17.
 */
public class CharacterTest {

    private int[] imageIds = {
            R.drawable.st_std_l_1,
            R.drawable.st_std_l_2
    };

    public Point sizePoint;
    public Point displayPoint;
    private Bitmap img,img1,img2;

    private int division = 8;
    private int displayCharacterWidth;
    private int retio;
    //ジャンプ処理
    boolean jflag = false;
    int y_def = 360;
    int speed = 20;//ジャンプのスピード20くらいがいいかも
    float jf = 0.8f;//高さ調整など
    int y=y_def,y_prev=0,y_temp=0,y_move=0;

    public CharacterTest(View view, int width, int height){
        //画像の読み込み
        Resources r=view.getResources();
        img1= BitmapFactory.decodeResource(r, imageIds[0]);
        img2= BitmapFactory.decodeResource(r, imageIds[1]);
        img = img1;

        sizePoint = new Point(img1.getWidth(),img1.getHeight());
        displayPoint = new Point(width, height);
        retio = width/division;
        displayCharacterWidth =  sizePoint.x/retio;

    }

    public void draw(Canvas canvas, Paint paint) {
        int jp = getJp();


        Rect src = new Rect(0, 0, sizePoint.x, sizePoint.y);
        Rect dst = new Rect(
                0,
                displayPoint.y-(sizePoint.y/displayCharacterWidth) -jp,
                sizePoint.x/displayCharacterWidth,
                displayPoint.y-(sizePoint.y/displayCharacterWidth) + sizePoint.y/displayCharacterWidth -jp
        );
        canvas.drawRect(0,displayPoint.y,displayPoint.x,displayPoint.y,paint);//地面
        canvas.drawBitmap(getImg(),src,dst,null);//character

    }

    private int getJp() {
        //ジャンプ処理
        if(jflag==true){
            y_temp = y;
            y +=(y-y_prev)+1;
            y_prev = y_temp;
            if(y==y_def)
                jflag=false;

        }
        y_move=y_def-y;
        int jp = (int) (y_move*jf);

        if(jflag==false){
            jflag=true;
            y_prev=y;
            y=y-speed;
        }
        return jp;
    }

    public void changeImg(){
        if(img == img1) {
            img = img2;
        } else {
            img = img1;
        }
    }

    public Bitmap getImg(){
        return img;
    }
}
