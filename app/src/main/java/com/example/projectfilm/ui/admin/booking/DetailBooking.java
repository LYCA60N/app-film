package com.example.projectfilm.ui.admin.booking;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.projectfilm.R;

public class DetailBooking extends AppCompatActivity {
    ImageView btn_back;
    TextView textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        String bookingId = getIntent().getStringExtra("bookingId");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String cinema = documentSnapshot.getString("cinema");
                        String email = documentSnapshot.getString("email");
                        String name = documentSnapshot.getString("name");
                        String paymentMethod = documentSnapshot.getString("paymentMethod");
                        String price = documentSnapshot.getString("price");
                        String seats = documentSnapshot.getString("seats");
                        String time = documentSnapshot.getString("time");
                        String id = documentSnapshot.getString("userId");
                        textView2.setText(cinema);
                        textView3.setText(email);
                        textView4.setText(name);
                        textView5.setText(paymentMethod);
                        textView6.setText(price);
                        textView7.setText(seats);
                        textView8.setText(time);
                        textView9.setText(id);
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });
    }
}