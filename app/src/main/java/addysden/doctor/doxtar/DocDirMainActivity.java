package addysden.doctor.doxtar;

import android.accounts.OnAccountsUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DocDirMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private String userName, userCode;
    private CheckInternetConnection internetConn = null;
    private NavigationView navigationView;
    private boolean doubleBackToExitPressedOnce = false;
    private ImageView mNavViewImageView;
    private TextView mNavViewTextView;
    private boolean firstVisit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_dir_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        userName = getIntent().getExtras().getString("userName");
        userCode = getIntent().getExtras().getString("userCode");

//        System.out.println("DocDirMainActivity: " + userName + userCode);

        TextView postLoginUserName = (TextView) headerView.findViewById(R.id.doc_dir_login_email_textView);
        postLoginUserName.setText(userName);

        if(savedInstanceState != null) {
            firstVisit = savedInstanceState.getBoolean("FIRST_VISIT");
        }

        if(firstVisit) {
            Bundle userParamBundle = new Bundle();
            userParamBundle.putString("userName", userName);
            HomeFragment fragment2 = new HomeFragment();
            fragment2.setArguments(userParamBundle);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.docDirContentFrame, fragment2, "HOME");
            fragmentTransaction.addToBackStack("HOME");
            fragmentTransaction.commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("FIRST_VISIT", firstVisit);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if ((getSupportFragmentManager().getBackStackEntryCount() == 0) || (entry.getName().equals("HOME"))) {
            System.out.println("onBackPressed Count: " + getSupportFragmentManager().getBackStackEntryCount());

            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
//                super.onBackPressed();
//                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.logout_warning, Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        } else {
            System.out.println("OnbackPressed: else loop");
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doc_dir_main, menu);
        return true;
    }

    public void logout(String userName) {
        UpdateUserSQLiteDb sqLiteDb = new UpdateUserSQLiteDb(getApplicationContext());
        sqLiteDb.deleteUser(userName);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            logout(userName);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Bundle userParamBundle = new Bundle();
            userParamBundle.putString("userName", userName);
            userParamBundle.putString("userCode", userCode);
            ClientSearchFragment clientSearchFragment = new ClientSearchFragment();
            clientSearchFragment.setArguments(userParamBundle);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.docDirContentFrame, clientSearchFragment, "SEARCH");
//            fragmentTransaction.add(R.id.docDirContentLinearLayout, clientSearchFragment, "SEARCH");
            fragmentTransaction.addToBackStack("SEARCH");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_register) {
            Bundle userParamBundle = new Bundle();
            userParamBundle.putString("userName", userName);
            userParamBundle.putString("userCode", userCode);
            ClientRegisterFragment clientRegisterFragment = new ClientRegisterFragment();
            clientRegisterFragment.setArguments(userParamBundle);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.docDirContentFrame, clientRegisterFragment, "REGISTER");
//            fragmentTransaction.add(R.id.docDirContentLinearLayout, clientRegisterFragment, "REGISTER");
            fragmentTransaction.addToBackStack("REGISTER");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_slideshow) {
            Bundle userParamBundle = new Bundle();
            userParamBundle.putString("userName", userName);
            userParamBundle.putString("userCode", userCode);
            ClientListFragment clientListFragment = new ClientListFragment();
            clientListFragment.setArguments(userParamBundle);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.docDirContentFrame, clientListFragment, "ALLMEDS");
//            fragmentTransaction.add(R.id.docDirContentLinearLayout, clientListFragment, "ALLMEDS");
            fragmentTransaction.addToBackStack("ALLMEDS");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_profile) {
            Bundle userParamBundle = new Bundle();
            userParamBundle.putString("userName", userName);
            userParamBundle.putString("userCode", userCode);
            UserProfileFragment userProfileFragment = new UserProfileFragment();
            userProfileFragment.setArguments(userParamBundle);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.docDirContentFrame, userProfileFragment, "USER_PROFILE");
            fragmentTransaction.addToBackStack("USER_PROFILE");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_home) {
            Bundle userParamBundle = new Bundle();
            userParamBundle.putString("userName", userName);
            userParamBundle.putString("userCode", userCode);
            HomeFragment clientHomeFragment = new HomeFragment();
            clientHomeFragment.setArguments(userParamBundle);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.docDirContentFrame, clientHomeFragment, "HOME");
//            fragmentTransaction.add(R.id.docDirContentLinearLayout, clientListFragment, "ALLMEDS");
            fragmentTransaction.addToBackStack("HOME");
            fragmentTransaction.commit();
        }
        firstVisit = false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        } else {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);

            if (entry.getName().equals("HOME")) {
                System.out.println("Home clicked");
                navigationView.getMenu().getItem(0).setChecked(true);
            } else if(entry.getName().equals("SEARCH") || entry.getName().equals("MED_FRG2")) {
                System.out.println("Search/Med_frg2 clicked");
                navigationView.getMenu().getItem(1).setChecked(true);
            } else if (entry.getName().equals("REGISTER")) {
                System.out.println("register clicked");
                navigationView.getMenu().getItem(2).setChecked(true);
            } else if (entry.getName().equals("ALLMEDS") || entry.getName().equals("MED_LIST") || entry.getName().equals("MED_FRG1")) {
                System.out.println("allmeds/med_list_med_frg1 clicked");
                navigationView.getMenu().getItem(3).setChecked(true);
            }
        }
    }
}
