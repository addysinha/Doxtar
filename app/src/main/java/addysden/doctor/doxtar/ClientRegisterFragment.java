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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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

public class ClientRegisterFragment extends Fragment {

    private Spinner mClientRegCountryCodeSpnr, mClientRegTypeSpnr;
    private AutoCompleteTextView mClientRegDescTextView, mClientRegNameTextView, mClientRegAddressTextView, mClientRegPinTextView;
    private AutoCompleteTextView mClientRegPhone1TextView, mClientRegPhone2TextView, mClientRegLicNumTextView, mClientRegSpecTextView;
    private AutoCompleteTextView mClientRegEmailTextView;
    private Button mClientRegSaveButton;
    private ImageButton mClientRegCameraButton;
    private ImageView mClientRegProfileImageView, mClientLicenseHintImageView;

    private LinearLayout mClientRegFormView;

    private ArrayList<String> fCode, fClientTypeCode;
    private ArrayList<String> fCountry, fClientType;

    private CheckInternetConnection internetConn = null;
    private ClientRegisterTask crt = null;

    private ProgressBar mClientRegProgressBar;
    private String userName, userCode;

    private static final Integer REQUEST_CAMERA = 0;
    private static final Integer REQUEST_STORAGE = 1;
    private static final Integer RESULT_LOAD_IMAGE = 1;
    private static final Integer RESULT_CLICK_LOAD_IMAGE = 2;
    private static Integer CHECK=0;

