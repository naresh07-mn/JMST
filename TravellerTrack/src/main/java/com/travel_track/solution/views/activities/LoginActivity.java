package com.travel_track.solution.views.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;

import android.os.Build;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputLayout;
import com.travel_track.solution.R;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.data.PreferenceManager;
import com.travel_track.solution.model.LoginDetails;
import com.travel_track.solution.utils.DeviceUuidFactory;

import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    // UI references.
    @BindView(R.id.username)
    @Nullable

    EditText mEmailView;

    @BindView(R.id.password)
    @Nullable
    EditText mPasswordView;

    @BindView(R.id.radioGroup)
    @Nullable
    RadioGroup radioGroup;

    @BindView(R.id.textInputUsername)
    @Nullable
    TextInputLayout textInputUsername;

    @BindView(R.id.textInputPassword)
    @Nullable
    TextInputLayout textInputPassword;

    @BindView(R.id.email_sign_in_button)
    @Nullable
    Button mEmailSignInButton;

    @BindView(R.id.login_progress)
    @Nullable
    View mProgressView;
    @BindView(R.id.login_form)
    @Nullable
    View mLoginFormView;

    ApiHandler.LoginAPIService loginAPIService;
    //ApiHandler.GuestLoginAPIService guestLoginAPIService;

    boolean userTypeDriver = false;

    @Override
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_login, null);
        setViewContents(pageContent);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            userTypeDriver = checkedId == R.id.driver_login;
        });

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        hideKeyBoard(mPasswordView, true);

        // Reset errors.
        textInputUsername.setError(null);
        textInputPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            textInputPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            textInputUsername.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            textInputUsername.setError(getString(R.string.error_invalid_username));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            requestLogin(email, password);
        }
    }

    private void requestLogin(String email, String password) {
        loginAPIService = RestClient.getClient().create(ApiHandler.LoginAPIService.class);
        Call<LoginDetails> call;
        /*if(userTypeDriver){*/
            loginAPIService = RestClient.getClient().create(ApiHandler.LoginAPIService.class);
            call = loginAPIService.fetchLoginDetails(email, password, new DeviceUuidFactory(this).getDeviceUuid().toString());
        /*} else {
            guestLoginAPIService = RestClient.getClient().create(ApiHandler.GuestLoginAPIService.class);
            call = guestLoginAPIService.GuestLogin(email, password, 1);
        }*/
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                Log.e("Login", "Got success : " + response.message());
                showProgress(false);
                if (response!=null && response.body()!=null && response.body().getMessage()!=null
                        && response.body().getMessage().get(0)!=null
                        && response.body().getMessage().get(0).getResultcode()!=null
                        && response.body().getMessage().get(0).getResultcode().equalsIgnoreCase("0")) {
                    PreferenceManager.getInstance(getApplicationContext()).saveUserInfo(response.body().getData().get(0));
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }

                else {
                    textInputPassword.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                Log.e("errorLogin", "Got error : " + t.getLocalizedMessage());
                showProgress(false);
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return !email.contains(" ");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

