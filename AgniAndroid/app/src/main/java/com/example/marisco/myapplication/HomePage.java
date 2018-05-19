package com.example.marisco.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.io.Serializable;

import butterknife.ButterKnife;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    String username, name, email, type;
    private LoginResponse token;
    private static final String TOKEN = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = getIntent();
        LoginResponse response = (LoginResponse) i.getSerializableExtra(MainActivity.RESPONSE);
        NavigationView nv = findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(this);
        View header = nv.getHeaderView(0);
        TextView header_username = header.findViewById(R.id.header_username);
        header_username.setText(response.getUsername());

        this.username = response.getUsername();

        this.token = response;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            setTitle("Profile");
            ProfileFragment profile = new ProfileFragment();
            FragmentManager fman = getSupportFragmentManager();

            Bundle args = new Bundle();
            args.putSerializable(TOKEN, token);
            profile.setArguments(args);

            fman.beginTransaction().replace(R.id.fragment, profile).commit();
        } else if (id == R.id.nav_occurencies) {
            setTitle("Registar ocorrência");

            OccurrenceFragment of = new OccurrenceFragment();
            FragmentManager fman = getSupportFragmentManager();
            Bundle args = new Bundle();
            args.putSerializable(TOKEN, token);

            of.setArguments(args);
            fman.beginTransaction().replace(R.id.fragment, of).commit();
        } else if (id == R.id.nav_map) {
            setTitle("Map");

            MapFragment map = new MapFragment();
            FragmentManager fman = getSupportFragmentManager();
            fman.beginTransaction().replace(R.id.fragment, map).commit();
        } else if (id == R.id.nav_statistics) {

        } else if (id == R.id.nav_view) {

        }
        else if(id == R.id.occurences_list){
            setTitle("List Occurrences");

            ListOcurrences lo = new ListOcurrences();
            FragmentManager fman = getSupportFragmentManager();
            fman.beginTransaction().replace(R.id.fragment, lo).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}