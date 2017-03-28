package com.example.romanticamaj.garyiot;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PirCameraFragment extends Fragment {
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    private RecyclerView mRecyclerView;
    private PirEntryAdapter mAdapter;

    private View mMainView;

    public PirCameraFragment() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("logs");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("photos");
    }

    public static PirCameraFragment newInstance() {
        return new PirCameraFragment();
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

        mMainView = inflater.inflate(R.layout.fragment_pir_camera, container, false);

        if (null == mRecyclerView) {
            mRecyclerView = (RecyclerView) mMainView.findViewById(R.id.doorbellView);

            // Show most recent items at the top
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
            mRecyclerView.setLayoutManager(layoutManager);
        }

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAdapter = new PirEntryAdapter(getActivity(), mDatabaseRef, mStorageRef);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAdapter != null) {
            mAdapter.cleanup();
            mAdapter = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
