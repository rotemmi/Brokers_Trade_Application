package com.example.erbroker.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.erbroker.Fragmants.AccountFragmant;
import com.example.erbroker.Fragmants.HomeFragmant;
import com.example.erbroker.Fragmants.PortfolioFragmant;
import com.example.erbroker.Fragmants.TradeFragmant;
import com.example.erbroker.Fragmants.WatchListFragmant;
import com.example.erbroker.Logic.Customer;
import com.example.erbroker.Logic.Investing;
import com.example.erbroker.Logic.Adapter;
import com.example.erbroker.Logic.Watch;
import com.example.erbroker.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity
{
    private BottomNavigationView bottomNavigationView;
    private Customer c;
    private ArrayList<Investing> investingList;
    private ArrayList<Watch> watchList;

    //return current user online
    public Customer getCustomer() {
        return c;
    }



    // initialize bottom menu - > reading user information from firebase - > setting fragment's transactions -> start with home fragment
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        readUserDetailsFromFireStore();
        Toast.makeText(UserActivity.this, "Loading your data be patient",
                Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override public void run()
            {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new HomeFragmant(),"Home").commit();
                findViewById(R.id.bottom_nav).setVisibility(View.VISIBLE);
            }
        }, 1500);
        investingList = new ArrayList<Investing>();
        watchList = new ArrayList<Watch>();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selected = null;
                String FRAGMENT_TAG="";
                switch (item.getItemId())
                {
                    case (R.id.home_nav):
                        selected = new HomeFragmant();
                        FRAGMENT_TAG="Home";
                        break;

                    case (R.id.portfolio_nav):
                        selected = new PortfolioFragmant();
                        FRAGMENT_TAG="Portfolio";
                        break;

                    case (R.id.trade_nav):
                        FRAGMENT_TAG="Trade";
                        selected = new TradeFragmant();
                        break;

                    case (R.id.watch_list_nav):
                        FRAGMENT_TAG="Watch_List";
                        selected = new WatchListFragmant();
                        break;

                    case (R.id.account_nav):
                        FRAGMENT_TAG="Account";
                        selected = new AccountFragmant();
                        break;
                }
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected,FRAGMENT_TAG).commit();
                return true;
            }
        });
    }

    // reading details information from firebase -> setting account number UI label
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void readUserDetailsFromFireStore()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DocumentReference docRef = db.collection("Users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                c =  (documentSnapshot.toObject(Customer.class));
                ((TextView) findViewById(R.id.account_number)).setText("Account Number: " + c.getAccountNumber());
            }
        });
    }

    // updating information in firebase
    public void updateDataBase(boolean flag)
    {
        final boolean showAlert=flag;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        db.collection("Users").document(uid).set(c).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run()
                    {
                        if(flag)
                        {
                            Toast.makeText(UserActivity.this, "Your account has been updated.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, 3000);

            }});
   }

   // cash method function
    // method behavior different in some keys:
    // key = 0  - >  showing total cash in Usd/Shekels according to real time values in PortfolioFragmant
    // key = 1  - >  converting Usd/Shekels User cash according to real time values in AccountFragmant
    public void cashFunc(int key)
    {
        String url = "https://api.exchangeratesapi.io/latest?base=USD";
        RequestQueue myQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject rates = response.getJSONObject("rates");
                    double usd = rates.getDouble("ILS");
                    double usdCash = c.getUsdCash();
                    double iLCash = c.getILCash();
                    if(key ==0)
                    {
                        int radioId = ((RadioGroup) findViewById(R.id.r_cash)).getCheckedRadioButtonId();
                        TextView totalCash = (TextView) (findViewById(R.id.total_cash));
                        if (radioId == R.id.shekels) {
                            iLCash = iLCash + (usdCash * usd);
                            totalCash.setText("Total Cash: " + String.format("%.3f", iLCash));
                        } else {
                            usdCash = usdCash + (iLCash / usd);
                            totalCash.setText("Total Cash: " + String.format("%.3f", usdCash));
                        }
                    }
                    else
                    {
                        int radioId = ((RadioGroup) findViewById(R.id.from_group_id)).getCheckedRadioButtonId();
                        if(radioId!=-1)
                        {
                            String amount = ((EditText)(findViewById(R.id.converter_text_id))).getText().toString();
                            if(!amount.matches(" ") )
                            {
                                double moneyforConvert = Double.parseDouble(amount);
                                if (moneyforConvert > 0)
                                {
                                    if (radioId == R.id.from_shekels_id)
                                    {
                                        if (moneyforConvert <= iLCash)
                                        {
                                            c.setILCash(-moneyforConvert);
                                            c.setUsdCash(moneyforConvert / usd);
                                            updateDataBase(true);
                                        }
                                        else
                                            Toast.makeText(UserActivity.this, "You dont have enough cash.",
                                                    Toast.LENGTH_LONG).show();
                                    }
                                    else if (moneyforConvert <= usdCash)
                                        {
                                            c.setUsdCash(-moneyforConvert);
                                            c.setILCash(moneyforConvert * usd);
                                            updateDataBase(true);
                                        }
                                    else
                                    {
                                        Toast.makeText(UserActivity.this, "You dont have enough cash.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                        }
                        else
                        {
                            Toast.makeText(UserActivity.this, "You need to choose Usd/Shekels",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        myQueue.add(request);
    }

    // set visible button Check Box when user decide to choose converting from Shekels/Usd to Shekels/Usd in AccountFragmant
    public void convertCheckBoxClick(View view)
    {
        RadioGroup group = findViewById(R.id.from_group_id);
        RadioButton toShekels = findViewById(R.id.to_shekels_id);
        RadioButton toUsd = findViewById(R.id.to_usd_id);
        if(group.getCheckedRadioButtonId()==R.id.from_usd_id)
        {
            toUsd.setChecked(false);
            toShekels.setChecked(true);
        }
        else
        {
            toUsd.setChecked(true);
            toShekels.setChecked(false);
        }
    }

    // navigating to cash func in order to show user total cash
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cashClick(View v)
    {
        readUserDetailsFromFireStore();
        cashFunc(0);
    }

    // navigating to cash func in order to convert Usd/Shekels User cash
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void convertFunc(View v)
    {
        readUserDetailsFromFireStore();
        cashFunc(1);
    }

    // deposit cash in Shekels/USD according to user choose + checking input Tests
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void depositCashAccount(View view)
    {
        int radioId = ((RadioGroup) findViewById(R.id.cash_account)).getCheckedRadioButtonId();
        if(radioId !=-1)
        {
            EditText deposit = (EditText) (findViewById(R.id.deposit_id));
            Double amount = Double.parseDouble(deposit.getText().toString());
            if(amount > 0)
            {
                readUserDetailsFromFireStore();
                if (radioId == R.id.shekels_account) {
                    c.setILCash(Double.parseDouble(deposit.getText().toString()));
                } else {
                    c.setUsdCash(Double.parseDouble(deposit.getText().toString()));
                }
                updateDataBase(true);
            }
            else
            {
                Toast.makeText(UserActivity.this, "Deposit ammount must be greather than zero.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }
        else
        {
            Toast.makeText(UserActivity.this, "Please choose : USD/ILS.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // getting price of stock method function
    // method behavior different in some keys:
    // key = 0  - >  showing price stock according to real time values  + Checking stock name validation in TradeFragmant
    // key = 1  - >  updating price stock according to real time values in PortfolioFragmant(setting recycleview adapter)
    // key = 2  - >  updating price stock according to real time values in WatchListFragmant(setting recycleview adapter)
    public void searchStock(String stock,int key)
    {
        String url = "https://financialmodelingprep.com/api/v3/quote-short/"+stock+"?apikey=f9867ba3b97fedb5c7bb83ebaa6aa98c";
        RequestQueue myQueue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                //  JSONObject rates = response.getJSONObject("price");
                JSONObject s;
                try {
                    s = response.getJSONObject(0);
                    if(key==0)
                    {
                        TextView priceOfStock = ((TextView) findViewById(R.id.stock_price_id));
                        priceOfStock.setText("" + s.getDouble("price"));
                    }
                    else
                    {
                        if(key==1)
                        {
                            investingList.add(new Investing(stock, s.getDouble("price"), c.getStocks().get(stock).get(0), c.getStocks().get(stock).get(1)));
                            ((PortfolioFragmant) (getSupportFragmentManager().findFragmentByTag("Portfolio"))).recyclerView.setAdapter(new Adapter(UserActivity.this, investingList,1));
                        }
                        else
                        {
                            c.setWatchPrice(stock,s.getDouble("price"));
                            watchList.add(new Watch(stock,s.getDouble("price")));
                            ((WatchListFragmant) (getSupportFragmentManager().findFragmentByTag("Watch_List"))).recyclerView.setAdapter(new Adapter(UserActivity.this,watchList,2));
                            updateDataBase(false);
                        }
                    }

                } catch (JSONException e)
                {
                    if (key==0)
                        Toast.makeText(UserActivity.this, "The Stock You put is not correct ,Try Again", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            }
        });
        myQueue.add(request);
    }

    // navigate fo searchStock method with stock name from UI (TradeFragmant)
    public void searchStock(View v)
    {
        String stock = ((EditText)findViewById(R.id.search_stock_id)).getText().toString();
        CheckBox c = findViewById(R.id.watch_list_cbox_id);
        c.setChecked(false);
        if(stock.matches(""))
        {
            Toast.makeText(UserActivity.this, "Please Enter Stock", Toast.LENGTH_LONG).show();
            return;
        }
        searchStock(stock.toUpperCase(),0);
    }

    // Buying stock in real-time price + checking Tests
    public void buyStock(View v)
    {
        String stockName = (((EditText) (findViewById(R.id.search_stock_id))).getText()).toString().toUpperCase();
        String positionStr  = (((EditText) (findViewById(R.id.position_trade_id))).getText()).toString();
        String stockPriceStr  = (((TextView) (findViewById(R.id.stock_price_id))).getText()).toString();
        if (!stockName.matches("") &&!positionStr.matches("") &&!stockPriceStr.matches(""))
        {
            double position = Double.parseDouble(positionStr);
            double stockPrice = Double.parseDouble(stockPriceStr);
            if ( position > 0)
            {
                double buyPrice=stockPrice*position;
                if(buyPrice<=c.getUsdCash())
                {
                    c.setStocks(stockName, position, stockPrice);
                    c.setUsdCash(-buyPrice);
                    Toast.makeText(UserActivity.this, "Your are Successfuly buy "+positionStr+" positions of " +stockName+" in price "+stockPriceStr, Toast.LENGTH_LONG).show();
                    updateDataBase(true);
                    return;
                }
                Toast.makeText(UserActivity.this, "you dont have enough USD Cash", Toast.LENGTH_LONG).show();
                return;
            }
        }
        Toast.makeText(UserActivity.this, "One of the arguments is not correct.", Toast.LENGTH_LONG).show();
    }


    // Selling stock in real-time price + checking Tests
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sellStock(View view)
    {
        readUserDetailsFromFireStore();
        String stockName = (((EditText) (findViewById(R.id.search_stock_id))).getText()).toString().toUpperCase();
        String positionStr = (((EditText) (findViewById(R.id.position_trade_id))).getText()).toString();
        String stockPriceStr  = (((TextView) (findViewById(R.id.stock_price_id))).getText()).toString();
        if(!stockPriceStr.matches("") && !positionStr.matches("")&& !stockName.matches("")) {
            if (c.getStocks().get(stockName)!=null)
            {
                Double currentPosition = c.getStocks().get(stockName).get(0);
                if (currentPosition >= Double.parseDouble(positionStr))
                {
                    Double position = Double.parseDouble(positionStr);
                    c.getStocks().get(stockName).set(0, currentPosition -position);
                    c.setUsdCash((Double.parseDouble(stockPriceStr))*position);
                    if(c.getStocks().get(stockName).get(0)==0)
                    {
                        c.getStocks().remove(stockName);
                    }
                     Toast.makeText(UserActivity.this, "Your are Successfuly sold "+positionStr+" positions of " +stockName+" in price "+stockPriceStr, Toast.LENGTH_LONG).show();
                    updateDataBase(true);
                }
                else
                {
                    Toast.makeText(UserActivity.this, "You don't amount of :"+positionStr+" positions at "+ stockName, Toast.LENGTH_LONG).show();

                }
            }
            else
            {
                Toast.makeText(UserActivity.this, "You don't have positions of :"+stockName, Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(UserActivity.this, "You must search the stock and choose positions before selling", Toast.LENGTH_LONG).show();
        }
    }

    // refreshing portfolio investing list in recycleView (PortfolioFragmant)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Refresh(View v)
    {
        readUserDetailsFromFireStore();
        investingList= new ArrayList<>();
        getList();
    }
    public void getList()
    {
        for (HashMap.Entry<String, ArrayList<Double>> entry : c.getStocks().entrySet()) {
            searchStock(entry.getKey(), 1);
        }
    }


    // adding stock for watch list
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addToWatchList(View view)
    {
        boolean checked = ((CheckBox) view).isChecked();
        if(checked) {
            readUserDetailsFromFireStore();
            String stockName = (((EditText) (findViewById(R.id.search_stock_id))).getText()).toString().toUpperCase();
            if (!stockName.matches("")) {
                c.setLastPrice(stockName, 0);
                updateDataBase(true);
            } else {
                Toast.makeText(UserActivity.this, "You must search the stock before add to watch list", Toast.LENGTH_LONG).show();
            }
        }
    }


    // updating stocks prices in watch list (WatchListFragmant)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refreshWatchList(View v)
    {
        readUserDetailsFromFireStore();
        watchList = new ArrayList<>();
        getWatchList();
    }

    public void getWatchList()
    {
        for (HashMap.Entry<String,Double> entry : c.getWatchList().entrySet())
        {
            searchStock(entry.getKey(), 2);
        }
    }

    // clear watch list -> initialise recycle view (WatchListFragmant)
    public void clearList(View view)
    {
        c.setWatchList(new HashMap<>());
        updateDataBase(false);
        ((WatchListFragmant) (getSupportFragmentManager().findFragmentByTag("Watch_List"))).recyclerView.setAdapter(new Adapter(UserActivity.this, new ArrayList<Watch>(), 2));

    }
}