package com.example.projectfilm.ui.user.booking;

import com.example.projectfilm.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfilm.ui.user.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ThanhToanActivity extends AppCompatActivity {

    private TextView textCinema, textTime, textSeats, textPrice;
    private EditText editName, editEmail;
    private RadioGroup radioGroup;
    private RadioButton radioMomo, radioBank;
    private Button btnConfirmPayment;

    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;

    private String cinema, time, seats, price;

    private boolean isCheckingPayment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        // Ánh xạ view
        textCinema = findViewById(R.id.textCinema);
        textTime = findViewById(R.id.textTime);
        textSeats = findViewById(R.id.textSeats);
        textPrice = findViewById(R.id.textPrice);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        radioGroup = findViewById(R.id.radioGroup);
        radioMomo = findViewById(R.id.radioMomo);
        radioBank = findViewById(R.id.radioBank);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // Nhận dữ liệu
        Intent intent = getIntent();
        cinema = intent.getStringExtra("cinema");
        time = intent.getStringExtra("time");
        seats = intent.getStringExtra("seats");
        price = intent.getStringExtra("price");

        // Hiển thị dữ liệu
        textCinema.setText("Rạp: " + cinema);
        textTime.setText("Giờ chiếu: " + time);
        textSeats.setText("Ghế: " + seats);
        textPrice.setText("Tổng tiền: " + price);

        // Lấy tên, email từ Firestore
        if (currentUser != null) {
            String uid = currentUser.getUid();
            firestore.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String name = snapshot.getString("name");
                            String email = snapshot.getString("email");
                            String address = snapshot.getString("address");

                            if (name == null || name.isEmpty() ||
                                    email == null || email.isEmpty() ||
                                    address == null || address.isEmpty()) {
                                Toast.makeText(this, "Vui lòng hoàn thiện thông tin cá nhân trước khi thanh toán", Toast.LENGTH_LONG).show();
                                Intent profileIntent = new Intent(this, ProfileActivity.class);
                                profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(profileIntent);
                                finish();
                            } else {
                                editName.setText(name);
                                editEmail.setText(email);
                                editName.setEnabled(false);
                                editEmail.setEnabled(false);
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy thông tin người dùng, vui lòng cập nhật hồ sơ.", Toast.LENGTH_LONG).show();
                            Intent profileIntent = new Intent(this, ProfileActivity.class);
                            profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(profileIntent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi tải thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnConfirmPayment.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            isCheckingPayment = true;

            String paymentMethod = (checkedId == R.id.radioMomo) ? "Ví MoMo" : "Ngân hàng";
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            if (paymentMethod.equals("Ví MoMo")) {
                @SuppressLint("StaticFieldLeak")
                int userIdInt = currentUser.getUid().hashCode();
                MomoPaymentTask momoTask = new MomoPaymentTask(this, userIdInt, price);
                momoTask.execute();
            } else {
                Intent confirmIntent = new Intent(ThanhToanActivity.this, ThanhToanThanhCongActivity.class);
                confirmIntent.putExtra("cinema", cinema);
                confirmIntent.putExtra("time", time);
                confirmIntent.putExtra("seats", seats);
                confirmIntent.putExtra("name", name);
                confirmIntent.putExtra("email", email);
                confirmIntent.putExtra("paymentMethod", paymentMethod);
                startActivity(confirmIntent);
                finish();
            }
        });

        // Xử lý callback MoMo nếu app mở lại
        handleMomoResult(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleMomoResult(intent);
    }

    private void handleMomoResult(Intent intent) {
        Uri data = intent.getData();
        if (data != null && "myapp".equals(data.getScheme())) {
            String resultCode = data.getQueryParameter("resultCode");
            String message = data.getQueryParameter("message");
            String orderId = data.getQueryParameter("orderId");

            Log.d("MOMO_RESULT", "resultCode: " + resultCode + ", message: " + message);

            if ("0".equals(resultCode)) {
                updateFirestoreSuccess(orderId, data.toString());
            } else {
                Toast.makeText(this, "Thanh toán thất bại hoặc bị hủy", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateFirestoreSuccess(String orderId, String callbackUrl) {
        firestore.collection("bookings")
                .whereEqualTo("userId", currentUser.getUid())
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    if (!querySnapshots.isEmpty()) {
                        querySnapshots.getDocuments().get(0).getReference()
                                .update("status", "success", "callbackUrl", callbackUrl, "orderId", orderId, "updatedAt", System.currentTimeMillis())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ThanhToanActivity.this, ThanhToanThanhCongActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("PAYMENT", "Lỗi update Firestore", e);
                                    Toast.makeText(this, "Lỗi update Firestore", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Không tìm thấy giao dịch pending", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PAYMENT", "Lỗi truy vấn Firestore", e);
                    Toast.makeText(this, "Lỗi truy vấn Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isCheckingPayment) {
            return;
        }

        firestore.collection("bookings")
                .whereEqualTo("userId", currentUser != null ? currentUser.getUid() : "")
                .whereEqualTo("status", "success")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    if (!querySnapshots.isEmpty()) {
                        startActivity(new Intent(ThanhToanActivity.this, ThanhToanThanhCongActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PAYMENT", "Lỗi khi kiểm tra Firestore", e);
                });
    }
}
