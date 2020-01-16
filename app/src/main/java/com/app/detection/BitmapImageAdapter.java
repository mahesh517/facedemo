package com.app.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BitmapImageAdapter extends RecyclerView.Adapter<BitmapImageAdapter.ViewHolder> {


    // Store the context for later use
    private Context context;

    private ArrayList<Bitmap> bitmaps;
    private int check = 0;


    public BitmapImageAdapter(Context context, ArrayList<Bitmap> bitmaps) {

        this.context = context;
        this.bitmaps = bitmaps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_image_preview, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {


        viewHolder.imgPreview.setImageBitmap(bitmaps.get(i));


    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout layoutCheck;
        public ImageView imgPreview;

        public ViewHolder(View itemView) {
            super(itemView);
            this.layoutCheck = (RelativeLayout) itemView.findViewById(R.id.layoutCheck);
            this.imgPreview = (ImageView) itemView.findViewById(R.id.imagePreview);
        }


    }
}