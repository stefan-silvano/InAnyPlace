package com.example.inanyplace.Activities.Windows.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inanyplace.Activities.Windows.login.LoginFragment;
import com.example.inanyplace.Activities.Windows.welcome.WelcomeFragment;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RegisterFragment extends Fragment {

    private Unbinder unbinder;
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.name_register_edit_text)
    EditText name;

    @BindView(R.id.email_register_edit_text)
    EditText email;

    @BindView(R.id.password_register_edit_text)
    EditText password;

    @BindView(R.id.password_check_register_edit_text)
    EditText rpassword;

    @BindView(R.id.register_button)
    Button registerButton;

    @BindView(R.id.go_to_login_text_view)
    TextView goToLogin;

    @BindView(R.id.button_back_register)
    ImageButton buttonBackWelcome;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        unbinder = ButterKnife.bind(this, root);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonBackWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, WelcomeFragment.newInstance())
                        .commitNow();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameTemp = name.getText().toString().trim();
                String emailTemp = email.getText().toString().trim();
                String passwordTemp = password.getText().toString().trim();
                String rpasswordTemp = rpassword.getText().toString().trim();

                if (nameTemp.isEmpty()) {
                    name.setError("The field cannot be empty.");
                    return;
                }

                if (!Validator.isValidEmail(emailTemp)) {
                    email.setError("Invalid email type.");
                    return;
                }

                if (!Validator.isValidPassword(passwordTemp)) {
                    password.setError("Password must have minimum 8 charcter, at least 1 number, " +
                            "1 upper letter, 1 lower letter and no whitespace.");
                    return;
                }

                if (!passwordTemp.equals(rpasswordTemp)) {
                    rpassword.setError("Password is not the same.");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(emailTemp, passwordTemp).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(nameTemp)
                                            .build();
                                    firebaseUser.updateProfile(profileUpdates);
                                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            firebaseAuth.signOut();
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.container, LoginFragment.newInstance())
                                                    .commitNow();
                                            Toast.makeText(root.getContext(), "Successfully registered." + "\nA verification email has been sent.",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                } else {
                                    Toast.makeText(root.getContext(), "An account with this email exists.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
