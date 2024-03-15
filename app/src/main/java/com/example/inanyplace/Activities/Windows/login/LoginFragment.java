package com.example.inanyplace.Activities.Windows.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inanyplace.Activities.MainActivity;
import com.example.inanyplace.Activities.Windows.register.RegisterFragment;
import com.example.inanyplace.Activities.Windows.resetpassword.ResetPasswordFragment;
import com.example.inanyplace.Activities.Windows.welcome.WelcomeFragment;
import com.example.inanyplace.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class LoginFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private Unbinder unbinder;

    @BindView(R.id.email_login_edit_text)
    EditText email;

    @BindView(R.id.password_login_edit_text)
    EditText password;

    @BindView(R.id.login_button)
    Button loginButton;

    @BindView(R.id.login_google_custom_button)
    ImageButton loginGoogleButton;

    @BindView(R.id.go_to_register_text_view)
    TextView goToRegister;

    @BindView(R.id.go_to_reset_text_view)
    TextView goToResetPassword;

    @BindView(R.id.login_fb_button)
    LoginButton facebookButton;

    @BindView(R.id.login_fb_custom_button)
    ImageButton loginFacebookButton;

    @BindView(R.id.button_back_login)
    ImageButton buttonBackWelcome;

    public static final int GOOGLE_SINGIN_CODE = 10005;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, root);

        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth = FirebaseAuth.getInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken("211495899436-mi5dhgj6430bai5a0n3vkho54cigf320.apps.googleusercontent.com").
                requestEmail().
                build();
        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTemp = email.getText().toString().trim();
                String passwordTemp = password.getText().toString().trim();

                if (emailTemp.isEmpty()) {
                    email.setError("You must complete this field.");
                    return;
                }
                if (passwordTemp.isEmpty()) {
                    password.setError("You must complete this field.");
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(emailTemp, passwordTemp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                Toast.makeText(getContext(), "Login with mail succesfully.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), MainActivity.class));
                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "The email has not been verified." +
                                                "\nPlease check your email.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getContext(), "Email or password are wrong.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //Google Log In
        loginGoogleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(sIntent, GOOGLE_SINGIN_CODE);
            }
        });

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, RegisterFragment.newInstance())
                        .commitNow();
            }
        });

        goToResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ResetPasswordFragment.newInstance())
                        .commitNow();
            }
        });

        //Facebook Log In
        loginFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookButton.performClick();
            }
        });

        facebookButton.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        facebookButton.setFragment(this);
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                FirebaseAuth.getInstance().signInWithCredential(credential).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                        Toast.makeText(getContext(), "Login with facebook succesfully.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getContext(), "Failed login with facebook.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonBackWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, WelcomeFragment.newInstance())
                        .commitNow();
            }
        });


        return root;
    }

    //Facebook and Google OnActivityResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == GOOGLE_SINGIN_CODE) {
            Task<GoogleSignInAccount> singInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount signInAccount = singInTask.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        startActivity(new Intent(getContext(), MainActivity.class));
                        Toast.makeText(getContext(), "Login with google succesfully.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                });

            } catch (ApiException e) {
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}