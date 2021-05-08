/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.VisibleForTesting;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.AboutActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.SettingsActivity;
import com.mifos.mifosxdroid.activity.pathtracking.PathTrackingActivity;
import com.mifos.mifosxdroid.adapters.DrawerAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardFragment;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncCenter;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncClient;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncGroup;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncLoanRepayment;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncSavingsAccount;
import com.mifos.mifosxdroid.online.centerlist.CenterListFragment;
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxPendingTasksActivity;
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment;
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment;
import com.mifos.mifosxdroid.online.groupslist.GroupsListFragment;
import com.mifos.mifosxdroid.online.search.SearchFragment;
import com.mifos.objects.DrawerItem;
import com.mifos.objects.SimpleItem;
import com.mifos.objects.user.User;
import com.mifos.utils.Constants;
import com.mifos.utils.EspressoIdlingResource;
import com.mifos.utils.PrefManager;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;


import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class DashboardActivity extends MifosBaseActivity
        implements DrawerAdapter.OnItemSelectedListener {

    public static final String TAG = DashboardActivity.class.getSimpleName();
    private static final int POS_DASHBOARD = 0;
    private static final int POS_CLIENTS = 1;
    private static final int POS_GROUPS = 2;
    private static final int POS_CENTERS = 3;
    private static final int POS_CHECKER_INBOX = 4;
    private static final int POS_INDIVIDUAL_COLLECTION_REPORT = 5;
    private static final int POS_COLLECTION_REPORT = 6;
    private static final int POS_RUN_REPORTS = 7;
    private static final int POS_PATH_TRACKER = 8;
    private static final int POS_SETTINGS = 9;
    private static final int POS_ABOUT = 10;
    private static final int POS_OFFLINE_SYNC = 11;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

//    @BindView(R.id.navigation_view)
//    NavigationView mNavigationView;
//
//    @BindView(R.id.drawer)
//    DrawerLayout mDrawerLayout;


    View mNavigationHeader;
    SwitchCompat userStatusToggle;
    private Menu menu;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean itemClient = true, itemCenter = true, itemGroup = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);
        runJobs();
        replaceFragment(new SearchFragment(), false, R.id.container);

        Toolbar toolbarr = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbarr)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_CLIENTS),
                createItemFor(POS_GROUPS),
                createItemFor(POS_CENTERS),
                createItemFor(POS_CHECKER_INBOX),
                createItemFor(POS_INDIVIDUAL_COLLECTION_REPORT),
                createItemFor(POS_COLLECTION_REPORT),
                createItemFor(POS_RUN_REPORTS),
                createItemFor(POS_PATH_TRACKER),
                createItemFor(POS_SETTINGS),
                createItemFor(POS_ABOUT),
                createItemFor(POS_OFFLINE_SYNC)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);


        final User loggedInUser = PrefManager.getUser();
        TextView username =  findViewById(R.id.tv_user_namee);
        username.setText(loggedInUser.getUsername());

        // no profile picture credential, using dummy profile picture
        ImageView imageViewUserPicture = findViewById(R.id.iv_user_picturee);
        imageViewUserPicture.setImageResource(R.drawable.ic_dp_placeholder);

        // Navigation Toggle click and Offline Mode SwitchButton
        userStatusToggle = findViewById(R.id.user_status_togglee);
        if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
            userStatusToggle.setChecked(true);
        }

        userStatusToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
                    PrefManager.setUserStatus(Constants.USER_ONLINE);
                    userStatusToggle.setChecked(false);
                } else {
                    PrefManager.setUserStatus(Constants.USER_OFFLINE);
                    userStatusToggle.setChecked(true);
                }
            }
        });

//        if (slidingRootNav.isMenuOpened()) {
//            setUserStatus(userStatusToggle);
//        }

        //addOnBackStackChangedListener
        //to change title after Back Stack Changed
