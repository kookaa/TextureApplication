package com.koo.android.practice.textureapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by kenz on 15/04/19.
 */
public class Block implements DrawableItem {

    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;

    public Block(float top, float left, float bottom, float right) {
        mTop = top;
        mLeft = left;
        mBottom = bottom;
        mRight = right;
        mHard = 1;
    }

    private boolean mIsCollision = false; // 衝突状態を記録するフラグ 27

    /**
     * ボールが衝突した時の処理
     */
    public void collision() {
        mIsCollision = true; // 衝突したことだけを記録し、実際の破壊はdraw()時に行う
    }

    private boolean mIsExist = true; // ブロックが破壊されていないか

    /**
     * ブロックが破壊されていないか
     * @return 破壊されていない場合true
     */
    public boolean isExist() {
        return mIsExist;
    }

    /**
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint) {
        if (mIsExist) {
            // 耐久力が0以上の場合のみ
            if (mIsCollision) {
                mHard--;
                mIsCollision = false;
                if (mHard <= 0) {
                    mIsExist = false;
                    return;
                }
            }
        }else{
            return;
        }
        // 塗りつぶし部分を描画
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mLeft, mTop, mRight, mBottom, paint); // 枠線部分を描画
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
    }
}
