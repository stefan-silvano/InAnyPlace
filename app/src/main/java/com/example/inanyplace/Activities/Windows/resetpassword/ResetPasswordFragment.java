package com.example.inanyplace.Activities.Windows.resetpassword;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.inanyplace.Activities.Windows.login.LoginFragment;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Validator;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ResetPasswordFragment extends Fragment {

    private Unbinder unbinder;
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.email_reset_button)
    Button resetButton;

    @BindView(R.id.email_reset_text)
    EditText emailReset;

    @BindView(R.id.email_reset_verify_text)
    EditText emailResetVerify;

    @BindView(R.id.go_to_login_reset_text_view)
    TextView goToLogin;


    public static ResetPasswordFragment newInstance() {
        return new ResetPasswordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reset_password, container, false);

        unbinder = ButterKnife.bind(this,root);

        firebaseAuth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailTemp = emailReset.getText().toString().trim();
                String emailVerifyTemp = emailResetVerify.getText().toString().trim();

                if (emailTemp.isEmpty()) {
                    emailReset.setError("The field cannot be empty.");
                    return;
                } else if (!Validator.isValidEmail(emailTemp)) {
                    emailReset.setError("Invalid email type.");
                    return;
                }

                if (emailVerifyTemp.isEmpty()) {
                    emailResetVerify.setError("The field cannot be empty.");
                    return;
                } else if (!emailTemp.equals(emailVerifyTemp)) {
                    emailResetVerify.setError("Email is not the same.");
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(emailTemp);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, LoginFragment.newInstance())
                        .commitNow();
                Toast.makeText(root.getContext(), "A password reset email has been sent.", Toast.LENGTH_SHORT).show();
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, LoginFragment.newInstance())
                        .commitNow();
            }
        });

        return root;
    }

}
