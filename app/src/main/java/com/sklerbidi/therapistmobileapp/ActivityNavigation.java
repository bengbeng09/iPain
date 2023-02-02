package com.sklerbidi.therapistmobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
    String user_type = "";
    Bundle bundle;

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
            user_type = extras.getString("user_type");
        }

        set_item(user_type);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bundle = new Bundle();
        bundle.putString("type", user_type);
        getSupportFragmentManager().setFragmentResult("request_type", bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuDashboard()).commit();
        navigationView.setCheckedItem(R.id.navigation_dashboard);
    }

    private void set_item(String type)
    {
        Menu nav_Menu = navigationView.getMenu();
        switch (type){
            case "Regular User":
                nav_Menu.findItem(R.id.navigation_patients).setVisible(false);
                nav_Menu.findItem(R.id.navigation_therapy_session).setVisible(false);
                break;
            case "Clinic Therapist":
                nav_Menu.findItem(R.id.navigation_therapy_session).setVisible(false);
                break;
            case "Clinic Patient":
                nav_Menu.findItem(R.id.navigation_patients).setVisible(false);
                break;
        }


    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        bundle = new Bundle();
        bundle.putString("type", user_type);
        getSupportFragmentManager().setFragmentResult("request_type", bundle);

        switch (item.getItemId()) {

            case R.id.navigation_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuDashboard()).commit();
                getFragmentManager().beginTransaction();

                break;
            case R.id.navigation_therapy_session:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PMenuTherapySession()).commit();
                getFragmentManager().beginTransaction();
                break;
            case R.id.navigation_patients:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TMenuPatients()).commit();
                getFragmentManager().beginTransaction();
                break;
            case R.id.navigation_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuSettings()).commit();
                getFragmentManager().beginTransaction();
                break;
            default:
                return false;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}