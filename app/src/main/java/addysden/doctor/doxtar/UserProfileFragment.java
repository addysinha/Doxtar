package addysden.doctor.doxtar;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class UserProfileFragment extends Fragment {

    private ProgressBar mUserProfileProgressBar;

    private ImageButton mUserProfilePhotoImageButton;
    private ImageView mUserProfileProfileImageView;

    private AutoCompleteTextView mUserProfileNameTextView, mUserProfileAddressTextView,
    mUserProfilePinTextView, mUserProfilePhoneTextView, mUserProfileEmailTextView;

    private Spinner mUserProfileCountrySpinner;

    private Button mUserProfileSaveButton;
    private ArrayList<String> fCode, fCountry;

    private LinearLayout mUserProfileLinearLayout;
    private UserProfileSaveTask upt;

    private CheckInternetConnection internetConn = null;
    private String mImageName = null;

    private String userName, userCode;

    private static final Integer REQUEST_CAMERA = 0;
    private static final Integer REQUEST_STORAGE = 1;
    private static final Integer RESULT_LOAD_IMAGE = 1;
    private static final Integer RESULT_CLICK_LOAD_IMAGE = 2;
    private static Integer CHECK=0;

    private File imgFile = null;
    private Uri selectedImageUri = null;
    private final static String CAPTURED_PHOTO_URI_KEY = "mUserProfileProfileImageView";
    private final static String CAPTURED_PHOTO_CHECK_FLAG = "flag";
    private View rootView;

    private ImageButton mUserProfileNameImageButton, mUserProfileAddressImageButton,
    mUserProfilePinImageButton, mUserProfilePhoneImageButton;

    private String action = "select";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mUserProfileProgressBar = (ProgressBar) rootView.findViewById(R.id.user_profile_progressBar);
        mUserProfilePhotoImageButton = (ImageButton) rootView.findViewById(R.id.user_profile_photo_image_btn);
        mUserProfileNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.user_profile_name_textView);
        mUserProfileAddressTextView = (AutoCompleteTextView) rootView.findViewById(R.id.user_profile_address_textView);
        mUserProfilePinTextView = (AutoCompleteTextView) rootView.findViewById(R.id.user_profile_pincode_textView);
        mUserProfilePhoneTextView = (AutoCompleteTextView) rootView.findViewById(R.id.user_profile_phone_textView);
        mUserProfileEmailTextView = (AutoCompleteTextView) rootView.findViewById(R.id.user_profile_email_textView);
        mUserProfileCountrySpinner = (Spinner) rootView.findViewById(R.id.user_profile_country_spinner);
        mUserProfileSaveButton = (Button) rootView.findViewById(R.id.user_profile_save_button);
        mUserProfileLinearLayout = (LinearLayout) rootView.findViewById(R.id.user_profile_det_linear_layout);
        mUserProfileProfileImageView = (ImageView) rootView.findViewById(R.id.user_profile_photo_image_view);

        mUserProfileNameImageButton = (ImageButton) rootView.findViewById(R.id.user_profile_name_imagebutton);
        mUserProfileAddressImageButton = (ImageButton) rootView.findViewById(R.id.user_profile_address_imagebutton);
        mUserProfilePinImageButton = (ImageButton) rootView.findViewById(R.id.user_profile_pincode_imagebutton);
        mUserProfilePhoneImageButton = (ImageButton) rootView.findViewById(R.id.user_profile_phone_imagebutton);

        AdView mAdView = (AdView) rootView.findViewById(R.id.fragment_user_profile_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mUserProfileNameImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserProfileNameTextView.isEnabled()) {
                    mUserProfileNameTextView.setEnabled(false);
                    mUserProfileNameImageButton.setImageResource(R.drawable.ic_menu_pencil);
                }
                else {
                    mUserProfileNameTextView.setEnabled(true);
                    mUserProfileNameImageButton.setImageResource(R.drawable.ic_menu_locked_pencil);
                    mUserProfileNameTextView.setError(null);
                }
            }
        });

        mUserProfileAddressImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserProfileAddressTextView.isEnabled()) {
                    mUserProfileAddressTextView.setEnabled(false);
                    mUserProfileAddressImageButton.setImageResource(R.drawable.ic_menu_pencil);
                }
                else {
                    mUserProfileAddressTextView.setEnabled(true);
                    mUserProfileAddressImageButton.setImageResource(R.drawable.ic_menu_locked_pencil);
                    mUserProfileAddressTextView.setError(null);
                }
            }
        });

        mUserProfilePinImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserProfilePinTextView.isEnabled()) {
                    mUserProfilePinTextView.setEnabled(false);
                    mUserProfilePinImageButton.setImageResource(R.drawable.ic_menu_pencil);
                }
                else {
                    mUserProfilePinTextView.setEnabled(true);
                    mUserProfilePinImageButton.setImageResource(R.drawable.ic_menu_locked_pencil);
                    mUserProfilePinTextView.setError(null);
                }
            }
        });

        mUserProfilePhoneImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserProfilePhoneTextView.isEnabled()) {
                    mUserProfilePhoneTextView.setEnabled(false);
                    mUserProfilePhoneImageButton.setImageResource(R.drawable.ic_menu_pencil);
                }
                else {
                    mUserProfilePhoneTextView.setEnabled(true);
                    mUserProfilePhoneImageButton.setImageResource(R.drawable.ic_menu_locked_pencil);
                    mUserProfilePhoneTextView.setError(null);
                }
            }
        });

        Bundle userParamBundle = this.getArguments();
        userName = userParamBundle.getString("userName");
        userCode = userParamBundle.getString("userCode");

        setCountryCode(rootView);
        showProgress(false);

        internetConn = new CheckInternetConnection(getActivity());

        mUserProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConn.isNetworkConnected() == true) {
                    attemptProfileSave(rootView);
                } else {
                    displayAlert();
                }
            }
        });

        mUserProfilePhotoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(rootView);
            }
        });

        if(savedInstanceState != null) {

            System.out.println("saveInstanceState is not null");

            if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
                selectedImageUri = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));

                if(selectedImageUri == null) {
                    selectedImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(R.mipmap.no_photo)
                            + '/' + getResources().getResourceTypeName(R.mipmap.no_photo) + '/' + getResources().getResourceEntryName(R.mipmap.no_photo));
                }
            }

            if (savedInstanceState.containsKey(CAPTURED_PHOTO_CHECK_FLAG)) {
                CHECK = savedInstanceState.getInt(CAPTURED_PHOTO_CHECK_FLAG);
            }

            if(savedInstanceState.getString("USER_NAME").equals(""))
                mUserProfileNameTextView.setHint("Click on Icon to Enter Name");
            else
                mUserProfileNameTextView.setText(savedInstanceState.getString("USER_NAME"));

            if(savedInstanceState.getString("USER_ADDRESS").equals(""))
                mUserProfileAddressTextView.setHint("Click on Icon to Enter Address");
            else
                mUserProfileAddressTextView.setText(savedInstanceState.getString("USER_ADDRESS"));

            if(savedInstanceState.getString("USER_PIN").equals(""))
                mUserProfilePinTextView.setHint("Click on Icon to Enter Pin Code");
            else
                mUserProfilePinTextView.setText(savedInstanceState.getString("USER_PIN"));

            ArrayAdapter spinnerAdapter = (ArrayAdapter) mUserProfileCountrySpinner.getAdapter(); //cast to an ArrayAdapter
            int spinnerPosition = spinnerAdapter.getPosition(savedInstanceState.getString("USER_COUNTRY"));
            mUserProfileCountrySpinner.setSelection(spinnerPosition);

            if (savedInstanceState.getString("USER_PHONE").equals(""))
                mUserProfilePhoneTextView.setHint("Click on Icon to Enter Phone");
            else
                mUserProfilePhoneTextView.setText(savedInstanceState.getString("USER_PHONE"));

            if(savedInstanceState.getString("USER_EMAIL").equals(""))
                mUserProfileEmailTextView.setHint("Click on Icon to Enter Email");
            else
                mUserProfileEmailTextView.setText(savedInstanceState.getString("USER_EMAIL"));

            mUserProfileNameTextView.setEnabled(false);
            mUserProfileNameImageButton.setImageResource(R.drawable.ic_menu_pencil);
            mUserProfileAddressTextView.setEnabled(false);
            mUserProfileAddressImageButton.setImageResource(R.drawable.ic_menu_pencil);
            mUserProfilePinTextView.setEnabled(false);
            mUserProfilePinImageButton.setImageResource(R.drawable.ic_menu_pencil);
            mUserProfilePhoneTextView.setEnabled(false);
            mUserProfilePhoneImageButton.setImageResource(R.drawable.ic_menu_pencil);

            showImage();
        } else if (internetConn.isNetworkConnected() == true) {
            if(action.equals("select")) {
                System.out.println("select");
                showProgress(true);
                upt = new UserProfileSaveTask(action, userCode);
                upt.execute(action, userCode);
            }
        } else {
            displayAlert();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        System.out.println("onSaveInstanceState clicked");

        if(selectedImageUri != null) {
            outState.putString(CAPTURED_PHOTO_URI_KEY, selectedImageUri.toString());
        }

        if (CHECK != 0) {
            outState.putInt(CAPTURED_PHOTO_CHECK_FLAG, CHECK);
        }

        outState.putString("USER_NAME", mUserProfileNameTextView.getText().toString());
        outState.putString("USER_ADDRESS", mUserProfileAddressTextView.getText().toString());
        outState.putString("USER_PIN", mUserProfilePinTextView.getText().toString());
        outState.putString("USER_COUNTRY", mUserProfileCountrySpinner.getSelectedItem().toString());
        outState.putString("USER_PHONE", mUserProfilePhoneTextView.getText().toString());
        outState.putString("USER_EMAIL", mUserProfileEmailTextView.getText().toString());
    }

    private void attemptProfileSave(View rootView) {
        try {
            if (upt != null) {
                return;
            }

            String errorStr = "";

            // Reset errors.
            mUserProfileNameTextView.setError(null);
            //mUserProfileAddressTextView.setError(null);
            mUserProfilePinTextView.setError(null);
            mUserProfileEmailTextView.setError(null);
            //mUserProfilePhoneTextView.setError(null);

            // Store values at the time of the login attempt.
            String name = mUserProfileNameTextView.getText().toString();
            String address = mUserProfileAddressTextView.getText().toString();
            String pincode = mUserProfilePinTextView.getText().toString();
            String email = mUserProfileEmailTextView.getText().toString();
            String phone = mUserProfilePhoneTextView.getText().toString();

            Bitmap profilePhoto = ((BitmapDrawable) mUserProfileProfileImageView.getDrawable()).getBitmap();
            String profilePhotoFileNm = mImageName;

            boolean cancel = false;
            View focusView = null;

            // Check if Number entered.
            if (TextUtils.isEmpty(name)) {
                mUserProfileNameTextView.setError(getString(R.string.error_field_required));
                focusView = mUserProfileNameTextView;
                cancel = true;
            } else if (!isNameValid(name)) {
                mUserProfileNameTextView.setError(getString(R.string.error_invalid_name));
                focusView = mUserProfileNameTextView;
                cancel = true;
            }

            // Check if pin entered
            if (TextUtils.isEmpty(pincode)) {
                mUserProfilePinTextView.setError(getString(R.string.error_field_required));
                focusView = mUserProfilePinTextView;
                cancel = true;
            }

            // Check for a valid Phone.
            if (TextUtils.isEmpty(phone)) {
                mUserProfilePhoneTextView.setError(getString(R.string.error_field_required));
                focusView = mUserProfilePhoneTextView;
                cancel = true;
            } else if (!isPhoneValid(phone)) {
                mUserProfilePhoneTextView.setError(getString(R.string.error_invalid_phone));
                focusView = mUserProfilePhoneTextView;
                cancel = true;
            }

            String selectedCountry = (String) mUserProfileCountrySpinner.getSelectedItem();
            int selectedPosition = fCountry.indexOf(selectedCountry);
            String correspondingCode = fCode.get(selectedPosition);
            if (correspondingCode.equals("0")) {
                errorStr += " Country";
                focusView = mUserProfileCountrySpinner;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                if (!errorStr.equals("")) {
                    errorStr += " should be selected.";
                    Toast.makeText(getActivity().getApplicationContext(), errorStr, Toast.LENGTH_LONG).show();
                }
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);

                upt = new UserProfileSaveTask(name, address, pincode, correspondingCode,
                        selectedCountry, phone, email, profilePhotoFileNm, userCode, profilePhoto);

                upt.execute("set", name, address, pincode, correspondingCode,
                        selectedCountry, phone, email, profilePhotoFileNm, userCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPhoneValid(String phone) {
        return ((phone.length() >= 10)&&(phone.matches("[0-9]+")));
    }

    private boolean isNameValid(String name) {
        return ((name.length() > 2)&&(name.matches("[a-zA-Z .]+")));
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void displayAlert()
    {
        new AlertDialog.Builder(getActivity()).setMessage("Please Check Your Internet Connection and Try Again")
                .setTitle("Network Error")
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){
                                getActivity().finish();
                            }
                        })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUserProfileLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mUserProfileLinearLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUserProfileLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mUserProfileProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mUserProfileProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUserProfileProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mUserProfileProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mUserProfileLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private File getFileName() {
        File mediaStorageDir= null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + getContext().getPackageName()
                    + File.separator
                    + "Images");
        } else {
            ContextWrapper cw = new ContextWrapper(getContext());
            // path to /data/data/yourapp/app_data/imageDir
            mediaStorageDir = cw.getDir("Images", Context.MODE_PRIVATE);
        }

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmssSS").format(new Date());
        mImageName="GM_"+ timeStamp +".jpg";

        return new File(mediaStorageDir.getPath() + '/' + mImageName);
    }

    private void showCamera(View rootView) {
        // BEGIN_INCLUDE(camera_permission)
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestCameraPermission(rootView);
            ///requestStoragePermission(rootView);
        } else {
            // Camera permissions is already available, show the camera preview.
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            imgFile = getFileName();
            if(imgFile == null) {
                requestStoragePermission(rootView);
                return;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
            try {
                FileOutputStream imageOutputStream = new FileOutputStream(imgFile);
                startActivityForResult(intent, RESULT_CLICK_LOAD_IMAGE);
            } catch (FileNotFoundException e) {
                requestStoragePermission(rootView);
                e.printStackTrace();
            }
        }
        // END_INCLUDE(camera_permission)
    }

    private void requestCameraPermission(View rootView) {
        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(rootView, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    private void requestStoragePermission(View rootView) {
        // BEGIN_INCLUDE(camera_permission_request)
        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //if((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            //  && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(rootView, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_STORAGE);
                        }
                    })
                    .show();
        } else {
            // storage permission has not been granted yet. Request it directly.
            // storage permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }
        // END_INCLUDE(storage_permission_request)
    }

    private void selectImage(final View rootView) {
        final CharSequence[] options = { "Click Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Click Photo"))
                {
                    showCamera(rootView);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);

                    File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String photoPath = pictureDirectory.getPath();

                    Uri photoUri = Uri.parse(photoPath);
                    galleryIntent.setDataAndType(photoUri, "image/*");

                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            CHECK = 0;
            if(requestCode == RESULT_LOAD_IMAGE && data != null && resultCode == getActivity().RESULT_OK) {
                if (data.getData() == null) {
                    selectedImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(R.mipmap.no_photo)
                            + '/' + getResources().getResourceTypeName(R.mipmap.no_photo) + '/' + getResources().getResourceEntryName(R.mipmap.no_photo));
                } else {
                    selectedImageUri = data.getData();
                    CHECK = RESULT_LOAD_IMAGE;
                    mImageName = new File("" + selectedImageUri).getName().toString();
                }
            } else if (requestCode == RESULT_CLICK_LOAD_IMAGE && resultCode == getActivity().RESULT_OK) {
                if(Uri.fromFile(imgFile) == null) {
                    selectedImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(R.mipmap.no_photo)
                            + '/' + getResources().getResourceTypeName(R.mipmap.no_photo) + '/' + getResources().getResourceEntryName(R.mipmap.no_photo));
                } else {
                    selectedImageUri = Uri.fromFile(imgFile);
                    CHECK = RESULT_CLICK_LOAD_IMAGE;
                }
            } else {
                selectedImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.mipmap.no_photo)
                        + '/' + getResources().getResourceTypeName(R.mipmap.no_photo) + '/' + getResources().getResourceEntryName(R.mipmap.no_photo));
            }

            showImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showImage () {
        Bitmap capturePhotoBitmap = null;
        int srcHeight, srcWidth, inSampleSize;
        BitmapFactory.Options mClientRegBitmapOptions = new BitmapFactory.Options();
        mClientRegBitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mClientRegBitmapOptions.inJustDecodeBounds=true;

        if (CHECK == 0) {
            mUserProfileProfileImageView.setImageURI(selectedImageUri);
        } else {
            BitmapFactory.decodeFile(getRealPathFromUri(getContext(), selectedImageUri, CHECK), mClientRegBitmapOptions);

//            if(new File(getRealPathFromUri(getContext(), selectedImageUri, CHECK)).exists()) {
//                System.out.println("File Already Existssss");
//            } else {
//                System.out.println("File NOT Existssss");
//            }

            srcHeight = mClientRegBitmapOptions.outHeight;
            srcWidth = mClientRegBitmapOptions.outWidth;

            inSampleSize = calculateInSampleSize(mClientRegBitmapOptions, 400, 400);

            mClientRegBitmapOptions.inSampleSize = inSampleSize;
            mClientRegBitmapOptions.inDensity = srcWidth;
            mClientRegBitmapOptions.inTargetDensity = 400 * mClientRegBitmapOptions.inSampleSize;

            mClientRegBitmapOptions.inJustDecodeBounds=false;

            capturePhotoBitmap = BitmapFactory.decodeFile(getRealPathFromUri(getContext(), selectedImageUri, CHECK), mClientRegBitmapOptions);

            mUserProfileProfileImageView.setImageBitmap(capturePhotoBitmap);
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri, int CHECK) {
        Cursor cursor = null;
        try {
            if (CHECK == RESULT_LOAD_IMAGE) {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else if (CHECK == RESULT_CLICK_LOAD_IMAGE) {
                return contentUri.getPath().toString();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private void setCountryCode (View rootView) {
        Resources resources = getResources();
        TypedArray countryCode = resources.obtainTypedArray(R.array.countries);

        ArrayList<String> country = new ArrayList<String>();
        ArrayList<String> code = new ArrayList<String>();

        int countryArrLen = countryCode.length();
        for (int i=0; i < countryArrLen; i++) {
            int countryId = countryCode.getResourceId(i, 0);
            code.add(resources.getStringArray(countryId)[0]);
            country.add(resources.getStringArray(countryId)[1]);
        }

        countryCode.recycle();

        fCode = code;
        fCountry = country;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, country);
        mUserProfileCountrySpinner.setAdapter(dataAdapter);
        mUserProfileCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selectedCountry = (String) mUserProfileCountrySpinner.getSelectedItem();
                int selectedPosition = fCountry.indexOf(selectedCountry);
                String correspondingCode = fCode.get(selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            Bitmap myBitmap = null;
            if(src.trim().equals(getString(R.string.url) + "/clientImages/"))  {
                Drawable myDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.no_photo);
                myBitmap = ((BitmapDrawable) myDrawable).getBitmap();
                return myBitmap;
            }
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public class UserProfileSaveTask extends AsyncTask<String, String, String> {

        private final String mUserAddress, mUserPin, mUserCountryCode, mUserCountryName, mUserPhoneNum,
                mUserEmail, mUserName, mUserCode, mUserProfilePhotoNm, mUserAction;

        private final Bitmap mUserPhoto;

        UserProfileSaveTask(String userName, String userAddress, String userPin, String userCountryCode,
                           String userCountryName, String userPhoneNum, String userEmail, String clientProfilePhotoNm,
                           String userCode, Bitmap userPhoto) {
            mUserAction="set";
            mUserName=userName;
            mUserAddress=userAddress;
            mUserPin=userPin;
            mUserCountryCode=userCountryCode;
            mUserPhoneNum=userPhoneNum;
            mUserEmail=userEmail;
            mUserCode=userCode;
            mUserProfilePhotoNm = clientProfilePhotoNm;
            mUserPhoto = userPhoto;
            mUserCountryName = userCountryName;
        }

        UserProfileSaveTask(String action, String userCode) {
            mUserAction = action;
            mUserCode = userCode;
            mUserName="";
            mUserAddress="";
            mUserPin="";
            mUserCountryCode="";
            mUserPhoneNum="";
            mUserEmail="";
            mUserProfilePhotoNm = "";
            mUserPhoto = null;
            mUserCountryName = "";
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String loginResponse = "";

            ByteArrayOutputStream clientPhotoByteArrOpStream = null;
            String encodedPhoto = "";

            if(params[0].equals("set")) {
                if (params[8] != null) {
                    clientPhotoByteArrOpStream = new ByteArrayOutputStream();
                    mUserPhoto.compress(Bitmap.CompressFormat.JPEG, 100, clientPhotoByteArrOpStream);
                    encodedPhoto = Base64.encodeToString(clientPhotoByteArrOpStream.toByteArray(), Base64.DEFAULT);
                }
            }

            try {
                final String login_url = getString(R.string.url) + "/userProfileSave.php";

                try {
                    URL loginURL = new URL(login_url);
                    HttpURLConnection loginHTTP = (HttpURLConnection) loginURL.openConnection();
                    loginHTTP.setRequestMethod("POST");
                    loginHTTP.setDoOutput(true);
                    OutputStream loginOS = loginHTTP.getOutputStream();
                    BufferedWriter loginWriter = new BufferedWriter(new OutputStreamWriter(loginOS, "UTF-8"));

                    String loginData = "";

                    if(params[0].equals("select")) {

                        loginData = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                URLEncoder.encode("user_code", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

                        System.out.println("loginData: " + loginData);

                    } else if (params[0].equals("set")) {

                        loginData = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                URLEncoder.encode("userprofile_name", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                                URLEncoder.encode("userprofile_address", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                                URLEncoder.encode("userprofile_pin", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                                URLEncoder.encode("userprofile_country_cd", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                                URLEncoder.encode("userprofile_country_nm", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                                URLEncoder.encode("userprofile_phone", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&" +
                                URLEncoder.encode("userprofile_email", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8") + "&" +
//                                URLEncoder.encode("userprofile_photo_path", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8") + "&" +
                                URLEncoder.encode("user_code", "UTF-8") + "=" + URLEncoder.encode(params[9], "UTF-8");

                        if (params[8] == null) {
                            loginData += "&" + URLEncoder.encode("userprofile_photo_path", "UTF-8") + "=&" + URLEncoder.encode("client_photo", "UTF-8") + "=";
                        } else {
                            loginData += "&" + URLEncoder.encode("userprofile_photo_path", "UTF-8") + "=" + URLEncoder.encode(params[8], "UTF-8") + "&" +
                                    URLEncoder.encode("userprofile_photo", "UTF-8") + "=" + URLEncoder.encode(encodedPhoto, "UTF-8");
                        }

                        System.out.println("loginData: " + loginData);

                    }

                    loginWriter.write(loginData);
                    loginWriter.flush();
                    loginWriter.close();
                    loginOS.close();

                    InputStream loginIS = loginHTTP.getInputStream();
                    BufferedReader loginReader = new BufferedReader(new InputStreamReader(loginIS, "iso-8859-1"));
                    String line = "";
                    while ((line = loginReader.readLine()) != null) {
                        loginResponse += line;
                    }

                    System.out.println(loginResponse);

                    loginReader.close();
                    loginIS.close();
                    loginHTTP.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                return "Something unfortunate happened.";
            }
            return loginResponse;
        }

        @Override
        protected void onPostExecute(final String result) {

            try {
                System.out.println(result);

                JSONArray clientListJsonArr = new JSONArray(result);

                for(int i = 0; i < clientListJsonArr.length(); i++){
                    JSONObject c = null;
                    c = clientListJsonArr.getJSONObject(i);

                    if(c.getString("userName").equals(""))
                        mUserProfileNameTextView.setHint("Click on Icon to Enter Name");
                    else
                        mUserProfileNameTextView.setText(c.getString("userName"));

                    if(c.getString("userAddress").equals(""))
                        mUserProfileAddressTextView.setHint("Click on Icon to Enter Address");
                    else
                        mUserProfileAddressTextView.setText(c.getString("userAddress"));

                    if(c.getString("userPinCode").equals(""))
                        mUserProfilePinTextView.setHint("Click on Icon to Enter Pin Code");
                    else
                       mUserProfilePinTextView.setText(c.getString("userPinCode"));

                    ArrayAdapter spinnerAdapter = (ArrayAdapter) mUserProfileCountrySpinner.getAdapter(); //cast to an ArrayAdapter
                    System.out.println("spinnerAdapter" + spinnerAdapter);
                    System.out.println("countryCode" + c.getString("userCountryCode"));

                    int spinnerPosition = spinnerAdapter.getPosition(c.getString("userCountry"));
                    System.out.println("spinnerPosition: " + spinnerPosition);
                    //set the default according to value
                    mUserProfileCountrySpinner.setSelection(spinnerPosition);

                    if (c.getString("userPhone").equals(""))
                        mUserProfilePhoneTextView.setHint("Click on Icon to Enter Phone");
                    else
                        mUserProfilePhoneTextView.setText(c.getString("userPhone"));

                    if(c.getString("userEmail").equals(""))
                        mUserProfileEmailTextView.setHint("Click on Icon to Enter Email");
                    else
                        mUserProfileEmailTextView.setText(c.getString("userEmail"));

                    if (!c.getString("userPhotoPath").equals("")) {
                        System.out.println(c.getString("userPhotoPath"));
                        mUserProfileProfileImageView.setImageBitmap(;(c.getString("userPhotoPath")));
                    }

                    mUserProfileNameTextView.setEnabled(false);
                    mUserProfileNameImageButton.setImageResource(R.drawable.ic_menu_pencil);
                    mUserProfileAddressTextView.setEnabled(false);
                    mUserProfileAddressImageButton.setImageResource(R.drawable.ic_menu_pencil);
                    mUserProfilePinTextView.setEnabled(false);
                    mUserProfilePinImageButton.setImageResource(R.drawable.ic_menu_pencil);
                    mUserProfilePhoneTextView.setEnabled(false);
                    mUserProfilePhoneImageButton.setImageResource(R.drawable.ic_menu_pencil);

                    upt = null;
                    showProgress(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            upt = null;
            showProgress(false);
        }
    }
}

