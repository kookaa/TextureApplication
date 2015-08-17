package com.koo.android.practice.textureapplication;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
    private GameView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = new GameView(this);
        setContentView(R.layout.layout);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_base);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.setMargins(0, 0, 0, 80);
        mView.setLayoutParams(layoutParams);

        layout.addView(mView);

        Button rightBtn = (Button) findViewById(R.id.move_right);
        Button leftBtn = (Button) findViewById(R.id.move_left);
        Button jRightBtn = (Button) findViewById(R.id.jump_right);

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.onBtnRight(v);
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.onBtnLeft(v);
            }
        });

        jRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.onBtnJRight(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.stop();
    }
}
