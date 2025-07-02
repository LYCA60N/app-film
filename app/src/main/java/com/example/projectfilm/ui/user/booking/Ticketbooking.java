package com.example.projectfilm.ui.user.booking;
import com.example.projectfilm.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Ticketbooking extends AppCompatActivity {

    private String selectedCinema = null;
    private String selectedTime = null;
    private Button btnChooseSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtime);

        btnChooseSeat = findViewById(R.id.btnChooseSeat);
        btnChooseSeat.setVisibility(View.GONE);

        // CGV Crescent Mall
        setupTimeButton(R.id.buttonCgv10, "CGV Crescent Mall", "10:00");
        setupTimeButton(R.id.buttonCgv17, "CGV Crescent Mall", "17:00");
        setupTimeButton(R.id.buttonCgv20, "CGV Crescent Mall", "20:00");

        // Lotte Nowzone
        setupTimeButton(R.id.buttonLotte10, "Lotte Nowzone", "10:00");
        setupTimeButton(R.id.buttonLotte17, "Lotte Nowzone", "17:00");
        setupTimeButton(R.id.buttonLotte20, "Lotte Nowzone", "20:00");

        // Galaxy Tân Bình
        setupTimeButton(R.id.buttonGalaxy10, "Galaxy Tân Bình", "10:00");
        setupTimeButton(R.id.buttonGalaxy17, "Galaxy Tân Bình", "17:00");
        setupTimeButton(R.id.buttonGalaxy20, "Galaxy Tân Bình", "20:00");

        // Xử lý nút "Chọn ghế"
        btnChooseSeat.setOnClickListener(v -> {
            if (selectedCinema != null && selectedTime != null) {
                Intent intent = new Intent(Ticketbooking.this, SeatListActivity.class);
                intent.putExtra("cinema", selectedCinema);
                intent.putExtra("time", selectedTime);
                startActivity(intent);
            }
        });
    }

    private void setupTimeButton(int buttonId, final String cinemaName, final String time) {
        Button button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> {
                selectedCinema = cinemaName;
                selectedTime = time;
                btnChooseSeat.setVisibility(View.VISIBLE); // Hiện nút "Chọn ghế"
            });
        }
    }
}
