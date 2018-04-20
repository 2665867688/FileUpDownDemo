package com.yue.fileupdown.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yue.fileupdown.R;
import com.yue.fileupdown.bean.MainItem;
import com.yue.fileupdown.databinding.ItemMainBinding;
import com.yue.fileupdown.listeners.OnRcyItemClickListener;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainHolder> {


    private Context context;
    private List<MainItem> list;
    private OnRcyItemClickListener onRcyItemClickListener;

    public void setOnRcyItemClickListener(OnRcyItemClickListener onRcyItemClickListener) {
        this.onRcyItemClickListener = onRcyItemClickListener;
    }

    public MainAdapter(Context context, List<MainItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        return new MainHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, final int position) {
        holder.tvText.setText(list.get(position).getName() + "\n" + list.get(position).getDesc());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRcyItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MainHolder extends RecyclerView.ViewHolder {
        TextView tvText;

        public MainHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_main_text);
        }
    }
}
