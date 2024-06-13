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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userEmail;
    private String documentId;

    public ShopItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
        final TextView userMoneyTextView = popupView.findViewById(R.id.textView11);

        // Obtener el usuario actual y su dinero!
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();

            db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            documentId = document.getId();
                            Long userMoney = document.getLong("money");
                            userMoneyTextView.setText("User Money: " + userMoney);
                        } else {
                            Toast.makeText(context, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

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

        popupWindow.setOnDismissListener(() -> rootViewGroup.getOverlay().remove(dimDrawable));

        final TextView textquantity = popupView.findViewById(R.id.textquantity);
        final TextView textNumberTotal = popupView.findViewById(R.id.textNumberTotal);
        Button buttonplus = popupView.findViewById(R.id.buttonplus);
        Button buttonless = popupView.findViewById(R.id.buttonless);
        Button buttonBuy = popupView.findViewById(R.id.buttonCapture);

        textquantity.setText("0");
        textNumberTotal.setText("0");

        buttonplus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textquantity.getText().toString());
            quantity++;
            textquantity.setText(String.valueOf(quantity));
            textNumberTotal.setText(String.valueOf(quantity * item.getPrice()));
        });

        buttonless.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textquantity.getText().toString());
            if (quantity > 0) {
                quantity--;
                textquantity.setText(String.valueOf(quantity));
                textNumberTotal.setText(String.valueOf(quantity * item.getPrice()));
            }
        });

        buttonBuy.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textquantity.getText().toString());
            if (quantity > 0) {


            int totalCost = quantity * item.getPrice();

            db.collection("users")
                    .document(documentId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Long userMoney = task.getResult().getLong("money");
                            if (userMoney != null && userMoney >= totalCost) {
                                DocumentReference userDocRef = db.collection("users").document(documentId);
//                                userDocRef.get().addOnSuccessListener(documentSnapshot -> {
//                                    if (documentSnapshot.exists()) {
//                                        Map<String, Object> itemsMap = (Map<String, Object>) documentSnapshot.get("items");
//                                        if (itemsMap == null) {
//                                            itemsMap = new HashMap<>();
//                                        }
//                                        int currentQuantity = itemsMap.containsKey(item.getName()) ? ((Long) itemsMap.get(item.getName())).intValue() : 0;
//                                        itemsMap.put(item.getName(), currentQuantity + quantity);
//
//                                        userDocRef.update("money", userMoney - totalCost, "items", itemsMap)
//                                                .addOnSuccessListener(aVoid -> {
//                                                    Toast.makeText(context, "Item purchased successfully", Toast.LENGTH_SHORT).show();
//                                                    popupWindow.dismiss();
//                                                })
//                                                .addOnFailureListener(e -> Toast.makeText(context, "Purchase failed", Toast.LENGTH_SHORT).show());
//                                    }
//                                });
                                userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        Object itemsObject = documentSnapshot.get("items");
                                        Map<String, Object> itemsMap;

                                        if (itemsObject instanceof Map) {
                                            itemsMap = (Map<String, Object>) itemsObject;
                                        } else {
                                            itemsMap = new HashMap<>();
                                        }

                                        int currentQuantity = itemsMap.containsKey(item.getName()) ? ((Long) itemsMap.get(item.getName())).intValue() : 0;
                                        itemsMap.put(item.getName(), currentQuantity + quantity);

                                        userDocRef.update("money", userMoney - totalCost, "items", itemsMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(context, "Item purchased successfully", Toast.LENGTH_SHORT).show();
                                                    popupWindow.dismiss();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(context, "Purchase failed", Toast.LENGTH_SHORT).show());
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Not enough money", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            } else {
                Toast.makeText(context, "You have not indicated how many units", Toast.LENGTH_SHORT).show();
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
