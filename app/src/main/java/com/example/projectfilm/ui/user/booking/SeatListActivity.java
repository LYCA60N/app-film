package com.example.projectfilm.ui.user.booking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfilm.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SeatListActivity extends AppCompatActivity {

    private List<String> bookedSeats = new ArrayList<>();

    private GridLayout gridSeats;
    private TextView textSelectedSeats, textTotalPrice;
    private Button btnConfirm;

    private List<String> selectedSeats = new ArrayList<>();
    private static final int SEAT_PRICE = 100000;

    private String cinemaName = "";
    private String timeSlot = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_list);

        gridSeats = findViewById(R.id.gridSeats);
        textSelectedSeats = findViewById(R.id.textSelectedSeats);
        textTotalPrice = findViewById(R.id.textTotalPrice);
        btnConfirm = findViewById(R.id.btnConfirm);

        Intent intent = getIntent();
        cinemaName = intent.getStringExtra("cinema");
        timeSlot = intent.getStringExtra("time");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings")
                .whereEqualTo("cinema", cinemaName)
                .whereEqualTo("time", timeSlot)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String seatsStr = doc.getString("seats"); // "A3" hoặc "E3, E4"
                        if (seatsStr != null && !seatsStr.isEmpty()) {
                            String[] seatsArray = seatsStr.split(",\\s*");
                            for (String seat : seatsArray) {
                                bookedSeats.add(seat.trim());
                            }
                        }
                    }
                    generateSeatButtons(); // Chỉ gọi khi đã có danh sách ghế đã đặt
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể tải dữ liệu ghế đã đặt", Toast.LENGTH_SHORT).show();
                    generateSeatButtons(); // Vẫn gọi để tránh treo giao diện
                });

        btnConfirm.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 ghế", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent payIntent = new Intent(SeatListActivity.this, ThanhToanActivity.class);
            payIntent.putExtra("cinema", cinemaName);
            payIntent.putExtra("time", timeSlot);
            payIntent.putExtra("seats", TextUtils.join(", ", selectedSeats));
            payIntent.putExtra("price", String.valueOf(selectedSeats.size() * SEAT_PRICE));
            startActivity(payIntent);
        });
    }

    private void generateSeatButtons() {
        String[] rows = {"A", "B", "C", "D", "E"};
        int cols = 5;
        gridSeats.setColumnCount(cols);

        float scale = getResources().getDisplayMetrics().density;
        int seatSize = (int) (53 * scale);
        int seatMargin = (int) (6 * scale);

        for (String row : rows) {
            for (int col = 1; col <= cols; col++) {
                String seatId = row + col;
                Button seatButton = new Button(this);
                seatButton.setText(seatId);
                seatButton.setTextSize(12);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = seatSize;
                params.height = seatSize;
                params.setMargins(seatMargin, seatMargin, seatMargin, seatMargin);
                seatButton.setLayoutParams(params);

                if (bookedSeats.contains(seatId)) {
                    seatButton.setBackgroundResource(R.drawable.bg_seat_unavailable);
                    seatButton.setEnabled(false);
                } else {
                    seatButton.setBackgroundResource(R.drawable.bg_seat_available);
                    seatButton.setOnClickListener(v -> {
                        if (selectedSeats.contains(seatId)) {
                            selectedSeats.remove(seatId);
                            seatButton.setBackgroundResource(R.drawable.bg_seat_available);
                        } else {
                            selectedSeats.add(seatId);
                            seatButton.setBackgroundResource(R.drawable.bg_seat_selected);
                        }
                        updateSeatInfo();
                    });
                }

                gridSeats.addView(seatButton);
            }
        }
    }

    private void updateSeatInfo() {
        textSelectedSeats.setText(selectedSeats.size() + " Ghế: " + TextUtils.join(", ", selectedSeats));
        int total = selectedSeats.size() * SEAT_PRICE;
        textTotalPrice.setText(total + " VND");
    }
}
