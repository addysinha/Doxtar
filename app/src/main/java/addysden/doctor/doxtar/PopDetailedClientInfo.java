package addysden.doctor.doxtar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PopDetailedClientInfo extends Fragment {

    private String userName, userCode;
    private String clientRefNumber;
    private OnFragmentInteractionListener mListener;
    private LinearLayout mClientDataPopFrameLayout, mClientDataPopSetRatingLayout;
    private ProgressBar mClientDataPopProgressBar;
    private ImageView mClientDataPopImageView;
    private TextView mClientDataPopClientNameTextView, mClientDataPopSpecialityTextView,
                mClientDataPopAddressTextView, mClientDataPopPhone1TextView, mClientDataPopPhone2TextView,
                mClientDataPopEmailTextView, mClientDataPopDescriptionTextView,
                mClientDataPopRatingTextView, mClientDataPopViewsTextView;
    private fetchClientInfo fcd;
    private updateViewRating uvr;
    private ArrayList<clientDataInfo> clientDataInfoArrayList;
    private Bitmap clientImageBitmap;
    private RatingBar mClientDataPopRatingBar;

    private ImageView mClientDataPopClientTypeImageView;
    private TextView mClientDataPopClientTypeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pop_detailed_client_info, container, false);
        mClientDataPopFrameLayout = (LinearLayout) rootView.findViewById(R.id.client_data_pop_linear_layout);
        mClientDataPopProgressBar = (ProgressBar) rootView.findViewById(R.id.client_data_pop_progressBar);

        mClientDataPopImageView = (ImageView) rootView.findViewById(R.id.client_data_pop_photo_image_view);
        mClientDataPopClientNameTextView = (TextView) rootView.findViewById(R.id.client_data_pop_name_text_view);
        mClientDataPopSpecialityTextView = (TextView) rootView.findViewById(R.id.client_data_pop_speciality_text_view);
        mClientDataPopAddressTextView = (TextView) rootView.findViewById(R.id.client_data_pop_address_text_view);
        mClientDataPopPhone1TextView = (TextView) rootView.findViewById(R.id.client_data_pop_phone1_text_view);
        mClientDataPopPhone2TextView = (TextView) rootView.findViewById(R.id.client_data_pop_phone2_text_view);
        mClientDataPopEmailTextView = (TextView) rootView.findViewById(R.id.client_data_pop_email_value_textView);
        mClientDataPopDescriptionTextView = (TextView) rootView.findViewById(R.id.client_data_pop_description_text_view);
        clientDataInfoArrayList = new ArrayList<clientDataInfo>();
        mClientDataPopSetRatingLayout = (LinearLayout) rootView.findViewById(R.id.client_data_pop_rating_click);
        mClientDataPopViewsTextView = (TextView) rootView.findViewById(R.id.client_data_pop_views_textview);
        mClientDataPopRatingBar = (RatingBar) rootView.findViewById(R.id.client_data_pop_ratingBar);
        mClientDataPopRatingTextView = (TextView) rootView.findViewById(R.id.client_data_pop_rating_textview);
        mClientDataPopClientTypeImageView = (ImageView) rootView.findViewById(R.id.client_data_pop_client_type_image_view);
        mClientDataPopClientTypeTextView = (TextView) rootView.findViewById(R.id.client_data_pop_client_type_text_view);

        AdView mAdView = (AdView) rootView.findViewById(R.id.fragment_clientdet_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Bundle userParamBundle = this.getArguments();
        userName = userParamBundle.getString("userName");
        userCode = userParamBundle.getString("userCode");

        System.out.println("userCode: " + userCode);

        clientRefNumber = userParamBundle.getString("clientRefNumber");

        if(savedInstanceState != null) {
            showProgress(false);
            clientDataInfoArrayList = savedInstanceState.getParcelableArrayList("CLIENT_BUNDLE_DATA");
            clientImageBitmap = savedInstanceState.getParcelable("CLIENT_IMAGE");

            mClientDataPopClientNameTextView.setText(clientDataInfoArrayList.get(0).getmClientName().toString());
            mClientDataPopSpecialityTextView.setText(clientDataInfoArrayList.get(0).getmClientSpeciality().toString());
            mClientDataPopAddressTextView.setText(clientDataInfoArrayList.get(0).getmClientAddress().toString());
            mClientDataPopPhone1TextView.setText(clientDataInfoArrayList.get(0).getmClientPhone1().toString());
            mClientDataPopPhone2TextView.setText(clientDataInfoArrayList.get(0).getmClientPhone2().toString());
            mClientDataPopEmailTextView.setText(clientDataInfoArrayList.get(0).getmClientEmail().toString());
            mClientDataPopDescriptionTextView.setText(clientDataInfoArrayList.get(0).getmClientDescription().toString());

            mClientDataPopViewsTextView.setText(clientDataInfoArrayList.get(0).getmClientView().toString());
            mClientDataPopRatingTextView.setText("(" + clientDataInfoArrayList.get(0).getmClientRating().toString() + "/5.0)");
            mClientDataPopRatingBar.setRating(Float.parseFloat(clientDataInfoArrayList.get(0).getmClientRating().toString()));
            mClientDataPopClientTypeTextView.setText(clientDataInfoArrayList.get(0).getmClientType());

            if(clientDataInfoArrayList.get(0).getmClientType().equals("Doctor")) {
                mClientDataPopClientTypeImageView.setColorFilter(R.color.colorPurple);
                mClientDataPopClientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorPurple));
            } else if (clientDataInfoArrayList.get(0).getmClientType().equals("Hospital")) {
                mClientDataPopClientTypeImageView.setColorFilter(R.color.colorPink);
                mClientDataPopClientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorPink));
            } else if (clientDataInfoArrayList.get(0).getmClientType().equals("Chemist")) {
                mClientDataPopClientTypeImageView.setColorFilter(R.color.colorGreen);
                mClientDataPopClientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorGreen));
            } else if (clientDataInfoArrayList.get(0).getmClientType().equals("Pathology")) {
                mClientDataPopClientTypeImageView.setColorFilter(R.color.colorOrange);
                mClientDataPopClientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorOrange));
            }
        } else {
            clientImageBitmap = BitmapFactory.decodeByteArray(
                    userParamBundle.getByteArray("clientImage"), 0, userParamBundle.getByteArray("clientImage").length);

            showProgress(true);
            fcd = new fetchClientInfo(clientRefNumber, userCode);
            fcd.execute(clientRefNumber, userCode);
        }

        mClientDataPopImageView.setImageBitmap(clientImageBitmap);

        freezeOrientation();
        uvr = new updateViewRating(clientRefNumber, userCode);
        uvr.execute(clientRefNumber, userCode);

        mClientDataPopSetRatingLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog popDialog = new Dialog(getContext());

                popDialog.setContentView(R.layout.dialog_rating_bar);
                popDialog.setCancelable(true);
                final RatingBar ratingBar = (RatingBar) popDialog.findViewById(R.id.rating_dialog_ratingbar);

                Button ratingButton = (Button) popDialog.findViewById(R.id.rating_dialog_submit_button);
                ratingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uvr = new updateViewRating(clientRefNumber, userCode, String.valueOf(ratingBar.getRating()));
                        uvr.execute(clientRefNumber, userCode, String.valueOf(ratingBar.getRating()));
                        popDialog.dismiss();
                    }
                });

                popDialog.setTitle("Click2Rate");

                popDialog.show();
            }
        });

        return rootView;
    }

    public void freezeOrientation() {
        int orientation = getActivity().getRequestedOrientation();
        int rotation = ((WindowManager) getActivity().getSystemService(
                getContext().WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }

        getActivity().setRequestedOrientation(orientation);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(clientDataInfoArrayList != null) {
            outState.putParcelableArrayList("CLIENT_BUNDLE_DATA", clientDataInfoArrayList);
            outState.putParcelable("CLIENT_IMAGE", clientImageBitmap);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mClientDataPopFrameLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mClientDataPopFrameLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mClientDataPopFrameLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mClientDataPopProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mClientDataPopProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mClientDataPopProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mClientDataPopProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mClientDataPopFrameLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class updateViewRating extends AsyncTask<String, Void, String> {

        private String mClientInfoRefNumber, mUserCode, mRating=null;
        private String clientRatingData;

        updateViewRating(String clientRefNumber, String userCode, String rating) {
            mClientInfoRefNumber = clientRefNumber;
            mUserCode = userCode;
            mRating = rating;
        }

        updateViewRating(String clientRefNumber, String userCode) {
            mClientInfoRefNumber = clientRefNumber;
            mUserCode = userCode;
        }

        @Override
        protected String doInBackground(String... params) {
            String clientRatingResponse = "";

            try {
                final String clientRatingResponse_url = getString(R.string.url) + "/clientRating.php";

                URL clientRatingURL = new URL(clientRatingResponse_url);
                HttpURLConnection clientRatingHTTP = (HttpURLConnection) clientRatingURL.openConnection();
                clientRatingHTTP.setRequestMethod("POST");
                clientRatingHTTP.setDoOutput(true);
                OutputStream clientRatingOS = clientRatingHTTP.getOutputStream();
                BufferedWriter clientRatingWriter = new BufferedWriter(new OutputStreamWriter(clientRatingOS, "UTF-8"));

                if (mRating == null) {
                    clientRatingData = URLEncoder.encode("action", "UTF-8") + "=set&" + URLEncoder.encode("client_ref_num", "UTF-8") +
                            "=" + URLEncoder.encode(params[0], "UTF-8") + "&" + URLEncoder.encode("user_code", "UTF-8") +
                            "=" + URLEncoder.encode(params[1], "UTF-8");
                } else {
                    clientRatingData = URLEncoder.encode("action", "UTF-8") + "=set&" + URLEncoder.encode("client_ref_num", "UTF-8") +
                            "=" + URLEncoder.encode(params[0], "UTF-8") + "&" + URLEncoder.encode("user_code", "UTF-8") +
                            "=" + URLEncoder.encode(params[1], "UTF-8") + "&" + URLEncoder.encode("rate", "UTF-8") +
                            "=" + URLEncoder.encode(params[2], "UTF-8");
                }

                System.out.println("clientRatingData: " + clientRatingData);
                clientRatingWriter.write(clientRatingData);
                clientRatingWriter.flush();
                clientRatingWriter.close();
                clientRatingOS.close();

                InputStream clientRatingIS = clientRatingHTTP.getInputStream();
                BufferedReader clientRatingReader = new BufferedReader(new InputStreamReader(clientRatingIS, "iso-8859-1"));
                String line = "";
                while ((line = clientRatingReader.readLine()) != null) {
                    clientRatingResponse += line;
                }
                clientRatingReader.close();
                clientRatingIS.close();
                clientRatingHTTP.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return clientRatingResponse;
        }

        @Override
        protected void onPostExecute(String clientRatingView) {
            if(!clientRatingView.equals("Rating or View not updated")) {
                System.out.println("onPostExecute: " + clientRatingView);
                String[] ratingViewArr = clientRatingView.split("\\|");
                System.out.println(ratingViewArr[0] + ratingViewArr[1]);
                mClientDataPopViewsTextView.setText(ratingViewArr[0]);
                mClientDataPopRatingTextView.setText("(" + ratingViewArr[1] + "/5.0)");
                mClientDataPopRatingBar.setRating(Float.parseFloat(ratingViewArr[1]));
//                Toast.makeText(getContext(), "You rated " + ratingViewArr[1] + " star!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), clientRatingView, Toast.LENGTH_LONG).show();
            }
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            uvr = null;
        }

        @Override
        protected void onCancelled() {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            uvr = null;
        }
    }

    public class fetchClientInfo extends AsyncTask<String, Void, ArrayList<clientDataInfo>> {

        private String mClientInfoRefNumber, mUserCode;

        fetchClientInfo (String clientRefNumber, String userCode) {
            mClientInfoRefNumber = clientRefNumber;
            mUserCode = userCode;
        }

        @Override
        protected ArrayList<clientDataInfo> doInBackground(String... params) {
            String clientDataResponse = "";

            try {
                final String clientDataResponse_url = getString(R.string.url) + "/selectClients.php";

                URL clientDataURL = new URL(clientDataResponse_url);
                HttpURLConnection clientDataHTTP = (HttpURLConnection) clientDataURL.openConnection();
                clientDataHTTP.setRequestMethod("POST");
                clientDataHTTP.setDoOutput(true);
                OutputStream clientDataOS = clientDataHTTP.getOutputStream();
                BufferedWriter clientDataWriter = new BufferedWriter(new OutputStreamWriter(clientDataOS,"UTF-8"));
                String clientDataData = URLEncoder.encode("select_client", "UTF-8") + "=" + URLEncoder.encode(params[0],"UTF-8") +
                        "&" + URLEncoder.encode("user_code", "UTF-8") + "=" + URLEncoder.encode(params[1],"UTF-8");
                clientDataWriter.write(clientDataData);
                clientDataWriter.flush();
                clientDataWriter.close();
                clientDataOS.close();

                InputStream clientDataIS = clientDataHTTP.getInputStream();
                BufferedReader clientDataReader = new BufferedReader(new InputStreamReader(clientDataIS,"iso-8859-1"));
                String line = "";
                while ((line = clientDataReader.readLine()) != null)
                {
                    clientDataResponse += line;
                }
                clientDataReader.close();
                clientDataIS.close();
                clientDataHTTP.disconnect();

                JSONArray policyJsonArr = new JSONArray(clientDataResponse);

                for(int i = 0; i < policyJsonArr.length(); i++){
                    clientDataInfo cdi = new clientDataInfo();

                    JSONObject c = policyJsonArr.getJSONObject(i);

                    cdi.setmClientName(c.getString("clientName"));
                    cdi.setmClientSpeciality(c.getString("clientSpeciality"));
                    cdi.setmClientAddress(c.getString("clientAddress") + "\nPinCode: " + c.getString("clientPinCode"));
                    cdi.setmClientPhone1(c.getString("clientPhoneNum1"));
                    cdi.setmClientPhone2(c.getString("clientPhoneNum2"));
                    cdi.setmClientEmail(c.getString("clientEmailId"));
                    cdi.setmClientDescription(c.getString("clientDescription"));
                    cdi.setmClientView(c.getString("view"));
                    cdi.setmClientRating(c.getString("rating"));
                    cdi.setmClientType(c.getString("clientType"));

                    clientDataInfoArrayList.add(cdi);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return clientDataInfoArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<clientDataInfo> clientData) {
            if (!clientData.isEmpty()) {
                mClientDataPopClientNameTextView.setText(clientData.get(0).getmClientName().toString());
                mClientDataPopClientNameTextView.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left));

                mClientDataPopSpecialityTextView.setText(clientData.get(0).getmClientSpeciality().toString());
                mClientDataPopSpecialityTextView.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right));

                mClientDataPopAddressTextView.setText(clientData.get(0).getmClientAddress().toString());
                mClientDataPopAddressTextView.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left));

                mClientDataPopPhone1TextView.setText(clientData.get(0).getmClientPhone1().toString());
                mClientDataPopPhone1TextView.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right));

                mClientDataPopPhone2TextView.setText(clientData.get(0).getmClientPhone2().toString());
                mClientDataPopPhone2TextView.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left));

                mClientDataPopEmailTextView.setText(clientData.get(0).getmClientEmail().toString());
                mClientDataPopEmailTextView.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right));

                mClientDataPopDescriptionTextView.setText(clientData.get(0).getmClientDescription().toString());
                mClientDataPopDescriptionTextView.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left));

                mClientDataPopViewsTextView.setText(clientData.get(0).getmClientView());
                mClientDataPopRatingTextView.setText("(" + clientData.get(0).getmClientRating() + "/5.0)");
                mClientDataPopRatingBar.setRating(Float.parseFloat(clientData.get(0).getmClientRating()));

                mClientDataPopClientTypeTextView.setText(clientDataInfoArrayList.get(0).getmClientType().toString());

                if(clientDataInfoArrayList.get(0).getmClientType().equals("Doctor")) {
                    DrawableCompat.setTint(mClientDataPopClientTypeImageView.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorPurple));
                    mClientDataPopClientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorPurple));
                } else if (clientDataInfoArrayList.get(0).getmClientType().equals("Hospital")) {
                    DrawableCompat.setTint(mClientDataPopClientTypeImageView.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorPink));
                    mClientDataPopClientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorPink));
                } else if (clientDataInfoArrayList.get(0).getmClientType().equals("Chemist")) {
                    DrawableCompat.setTint(mClientDataPopClientTypeImageView.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorGreen));
                    mClientDataPopClientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorGreen));
                } else if (clientDataInfoArrayList.get(0).getmClientType().equals("Pathology")) {
                    DrawableCompat.setTint(mClientDataPopClientTypeImageView.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorOrange));
                    mClientDataPopClientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorOrange));
                }

            } else {
                Toast.makeText(getContext(), "Client Info could not be fetched.", Toast.LENGTH_LONG).show();
            }

            fcd = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            fcd=null;
            showProgress(false);
        }
    }
}
