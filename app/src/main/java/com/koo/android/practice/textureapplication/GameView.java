package com.koo.android.practice.textureapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * ゲーム画面を描画するためのTextureView
 */
public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener {
    static final int BLOCK_COUNT = 100;
    volatile private boolean mIsRunnable;
    volatile private float mTouchedX;
    volatile private float mTouchedY;
    private Thread mThread;
    private int mLife;

    /**
     * スーパークラスにはデフォルトコンストラクターがないため引数付きのコンストラクターを明示的に呼び出す * @param context Activity
     */
    public GameView(Context context) {
        super(context); // 親クラスのコンストラクターを引数 contextで呼び出す
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
    }


    public void start() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
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
                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);
                        mBall.move();
                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;
                        if (ballLeft < 0 && mBall.getSpeedX() < 0 || ballRight >= getWidth() && mBall.getSpeedX() > 0) {
                            mBall.setSpeedX(-mBall.getSpeedX()); // 横方向の壁にぶつかったので横の速度を反転
                        }
                        if (ballTop < 0) {
                            mBall.setSpeedY(-mBall.getSpeedY()); // 縦方向の壁にぶつかったので縦の速度を反転
                        }
                        if (ballTop > getHeight()) {
                            if (mLife > 0) {
                                mLife--;
                                mBall.reset();
                            }
                        }
                        Block leftBlock = getBlock(ballLeft, mBall.getY());
                        Block topBlock = getBlock(mBall.getX(), ballTop);
                        Block rightBlock = getBlock(ballRight, mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(), ballBottom);
                        if (leftBlock != null) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                        }
                        if (topBlock != null) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                        }
                        if (rightBlock != null) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                        }
                        if (bottomBlock != null) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                            bottomBlock.collision();
                        }
                        // パッドとボールの衝突判定処理
                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();
                        if (ballBottom > padTop && ballBottom - ballSpeedY < padTop && padLeft < ballRight && padRight > ballLeft) {
                            if (ballSpeedY < mBlockHeight / 3) {
                                ballSpeedY *= -1.05f;
                            } else {
                                ballSpeedY = -ballSpeedY;
                            }
                            float ballSpeedX = mBall.getSpeedX() + (mBall.getX() - mTouchedX) / 10;
                            if (ballSpeedX > mBlockWidth / 5) {
                                ballSpeedX = mBlockWidth / 5;
                            }
                            mBall.setSpeedY(ballSpeedY);
                            mBall.setSpeedX(ballSpeedX);

                        }
                        for (DrawableItem item : mItemList) {
                            // mItemListの内容が1つずつitemに渡される
                            item.draw(canvas, paint);
                        }

                        if(i%50==0) character.changeImg();
                        character.draw(canvas,paint);
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
    private Pad mPad;
    private float mPadHalfWidth;
    private Ball mBall;
    private float mBallRadius;
    private float mBlockWidth;
    private float mBlockHeight;

    private Character character;

    public void readyObjects(int width, int height) {
        mBlockWidth = width / 10;
        mBlockHeight = height / 20;
        mItemList = new ArrayList<DrawableItem>();
        for (int i = 0; i < BLOCK_COUNT; i++) {
            float blockTop = i / 10 * mBlockHeight;
            float blockLeft = i % 10 * mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            float blockRight = blockLeft + mBlockWidth;
            mItemList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));
        }
        mPad = new Pad(height * 0.8f, height * 0.85f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;
        mBallRadius = width < height ? width / 40 : height / 40;
        mBall = new Ball(mBallRadius, width / 2, height / 2);
        mItemList.add(mBall);
        mLife = 5;

        character = new Character(this,width,height);
    }

    private Block getBlock(float x, float y) {
        int index = (int) (x / mBlockWidth) + (int) (y / mBlockHeight) * 10;
        if (0 <= index && index < BLOCK_COUNT) {
            Block block = (Block) mItemList.get(index);
            if (block.isExist()) {
                return block;
            }
        }
        return null;
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }
}
