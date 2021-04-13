package com.example.erbroker.Logic;
import android.graphics.Color;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Customer
{
    private String name;
    private String email;
    private String address;
    private String phone;
    private String broker;
    private String accountNumber;
    private double usdCash;
    private double iLCash;
    private HashMap<String , ArrayList<Double>> stocks;
    private HashMap<String , Double> watchList;
    private int daysCounter;
    private Date initDate;
    private int drawFrom;
    private HashMap<String,Double>series;

    public Customer()
    {

    }

    public int getDrawFrom() {
        return drawFrom;
    }

    public void setDrawFrom(int drawFrom) {
        this.drawFrom = drawFrom;
    }

    public int getDaysCounter() {
        return daysCounter;
    }

    public void setDaysCounter(int daysCounter) {
        this.daysCounter = daysCounter;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }


    public void addPoint(int x,double y)
    {
        series.put(x+"",y);
    }

    public Customer(String name, String email, String address, String phone, String broker)
    {
        this.daysCounter = 0;
        this.drawFrom=0;
        series =new HashMap<String, Double>();
        this.initDate = Calendar.getInstance().getTime();
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.broker=broker;
        this.accountNumber="";
        stocks = new HashMap<>();
        watchList = new HashMap<>();
        for(int i=0;i<6;i++)
        {
            this.accountNumber+=(new Random().nextInt(8) + 1);
        }
        this.usdCash=0;
        this.iLCash=0;
    }


    public HashMap<String, Double> getSeries() {
        return series;
    }

    public void setSeries(HashMap<String, Double> series1) {
        this.series = series1;
    }

    public HashMap<String, Double> getWatchList() {
        return watchList;
    }

    public void setWatchList(HashMap<String, Double> watchList) {
        this.watchList = watchList;
    }

    public void setWatchPrice(String watch,Double price) {
        watchList.put(watch,price);
    }

    public void setLastPrice(String stockName,double last)
    {
        watchList.put(stockName,last);
    }


    public String getAddress()
    {
        return address;
    }
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public void setBroker(String broker) { this.broker = broker; }

    public String getBroker() { return broker; }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getUsdCash() {
        return usdCash;
    }

    public void setUsdCash(double usdCash) {
        this.usdCash += usdCash;
    }

    public double getILCash() {
        return iLCash;
    }

    public void setILCash(double iLCash) {
        this.iLCash += iLCash;
    }

    public HashMap<String, ArrayList<Double>> getStocks() {
        return stocks;
    }

    public void setStocks(String stockName,Double positions,Double price)
    {
        if(stocks.containsKey(stockName))
        {
            double avg =  stocks.get(stockName).get(1);
            double lastPositions =  stocks.get(stockName).get(0);
            stocks.get(stockName).set(1,(lastPositions*avg + positions*price)/(lastPositions+positions));
            stocks.get(stockName).set(0,lastPositions+positions);
        }
        else
        {
            stocks.put(stockName,new ArrayList<>(Arrays.asList(positions ,price)));
        }
    }

    // updating series of points from graph view in HomeFragmant
    public LineGraphSeries<DataPoint> makeSeries( )
    {
        LineGraphSeries s = new LineGraphSeries();
        s.setColor(Color.WHITE);
        for (HashMap.Entry<String,Double> entry : this.series.entrySet())
        {
            s.appendData(new DataPoint(Integer.parseInt(entry.getKey()),entry.getValue()),false,11);
        }
        return s;
    }


    // calculation profit in usd in relation to last day
    public String calcProfit()
    {
        if(drawFrom == 0)
            return " 0%";
        double oldCash = series.get((drawFrom-1)+"");

        if(oldCash==0)
            return String.format("%.2f",usdCash*100)+"%";
        double ratio = usdCash / oldCash;
        if(ratio > 1)
        {
            return " + "+String.format("%.2f",(((usdCash -oldCash)/oldCash)*100))+"%";
        }
        else if(ratio ==1)
        {
            return " 0%";
        }
        return ""+String.format("%.2f", (ratio -1)*100)+"%";
    }
}
