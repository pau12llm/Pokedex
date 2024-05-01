package com.example.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ItemViewHolder> {

    private List<item> itemList;
    private Context context;

    // Constructor, setters, getters, etc.

    public ShopItemAdapter(Context context, List<item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        item item = itemList.get(position);

        // Establecer el nombre y la categoría del Item en los TextView correspondientes
        holder.textViewItemName.setText(item.getName());
        holder.textViewItemDescription.setText(item.getCategory());

        // Cargar la imagen desde la URL utilizando Picasso
        if (item.getImageUrl() != null) {
            Picasso.get().load(item.getImageUrl()).into(holder.imageViewItem);
        } else {
            holder.imageViewItem.setImageResource(R.drawable.pokeball);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewItem;
        TextView textViewItemName;
        TextView textViewItemDescription;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageView2);
            textViewItemName = itemView.findViewById(R.id.textView);
            textViewItemDescription = itemView.findViewById(R.id.textView3);
        }
    }
}