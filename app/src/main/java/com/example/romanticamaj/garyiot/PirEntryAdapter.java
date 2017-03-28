package com.example.romanticamaj.garyiot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * RecyclerView adapter to populate pir entries from Firebase.
 */
public class PirEntryAdapter extends FirebaseRecyclerAdapter<PirEntry, PirEntryAdapter.PirEntryViewHolder> {

    /**
     * ViewHolder for each pir entry
     */
    public static class PirEntryViewHolder extends RecyclerView.ViewHolder {

        public final ImageView image;
        public final TextView time;
        public final TextView metadata;

        public PirEntryViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.snapshot);
            this.time = (TextView) itemView.findViewById(R.id.textView_time);
            this.metadata = (TextView) itemView.findViewById(R.id.textView_metadata);
        }
    }

    private Context mApplicationContext;
    private StorageReference mStorageRef;

    public PirEntryAdapter(Context context, DatabaseReference ref, StorageReference storageReference) {
        super(PirEntry.class, R.layout.pir_entry, PirEntryViewHolder.class, ref);

        mApplicationContext = context.getApplicationContext();
        mStorageRef = storageReference;
    }

    @Override
    protected void populateViewHolder(PirEntryViewHolder viewHolder, PirEntry model, int position) {

        // Display the timestamp
        if (model.getTimestamp() != null) {
            CharSequence prettyTime = DateUtils.getRelativeDateTimeString(mApplicationContext,
                    model.getTimestamp(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
            viewHolder.time.setText(prettyTime);
        }

        if (model.getImageName() != null) {
            final String strPath = model.getImageName();
            final ImageView image = viewHolder.image;
            StorageReference imgStorageRef = mStorageRef.child(strPath);

            imgStorageRef.getDownloadUrl()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();

                            // Display the image
                            Drawable placeholder = ContextCompat.getDrawable(mApplicationContext, R.drawable.ic_image);
                            image.setImageDrawable(placeholder);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String strDownloadUrl = uri.toString();

                            Log.d("GaryDebug", "path=" + strPath + " downloadUrl=" + strDownloadUrl);

                            Glide.with(mApplicationContext)
                                    .load(strDownloadUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.ic_image)
                                    .fitCenter()
                                    .into(image);
                        }
                    });
        }

        // Display the metadata
        if (model.getAnnotations() != null) {
            ArrayList<String> keywords = new ArrayList<>(model.getAnnotations().keySet());

            int limit = Math.min(keywords.size(), 3);
            viewHolder.metadata.setText(TextUtils.join("\n", keywords.subList(0, limit)));
        } else {
            viewHolder.metadata.setText("no annotations yet");
        }
    }
}