    private File imgFile = null;
    private Uri selectedImageUri = null;
    private final static String CAPTURED_PHOTO_URI_KEY = "mClientRegProfileImageView";
    private final static String CAPTURED_PHOTO_CHECK_FLAG = "flag";
    private String mImageName = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_client_register, container, false);
        mClientRegProgressBar = (ProgressBar) rootView.findViewById(R.id.client_register_progressBar);
        mClientRegFormView = (LinearLayout) rootView.findViewById(R.id.client_register_det_linear_layout);

        mClientRegDescTextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_descr_textView);
        mClientRegNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_name_textView);
        mClientRegAddressTextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_address_textView);
        mClientRegPinTextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_pincode_textView);
        mClientRegPhone1TextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_phone1_textView);
        mClientRegPhone2TextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_phone2_textView);
        mClientRegLicNumTextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_lic_num_textView);
        mClientRegSpecTextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_speclty_textView);
        mClientRegEmailTextView = (AutoCompleteTextView) rootView.findViewById(R.id.client_register_email_textView);

        mClientRegSaveButton = (Button) rootView.findViewById(R.id.client_register_save_button);
        mClientRegCameraButton = (ImageButton) rootView.findViewById(R.id.client_register_photo_image_btn);

        mClientRegProfileImageView = (ImageView) rootView.findViewById(R.id.client_register_photo_image_view);
        mClientLicenseHintImageView = (ImageView) rootView.findViewById(R.id.client_register_lic_hint_image_view);

        AdView mAdView = (AdView) rootView.findViewById(R.id.fragment_register_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mClientLicenseHintImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(rootView, R.string.optional_field_required,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
            }
        });

        Bundle userParamBundle = this.getArguments();
        userName = userParamBundle.getString("userName");
        userCode = userParamBundle.getString("userCode");

        setCountryCode(rootView);
        setClientType(rootView);
        showProgress(false);

        internetConn = new CheckInternetConnection(getActivity());

        mClientRegSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConn.isNetworkConnected() == true) {
                    attemptRegister(rootView);
                } else {
                    displayAlert();
                }
            }
        });

        mClientRegCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(rootView);
            }
        });

        if(savedInstanceState != null) {
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

            showImage();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Snackbar.make(getView(), R.string.optional_field_required,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(selectedImageUri != null) {
            outState.putString(CAPTURED_PHOTO_URI_KEY, selectedImageUri.toString());
        }

        if (CHECK != 0) {
            outState.putInt(CAPTURED_PHOTO_CHECK_FLAG, CHECK);
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
            mClientRegProfileImageView.setImageURI(selectedImageUri);
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

            mClientRegProfileImageView.setImageBitmap(capturePhotoBitmap);
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
        mClientRegCountryCodeSpnr = (Spinner) rootView.findViewById(R.id.client_register_country_spinner);

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
        mClientRegCountryCodeSpnr.setAdapter(dataAdapter);
        mClientRegCountryCodeSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selectedCountry = (String) mClientRegCountryCodeSpnr.getSelectedItem();
                int selectedPosition = fCountry.indexOf(selectedCountry);
                String correspondingCode = fCode.get(selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setClientType (View rootView) {
        mClientRegTypeSpnr = (Spinner) rootView.findViewById(R.id.client_register_type_spinner);

        Resources resources = getResources();
        TypedArray clientTypeArr = resources.obtainTypedArray(R.array.clientType);

        ArrayList<String> type = new ArrayList<String>();
        ArrayList<String> code = new ArrayList<String>();

        int clientTypArrLen = clientTypeArr.length();
        for (int i=0; i < clientTypArrLen; i++) {
            int clientTypeId = clientTypeArr.getResourceId(i, 0);
            code.add(resources.getStringArray(clientTypeId)[0]);
            type.add(resources.getStringArray(clientTypeId)[1]);
        }

        clientTypeArr.recycle();

        fClientTypeCode = code;
        fClientType = type;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, type);
        mClientRegTypeSpnr.setAdapter(dataAdapter);
        mClientRegTypeSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selectedType = (String) mClientRegTypeSpnr.getSelectedItem();
                int selectedPosition = fClientType.indexOf(selectedType);
                String correspondingCode = fClientTypeCode.get(selectedPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
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

    private void attemptRegister(View rootView) {
        try {
            if (crt != null) {
                return;
            }

            String errorStr = "";

            // Reset errors.
            mClientRegDescTextView.setError(null);
            mClientRegNameTextView.setError(null);
            mClientRegAddressTextView.setError(null);
            mClientRegPinTextView.setError(null);
            mClientRegPhone1TextView.setError(null);
            mClientRegPhone2TextView.setError(null);
            mClientRegLicNumTextView.setError(null);
            mClientRegSpecTextView.setError(null);
            mClientRegEmailTextView.setError(null);

            // Store values at the time of the login attempt.
            String description = mClientRegDescTextView.getText().toString();
            String name = mClientRegNameTextView.getText().toString();
            String address = mClientRegAddressTextView.getText().toString();
            String pincode = mClientRegPinTextView.getText().toString();
            String phone1 = mClientRegPhone1TextView.getText().toString();
            String phone2 = mClientRegPhone2TextView.getText().toString();
            String licenseNum = mClientRegLicNumTextView.getText().toString();
            String speciality = mClientRegSpecTextView.getText().toString();
            String email = mClientRegEmailTextView.getText().toString();
            Bitmap profilePhoto = ((BitmapDrawable) mClientRegProfileImageView.getDrawable()).getBitmap();
            String profilePhotoFileNm = mImageName;

            boolean cancel = false;
            View focusView = null;

            // Check if Description entered
            if (TextUtils.isEmpty(description)) {
                mClientRegDescTextView.setError(getString(R.string.error_field_required));
                focusView = mClientRegDescTextView;
                cancel = true;
            }

            // Check if Name entered.
            if (TextUtils.isEmpty(name)) {
                mClientRegNameTextView.setError(getString(R.string.error_field_required));
                focusView = mClientRegNameTextView;
                cancel = true;
            } else if (!isNameValid(name)) {
                mClientRegNameTextView.setError(getString(R.string.error_invalid_name));
                focusView = mClientRegNameTextView;
                cancel = true;
            }

            // Check if Address entered
            if (TextUtils.isEmpty(address)) {
                mClientRegAddressTextView.setError(getString(R.string.error_field_required));
                focusView = mClientRegAddressTextView;
                cancel = true;
            }

            // Check if PinCode entered
            if (TextUtils.isEmpty(pincode)) {
                mClientRegAddressTextView.setError(getString(R.string.error_field_required));
                focusView = mClientRegPinTextView;
                cancel = true;
            }

            // Check for a valid Phone1.
            if (TextUtils.isEmpty(phone1)) {
                mClientRegPhone1TextView.setError(getString(R.string.error_field_required));
                focusView = mClientRegPhone1TextView;
                cancel = true;
            } else if (!isPhoneValid(phone1)) {
                mClientRegPhone1TextView.setError(getString(R.string.error_invalid_phone));
                focusView = mClientRegPhone1TextView;
                cancel = true;
            }

            // Check for a valid Phone2.
            if (!isPhoneValid(phone2)) {
                mClientRegPhone2TextView.setError(getString(R.string.error_invalid_phone));
                focusView = mClientRegPhone2TextView;
                cancel = true;
            }

            // Check for a valid Email.
            if (TextUtils.isEmpty(email)) {
                mClientRegEmailTextView.setError(getString(R.string.error_field_required));
                focusView = mClientRegEmailTextView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mClientRegEmailTextView.setError(getString(R.string.error_invalid_email));
                focusView = mClientRegEmailTextView;
                cancel = true;
            }

            // Check if License Number entered
            if (TextUtils.isEmpty(licenseNum)) {
                mClientRegLicNumTextView.setError(getString(R.string.error_field_required));
                focusView = mClientRegLicNumTextView;
                cancel = true;
            }

            // Check if Speciality entered
            if (TextUtils.isEmpty(speciality)) {
                mClientRegSpecTextView.setError(getString(R.string.error_field_required));
                focusView = mClientRegSpecTextView;
                cancel = true;
            }

            String selectedRegType = (String) mClientRegTypeSpnr.getSelectedItem();
            int selectedTypePosition = fClientType.indexOf(selectedRegType);
            String correspondingRegTypeCode = fClientTypeCode.get(selectedTypePosition);
            if (correspondingRegTypeCode.equals("0")) {
                errorStr += " RegistrationType";
                focusView = mClientRegTypeSpnr;
                cancel = true;
            }

            String selectedCountry = (String) mClientRegCountryCodeSpnr.getSelectedItem();
            int selectedPosition = fCountry.indexOf(selectedCountry);
            String correspondingCode = fCode.get(selectedPosition);
            if (correspondingCode.equals("0")) {
                errorStr += " Country";
                focusView = mClientRegCountryCodeSpnr;
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

                crt = new ClientRegisterTask(name, address, pincode, correspondingCode, phone1, phone2,
                        email, licenseNum, speciality, selectedRegType, description, userName, userCode, profilePhotoFileNm, profilePhoto);
                crt.execute(name, address, pincode, correspondingCode, phone1, phone2,
                        email, licenseNum, speciality, selectedRegType, description, userName, userCode, profilePhotoFileNm);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mClientRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mClientRegFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mClientRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mClientRegProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mClientRegProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mClientRegProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mClientRegProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mClientRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class ClientRegisterTask extends AsyncTask<String, String, String> {

        //crt = new ClientRegisterTask(userName, firstName, middleName, lastName, mClientRegGenderRadioBtn.getText(),
        //dob, selectedCountry, correspondingCode, phone, email, address);

        private final String mClientName, mClientAddress, mClientPin, mClientCountryCode, mClientPhoneNum1, mClientPhoneNum2;
        private final String mClientEmail, mClientLicenseNum, mClientSpeciality, mClientType, mClientDesc, mUserName, mUserCode, mClientProfilePhotoNm;

        private final Bitmap mClientPhoto;

        ClientRegisterTask(String clientName, String clientAddress, String clientPin, String clientCountryCode,
                           String clientPhoneNum1, String clientPhoneNum2, String clientEmail, String clientLicenseNum,
                           String clientSpeciality, String clientType, String clientDesc, String userName, String userCode, String clientProfilePhotoNm,
                           Bitmap clientPhoto) {
            mClientName=clientName;
            mClientAddress=clientAddress;
            mClientPin=clientPin;
            mClientCountryCode=clientCountryCode;
            mClientPhoneNum1=clientPhoneNum1;
            mClientPhoneNum2=clientPhoneNum2;
            mClientEmail=clientEmail;
            mClientLicenseNum=clientLicenseNum;
            mClientSpeciality=clientSpeciality;
            mClientType=clientType;
            mClientDesc=clientDesc;
            mUserName=userName;
            mUserCode=userCode;
            mClientProfilePhotoNm = clientProfilePhotoNm;
            mClientPhoto = clientPhoto;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String loginResponse = "";

            ByteArrayOutputStream clientPhotoByteArrOpStream = null;
            String encodedPhoto = "";

            if(params[13] != null) {
                clientPhotoByteArrOpStream = new ByteArrayOutputStream();
                mClientPhoto.compress(Bitmap.CompressFormat.JPEG, 100, clientPhotoByteArrOpStream);
                encodedPhoto = Base64.encodeToString(clientPhotoByteArrOpStream.toByteArray(), Base64.DEFAULT);
            }

            try {
                // Simulate network access.
                Thread.sleep(2000);

                final String login_url = getString(R.string.url) + "/clientRegister.php";
                try {
                    URL loginURL = new URL(login_url);
                    HttpURLConnection loginHTTP = (HttpURLConnection) loginURL.openConnection();
                    loginHTTP.setRequestMethod("POST");
                    loginHTTP.setDoOutput(true);
                    OutputStream loginOS = loginHTTP.getOutputStream();
                    BufferedWriter loginWriter = new BufferedWriter(new OutputStreamWriter(loginOS,"UTF-8"));

                    String loginData = URLEncoder.encode("client_name", "UTF-8") + "=" + URLEncoder.encode(params[0],"UTF-8") + "&" +
                            URLEncoder.encode("client_address", "UTF-8") + "=" + URLEncoder.encode(params[1],"UTF-8") + "&" +
                            URLEncoder.encode("client_pin", "UTF-8") + "=" + URLEncoder.encode(params[2],"UTF-8") + "&" +
                            URLEncoder.encode("client_country_cd", "UTF-8") + "=" + URLEncoder.encode(params[3],"UTF-8") + "&" +
                            URLEncoder.encode("client_phone1", "UTF-8") + "=" + URLEncoder.encode(params[4],"UTF-8") + "&" +
                            URLEncoder.encode("client_phone2", "UTF-8") + "=" + URLEncoder.encode(params[5],"UTF-8") + "&" +
                            URLEncoder.encode("client_email", "UTF-8") + "=" + URLEncoder.encode(params[6],"UTF-8") + "&" +
                            URLEncoder.encode("client_lic_num", "UTF-8") + "=" + URLEncoder.encode(params[7],"UTF-8") + "&" +
                            URLEncoder.encode("client_speciality", "UTF-8") + "=" + URLEncoder.encode(params[8],"UTF-8") + "&" +
                            URLEncoder.encode("client_type", "UTF-8") + "=" + URLEncoder.encode(params[9], "UTF-8") + "&" +
                            URLEncoder.encode("client_descr", "UTF-8") + "=" + URLEncoder.encode(params[10], "UTF-8") + "&" +
                            URLEncoder.encode("user_code", "UTF-8") + "=" + URLEncoder.encode(params[12], "UTF-8");

                    if(params[13] == null) {
                        loginData += "&" + URLEncoder.encode("client_photo_path", "UTF-8") + "=&" + URLEncoder.encode("client_photo", "UTF-8") + "=";
                    } else {
                        loginData += "&" + URLEncoder.encode("client_photo_path", "UTF-8") + "=" + URLEncoder.encode(params[13], "UTF-8") + "&" +
                                URLEncoder.encode("client_photo", "UTF-8") + "=" + URLEncoder.encode(encodedPhoto, "UTF-8");
                    }

                    loginWriter.write(loginData);
                    loginWriter.flush();
                    loginWriter.close();
                    loginOS.close();

                    InputStream loginIS = loginHTTP.getInputStream();
                    BufferedReader loginReader = new BufferedReader(new InputStreamReader(loginIS,"iso-8859-1"));
                    String line = "";
                    while ((line = loginReader.readLine()) != null)
                    {
                        loginResponse += line;
                    }
                    loginReader.close();
                    loginIS.close();
                    loginHTTP.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                return "Something unfortunate happened.";
            }
            return loginResponse;
        }

        @Override
        protected void onPostExecute(final String result) {
            crt = null;
            showProgress(false);
            if(result.startsWith("Client registration completed")) {
                Bundle userParamBundle = new Bundle();
                userParamBundle.putString("userName", userName);
                userParamBundle.putString("userCode", userCode);
                ClientListFragment fragment2 = new ClientListFragment();
                fragment2.setArguments(userParamBundle);
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.docDirContentFrame, fragment2, "MED_LIST");
//                fragmentTransaction.add(R.id.docDirContentLinearLayout, fragment2, "MED_LIST");
                fragmentTransaction.addToBackStack("MED_LIST");
                fragmentTransaction.commit();
            }
            Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            crt = null;
            showProgress(false);
        }
    }

}