//        addOnBackStackChangedListener();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(R.color.red_app)
                .withTextTint(R.color.black)
                .withSelectedIconTint(R.color.deposit_green)
                .withSelectedTextTint(R.color.deposit_green);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }


    private void runJobs() {
        OfflineSyncCenter.schedulePeriodic();
        OfflineSyncGroup.schedulePeriodic();
        OfflineSyncClient.schedulePeriodic();
        OfflineSyncSavingsAccount.schedulePeriodic();
        OfflineSyncLoanRepayment.schedulePeriodic();
    }

    private void addOnBackStackChangedListener() {
        if (getSupportFragmentManager() == null) {
            return;
        }
        getSupportFragmentManager()
                .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
                        if (fragment instanceof CreateNewClientFragment) {
                            setActionBarTitle(R.string.create_client);
                            itemClient = false;
                            itemGroup = true;
                            itemCenter = true;
                            invalidateOptionsMenu();
                        } else if (fragment instanceof CreateNewGroupFragment) {
                            setActionBarTitle(R.string.create_group);
                            itemClient = true;
                            itemGroup = false;
                            itemCenter = true;
                            invalidateOptionsMenu();
                        } else if (fragment instanceof CreateNewCenterFragment) {
                            setActionBarTitle(R.string.create_center);
                            itemClient = true;
                            itemGroup = true;
                            itemCenter = false;
                            invalidateOptionsMenu();
                        } else {
                            itemClient = true;
                            itemGroup = true;
                            itemCenter = true;
                        }
                    }
                });

    }

    private void setMenuCreateGroup(boolean isEnabled) {
        if (menu != null) {
            //position of mItem_create_new_group is 2
            menu.getItem(2).setEnabled(isEnabled);
        }

    }

    private void setMenuCreateCentre(boolean isEnabled) {
        if (menu != null) {
            //position of mItem_create_new_centre is 1
            menu.getItem(1).setEnabled(isEnabled);
        }
    }

    private void setMenuCreateClient(boolean isEnabled) {
        if (menu != null) {
            //position of mItem_create_new_client is 0
            menu.getItem(0).setEnabled(isEnabled);
        }
    }

    @Override
    public void onItemSelected(int position) {
        slidingRootNav.closeMenu();
        clearFragmentBackStack();
        final Intent intent = new Intent();
        switch (position) {
            case 0:
                replaceFragment(new SearchFragment(), false, R.id.container);
                slidingRootNav.closeMenu();
                break;
            case 1:
                replaceFragment(ClientListFragment.newInstance(), false, R.id.container);
                slidingRootNav.closeMenu();
                break;
            case 2:
                replaceFragment(GroupsListFragment.newInstance(), false, R.id.container);
                slidingRootNav.closeMenu();
                break;
            case 3:
                replaceFragment(CenterListFragment.newInstance(), false, R.id.container);
                slidingRootNav.closeMenu();
                break;
            case 4:
                slidingRootNav.closeMenu();
                intent.setClass(this, CheckerInboxPendingTasksActivity.class);
                startActivity(intent);
                break;
            case 5:
                slidingRootNav.closeMenu();
                intent.setClass(this, GenerateCollectionSheetActivity.class);
                intent.putExtra(Constants.COLLECTION_TYPE, Constants.EXTRA_COLLECTION_INDIVIDUAL);
                startActivity(intent);
                break;
            case 6:
                slidingRootNav.closeMenu();
                intent.setClass(this, GenerateCollectionSheetActivity.class);
                intent.putExtra(Constants.COLLECTION_TYPE, Constants.EXTRA_COLLECTION_COLLECTION);
                startActivity(intent);
                break;
            case 7:
                slidingRootNav.closeMenu();
                intent.setClass(this, RunReportsActivity.class);
                startActivity(intent);
                break;
            case 8:
                slidingRootNav.closeMenu();
                intent.setClass(getApplicationContext(), PathTrackingActivity.class);
                startNavigationClickActivity(intent);
                break;
            case 9:
                slidingRootNav.closeMenu();
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 10:
                slidingRootNav.closeMenu();
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                break;
            case 11:
                replaceFragment(OfflineDashboardFragment.newInstance(), false, R.id.container);
                slidingRootNav.closeMenu();
                break;
        }

    }
    /**
     * sets up the navigation mDrawer in the activity
     */
//    protected void setupNavigationBar() {
//
//        mNavigationHeader = mNavigationView.getHeaderView(0);
//        setupUserStatusToggle();
//        mNavigationView.setNavigationItemSelectedListener(this);
//
//        // setup drawer layout and sync to toolbar
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
//                mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                setUserStatus(userStatusToggle);
//            }
//
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                if (slideOffset != 0)
//                    hideKeyboard(mDrawerLayout);
//                super.onDrawerSlide(drawerView, slideOffset);
//            }
//        };
//        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//
//        // make an API call to fetch logged in client's details
//    }


