package com.example.erbroker.Fragmants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erbroker.Activites.LoginActivity;
import com.example.erbroker.Activites.UserActivity;
import com.example.erbroker.Logic.Customer;
import com.example.erbroker.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragmant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragmant extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GraphView graph;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragmant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragmant.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragmant newInstance(String param1, String param2) {
        HomeFragmant fragment = new HomeFragmant();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //setting graph properties and values + initialize sign out button + showing profit in percents
    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.home_fragmant, container, false);
        graph = (GraphView) v.findViewById(R.id.graph);
        GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();
        graph.setTitle("Cash Graph");
        graph.setTitleTextSize(60);
        graph.setTitleColor(Color.WHITE);
        gridLabelRenderer.setPadding(50);
        gridLabelRenderer.setHorizontalAxisTitle("Days");
        gridLabelRenderer.setGridColor(Color.WHITE);
        gridLabelRenderer.setHorizontalAxisTitleTextSize(40);
        gridLabelRenderer.setVerticalAxisTitle("Cash USD");
        gridLabelRenderer.setLabelsSpace(5);
        gridLabelRenderer.setVerticalAxisTitleTextSize(40);
        gridLabelRenderer.setVerticalLabelsColor(Color.WHITE);
        gridLabelRenderer.setHorizontalLabelsColor(Color.WHITE);
        gridLabelRenderer.setHorizontalAxisTitleColor(Color.WHITE);
        gridLabelRenderer.setVerticalAxisTitleColor(Color.WHITE);

        Button b = v.findViewById(R.id.sign_out_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String loginPreferences = "login";
                SharedPreferences sharedPreferences = ((UserActivity)getActivity()).getSharedPreferences(loginPreferences, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                ((UserActivity)getActivity()).finishAffinity();
                startActivity(new Intent((UserActivity)getActivity(), LoginActivity.class));
            }
        });


        Customer c = ((UserActivity)getActivity()).getCustomer();
        ((TextView)(v.findViewById(R.id.d_name_txt))).setText(c.getName());
        ((TextView)(v.findViewById(R.id.d_b_name_txt))).setText(c.getBroker());
        TextView profitText =  v.findViewById(R.id.profit_txt_id);
        String s= c.calcProfit();
        profitText.setText("Profit: "+s);
        if(s.charAt(0)=='-')
        {
            profitText.setTextColor(Color.parseColor("#EA4335"));
        }
        else
            profitText.setTextColor(Color.parseColor("#bdf708"));

        if(c!=null)
        {
            if(needUpdate()==true)
            {
                updateGraph();
            }
        }
        return v;
    }


    // checking if one day at least passed from current day  ( In the first time , User register and connect  - > initialize graph)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean needUpdate() {
        SimpleDateFormat formatterCurrent = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterNew = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Customer c = ((UserActivity) getActivity()).getCustomer();
        graph.removeAllSeries();
        calendar.add(Calendar.DATE, c.getDaysCounter() + 1);
        Date New = calendar.getTime();
        String formattedDateCurrent = formatterCurrent.format(New);
        String formattedDateOld = formatterNew.format(c.getInitDate());
        LocalDate x1 = LocalDate.parse(formattedDateOld, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate x2 = LocalDate.parse(formattedDateCurrent, DateTimeFormatter.ISO_LOCAL_DATE);
        Duration diff = Duration.between(x1.atStartOfDay(), x2.atStartOfDay());
        long diffDays = diff.toDays();
        if (diffDays > 0)
        {
            c.setInitDate(New);
            if (c.getDaysCounter() == 0)
            {
                c.addPoint(0, 0.0);
                graph.addSeries(c.makeSeries());
                graph.getGridLabelRenderer().setNumHorizontalLabels(1);
                c.setDaysCounter(c.getDaysCounter() + 1);
                c.setDrawFrom(1);
                ((UserActivity) getActivity()).updateDataBase(false);
                return false;
            }
            else if (c.getDrawFrom() == 11)
            {
                double value = c.getSeries().get("10");
                c.setSeries(new HashMap<>());
                graph.removeAllSeries();
                c.addPoint(1, value);
                graph.getGridLabelRenderer().setNumHorizontalLabels(1);
                c.setDaysCounter(c.getDaysCounter() + 1);
                c.setDrawFrom(2);
                ((UserActivity) getActivity()).updateDataBase(false);
                return true;
            }
            return true;
        }
        return false;
}

    // updating cash graph in usd
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateGraph()
    {
        Customer c = ((UserActivity) getActivity()).getCustomer();
        c.addPoint(c.getDrawFrom(), c.getUsdCash());
        if(c.getDaysCounter()<=10)
            graph.getGridLabelRenderer().setNumHorizontalLabels(c.getDrawFrom()+1);
        else
            graph.getGridLabelRenderer().setNumHorizontalLabels(c.getDrawFrom());

        graph.addSeries(c.makeSeries());
        if(c.getDrawFrom() >2)
            c.setDaysCounter(c.getDaysCounter() + 1);
        else
        {
            if(c.getDaysCounter() <10 )
            {
                c.setDaysCounter(c.getDaysCounter() + 1);
            }
        }
        c.setDrawFrom(c.getDrawFrom()+1);
        ((UserActivity) getActivity()).updateDataBase(false);
    }
}

