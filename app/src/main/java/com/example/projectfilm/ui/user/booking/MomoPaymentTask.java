package com.example.projectfilm.ui.user.booking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MomoPaymentTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "MomoPayment";

    private Context context;
    private String amount;

    public MomoPaymentTask(Context context, String amount) {
        this.context = context;
        this.amount = amount;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String accessKey = "F8BBA842ECF85";
            String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
            String orderInfo = "pay with MoMo";
            String partnerCode = "MOMO";
            String redirectUrl = "https://webhook.site/b3088a6a-2d17-4f8d-a383-71389a6c600b";
            String ipnUrl = "https://webhook.site/b3088a6a-2d17-4f8d-a383-71389a6c600b";
            String requestType = "payWithMethod";
            String orderId = partnerCode + System.currentTimeMillis();
            String requestId = orderId;
            String extraData = "";
            String orderGroupId = "";
            boolean autoCapture = true;
            String lang = "vi";

            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + this.amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(rawSignature, secretKey);

            String jsonBody = "{"
                    + "\"partnerCode\":\"" + partnerCode + "\","
                    + "\"partnerName\":\"Test\","
                    + "\"storeId\":\"MomoTestStore\","
                    + "\"requestId\":\"" + requestId + "\","
                    + "\"amount\":\"" + this.amount + "\","
                    + "\"orderId\":\"" + orderId + "\","
                    + "\"orderInfo\":\"" + orderInfo + "\","
                    + "\"redirectUrl\":\"" + redirectUrl + "\","
                    + "\"ipnUrl\":\"" + ipnUrl + "\","
                    + "\"lang\":\"" + lang + "\","
                    + "\"requestType\":\"" + requestType + "\","
                    + "\"autoCapture\":" + autoCapture + ","
                    + "\"extraData\":\"" + extraData + "\","
                    + "\"orderGroupId\":\"" + orderGroupId + "\","
                    + "\"signature\":\"" + signature + "\""
                    + "}";

            URL url = new URL("https://test-payment.momo.vn/v2/gateway/api/create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            Log.d(TAG, "Status: " + status);

            InputStream is = (status < HttpURLConnection.HTTP_BAD_REQUEST) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();
            conn.disconnect();

            Log.d(TAG, "Body: " + response.toString());

            return response.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error", e);
            return null;
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secret_key);

        byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute: " + result);

        if (result == null) {
            Log.e(TAG, "Kết quả null");
            return;
        }

        try {
            JSONObject json = new JSONObject(result);
            int resultCode = json.getInt("resultCode");

            if (resultCode == 0) {
                String payUrl = json.getString("payUrl");
                String orderId = json.getString("orderId");
                String amount = json.getString("amount");

                // Mở giao diện MoMo
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
                context.startActivity(browserIntent);

                // Ghi booking Firestore (nếu muốn lưu trước khi thanh toán xong)
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> bookingData = new HashMap<>();
                bookingData.put("orderId", orderId);
                bookingData.put("amount", amount);
                bookingData.put("status", "pending");
                bookingData.put("createdAt", System.currentTimeMillis());
                bookingData.put("userId", userId);

                db.collection("bookings")
                        .add(bookingData)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "Booking đã lưu Firestore, ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Lỗi khi lưu booking", e);
                        });

            } else {
                Log.e(TAG, "Thanh toán thất bại: " + json.getString("message"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi parse JSON", e);
        }
    }
}