//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//
//        // ignore the current selected item
//        /*if (item.isChecked()) {
//            mDrawerLayout.closeDrawer(Gravity.LEFT);
//            return false;
//        }*/
//
//        // select which activity to open
//        clearFragmentBackStack();
//        final Intent intent = new Intent();
//        switch (item.getItemId()) {
//            case R.id.item_dashboard:
//                replaceFragment(new SearchFragment(), false, R.id.container);
//                break;
//            case R.id.item_clients:
//                replaceFragment(ClientListFragment.newInstance(), false, R.id.container);
//                break;
//            case R.id.item_groups:
//                replaceFragment(GroupsListFragment.newInstance(), false, R.id.container);
//                break;
//            case R.id.item_centers:
//                replaceFragment(CenterListFragment.newInstance(), false, R.id.container);
//                break;
//            case R.id.item_checker_inbox:
//                intent.setClass(this, CheckerInboxPendingTasksActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.item_path_tracker:
//                intent.setClass(getApplicationContext(), PathTrackingActivity.class);
//                startNavigationClickActivity(intent);
//                break;
//            case R.id.item_offline:
//                replaceFragment(OfflineDashboardFragment.newInstance(), false, R.id.container);
//                break;
//            case R.id.individual_collection_sheet:
//                intent.setClass(this, GenerateCollectionSheetActivity.class);
//                intent.putExtra(Constants.COLLECTION_TYPE, Constants.EXTRA_COLLECTION_INDIVIDUAL);
//                startActivity(intent);
//                break;
//            case R.id.collection_sheet:
//                intent.setClass(this, GenerateCollectionSheetActivity.class);
//                intent.putExtra(Constants.COLLECTION_TYPE, Constants.EXTRA_COLLECTION_COLLECTION);
//                startActivity(intent);
//                break;
//            case R.id.item_settings:
//                startActivity(new Intent(this, SettingsActivity.class));
//                break;
//            case R.id.runreport:
//                intent.setClass(this, RunReportsActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.about:
//                intent.setClass(this, AboutActivity.class);
//                startActivity(intent);
//                break;
//        }
//
//        mDrawerLayout.closeDrawer(GravityCompat.START);
//        mNavigationView.setCheckedItem(R.id.item_dashboard);
//        return true;
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        String currentFragment = Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.container))
//                .getClass().getSimpleName();
//        switch (currentFragment) {
//            case "SearchFragment":
//                mNavigationView.setCheckedItem(R.id.item_dashboard);
//                break;
//            case "ClientListFragment":
//                mNavigationView.setCheckedItem(R.id.item_clients);
//                break;
//            case "GroupsListFragment":
//                mNavigationView.setCheckedItem(R.id.item_groups);
//                break;
//            case "CenterListFragment":
//                mNavigationView.setCheckedItem(R.id.item_centers);
//                break;
//            case "OfflineDashboardFragment":
//                mNavigationView.setCheckedItem(R.id.item_offline);
//        }
//    }

    /**
     * This SwitchCompat Toggle Handling the User Status.
     * Setting the User Status to Offline or Online
     */
//    public void setupUserStatusToggle() {
//        userStatusToggle
//                = mNavigationHeader.findViewById(R.id.user_status_toggle);
//        if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
//            userStatusToggle.setChecked(true);
//        }
//
//        userStatusToggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
//                    PrefManager.setUserStatus(Constants.USER_ONLINE);
//                    userStatusToggle.setChecked(false);
//                } else {
//                    PrefManager.setUserStatus(Constants.USER_OFFLINE);
//                    userStatusToggle.setChecked(true);
//                }
//            }
//        });
//    }

    public void startNavigationClickActivity(final Intent intent) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);

            }
        }, 500);
    }


    /**
     * downloads the logged in user's username
     * sets dummy profile picture as no profile picture attribute available
     */
    private void loadClientDetails() {

        // download logged in user
        final User loggedInUser = PrefManager.getUser();

        TextView textViewUsername = ButterKnife.findById(mNavigationHeader, R.id.tv_user_name);
        textViewUsername.setText(loggedInUser.getUsername());

        // no profile picture credential, using dummy profile picture
        ImageView imageViewUserPicture = ButterKnife
                .findById(mNavigationHeader, R.id.iv_user_picture);
        imageViewUserPicture.setImageResource(R.drawable.ic_dp_placeholder);
    }

    @Override
    public void onBackPressed() {
        // check if the nav mDrawer is open
        if (slidingRootNav != null && slidingRootNav.isMenuOpened()) {
            slidingRootNav.closeMenu();
        } else {
            if (doubleBackToExitPressedOnce) {
                setMenuCreateClient(true);
                setMenuCreateCentre(true);
                setMenuCreateGroup(true);
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.back_again, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(itemClient);
        menu.getItem(1).setEnabled(itemCenter);
        menu.getItem(2).setEnabled(itemGroup);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mItem_create_new_client:
                setActionBarTitle(R.string.create_client);
                openCreateClient();
                break;
            case R.id.mItem_create_new_center:
                setActionBarTitle(R.string.create_center);
                openCreateCenter();
                break;
            case R.id.mItem_create_new_group:
                openCreateGroup();
                setActionBarTitle(R.string.create_group);
                break;
            case R.id.logout:
                logout();
                break;
            default: //DO NOTHING
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openCreateClient() {
        replaceFragment(CreateNewClientFragment.newInstance(), true, R.id.container);
    }

    public void openCreateCenter() {
        replaceFragment(CreateNewCenterFragment.newInstance(), true, R.id.container);

    }

    public void openCreateGroup() {
        replaceFragment(CreateNewGroupFragment.newInstance(), true, R.id.container);
    }


}
