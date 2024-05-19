package com.example.pokedex;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private Context context;

    // Constructor, setters, getters, etc.

    public ShopItemAdapter(Context context, List<Item> itemList) {
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
        Item item = itemList.get(position);

        // Establecer el nombre y la categoría del Item en los TextView correspondientes
        holder.textViewItemName.setText(item.getName());
        holder.textViewItemDescription.setText(item.getCategory());
        holder.textPrice.setText("Price: "+ String.valueOf(item.getPrice()));

        // Cargar la imagen desde la URL utilizando Picasso
        if (item.getImageUrl() != null) {
            Picasso.get().load(item.getImageUrl()).into(holder.imageViewItem);
        } else {
            holder.imageViewItem.setImageResource(R.drawable.pokeball);
        }
        holder.imageViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    private void showPopup(Item item) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_shop, null);

        ImageView popupImageView = popupView.findViewById(R.id.imageViewFront);
        TextView popupItemName = popupView.findViewById(R.id.titleItem);
        TextView popupItemDescription = popupView.findViewById(R.id.textViewDescription);
        TextView popupItemDescription2 = popupView.findViewById(R.id.textViewAbility);
        TextView popupItemPrice = popupView.findViewById(R.id.textViewprice);

        if (item.getImageUrl() != null) {
            Picasso.get().load(item.getImageUrl()).into(popupImageView);
        } else {
            popupImageView.setImageResource(R.drawable.pokeball);
        }

        popupItemName.setText(item.getName());
        popupItemDescription.setText(item.getDescription());
        popupItemDescription2.setText(item.getDescriptionMotivation());
        popupItemPrice.setText("Price: " + item.getPrice());

        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Fondo transparente para ver la sombra
        popupWindow.setOutsideTouchable(true); // Permite cerrar el popup tocando fuera
        popupWindow.setElevation(60); // Sombra para el popup
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewItem;
        TextView textViewItemName;
        TextView textViewItemDescription;
        TextView textPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageView2);
            textViewItemName = itemView.findViewById(R.id.textView);
            textViewItemDescription = itemView.findViewById(R.id.textView3);
            textPrice=itemView.findViewById(R.id.textView4);
        }
    }

}