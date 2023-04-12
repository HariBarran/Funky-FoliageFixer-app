package com.example.foliagefixer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ImageItem> imageItems;
    private final OnImageClickListener onImageClickListener;
    private final OnImageLongClickListener onImageLongClickListener;

    public GalleryAdapter(Context context, ArrayList<ImageItem> imageItems, OnImageClickListener onImageClickListener, OnImageLongClickListener onImageLongClickListener) {
        this.context = context;
        this.imageItems = imageItems;
        this.onImageClickListener = onImageClickListener;
        this.onImageLongClickListener = onImageLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem imageItem = imageItems.get(position);
        Glide.with(context)
                .load(imageItem.getImageUri())
                .placeholder(R.drawable.baseline_person_24) // Replace with your placeholder drawable
                .into(holder.imageView);
        holder.titleTextView.setText(imageItem.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClickListener.onImageClick(imageItem);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onImageLongClickListener.onImageLongClick(imageItem, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public interface OnImageLongClickListener {
        void onImageLongClick(ImageItem imageItem, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(ImageItem imageItem);
    }
}



