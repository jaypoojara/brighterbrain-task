package com.brighterbrain.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brighterbrain.R;
import com.brighterbrain.activity.CreateEditItemActivity;
import com.brighterbrain.database.DBHandler;
import com.brighterbrain.model.Item;
import com.brighterbrain.util.Constants;

import java.util.ArrayList;

/**
 * Created by jaypoojara on 22-04-2018.
 */
public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Item> items;
    Context context;

    public ItemListAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public void updateAdapter(ArrayList<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.tvCost.setText(String.valueOf(items.get(position).getCost()));
        itemHolder.tvName.setText(String.valueOf(items.get(position).getName()));
        itemHolder.tvDescription.setText(String.valueOf(items.get(position).getDescription()));
        itemHolder.tvLocation.setText(String.valueOf(items.get(position).getLatitude()) + "," + String.valueOf(items.get(position).getLongitude()));

        itemHolder.rvImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        itemHolder.rvImages.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        itemHolder.rvImages.setAdapter(new ItemImageAdapter(items.get(position).getItemImages(), context));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void showWarning(final int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert!");
        builder.setMessage("Are you sure to delete this item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DBHandler(context).deleteRecord(Constants.ITEMS, Constants.ID + " = " + items.get(adapterPosition).getId());
                new DBHandler(context).deleteRecord(Constants.ITEM_IMAGES, Constants.ITEM_ID + " = " + items.get(adapterPosition).getId());
                items.remove(adapterPosition);
                notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout listLayout;
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvLocation;
        private TextView tvCost;
        private RecyclerView rvImages;
        private ImageView ivDelete, ivEdit;

        public ItemHolder(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        /**
         * Find the Views in the layout<br />
         * <br />
         * Auto-created on 2018-04-22 14:21:26 by Android Layout Finder
         * (http://www.buzzingandroid.com/tools/android-layout-finder)
         */
        private void findViews(View view) {
            listLayout = (LinearLayout) view.findViewById(R.id.list_layout);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvCost = (TextView) view.findViewById(R.id.tvCost);
            rvImages = (RecyclerView) view.findViewById(R.id.rvImages);
            ivDelete = view.findViewById(R.id.ivDelete);
            ivEdit = view.findViewById(R.id.ivEdit);
            ivDelete.setOnClickListener(this);
            ivEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == ivDelete) {
                showWarning(getAdapterPosition());
            } else if (view == ivEdit) {
                Intent intent = new Intent(context, CreateEditItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", items.get(getAdapterPosition()));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }

        }
    }
}
