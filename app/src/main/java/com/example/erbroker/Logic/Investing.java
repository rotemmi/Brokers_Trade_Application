package com.example.erbroker.Logic;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Investing
{
    private String Stock;
    private double last;
    private double Position;
    private double Average;

    public Investing(String stock, double last, double position, double average) {
        Stock = stock;
        this.last = last;
        Position = position;
        Average = average;
    }

    public String getStock() {
        return Stock;
    }

    public void setStock(String stock) {
        Stock = stock;
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public double getPosition() {
        return Position;
    }

    public void setPosition(double position) {
        Position = position;
    }

    public double getAverage() {
        return Average;
    }

    public void setAverage(double average) {
        Average = average;
    }

}
