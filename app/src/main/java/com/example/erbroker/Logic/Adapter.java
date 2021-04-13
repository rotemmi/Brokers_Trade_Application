package com.example.erbroker.Logic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.erbroker.R;
import java.util.ArrayList;

// adapter have responsible on portfolio investing list recycle view and watch list recycle view
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
{
    private Context context;
    private ArrayList list;
    private int typeOf;

    public Adapter(Context context, ArrayList list, int typeOf)
    {
        this.context = context;
        this.list = list;
        this.typeOf=typeOf;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if(typeOf ==1)
                view = LayoutInflater.from(context).inflate(R.layout.portfolio_table_layout, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.watch_list_table_layout, parent, false);
        return new ViewHolder(view);
    }

    // setting information on specific recycle view
    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        if (list != null && list.size() > 0) {
            if (typeOf == 1)
            {
                Investing investing = (Investing) (list.get(position));
                holder.stock_id.setText(investing.getStock());
                holder.last_id.setText(String.format("%.3f", investing.getLast()));
                holder.position_id.setText(String.format("%.1f", investing.getPosition()));
                holder.average_id.setText(String.format("%.3f", investing.getAverage()));
            }
            else
            {
                Watch watch = (Watch) (list.get(position));
                holder.stock_watch_list_id.setText(watch.getStockName());
                holder.last_watch_list_id.setText(String.format("%.3f", watch.getLastPrice()));
            }
        }
    }


    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView stock_id,last_id,position_id,average_id;
        TextView stock_watch_list_id,last_watch_list_id;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            stock_id = itemView.findViewById(R.id.stock_id);
            last_id = itemView.findViewById(R.id.last_id);
            position_id = itemView.findViewById(R.id.position_id);
            average_id = itemView.findViewById(R.id.average_id);
            stock_watch_list_id = itemView.findViewById(R.id.stock_watch_list_id);
            last_watch_list_id = itemView.findViewById(R.id.last_watch_list_id);
        }
    }
}
