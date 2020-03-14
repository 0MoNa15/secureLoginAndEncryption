package com.mona15.loginencryption.ui.ui.login;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mona15.loginencryption.R;
import com.mona15.loginencryption.ui.data.EncryptionAsymmetric;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    TextView mUserNameOnlyTextView, mUserNameEncryptedTextView, mPasswordNameOnlyTextView, mPasswordncryptedTextView;
    EncryptionAsymmetric mEncryptionAsymmetric;
    String mUserName = "";
    String mPassword = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        mEncryptionAsymmetric = new EncryptionAsymmetric();

        try {
            mEncryptionAsymmetric.generateKayPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUserNameOnlyTextView = findViewById(R.id.text_view_user_only);
        mPasswordNameOnlyTextView = findViewById(R.id.text_view_password_only);

        mUserNameEncryptedTextView = findViewById(R.id.text_view_user_encrypted);
        mPasswordncryptedTextView = findViewById(R.id.text_view_password_encrypted);


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                // Show data
                mUserNameEncryptedTextView.setText(String.format("%s%s%s", getString(R.string.prompt_user_points_encrypted), "   ", mUserName));
                mPasswordncryptedTextView.setText(String.format("%s%s%s", getString(R.string.prompt_password_points_encrypted), "   ", mPassword));

                try {
                    mUserNameOnlyTextView.setText(String.format("%s%s%s", getString(R.string.prompt_user_points), "   ", mEncryptionAsymmetric.descrypt(mUserName)));
                    mPasswordNameOnlyTextView.setText(String.format("%s%s%s", getString(R.string.prompt_user_points), "   ", mPassword));
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                // We send the encrypted data username with ecryptionAsymmetric

                try {
                    mUserName = mEncryptionAsymmetric.encrypt(usernameEditText.getText().toString());
                    mPassword = passwordEditText.getText().toString();

                    loginViewModel.login(mUserName, mPassword);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());*/
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
