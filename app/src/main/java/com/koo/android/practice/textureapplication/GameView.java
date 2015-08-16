package com.koo.android.practice.textureapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

/**
 * Created by koo on 2015/08/16.
 */
public class GameView extends TextureView implements TextureView.SurfaceTextureListener {

    private Thread mThread;
    volatile private boolean mlsRunnable;

    private Bitmap img1,img2;

    private int dw,dh;
    private final static int groundH = 10;

    public GameView(Context context) {
        super(context);
        setSurfaceTextureListener(this);

        //画像の読み込み
        Resources r=getResources();
        img1= BitmapFactory.decodeResource(r, R.drawable.st_std_l_1);
        img2= BitmapFactory.decodeResource(r, R.drawable.st_std_l_2);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void start(){
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0xff4bb520);
                int i = 0;
                Bitmap img = img1;
                int w = img1.getWidth();
                int h = img1.getHeight();

                // 描画元の矩形イメージ
                Rect src = new Rect(0, 0, w, h);
                // 描画先の矩形イメージ

                while (mlsRunnable) {
                    dw = getWidth();
                    dh = getHeight();

                    if(dw == 0) continue;
                    int dwt = dw/8;
                    int cw = w/dwt;
                    int ch = getHeight()/5;
                    Canvas canvas = lockCanvas();
                    if(canvas == null) {
                        continue;
                    }
                    canvas.drawColor(0xffc6eeb6);
                    if(i < 50) {//50ごとに画像入れ替え
                        img = img1;
                    } else {
                        img = img2;
                    }
                    if (i == 100) i = 0;

                    Rect dst = new Rect(0, getHeight()-(h/cw) - groundH, w/cw, getHeight()-(h/cw) + h/cw - groundH);

                    canvas.drawRect(0,dh-groundH,dw,dh,paint);//地面
                    canvas.drawBitmap(img,src,dst,null);//character

                    unlockCanvasAndPost(canvas);
                    i++;
                }
            }
        });

        mlsRunnable = true;
        mThread.start();
    }

    public void stop(){
        mlsRunnable = false;
    }
}
