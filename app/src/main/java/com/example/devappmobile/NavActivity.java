package com.example.devappmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CasesFragment.OnListFragmentInteractionListener, DraftFragment.OnListFragmentInteractionListener {
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://process.isiforge.tn/")
            .addConverterFactory(GsonConverterFactory.create(gson));
    Retrofit retrofit = builder.build();
    UserClient userClient = retrofit.create(UserClient.class);
    String token, pro_uid, tas_uid;
    Fragment demandFragment = new CasesFragment();
    Fragment draftFragment = new DraftFragment();
    final FragmentManager fm = getSupportFragmentManager();
    SharedPreferences prefs;


    // String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        prefs = this.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).edit();
        editor.putString("just", "just");
        editor.apply();

        SharedPreferences prefs = this.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String restoredText = prefs.getString("token", null);
        if (restoredText != null) {
            token = prefs.getString("token", "No token defined");


        }


        setContentView(R.layout.activity_nav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cases) {
            // Handle the camera action

            fm.beginTransaction().replace(R.id.nav_container, demandFragment, "1").commit();


        } else if (id == R.id.nav_draft) {

            fm.beginTransaction().replace(R.id.nav_container, draftFragment, "2").commit();


        } else if (id == R.id.nav_disconnect) {
            prefs.edit().remove("token").apply();
            prefs.edit().remove("refresh").apply();
            prefs.edit().remove("Expires_in").apply();
            Intent homeIntent = new Intent(NavActivity.this, MainActivity.class);
            startActivity(homeIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onListFragmentInteraction(final Cases item) {

        userClient.getStep(token, item.getPro_uid(), item.getTas_uid())
                .enqueue(new Callback<List<Step>>() {
                    @Override
                    public void onResponse(Call<List<Step>> call, Response<List<Step>> response) {
                        userClient.getDynaform(token, response.body().get(0).step_uid_obj)
                                .enqueue(new Callback<List<Object>>() {
                                    @Override
                                    public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {


                                        Fragment fragment = new FormFragment();
                                        Bundle args = new Bundle();

                                        args.putSerializable("formJsonString", (Serializable) response.body());
                                        args.putString("pro_uid", item.getPro_uid());
                                        args.putString("tas_uid", item.getTas_uid());
                                        args.putString("token", token);
                                        fragment.setArguments(args);
                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_container, fragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();


                                    }

                                    @Override
                                    public void onFailure(Call<List<Object>> call, Throwable t) {
                                        System.out.println("Failednav");
                                        System.out.println(call);
                                        t.printStackTrace();

                                        // Toast.makeText(getContext(), "scss", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<List<Step>> call, Throwable t) {
                        t.printStackTrace();

                        // Toast.makeText(getContext(), "scss", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onListDraftFragmentInteraction(Draft item) {

    }
}
