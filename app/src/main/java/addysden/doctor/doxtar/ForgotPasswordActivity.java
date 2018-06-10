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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private UserForgotPswdTask mAuthTask = null;
    private EditText forgot_pswd_email;
    private ProgressBar forgot_pswd_progressbar;

    private CheckInternetConnection internetConn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgot_pswd_email = (EditText) findViewById(R.id.forgot_pswd_email);
        forgot_pswd_progressbar = (ProgressBar) findViewById(R.id.forgot_pswd_progress);

        AdView mAdView = (AdView) findViewById(R.id.forgot_pswd_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final Button button_send_OTP = (Button) findViewById(R.id.forgot_pswd_button);

        //Check internet connection
        internetConn = new CheckInternetConnection(this);
        if (internetConn.isNetworkConnected() == true) {
            button_send_OTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateEmail();
                }
            });
        } else {
            displayAlert();
        }
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

    private void validateEmail() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        forgot_pswd_email.setError(null);

        // Store values at the time of the login attempt.
        String email = forgot_pswd_email.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            forgot_pswd_email.setError(getString(R.string.error_field_required));
            focusView = forgot_pswd_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            forgot_pswd_email.setError(getString(R.string.error_invalid_email));
            focusView = forgot_pswd_email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserForgotPswdTask(email);
            mAuthTask.execute(email);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

            forgot_pswd_email.setVisibility(show ? View.GONE : View.VISIBLE);
            forgot_pswd_email.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    forgot_pswd_email.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            forgot_pswd_progressbar.setVisibility(show ? View.VISIBLE : View.GONE);
            forgot_pswd_progressbar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    forgot_pswd_progressbar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            forgot_pswd_progressbar.setVisibility(show ? View.VISIBLE : View.GONE);
            forgot_pswd_email.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserForgotPswdTask extends AsyncTask<String, String, String> {

        private final String mEmail;

        UserForgotPswdTask(String email) {
            mEmail = email;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String forgotPswdResponse = "";

            try {
                // Simulate network access.
                Thread.sleep(2000);
                final String forgotPswd_url = getString(R.string.url) + "/updateUserInfo.php";
                try {
                    URL forgotPswdURL = new URL(forgotPswd_url);
                    HttpURLConnection forgotPswdHTTP = (HttpURLConnection) forgotPswdURL.openConnection();
                    forgotPswdHTTP.setRequestMethod("POST");
                    forgotPswdHTTP.setDoOutput(true);
                    OutputStream forgotPswdOS = forgotPswdHTTP.getOutputStream();
                    BufferedWriter forgotPswdWriter = new BufferedWriter(new OutputStreamWriter(forgotPswdOS,"UTF-8"));
                    String forgotPswdData = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(params[0],"UTF-8") + "&" +
                            URLEncoder.encode("user_OTP","UTF-8") + "=G";
                    forgotPswdWriter.write(forgotPswdData);
                    forgotPswdWriter.flush();
                    forgotPswdWriter.close();
                    forgotPswdOS.close();

                    InputStream forgotPswdIS = forgotPswdHTTP.getInputStream();
                    BufferedReader forgotPswdReader = new BufferedReader(new InputStreamReader(forgotPswdIS,"iso-8859-1"));
                    String line = "";
                    while ((line = forgotPswdReader.readLine()) != null)
                    {
                        forgotPswdResponse += line;
                    }
                    forgotPswdReader.close();
                    forgotPswdIS.close();
                    forgotPswdHTTP.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                return "Something unfortunate happened.";
            }
            return forgotPswdResponse;
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);

            if(result.endsWith("does not exists") || result.startsWith("Generated OTP could not be sent")) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            } else {
                if(result.startsWith("Generated OTP Successfully")) {
                    Intent enter_otp_intent = new Intent(ForgotPasswordActivity.this, EnterOTPActivity.class);
                    enter_otp_intent.putExtra("user_email", forgot_pswd_email.getText().toString());
                    startActivity(enter_otp_intent);
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}
