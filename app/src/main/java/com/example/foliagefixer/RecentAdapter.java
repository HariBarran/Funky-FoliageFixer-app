package com.example.foliagefixer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Scan> scanList;

    public RecentAdapter(Context context, ArrayList<Scan> scanList) {
        this.context = context;
        this.scanList = scanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Scan scan = scanList.get(position);

        Glide.with(context)
                .load(scan.getImage())
                .into(holder.scanImageView);

        holder.classificationTextView.setText(scan.getClassification().toString());
        holder.severityTextView.setText(String.format("Severity: %.2f", scan.getSeverity()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScanDetailsActivity.class);
                intent.putExtra("selected_scan", scan);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView scanImageView;
        TextView classificationTextView;
        TextView severityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            scanImageView = itemView.findViewById(R.id.scanImageView);
            classificationTextView = itemView.findViewById(R.id.classificationTextView);
            severityTextView = itemView.findViewById(R.id.severityTextView);
        }
    }

    public void setRecentScans(ArrayList<Scan> newScanList) {
        this.scanList = newScanList;
        notifyDataSetChanged();
    }
}

