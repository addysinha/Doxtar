package addysden.doctor.doxtar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import java.util.List;

public class EnterOTPActivity extends AppCompatActivity {

    private String mEmail;
    private EditText enter_OTP_email;
    private EditText enter_OTP_otp;
    private Button enter_OTP_button;
    private UserEnterOTPTask mAuthTask = null;
    private View enter_OTP_form;
    private ProgressBar enter_OTP_progress;
    private String intentAction;

    private CheckInternetConnection internetConn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        enter_OTP_email = (EditText) findViewById(R.id.enter_OTP_email);
        enter_OTP_otp = (EditText) findViewById(R.id.enter_OTP_otp);
        enter_OTP_button = (Button) findViewById(R.id.enter_OTP_button);
        enter_OTP_form = findViewById(R.id.enter_OTP_form);
        enter_OTP_progress = (ProgressBar) findViewById(R.id.enter_OTP_progress);

        AdView mAdView = (AdView) findViewById(R.id.otp_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(getIntent().getExtras().getString("user_email") != null) {
            mEmail = getIntent().getExtras().getString("user_email");
            enter_OTP_email.setText(mEmail);
            intentAction="forgotPswd";
        } else {
            Uri data = getIntent().getData();
            String scheme = data.getScheme(); // "http"
            String host = data.getHost(); // "addysden.com"
            List<String> params = data.getPathSegments();
            String action = params.get(0); // "action"
            if(action.equals("activate")) {
                String param1 = params.get(1); // "param1"
                String param2 = params.get(2); // "param2"

                enter_OTP_email.setText(param1);
                enter_OTP_otp.setText(param2);
            }
            intentAction="activateUser";
        }

        //Check internet connection
        internetConn = new CheckInternetConnection(this);
        enter_OTP_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConn.isNetworkConnected() == true) {
                    validateEmail();
                } else {
                    displayAlert();
                }
            }
        });
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
        // Reset errors.
        enter_OTP_email.setError(null);
        enter_OTP_otp.setError(null);

        // Store values at the time of the login attempt.
        String email = enter_OTP_email.getText().toString();
        String otp = enter_OTP_otp.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(otp)) {
            enter_OTP_otp.setError(getString(R.string.error_field_required));
            focusView = enter_OTP_otp;
            cancel = true;
        } else if (!TextUtils.isEmpty(otp) && !isOTPValid(otp)) {
            enter_OTP_otp.setError(getString(R.string.error_invalid_otp));
            focusView = enter_OTP_otp;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            enter_OTP_email.setError(getString(R.string.error_field_required));
            focusView = enter_OTP_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            enter_OTP_email.setError(getString(R.string.error_invalid_email));
            focusView = enter_OTP_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserEnterOTPTask(email, otp);
            mAuthTask.execute(email, otp);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isOTPValid(String otp) {
        boolean result=false;
        try {
            if(otp.length() == 5) {
                Integer otp_int = Integer.parseInt(otp);
                result=true;
            }
            else
                result=false;
        }
        catch (NumberFormatException nfe) {
            return result;
        }
        return result;
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

            enter_OTP_form.setVisibility(show ? View.GONE : View.VISIBLE);
            enter_OTP_form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    enter_OTP_form.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            enter_OTP_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            enter_OTP_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    enter_OTP_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            enter_OTP_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            enter_OTP_form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserEnterOTPTask extends AsyncTask<String, String, String> {

        private final String mEmail;
        private final String mOTP;

        UserEnterOTPTask(String email, String otp)
        {
            mEmail = email;
            mOTP = otp;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String enterOTPResponse = "";

            try {
                // Simulate network access.
                Thread.sleep(2000);
                final String enterOTP_url = getString(R.string.url) + "/updateUserInfo.php";
                try {
                    URL enterOTPURL = new URL(enterOTP_url);
                    HttpURLConnection enterOTPHTTP = (HttpURLConnection) enterOTPURL.openConnection();
                    enterOTPHTTP.setRequestMethod("POST");
                    enterOTPHTTP.setDoOutput(true);
                    OutputStream enterOTPOS = enterOTPHTTP.getOutputStream();
                    BufferedWriter enterOTPWriter = new BufferedWriter(new OutputStreamWriter(enterOTPOS,"UTF-8"));
                    String enterOTPData = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(params[0],"UTF-8") + "&" +
                            URLEncoder.encode("user_OTP","UTF-8") + "=C&" + URLEncoder.encode("user_OTP_val","UTF-8") + "=" +
                            URLEncoder.encode(params[1],"UTF-8") + "&" +
                            URLEncoder.encode("intent_action", "UTF-8") + "=" + URLEncoder.encode(intentAction,"UTF-8");
                    enterOTPWriter.write(enterOTPData);
                    enterOTPWriter.flush();
                    enterOTPWriter.close();
                    enterOTPOS.close();

                    InputStream enterOTPIS = enterOTPHTTP.getInputStream();
                    BufferedReader enterOTPReader = new BufferedReader(new InputStreamReader(enterOTPIS,"iso-8859-1"));
                    String line = "";
                    while ((line = enterOTPReader.readLine()) != null)
                    {
                        enterOTPResponse += line;
                    }
                    enterOTPReader.close();
                    enterOTPIS.close();
                    enterOTPHTTP.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                return "Something unfortunate happened.";
            }
            return enterOTPResponse;
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);

            if(result.equals("found")) {
                if(intentAction.equals("forgotPswd")) {
                    Intent resetPswdIntent = new Intent(EnterOTPActivity.this, PasswordResetActivity.class);
                    resetPswdIntent.putExtra("userEmail", enter_OTP_email.getText().toString());
                    startActivity(resetPswdIntent);
                } else if (intentAction.equals("activateUser")) {
                    Intent activateUserIntent = new Intent(EnterOTPActivity.this, LoginActivity.class);
                    activateUserIntent.putExtra("userEmail", enter_OTP_email.getText().toString());
                    startActivity(activateUserIntent);
                }
            }
            else {
                if(result.endsWith("not authorized for Password Reset")) {
                    startActivity(new Intent(EnterOTPActivity.this, LoginActivity.class));
                }
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}
