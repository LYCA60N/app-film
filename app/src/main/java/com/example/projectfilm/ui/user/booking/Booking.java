package com.example.projectfilm.ui.user.booking;

public class Booking {
    private String cinema, email, name, paymentMethod, price, seats, time, userId;
    private long timestamp;

    public Booking() {}

    // Getters và Setters
    public String getCinema() { return cinema; }
    public void setCinema(String cinema) { this.cinema = cinema; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getSeats() { return seats; }
    public void setSeats(String seats) { this.seats = seats; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
