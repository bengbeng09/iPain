package com.sklerbidi.therapistmobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.sklerbidi.therapistmobileapp.Menu.MenuDashboard;
import com.sklerbidi.therapistmobileapp.Menu.MenuSettings;
import com.sklerbidi.therapistmobileapp.Patient.PMenuTherapySession;
import com.sklerbidi.therapistmobileapp.Therapist.TMenuPatients;

public class ActivityNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    public DrawerLayout drawerLayout;
    public static NavigationView navigationView;
    public static String username, user_type, user_code,firstname, lastname, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            user_code = extras.getString("user_code");
            user_type = extras.getString("user_type");
            username = extras.getString("username");
            firstname = extras.getString("first_name");
            lastname = extras.getString("last_name");
            email = extras.getString("email");
        }

        set_item(user_type);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuDashboard()).commit();
        navigationView.setCheckedItem(R.id.navigation_dashboard);
    }

    private void set_item(String type)
    {
        Menu nav_Menu = navigationView.getMenu();
        switch (type){
            case "Clinic Therapist":
                nav_Menu.findItem(R.id.navigation_therapy_session).setVisible(false);
                break;
            case "Clinic Patient":
                nav_Menu.findItem(R.id.navigation_patients).setVisible(false);
                break;
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment selectedFragment;

        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                selectedFragment = new MenuDashboard();
                break;
            case R.id.navigation_therapy_session:
                selectedFragment = new PMenuTherapySession();
                break;
            case R.id.navigation_patients:
                selectedFragment = new TMenuPatients();
                break;
            case R.id.navigation_settings:
                selectedFragment = new MenuSettings();
                break;
            default:
                return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}