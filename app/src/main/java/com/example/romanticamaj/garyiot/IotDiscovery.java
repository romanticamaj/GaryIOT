package com.example.romanticamaj.garyiot;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.os.Handler;
import android.util.Log;

public class IotDiscovery {
    public interface IotDiscoveryListener {
        void onIotDiscoverySuccess();
        void onIotDiscoveryFail();
    }

    private static final String TAG = "IotDiscovery";
    private static final String SZ_SERVICE_TYPE = "_http._tcp.";
    private static final String SZ_SERVICE_NAME = "GaryIot";

    NsdManager mNsdManager;
    NsdServiceInfo mNsdInfo;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;

    IotDiscoveryListener mIotDiscoveryListener;

    public IotDiscovery(Context context, IotDiscoveryListener iotDiscoveryListener) {
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mIotDiscoveryListener = iotDiscoveryListener;
    }

    public void initialize() {
        initializeResolveListener();
        initializeDiscoveryListener();
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success. " + service);

                if (!service.getServiceType().equals(SZ_SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().contains(SZ_SERVICE_NAME)){
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                if (mNsdInfo == service) {
                    mNsdInfo = null;
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
                mIotDiscoveryListener.onIotDiscoveryFail();
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
                mIotDiscoveryListener.onIotDiscoveryFail();
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
                mIotDiscoveryListener.onIotDiscoveryFail();
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);

                mIotDiscoveryListener.onIotDiscoveryFail();
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(SZ_SERVICE_NAME)) {
                    Log.d(TAG, "Same IP. But that's ok!");
                }

                mNsdInfo = serviceInfo;

                mIotDiscoveryListener.onIotDiscoverySuccess();
            }
        };
    }

    public void discoverServices() {
        mNsdManager.discoverServices(SZ_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null == mNsdInfo) {
                    stopDiscovery();
                }
            }
        }, 2000);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mNsdInfo;
    }
}
