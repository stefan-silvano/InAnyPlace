package com.example.inanyplace.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inanyplace.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SplashActivity extends AppCompatActivity {

    private Animation animationFromTop;
    private Animation animationFromBottom;
    private Unbinder unbinder;

    @BindView(R.id.image_logo_splash)
    ImageView imageLogoSplash;

    @BindView(R.id.text_title_splash)
    TextView textTitleSplash;

    @BindView(R.id.text_description_splash)
    TextView textDescriptionSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        unbinder = ButterKnife.bind(this);

        animationFromTop = AnimationUtils.loadAnimation(this, R.anim.animation_from_top);
        animationFromBottom = AnimationUtils.loadAnimation(this, R.anim.animation_from_bottom);

        imageLogoSplash.setAnimation(animationFromTop);
        textTitleSplash.setAnimation(animationFromTop);
        textDescriptionSplash.setAnimation(animationFromBottom);


        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                //TODO
            }

            public void onFinish() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    finish();
                }
            }
        }.start();
    }
}