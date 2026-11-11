package com.mca.hallbooking.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mca.hallbooking.R;

import java.io.File;
import java.util.List;

public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder> {

    private final List<String> imageUris;

    public ImageCarouselAdapter(List<String> imageUris) {
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String uriStr = imageUris.get(position);
        if (uriStr != null && !uriStr.trim().isEmpty()) {
            try {
                if (uriStr.startsWith("android.resource://") || uriStr.startsWith("content://") || uriStr.startsWith("file://")) {
                    holder.imageView.setImageURI(Uri.parse(uriStr));
                } else if (uriStr.startsWith("/")) {
                    holder.imageView.setImageURI(Uri.fromFile(new File(uriStr)));
                } else if (uriStr.startsWith("res:")) {
                    int resId = holder.itemView.getContext().getResources().getIdentifier(
                            uriStr.substring(4), "drawable", holder.itemView.getContext().getPackageName());
                    if (resId != 0) {
                        holder.imageView.setImageResource(resId);
                    } else {
                        holder.imageView.setImageResource(R.drawable.bg_hall_banner_full);
                    }
                } else {
                    holder.imageView.setImageURI(Uri.parse(uriStr));
                }
            } catch (Exception e) {
                holder.imageView.setImageResource(R.drawable.bg_hall_banner_full);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.bg_hall_banner_full);
        }
    }

    @Override
    public int getItemCount() {
        return imageUris != null ? imageUris.size() : 0;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}