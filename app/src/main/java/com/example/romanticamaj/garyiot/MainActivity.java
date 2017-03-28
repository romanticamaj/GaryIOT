package com.example.romanticamaj.garyiot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private PowerSwitchFragment mPowerSwitchFragment = null;
    private PirCameraFragment mPirCameraFragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_power_switch:
                    if (null == mPowerSwitchFragment) {
                        mPowerSwitchFragment = PowerSwitchFragment.newInstance();
                    }

                    replaceFragment(mPowerSwitchFragment);

                    return true;
                case R.id.navigation_pir_camera:
                    if (null == mPirCameraFragment) {
                        mPirCameraFragment = PirCameraFragment.newInstance();
                    }

                    replaceFragment(mPirCameraFragment);

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mPowerSwitchFragment = PowerSwitchFragment.newInstance();
        replaceFragment(mPowerSwitchFragment);
    }

    private void replaceFragment(Fragment newFragment) {
        // Create new fragment and transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.content, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}
