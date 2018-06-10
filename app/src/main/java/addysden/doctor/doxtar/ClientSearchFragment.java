package addysden.doctor.doxtar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ClientSearchFragment extends Fragment {

    private CheckInternetConnection internetConn = null;

    private SearchView mClientSearchSearchView;
    private LinearLayout mClientSearchLinearLayout;
    private ListView mClientSearchListView;
    private ProgressBar mClientSearchProgressBar, mClientSearchLVFooterProgressBar;
    private SetClientListRowItem sclrt;
    private String userName, userCode;
    private int countRefresh=1, lastPosition;
    private boolean isLoading=false;
    private ArrayList<clientListData> clientListDataSavedList = new ArrayList<>();
    private MyClientListRecyclerViewAdapter adapter = null;
    private View rootView;
    private String searchString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_client_search, container, false);

        mClientSearchSearchView = (SearchView) rootView.findViewById(R.id.client_search_search_view);
        mClientSearchLinearLayout = (LinearLayout) rootView.findViewById(R.id.client_search_linear_layout);
        mClientSearchListView = (ListView) rootView.findViewById(R.id.client_search_list_view);
        mClientSearchProgressBar = (ProgressBar) rootView.findViewById(R.id.client_search_progressBar);
        mClientSearchLVFooterProgressBar = new ProgressBar(this.getActivity());

        Bundle userParamBundle = this.getArguments();
        userName = userParamBundle.getString("userName");
        userCode = userParamBundle.getString("userCode");
        internetConn = new CheckInternetConnection(getActivity());
        mClientSearchListView.addFooterView(mClientSearchLVFooterProgressBar);
        mClientSearchLVFooterProgressBar.setVisibility(View.INVISIBLE);

        AdView mAdView = (AdView) rootView.findViewById(R.id.fragment_search_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        showProgress(false);

        if(savedInstanceState!=null) {
            showProgress(false);
            clientListDataSavedList = savedInstanceState.getParcelableArrayList("LIST_INSTANCE_STATE");
            countRefresh = savedInstanceState.getInt("countRefresh");
            isLoading = savedInstanceState.getBoolean("isLoading");
            lastPosition = savedInstanceState.getInt("lastPosition");
            mClientSearchListView.setAdapter(new MyClientListRecyclerViewAdapter(getContext(), R.layout.fragment_clientlist, clientListDataSavedList));
            mClientSearchListView.setSelection(lastPosition);
        }

        mClientSearchSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (internetConn.isNetworkConnected() == true) {
                    searchString = query;
                    showProgress(true);
                    mClientSearchLVFooterProgressBar.setVisibility(View.VISIBLE);

                    freezeOrientation();
                    sclrt = new SetClientListRowItem();
                    sclrt.execute("initial", query);
                } else {
                    displayAlert();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mClientSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle userParamBundle = new Bundle();
                userParamBundle.putString("userName", userName);
                userParamBundle.putString("userCode", userCode);
                userParamBundle.putString("clientRefNumber", view.findViewById(R.id.client_list_image_view).getTag().toString());
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                view.findViewById(R.id.client_list_image_view).buildDrawingCache();
                view.findViewById(R.id.client_list_image_view).getDrawingCache().compress(Bitmap.CompressFormat.PNG, 50, bs);
                userParamBundle.putByteArray("clientImage", bs.toByteArray());

                if (internetConn.isNetworkConnected() == true) {
                    PopDetailedClientInfo fragment2 = new PopDetailedClientInfo();
                    fragment2.setArguments(userParamBundle);
                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.docDirContentFrame, fragment2, "MED_FRG2");
//                    fragmentTransaction.add(R.id.docDirContentLinearLayout, fragment2, "MED_FRG2");
                    fragmentTransaction.addToBackStack("MED_FRG2");
                    fragmentTransaction.commit();
                } else {
                    displayAlert();
                }
            }
        });

        mClientSearchListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastOnScreen = firstVisibleItem + visibleItemCount;

                if(firstVisibleItem == 0 && lastOnScreen == totalItemCount) {
                    mClientSearchLVFooterProgressBar.setVisibility(View.INVISIBLE);
                }

                if((lastOnScreen == totalItemCount) && (firstVisibleItem != 0) && !isLoading && (totalItemCount != 0)) {
                    isLoading = true;
                    mClientSearchLVFooterProgressBar.setVisibility(View.VISIBLE);
                    incrementList();
                    lastPosition = firstVisibleItem;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(clientListDataSavedList != null) {
            outState.putParcelableArrayList("LIST_INSTANCE_STATE", clientListDataSavedList);
            outState.putInt("countRefresh", countRefresh);
            outState.putBoolean("isLoading", isLoading);
            outState.putInt("lastPosition", lastPosition-1);
        }
    }

    public void incrementList() {
        if (internetConn.isNetworkConnected() == true) {
            freezeOrientation();
            sclrt = new SetClientListRowItem();
            sclrt.execute("next", searchString);
        } else  {
            displayAlert();
        }
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mClientSearchLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mClientSearchLinearLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mClientSearchLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mClientSearchProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mClientSearchProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mClientSearchProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mClientSearchProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mClientSearchLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        if(show || isLoading) {
            freezeOrientation();
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
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

    public class SetClientListRowItem extends AsyncTask<String, Void, ArrayList<clientListData>> {

        private ArrayList<clientListData> clientListDataList = new ArrayList<>();

        @Override
        protected ArrayList<clientListData> doInBackground(String... params) {
            String clientListResponse = "";

            try {
                final String clientListResponse_url = getString(R.string.url) + "/selectClients.php";
                try {
                    if(params[0].equals("initial")) {
                        countRefresh = 1;
                        lastPosition = 0;
                    } else if (params[0].equals("next")) {
                        countRefresh += 1;
                    }

                    URL clientListResponseURL = new URL(clientListResponse_url);
                    HttpURLConnection clientListResponseHTTP = (HttpURLConnection) clientListResponseURL.openConnection();
                    clientListResponseHTTP.setRequestMethod("POST");
                    clientListResponseHTTP.setDoOutput(true);
                    OutputStream clientListResponseOS = clientListResponseHTTP.getOutputStream();
                    BufferedWriter clientListResponseWriter = new BufferedWriter(new OutputStreamWriter(clientListResponseOS,"UTF-8"));
                    String clientListResponseData = URLEncoder.encode("select_all", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(countRefresh), "UTF-8") +
                            "&" + URLEncoder.encode("search_str", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");
                    clientListResponseWriter.write(clientListResponseData);
                    clientListResponseWriter.flush();
                    clientListResponseWriter.close();
                    clientListResponseOS.close();

                    InputStream clientListResponseIS = clientListResponseHTTP.getInputStream();
                    BufferedReader clientListResponseReader = new BufferedReader(new InputStreamReader(clientListResponseIS,"iso-8859-1"));
                    String line = "";
                    while ((line = clientListResponseReader.readLine()) != null)
                    {
                        clientListResponse += line;
                    }
                    clientListResponseReader.close();
                    clientListResponseIS.close();
                    clientListResponseHTTP.disconnect();

                    if(clientListResponse.equals("[]") && params[0].equals("initial")) {
                        Snackbar.make(rootView, R.string.seach_not_found_rationale,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .show();
                    } else {
                        JSONArray clientListJsonArr = new JSONArray(clientListResponse);

                        for (int i = 0; i < clientListJsonArr.length(); i++) {
                            clientListData cld = new clientListData();
                            JSONObject c = clientListJsonArr.getJSONObject(i);

                            cld.setClientName(c.getString("clientName"));
                            cld.setClientDescription(c.getString("clientDescription"));
                            cld.setClientSpeciality(c.getString("clientSpeciality"));
                            cld.setClientPhotoPath(c.getString("clientPhotoPath"));
                            cld.setClientRefNumber(c.getString("clientRefNumber"));
                            cld.setClientRating(c.getString("rating"));
                            cld.setClientView(c.getString("view"));
                            cld.setClientPhotoBitmap(getBitmapFromURL(c.getString("clientPhotoPath")));
                            cld.setClientType(c.getString("clientType"));

                            clientListDataList.add(cld);
                        }

                        if (params[0].equals("initial")) {
                            clientListDataSavedList = clientListDataList;
                        } else if (params[0].equals("next")) {
                            clientListDataSavedList.addAll(clientListDataList);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return clientListDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<clientListData> result) {
            adapter = new MyClientListRecyclerViewAdapter(getContext(), R.layout.fragment_clientlist, clientListDataSavedList);
            adapter.notifyDataSetChanged();
            mClientSearchListView.setAdapter(adapter);
            mClientSearchListView.setSelection(lastPosition-1);
            sclrt=null;
            showProgress(false);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            isLoading = false;
        }

        @Override
        protected void onCancelled()
        {
            sclrt=null;
            showProgress(false);
            isLoading = false;
            mClientSearchLVFooterProgressBar.setVisibility(View.INVISIBLE);
            countRefresh -= 1;
        }
    }
}
