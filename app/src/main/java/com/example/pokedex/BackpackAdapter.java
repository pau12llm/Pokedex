package com.example.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

public class BackpackAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private List<Item> mItemList;
    private OnItemUseClickListener listener;

    public BackpackAdapter(Context context, List<Item> itemList, OnItemUseClickListener listener) {
        super(context, 0, itemList);
        this.mContext = context;
        this.mItemList = itemList;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_backpack, parent, false);
        }

        Item currentItem = mItemList.get(position);

        ImageView imageView = listItem.findViewById(R.id.imageViewItem);
        Glide.with(mContext)
                .load(currentItem.getImageUrl())
                .into(imageView);

        TextView textViewName = listItem.findViewById(R.id.itemName);
        textViewName.setText(currentItem.getName());

        TextView textViewNumber = listItem.findViewById(R.id.itemNumber);
        textViewNumber.setText("15");

        Button btnUse = listItem.findViewById(R.id.btn_use);
        btnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemUseClick(currentItem);
                }
            }
        });

        return listItem;
    }



}