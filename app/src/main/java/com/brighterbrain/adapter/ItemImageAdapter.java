package com.brighterbrain.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brighterbrain.R;
import com.brighterbrain.model.ItemImage;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by jaypoojara on 11-09-2017.
 */

public class ItemImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemImage> images;
    private Context context;

    public ItemImageAdapter(List<ItemImage> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.view_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        Picasso.get().load(new File(images.get(position).getImagePath())).fit().centerCrop().into(itemHolder.ivDiagnosticReport);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public void updateAdapter(List<ItemImage> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivDiagnosticReport, ivDelete;

        ItemHolder(View itemView) {
            super(itemView);
            ivDiagnosticReport = itemView.findViewById(R.id.ivImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivDiagnosticReport.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
            ivDelete.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            if (view == ivDelete) {
                images.remove(getAdapterPosition());
                notifyDataSetChanged();
            }
        }
    }

    private class ItemHolderAdd extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivImage;

        ItemHolderAdd(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == ivImage) {
                ImagePicker.with((Activity) context)                         //  Initialize ImagePicker with activity or fragment context
                        .setCameraOnly(false)               //  Camera mode
                        .setMultipleMode(true)              //  Select multiple images or single image
                        .setFolderMode(true)                //  Folder mode
                        .setShowCamera(true)                //  Show camera button
                        .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
                        .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
                        .setDoneTitle("Done")               //  Done button title
                        .setLimitMessage("You have reached selection limit")    // Selection limit message
                        .setMaxSize(5)                     //  Max images can be selected
                        .setSavePath("ImagePicker")         //  Image capture folder name
                        .setKeepScreenOn(true)              //  Keep screen on when selecting images
                        .start();
            }
        }
    }
}
