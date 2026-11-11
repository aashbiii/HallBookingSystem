package com.mca.hallbooking.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mca.hallbooking.R;
import com.mca.hallbooking.ReceiptActivity;
import com.mca.hallbooking.model.Booking;

import java.util.List;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    public interface OnActionListener {
        void onApprove(Booking booking);
        void onReject(Booking booking);
    }

    private final List<Booking> bookings;
    private final OnActionListener listener;
    private final boolean showActions;

    public RequestAdapter(List<Booking> bookings, OnActionListener listener, boolean showActions) {
        this.bookings = bookings;
        this.listener = listener;
        this.showActions = showActions;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.tvEventName.setText(booking.getEventName());
        holder.tvUserName.setText("Requested by: " + booking.getUserName());
        if (holder.tvDepartment != null) {
            String dept = booking.getDepartment();
            holder.tvDepartment.setText("Dept: " + (dept != null && !dept.isEmpty() ? dept : "General"));
        }
        holder.tvHallName.setText(booking.getHallName());
        holder.tvDateSlot.setText(booking.getBookingDate() + "  •  " + booking.getSlot());
        holder.tvAttendees.setText(String.format(Locale.US, "Expected attendees: %d", booking.getAttendees()));

        if (showActions) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.tvStatusReadonly.setVisibility(View.GONE);
            holder.tvRemarks.setVisibility(View.GONE);
            holder.btnViewReceipt.setVisibility(View.GONE);

            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(booking);
            });
            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(booking);
            });
        } else {
            holder.layoutActions.setVisibility(View.GONE);
            holder.tvStatusReadonly.setVisibility(View.VISIBLE);
            holder.tvStatusReadonly.setText(booking.getStatus());

            switch (booking.getStatus()) {
                case Booking.STATUS_APPROVED:
                    holder.tvStatusReadonly.setBackgroundResource(R.drawable.bg_status_approved);
                    holder.tvStatusReadonly.setTextColor(holder.itemView.getResources().getColor(R.color.status_approved));
                    holder.tvRemarks.setVisibility(View.GONE);
                    holder.btnViewReceipt.setVisibility(View.VISIBLE);
                    holder.btnViewReceipt.setOnClickListener(v -> {
                        Intent intent = new Intent(holder.itemView.getContext(), ReceiptActivity.class);
                        intent.putExtra("booking_id", booking.getId());
                        holder.itemView.getContext().startActivity(intent);
                    });
                    break;
                case Booking.STATUS_REJECTED:
                    holder.tvStatusReadonly.setBackgroundResource(R.drawable.bg_status_rejected);
                    holder.tvStatusReadonly.setTextColor(holder.itemView.getResources().getColor(R.color.status_rejected));
                    holder.btnViewReceipt.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(booking.getRemarks())) {
                        holder.tvRemarks.setVisibility(View.VISIBLE);
                        holder.tvRemarks.setText("Reason: " + booking.getRemarks());
                    } else {
                        holder.tvRemarks.setVisibility(View.GONE);
                    }
                    break;
                default:
                    holder.tvStatusReadonly.setBackgroundResource(R.drawable.bg_status_pending);
                    holder.tvStatusReadonly.setTextColor(holder.itemView.getResources().getColor(R.color.status_pending));
                    holder.tvRemarks.setVisibility(View.GONE);
                    holder.btnViewReceipt.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvUserName, tvDepartment, tvHallName, tvDateSlot, tvAttendees, tvStatusReadonly, tvRemarks;
        View layoutActions, btnApprove, btnReject, btnViewReceipt;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvHallName = itemView.findViewById(R.id.tvHallName);
            tvDateSlot = itemView.findViewById(R.id.tvDateSlot);
            tvAttendees = itemView.findViewById(R.id.tvAttendees);
            tvStatusReadonly = itemView.findViewById(R.id.tvStatusReadonly);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnViewReceipt = itemView.findViewById(R.id.btnViewReceipt);
        }
    }
}
