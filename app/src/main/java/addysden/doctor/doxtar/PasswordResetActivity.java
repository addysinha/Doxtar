package addysden.doctor.doxtar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A login screen that offers login via email/password.
 */
public class PasswordResetActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserPswdResetTask mAuthTask = null;

    // UI references.
    private EditText mPasswordResetView;
    private EditText mRenterPasswordResetView;
    private View mResetFormProgressView;
    private View mPswdResetFormView;

    private CheckInternetConnection internetConn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        final String email = getIntent().getExtras().getString("userEmail");

        mPasswordResetView = (EditText) findViewById(R.id.pswd_reset_pswd);
        mRenterPasswordResetView = (EditText) findViewById(R.id.pswd_reset_renterpassword);

        AdView mAdView = (AdView) findViewById(R.id.pswd_reset_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mPasswordResetView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.pswd_reset_ime || id == EditorInfo.IME_NULL) {
                    attemptPswdReset(email);
                    return true;
                }
                return false;
            }
        });

        Button mPswdResetButton = (Button) findViewById(R.id.pswd_reset_button);
        internetConn = new CheckInternetConnection(this);
        mPswdResetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check internet connection
                if (internetConn.isNetworkConnected() == true) {
                    attemptPswdReset(email);
                } else {
                    displayAlert();
                }
            }
        });

        mPswdResetFormView = findViewById(R.id.pswd_reset_form);
        mResetFormProgressView = findViewById(R.id.pswd_reset_progress);
    }

    public void displayAlert()
    {
        new AlertDialog.Builder(this).setMessage("Please Check Your Internet Connection and Try Again")
                .setTitle("Network Error")
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){
                                finish();
                            }
                        })
                .show();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptPswdReset(String email) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mPasswordResetView.setError(null);
        mRenterPasswordResetView.setError(null);

        // Store values at the time of the login attempt.
        String password = mPasswordResetView.getText().toString();
        String renterPassword = mRenterPasswordResetView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordResetView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordResetView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(renterPassword) && !isPasswordValid(renterPassword)) {
            mRenterPasswordResetView.setError(getString(R.string.error_invalid_password));
            focusView = mRenterPasswordResetView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && isPasswordValid(password) && !TextUtils.isEmpty(renterPassword) && isPasswordValid(renterPassword)) {
            if (!password.equals(renterPassword)) {
                mPasswordResetView.setError(getString(R.string.error_password_match));
                focusView = mPasswordResetView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserPswdResetTask(password, renterPassword);
            mAuthTask.execute(email, password, renterPassword);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

            mPswdResetFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPswdResetFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPswdResetFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mResetFormProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mResetFormProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mResetFormProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mResetFormProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPswdResetFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserPswdResetTask extends AsyncTask<String, String, String> {

        private final String mPassword;
        private final String mRenterPassword;

        UserPswdResetTask(String password, String renterpassword) {
            mPassword = password;
            mRenterPassword = renterpassword;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String pswdResetResponse = "";

            try {
                // Simulate network access.
                Thread.sleep(2000);
                final String pswdReset_url = getString(R.string.url) + "/updateUserInfo.php";
                try {
                    URL pswdResetURL = new URL(pswdReset_url);
                    HttpURLConnection pswdResetHTTP = (HttpURLConnection) pswdResetURL.openConnection();
                    pswdResetHTTP.setRequestMethod("POST");
                    pswdResetHTTP.setDoOutput(true);
                    OutputStream pswdResetOS = pswdResetHTTP.getOutputStream();
                    BufferedWriter pswdResetWriter = new BufferedWriter(new OutputStreamWriter(pswdResetOS,"UTF-8"));
                    String pswdResetData = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(params[0],"UTF-8") + "&" +
                            URLEncoder.encode("user_pswd","UTF-8") + "=" + URLEncoder.encode(params[1],"UTF-8") + "&" +
                            URLEncoder.encode("user_pswd_renter","UTF-8") + "=" + URLEncoder.encode(params[2],"UTF-8");
                    pswdResetWriter.write(pswdResetData);
                    pswdResetWriter.flush();
                    pswdResetWriter.close();
                    pswdResetOS.close();

                    InputStream pswdResetIS = pswdResetHTTP.getInputStream();
                    BufferedReader pswdResetReader = new BufferedReader(new InputStreamReader(pswdResetIS,"iso-8859-1"));
                    String line = "";
                    while ((line = pswdResetReader.readLine()) != null)
                    {
                        pswdResetResponse += line;
                    }
                    pswdResetReader.close();
                    pswdResetIS.close();
                    pswdResetHTTP.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                return "Something unfortunate happened.";
            }
            return pswdResetResponse;
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            startActivity(new Intent(PasswordResetActivity.this, LoginActivity.class));
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

