package com.example.erbroker.Logic;

// Watch - Object of watch list
// Uses - in Recycle View
public class Watch
{
    private String stockName;
    private double lastPrice;

    public Watch(String stockName, double lastPrice)
    {
        this.stockName = stockName;
        this.lastPrice = lastPrice;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }
}
