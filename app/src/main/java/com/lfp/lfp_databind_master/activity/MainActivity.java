package com.lfp.lfp_databind_master.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.lfp.lfp_databind_master.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MainActivityFragment.TAG);
        if (fragment == null) {
            fragment = MainActivityFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment, fragment, MainActivityFragment.TAG)
                    .commitAllowingStateLoss();
        }

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_import:
                    MainActivityFragment fragment1 =
                            (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(MainActivityFragment.TAG);
                    if (fragment1 != null) {
                        fragment1.importData();
                    }
                    return true;

            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
