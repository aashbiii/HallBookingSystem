package com.mca.hallbooking.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mca.hallbooking.R;
import com.mca.hallbooking.model.Hall;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class HallAdapter extends RecyclerView.Adapter<HallAdapter.HallViewHolder> {

    public interface OnHallClickListener {
        void onHallClick(Hall hall);
    }

    private final List<Hall> halls;
    private final OnHallClickListener listener;

    public HallAdapter(List<Hall> halls, OnHallClickListener listener) {
        this.halls = halls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hall, parent, false);
        return new HallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HallViewHolder holder, int position) {
        Hall hall = halls.get(position);
        holder.tvHallName.setText(hall.getName());
        holder.tvLocation.setText(hall.getLocation());
        holder.tvCapacity.setText(String.format(Locale.US, "%d seats", hall.getCapacity()));
        holder.tvAmenities.setText(hall.getAmenities().replace(",", "  •  "));

        // Bind first photo if available and photo count badge
        String urisStr = hall.getImageUris();
        if (urisStr != null && !urisStr.trim().isEmpty()) {
            String[] split = urisStr.split(",");
            String firstUri = split[0].trim();
            try {
                if (firstUri.startsWith("res:")) {
                    int resId = holder.itemView.getContext().getResources().getIdentifier(
                            firstUri.substring(4), "drawable", holder.itemView.getContext().getPackageName());
                    if (resId != 0) {
                        holder.ivHallBanner.setImageResource(resId);
                    } else {
                        holder.ivHallBanner.setImageResource(R.drawable.bg_hall_banner_full);
                    }
                } else if (firstUri.startsWith("android.resource://") || firstUri.startsWith("content://") || firstUri.startsWith("file://")) {
                    holder.ivHallBanner.setImageURI(Uri.parse(firstUri));
                } else if (firstUri.startsWith("/")) {
                    holder.ivHallBanner.setImageURI(Uri.fromFile(new File(firstUri)));
                } else {
                    holder.ivHallBanner.setImageURI(Uri.parse(firstUri));
                }
            } catch (Exception e) {
                holder.ivHallBanner.setImageResource(R.drawable.bg_hall_banner_full);
            }

            if (holder.tvHallPhotoBadge != null) {
                holder.tvHallPhotoBadge.setVisibility(View.VISIBLE);
                holder.tvHallPhotoBadge.setText(split.length + (split.length == 1 ? " Photo" : " Photos"));
            }
        } else {
            holder.ivHallBanner.setImageResource(R.drawable.bg_hall_banner_full);
            if (holder.tvHallPhotoBadge != null) {
                holder.tvHallPhotoBadge.setVisibility(View.GONE);
            }
        }

        View.OnClickListener click = v -> {
            if (listener != null) listener.onHallClick(hall);
        };
        holder.itemView.setOnClickListener(click);
        if (holder.btnViewDetails != null) {
            holder.btnViewDetails.setOnClickListener(click);
        }
    }

    @Override
    public int getItemCount() {
        return halls.size();
    }

    static class HallViewHolder extends RecyclerView.ViewHolder {
        TextView tvHallName, tvLocation, tvCapacity, tvAmenities, tvHallPhotoBadge;
        ImageView ivHallBanner;
        View btnViewDetails;

        HallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHallName = itemView.findViewById(R.id.tvHallName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvAmenities = itemView.findViewById(R.id.tvAmenities);
            ivHallBanner = itemView.findViewById(R.id.ivHallBanner);
            tvHallPhotoBadge = itemView.findViewById(R.id.tvHallPhotoBadge);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
