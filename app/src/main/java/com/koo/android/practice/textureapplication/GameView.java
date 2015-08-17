package com.koo.android.practice.textureapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * ゲーム画面を描画するためのTextureView
 */
public class GameView extends TextureView implements TextureView.SurfaceTextureListener {
    volatile private boolean mIsRunnable;
    private Thread mThread;

    /**
     * スーパークラスにはデフォルトコンストラクターがないため引数付きのコンストラクターを明示的に呼び出す * @param context Activity
     */
    public GameView(Context context) {
        super(context); // 親クラスのコンストラクターを引数 contextで呼び出す
        setSurfaceTextureListener(this);
    }


    public void start() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                int i = 0;
                while (true) {
                    // アプリ実行中繰り返し呼ばれる
                    long startTime = System.currentTimeMillis();
                    synchronized (GameView.this) {
                        if (!mIsRunnable) {
                            break; // ループを終了する
                        }
                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue; // キャンバスが取得できない時はループのやり直し
                        }
                        canvas.drawColor(Color.BLACK);

                        for (DrawableItem item : mItemList) {
                            // mItemListの内容が1つずつitemに渡される
                            item.draw(canvas, paint);
                        }

                        try {
                            if (i % 50 == 0) character.changeImg();
                            //ブロックとキャラの衝突処理
                            character.draw(canvas, paint);
                        } catch (NullPointerException e) {
                            Log.e(this.toString(), e.toString());
                        }
                        intersectsBlocks();//衝突処理はCharacterクラスで
                        unlockCanvasAndPost(canvas);
                    }
                    long sleepTime = 16 - System.currentTimeMillis() + startTime;
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                        }
                    }

                    i++;
                }
            }
        });
        mIsRunnable = true;
        mThread.start();
    }

    public void stop() {
        mIsRunnable = false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    private ArrayList<DrawableItem> mItemList;
    private float mBlockWidth;
    private float mBlockHeight;

    private Character character;

    public void readyObjects(int width, int height) {
        mBlockWidth = width / 10;
        mBlockHeight = height / 20;
        mItemList = new ArrayList<DrawableItem>();
        character = new Character(this, width, height);

        boolean[][] blockMap = {
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                //{false, false, false, false, true, false, false, false, false, false}
                //{true, true, true, true, true, true, true, true, true, true}
                {true, true, true, true, false, false, true, true, true, true}
        };

        for (int i = 0; i < blockMap.length; i++) {
            for (int j = 0; j < blockMap[i].length; j++) {
                if (blockMap[i][j]) {
                    float blockTop = i * mBlockHeight;
                    float blockLeft = j % 10 * mBlockWidth;
                    float blockBottom = blockTop + mBlockHeight;
                    float blockRight = blockLeft + mBlockWidth;
                    mItemList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));
                }
            }
        }
    }

    private boolean intersectsBlocks() {
        for (DrawableItem item : mItemList) {
            if (item == null) {
                continue;
            }
            if (!(item instanceof Block)) {
                continue;
            }
            Block block = (Block) item;
            Rect rect = block.rect;

            if (rect == null) {
                continue;
            }
            if (character.intersects(rect)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        synchronized (this) {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public boolean onBtnRight(View v) {
        character.addEvent(Character.EVENTS.MOVERIGHT);
        return true;
    }

    public boolean onBtnLeft(View v) {
        character.addEvent(Character.EVENTS.MOVELEFT);
        return true;
    }
    public boolean onBtnJRight(View v) {
        character.addEvent(Character.EVENTS.JUMPRIGHT);
        return true;
    }
}
