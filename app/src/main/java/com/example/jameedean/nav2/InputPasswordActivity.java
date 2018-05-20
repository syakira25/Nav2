package com.example.jameedean.nav2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;

import java.util.List;

import io.paperdb.Paper;

/**
 * Created by JameeDean on 5/4/2018.
 */

public class InputPasswordActivity extends AppCompatActivity {

    PatternLockView mPatternLockView;
    String save_pattern_key = "pattern_code";
    String final_pattern = "";

    @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);

        Paper.init(this);
        final String save_pattern = Paper.book().read(save_pattern_key);

        if (save_pattern != null && !save_pattern.equals("null")) {
            setContentView(R.layout.activity_input_password);
            mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);       // Set the current viee more
            mPatternLockView.setInStealthMode(true);                                     // Set the pattern in stealth mode (pattern drawing is hidden)
            mPatternLockView.setTactileFeedbackEnabled(true);                            // Enables vibration feedback when the pattern is drawn
            mPatternLockView.setInputEnabled(true);                                     // Disables any input from the pattern lock view completely

            mPatternLockView.setDotCount(3);
            mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
            mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
            mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
            mPatternLockView.setAspectRatioEnabled(true);
            mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
            mPatternLockView.setNormalStateColor(ResourceUtils.getColor(this, R.color.colorPrimary));
            mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.colorPrimaryDark));
            mPatternLockView.setWrongStateColor(ResourceUtils.getColor(this, R.color.pomegranate));
            mPatternLockView.setDotAnimationDuration(150);
            mPatternLockView.setPathEndAnimationDuration(100);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {

                @Override
                public void onStarted() {
                    Log.d(getClass().getName(), "Pattern drawing started");
                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {
                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("password", PatternLockUtils.patternToString(mPatternLockView, pattern));
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), NavDrawerActivity.class);
                    startActivity(intent);
                    finish();

                    final_pattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
                    if (final_pattern.equals(save_pattern)) {
                        Toast.makeText(InputPasswordActivity.this, "Password Correct!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),NavDrawerActivity.class));
                        finish();
                    } else
                        Toast.makeText(InputPasswordActivity.this, "Password incorrect!!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCleared() {
                    Log.d(getClass().getName(), "Pattern has been cleared");
                }

            });
        }
    }

}