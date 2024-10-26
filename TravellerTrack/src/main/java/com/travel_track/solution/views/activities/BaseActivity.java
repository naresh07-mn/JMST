package com.travel_track.solution.views.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.travel_track.solution.R;
import com.travel_track.solution.data.PreferenceManager;
import com.travel_track.solution.interfaces.DialogClickListener;
import com.travel_track.solution.interfaces.DialogClickListenerWithInput;
import com.travel_track.solution.model.LoginModel;
import com.travel_track.solution.utils.AppUtils;
import com.travel_track.solution.views.components.CustomDialog;
import com.travel_track.solution.views.components.CustomDialogWithInput;

import org.jetbrains.annotations.Nullable;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_view_content)
    FrameLayout childView;

    private Unbinder unbinder;

    CustomDialog blockingMsgDialog;
    CustomDialogWithInput blockingMsgDialogWithInput;

    PreferenceManager preferenceManager;
    LoginModel userDetails;

    boolean userTypeDriver = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        preferenceManager = PreferenceManager.getInstance(this);
        userDetails = preferenceManager.getUserInfo();
        //Setting sliding drawer menu
        ImageView mapimage= findViewById(R.id.image_3);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Navigation for options...
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView user_name = headerView.findViewById(R.id.user_name);
        TextView user_type = headerView.findViewById(R.id.user_type);
        ImageView imageView = headerView.findViewById(R.id.imageView);
        userTypeDriver = false;
        if (userDetails != null && userDetails.getLoginMode() != null
                && userDetails.getLoginMode().equalsIgnoreCase("Driver")) {
            userTypeDriver = true;
            if(userDetails.getProfileUrl()!=null){
                Glide.with(this).load(userDetails.getProfileUrl()).into(imageView);
            }
        }
        if (userTypeDriver) {
            Menu menu = navigationView.getMenu();
            menu.removeItem(R.id.add_booking);
            if (userDetails != null && userDetails.getName() != null) {
                user_name.setText("Welcome " + userDetails.getName());
            }
            if (userDetails != null && userDetails.getLoginMode() != null) {
                user_type.setText("" + userDetails.getLoginMode());
            }
        } else {
            if (userDetails != null && userDetails.getFirstName() != null) {
                user_name.setText("Welcome " + userDetails.getFirstName());
            }
            if (userDetails != null && userDetails.getLoginMode() != null) {
                user_type.setText("" + userDetails.getLoginMode());
            }
        }

        //hiding menu for non supporting pages
        if (this instanceof LoginActivity || this instanceof SplashActivity) {
            toolbar.setNavigationIcon(null);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        onBuildUserInterface();
    }

    protected void setViewContents(View view) {
        childView.removeAllViews();
        childView.addView(view);
        unbinder = ButterKnife.bind(this);
    }

    abstract void onBuildUserInterface();

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppUtils.NOTIFICATION_RECEIVED);
        registerReceiver(pushReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (pushReceiver != null)
                unregisterReceiver(pushReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String title = extras.getString("title");
                String message = extras.getString("message");
                showBlockingMessage(null, title, message, getString(R.string.text_ok), null);
            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.home_page) {
            if (!(this instanceof HomeActivity)) {
                startActivity(new Intent(this, HomeActivity.class));
            }
        } else if (id == R.id.my_rides) {
            startActivity(new Intent(this, MyRidesActivity.class));
        } else if (id == R.id.add_booking) {
            startActivity(new Intent(this, UserTripBookingActivity.class));
        } else if(id==R.id.menu_map){
            startActivity(new Intent(this, CustomMapActivity.class));

        } else if(id==R.id.menu_signature){
            startActivity(new Intent(this, CustomSignatureActivity.class));

        }else if (id == R.id.logout) {
            PreferenceManager.getInstance(this).logout();
            Intent mIntent = new Intent(getApplicationContext(), LoginActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
        } else if (id == R.id.help) {
            Intent mIntent = new Intent(getApplicationContext(), WebviewActivity.class);
            startActivity(mIntent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    protected void showBlockingMessage(final DialogClickListener clickListner, final String title, final String message, String buttonName1, String buttonName2) {
        /* I believe this an effective fix. In general cases, we should not do this, but since the Android Framework does not provide any easy check for us, we have to use unusual way.
         * Also, if a dialog's isShowing() call working as we expect, we do not need this kind of hack.*/
        try {
            if ((blockingMsgDialog != null) && blockingMsgDialog.isShowing()) {
                blockingMsgDialog.dismiss();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            blockingMsgDialog = null;
        }
        blockingMsgDialog = new CustomDialog(this, buttonName1, buttonName2);
        blockingMsgDialog.setCancelable(false);
        blockingMsgDialog.setTitle(title);
        blockingMsgDialog.setMessage(message);
        blockingMsgDialog.setIcon(R.drawable.ic_launcher);
        if (buttonName1 != null)
            blockingMsgDialog.setButton1(buttonName1.intern(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blockingMsgDialog.dismiss();
                    if (clickListner != null)
                        clickListner.onButton1Click();
                }
            });
        if (buttonName2 != null)
            blockingMsgDialog.setButton2(buttonName2.intern(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blockingMsgDialog.dismiss();
                    if (clickListner != null)
                        clickListner.onButton2Click();
                }
            });
        //Introducing this new keylistner for some of the devices have search button. search buttons click dismissed dialog.
        blockingMsgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0; // Pretend we processed it
// Any other keys are still processed as normal
            }

        });
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!BaseActivity.this.isFinishing() && blockingMsgDialog != null) {
                    blockingMsgDialog.show();
                }
            }
        });

    }

    protected void showBlockingMessageWithInput(final DialogClickListenerWithInput clickListner, final String title, final String message, String hint, String value, final String error, String hint2, String value2, final String error2, String buttonName1, String buttonName2) {
        /* I believe this an effective fix. In general cases, we should not do this, but since the Android Framework does not provide any easy check for us, we have to use unusual way.
         * Also, if a dialog's isShowing() call working as we expect, we do not need this kind of hack.*/
        try {
            if ((blockingMsgDialogWithInput != null) && blockingMsgDialogWithInput.isShowing()) {
                blockingMsgDialogWithInput.dismiss();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            blockingMsgDialogWithInput = null;
        }
        blockingMsgDialogWithInput = new CustomDialogWithInput(this, buttonName1, buttonName2);
        blockingMsgDialogWithInput.setCancelable(false);
        blockingMsgDialogWithInput.setTitle(title);
        blockingMsgDialogWithInput.setMessage(message);
        blockingMsgDialogWithInput.setEditTextVisibility(hint, value);
        blockingMsgDialogWithInput.setEditTextVisibility2(hint2, value2);
        if (buttonName1 != null)
            blockingMsgDialogWithInput.setButton1(buttonName1.intern(), v -> {
                if (TextUtils.isEmpty(blockingMsgDialogWithInput.getInputText())) {
                    blockingMsgDialogWithInput.setError1(error);
                    return;
                } else if (!blockingMsgDialogWithInput.isValid) return;
                blockingMsgDialogWithInput.dismiss();
                if (clickListner != null) {
                    clickListner.onButton1Click(blockingMsgDialogWithInput.getInputText(), blockingMsgDialogWithInput.getInputText2());
                }
            });
        if (buttonName2 != null)
            blockingMsgDialogWithInput.setButton2(buttonName2.intern(), v -> {
                blockingMsgDialogWithInput.dismiss();
                if (clickListner != null)
                    clickListner.onButton2Click(blockingMsgDialogWithInput.getInputText(), blockingMsgDialogWithInput.getInputText2());
            });
        //Introducing this new keylistner for some of the devices have search button. search buttons click dismissed dialog.
        blockingMsgDialogWithInput.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0; // Pretend we processed it
// Any other keys are still processed as normal
            }

        });
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!BaseActivity.this.isFinishing() && blockingMsgDialogWithInput != null) {
                    blockingMsgDialogWithInput.show();
                }
            }
        });

    }

    protected static void hideKeyBoard(View view, boolean hideKeyBoard) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hideKeyBoard) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    ProgressDialog dialog;
    protected void showProgress() {
        if (!isFinishing()) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait...");
            dialog.show();
        }
    }

    protected void hideProgress() {
        if (dialog != null && !isFinishing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
