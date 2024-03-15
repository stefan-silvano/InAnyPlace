package com.example.inanyplace.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.inanyplace.Activities.Windows.welcome.WelcomeFragment;
import com.example.inanyplace.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, WelcomeFragment.newInstance())
                .commitNow();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, WelcomeFragment.newInstance())
                    .commitNow();
        }
    }
}