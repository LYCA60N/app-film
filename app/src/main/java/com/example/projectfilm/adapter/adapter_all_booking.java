package com.example.projectfilm.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfilm.R;
import com.example.projectfilm.data.model.Booking;
import com.example.projectfilm.ui.admin.booking.DetailBooking;

import java.util.List;

public class adapter_all_booking extends RecyclerView.Adapter<adapter_all_booking.ViewHolder> {
    List<Booking> bookingList;
    public adapter_all_booking(List<Booking> bookingList) {
        this.bookingList = bookingList;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Id_booking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Id_booking = itemView.findViewById(R.id.ID_booking);
        }
    }
    @NonNull
    @Override
    public adapter_all_booking.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new adapter_all_booking.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull adapter_all_booking.ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.Id_booking.setText(booking.getBookingId());
        holder.itemView.setOnClickListener(view -> {
            // Xử lý khi item được nhấn
            Intent intent = new Intent(view.getContext(), DetailBooking.class);
            intent.putExtra("bookingId", booking.getBookingId());
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }
}
