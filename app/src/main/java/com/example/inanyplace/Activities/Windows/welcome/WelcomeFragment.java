package com.example.inanyplace.Activities.Windows.welcome;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.inanyplace.Activities.Windows.login.LoginFragment;
import com.example.inanyplace.Activities.Windows.register.RegisterFragment;
import com.example.inanyplace.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WelcomeFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.button_welcome_login)
    Button buttonWelcomeLogin;

    @BindView(R.id.button_welcome_singin)
    Button buttonWelcomeSingin;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInsanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        unbinder = ButterKnife.bind(this, root);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        buttonWelcomeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, LoginFragment.newInstance())
                            .commitNow();
            }
        });

        buttonWelcomeSingin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, RegisterFragment.newInstance())
                            .commitNow();
            }
        });

        return root;
    }
}
