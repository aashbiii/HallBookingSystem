package com.mca.hallbooking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mca.hallbooking.R;
import com.mca.hallbooking.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    public interface OnReceiptClickListener {
        void onReceiptClick(Booking booking);
    }

    private final List<Booking> bookings;
    private final OnReceiptClickListener listener;

    public BookingAdapter(List<Booking> bookings, OnReceiptClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.tvEventName.setText(booking.getEventName());
        holder.tvHallName.setText(booking.getHallName());
        if (holder.tvDepartment != null) {
            String dept = booking.getDepartment();
            holder.tvDepartment.setText("Dept: " + (dept != null && !dept.isEmpty() ? dept : "General"));
        }
        holder.tvDateSlot.setText(booking.getBookingDate() + "  •  " + booking.getSlot());
        holder.tvStatus.setText(booking.getStatus());

        switch (booking.getStatus()) {
            case Booking.STATUS_APPROVED:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_approved);
                holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(R.color.status_approved));
                break;
            case Booking.STATUS_REJECTED:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(R.color.status_rejected));
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(R.color.status_pending));
                break;
        }

        // Show reject reason if rejected
        if (Booking.STATUS_REJECTED.equalsIgnoreCase(booking.getStatus())) {
            if (holder.layoutRejectReason != null) {
                holder.layoutRejectReason.setVisibility(View.VISIBLE);
                String reason = booking.getRemarks();
                if (reason != null && !reason.trim().isEmpty()) {
                    holder.tvRejectReason.setText(reason.trim());
                } else {
                    holder.tvRejectReason.setText("No specific reason provided by administration.");
                }
            }
        } else {
            if (holder.layoutRejectReason != null) {
                holder.layoutRejectReason.setVisibility(View.GONE);
            }
        }

        // Show approval slip button & reference when approved
        if (Booking.STATUS_APPROVED.equalsIgnoreCase(booking.getStatus())) {
            if (holder.tvReceiptNo != null) {
                holder.tvReceiptNo.setVisibility(View.VISIBLE);
                String ref = booking.getReceiptNo() != null ? booking.getReceiptNo() : "RCSS-APP-" + booking.getId();
                holder.tvReceiptNo.setText("Approval Ref: " + ref);
            }
            if (holder.btnViewReceipt != null) {
                holder.btnViewReceipt.setVisibility(View.VISIBLE);
            }
        } else {
            if (holder.tvReceiptNo != null) {
                holder.tvReceiptNo.setVisibility(View.GONE);
            }
            if (holder.btnViewReceipt != null) {
                holder.btnViewReceipt.setVisibility(View.GONE);
            }
        }

        if (holder.btnViewReceipt != null) {
            holder.btnViewReceipt.setOnClickListener(v -> {
                if (listener != null) listener.onReceiptClick(booking);
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvHallName, tvDepartment, tvDateSlot, tvStatus, tvReceiptNo, tvRejectReason;
        View btnViewReceipt, layoutRejectReason;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvHallName = itemView.findViewById(R.id.tvHallName);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvDateSlot = itemView.findViewById(R.id.tvDateSlot);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvReceiptNo = itemView.findViewById(R.id.tvReceiptNo);
            btnViewReceipt = itemView.findViewById(R.id.btnViewReceipt);
            layoutRejectReason = itemView.findViewById(R.id.layoutRejectReason);
            tvRejectReason = itemView.findViewById(R.id.tvRejectReason);
        }
    }
}
