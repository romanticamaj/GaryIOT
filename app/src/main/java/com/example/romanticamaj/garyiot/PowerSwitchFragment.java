package com.example.romanticamaj.garyiot;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;

public class PowerSwitchFragment extends Fragment implements IotDiscovery.IotDiscoveryListener {
    private static String TAG = PowerSwitchFragment.class.getName();
    private static int SWITCH_NUMBER = 8;

    private View mMainView;
    private SwitchButton[] mPowerSwitchArray;
    private FloatingActionButton mConnectFab;

    private IotDiscovery mDiscovery;
    private IotConnection mConnection;
    private Handler mMessageHandler;

    public PowerSwitchFragment() {
        mMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String strMsg = msg.getData().getString("msg");

                showMsg(strMsg);
            }
        };

        mConnection = new IotConnection(mMessageHandler);
        mPowerSwitchArray = new SwitchButton[SWITCH_NUMBER];
    }

    public static PowerSwitchFragment newInstance() {
        return new PowerSwitchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != mMainView) {
            return mMainView;
        }

        mMainView = inflater.inflate(R.layout.fragment_power_switch, container, false);

        if (null == mConnectFab) {
            mConnectFab = (FloatingActionButton) mMainView.findViewById(R.id.connect_fab);
            mConnectFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Connecting a power switch...", Snackbar.LENGTH_INDEFINITE)
                            .show();
                    mConnectFab.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDiscovery = new IotDiscovery(getActivity(), PowerSwitchFragment.this);
                            mDiscovery.initialize();
                            mDiscovery.discoverServices();
                        }
                    }, 1000);
                }
            });
        }

        initPowerSwitch();

        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mConnection.tearDown();
    }

    @Override
    public void onIotDiscoverySuccess() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), "Connected to a power switch", Snackbar.LENGTH_LONG)
                        .show();
                setSwitchViewEnabled(true);
                mConnectFab.setEnabled(true);
            }
        });

        NsdServiceInfo service = mDiscovery.getChosenServiceInfo();

        if (service != null) {
            Log.e(TAG, "Connecting to " + service);
            if (null != mConnection) {
                mConnection.connectToServer(service.getHost(), service.getPort());
            }
        } else {
            Log.e(TAG, "No service to connect to!");
        }
    }

    @Override
    public void onIotDiscoveryFail() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), "Failed to connect a power switch...", Snackbar.LENGTH_LONG)
                        .show();
                setSwitchViewEnabled(false);
                mConnectFab.setEnabled(true);
            }
        });
    }

    private void initPowerSwitch() {
        for (int i = 0; i != SWITCH_NUMBER; i++) {
            String strPower;

            if (null != mPowerSwitchArray[i]) {
                continue;
            }

            if (0 == i) {
                mPowerSwitchArray[i] = (SwitchButton) mMainView.findViewById(R.id.power_switch1);
                strPower = "power1";
            } else if (1 == i) {
                mPowerSwitchArray[i] = (SwitchButton) mMainView.findViewById(R.id.power_switch2);
                strPower = "power2";
            } else {
                /* Fill the rest switch button view up when needed */
                continue;
            }

            final String f_strPower = strPower;

            mPowerSwitchArray[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        mConnection.sendMessage(f_strPower + " on");
                    } else {
                        mConnection.sendMessage(f_strPower +" off");
                    }
                }
            });
            mPowerSwitchArray[i].setEnabled(false);
        }
    }

    private void setSwitchViewEnabled(boolean blEnabled) {
        for (SwitchButton switchButton: mPowerSwitchArray) {
            if (null != switchButton) {
                switchButton.setEnabled(blEnabled);
            }
        }
    }

    private void showMsg(String strMsg) {
        Toast.makeText(getActivity(), strMsg, Toast.LENGTH_SHORT).show();
    }
}
