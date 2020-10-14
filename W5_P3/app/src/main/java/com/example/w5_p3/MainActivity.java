package com.example.w5_p3;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements TopFragment.ScoreListener, BottomFragment.RestartListener {

    TopFragment topFragment;
    BottomFragment bottomFragment;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        topFragment = (TopFragment) getFragmentManager().findFragmentById(R.id.topFragment);
        bottomFragment = (BottomFragment) getFragmentManager().findFragmentById(R.id.bottomFragment);
    }

    @Override
    public void setScore(int score) {
        bottomFragment.setScore(score);
    }

    @Override
    public void restartGame() {
        topFragment.restartGame();
    }
}
