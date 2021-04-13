package com.example.erbroker.Fragmants;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.erbroker.Activites.UserActivity;
import com.example.erbroker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WatchListFragmant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WatchListFragmant extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public RecyclerView recyclerView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WatchListFragmant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WatchListFragmant.
     */
    // TODO: Rename and change types and number of parameters
    public static WatchListFragmant newInstance(String param1, String param2) {
        WatchListFragmant fragment = new WatchListFragmant();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.watch_list_fragmant, container, false);
        recyclerView= v.findViewById(R.id.recycler_view_watch_list);
        setRecyclerView(v);
        return v;
    }

    // updating stocks prices in recycle view
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setRecyclerView(View v)
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        ((UserActivity)getActivity()).refreshWatchList(v);
    }
}