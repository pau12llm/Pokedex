package com.example.pokedex;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        final Item item = itemList.get(position);

        holder.textViewItemName.setText(item.getName());
        holder.textViewItemDescription.setText(item.getCategory());
        holder.textPrice.setText(String.valueOf(item.getPrice()));

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

    private void showPopup(final Item item) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_shop, null);

        final ImageView popupImageView = popupView.findViewById(R.id.imageViewFront);
        final TextView popupItemName = popupView.findViewById(R.id.titleItem);
        final TextView popupItemDescription = popupView.findViewById(R.id.textViewDescription);
        final TextView popupItemPrice = popupView.findViewById(R.id.textViewprice);
        final TextView popupItemDescription2 = popupView.findViewById(R.id.textViewAbility);

        if (item.getImageUrl() != null) {
            Picasso.get().load(item.getImageUrl()).into(popupImageView);
        } else {
            popupImageView.setImageResource(R.drawable.pokeball);
        }

        popupItemName.setText(item.getName());
        popupItemDescription.setText(item.getDescription());
        popupItemPrice.setText(String.valueOf(item.getPrice()));

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(60);

        final View rootView = ((Activity) context).findViewById(android.R.id.content);
        final ViewGroup rootViewGroup = (ViewGroup) rootView;
        final ColorDrawable dimDrawable = new ColorDrawable(Color.BLACK);
        dimDrawable.setAlpha(150);
        rootViewGroup.getOverlay().add(dimDrawable);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rootViewGroup.getOverlay().remove(dimDrawable);
            }
        });

        final TextView textquantity = popupView.findViewById(R.id.textquantity);
        final TextView textNumberTotal = popupView.findViewById(R.id.textNumberTotal);
        Button buttonplus = popupView.findViewById(R.id.buttonplus);
        Button buttonless = popupView.findViewById(R.id.buttonless);

        textquantity.setText("0");
        textNumberTotal.setText("0");

        buttonplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(textquantity.getText().toString());
                quantity++;
                textquantity.setText(String.valueOf(quantity));
                textNumberTotal.setText( String.valueOf(quantity * item.getPrice()));
            }
        });

        buttonless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(textquantity.getText().toString());
                if (quantity > 0) {
                    quantity--;
                    textquantity.setText(String.valueOf(quantity));
                    textNumberTotal.setText(String.valueOf(quantity * item.getPrice()));
                }
            }
        });

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
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
            textPrice = itemView.findViewById(R.id.textView4);
        }
    }
}
