package com.example.jameedean.nav2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class PasswordApps_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App Lock");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        final Switch switch_button = (Switch) findViewById(R.id.switch_button);
        //switch_button.setTag("TAG");
        //switch_button.setChecked(false);
        boolean value = false; // default value if no value was found

        final SharedPreferences sharedPreferences = getSharedPreferences("isChecked", 0);

        value = sharedPreferences.getBoolean("isChecked", value); // retrieve the value of your key
        switch_button.setChecked(value);

//attach a listener to check for changes in state
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    sharedPreferences.edit().putBoolean("isChecked", true).apply();
                    Toast.makeText(getApplicationContext(), "Switch is ON", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), CreatePasswordActivity.class));
                    finish();
                } else {
                    sharedPreferences.edit().putBoolean("isChecked", false).apply();
                    Toast.makeText(getApplicationContext(), "Switch is OFF", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), InputPasswordActivity.class));
                    finish();
                }
            }

        });
    }


}