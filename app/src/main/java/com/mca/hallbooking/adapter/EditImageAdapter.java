package com.mca.hallbooking.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mca.hallbooking.R;

import java.util.List;

public class EditImageAdapter extends RecyclerView.Adapter<EditImageAdapter.ImageViewHolder> {

    private final List<String> imageUris;
    private final Runnable onDataChanged;

    public EditImageAdapter(List<String> imageUris, Runnable onDataChanged) {
        this.imageUris = imageUris;
        this.onDataChanged = onDataChanged;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String uriStr = imageUris.get(position);
        try {
            if (uriStr != null && uriStr.startsWith("res:")) {
                int resId = holder.itemView.getContext().getResources().getIdentifier(
                        uriStr.substring(4), "drawable", holder.itemView.getContext().getPackageName());
                if (resId != 0) {
                    holder.imageView.setImageResource(resId);
                } else {
                    holder.imageView.setImageResource(R.drawable.bg_hall_banner_full);
                }
            } else if (uriStr != null && uriStr.startsWith("/")) {
                holder.imageView.setImageURI(Uri.fromFile(new java.io.File(uriStr)));
            } else if (uriStr != null && !uriStr.isEmpty()) {
                holder.imageView.setImageURI(Uri.parse(uriStr));
            } else {
                holder.imageView.setImageResource(R.drawable.bg_hall_banner_full);
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.bg_hall_banner_full);
        }

        holder.btnRemove.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                imageUris.remove(pos);
                notifyItemRemoved(pos);
                onDataChanged.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris != null ? imageUris.size() : 0;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView btnRemove;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}