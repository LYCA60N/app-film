package com.example.projectfilm.ui.admin.booking;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfilm.R;
import com.example.projectfilm.adapter.adapter_all_booking;
import com.example.projectfilm.data.model.Booking;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderManage extends AppCompatActivity {
    ImageButton btn_back;
    RecyclerView recyclerView;
    adapter_all_booking adapter;
    List<Booking> bookingList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_manage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.RecyclerView);
        adapter = new adapter_all_booking(bookingList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        db.collection("bookings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookingList.clear(); // Clear trước nếu cần

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Booking booking = doc.toObject(Booking.class);

                        // Gán id vào booking (nếu model Booking có field id hoặc bookingId)
                        if (booking != null) {
                            booking.setBookingId(doc.getId()); // ⚡️ nhớ thêm setter nếu chưa có
                            bookingList.add(booking);
                        }
                    }

                    // Gọi notify để update RecyclerView
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lấy bookings: " + e.getMessage());
                });
    }
}