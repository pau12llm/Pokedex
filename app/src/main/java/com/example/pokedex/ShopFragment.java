package com.example.pokedex;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.pokedex.R;

public class ShopFragment extends Fragment {

    private static final String TAG = "ShopFragment";
    private int money = 0; // Variable para almacenar el dinero
    private TextView textNumberTotal; // TextView para mostrar el total de dinero

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Obtener referencia al TextView textNumberTotal
        textNumberTotal = view.findViewById(R.id.textNumberTotal);

        // Configurar OnClickListener para imageView2
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sumar 1 al dinero
                money += 200;
                // Actualizar el TextView con el nuevo valor del dinero
                textNumberTotal.setText(String.valueOf(money));
            }
        });

        // Configurar OnClickListener para imageView3
        ImageView imageView3 = view.findViewById(R.id.imageView3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sumar 2 al dinero
                money += 500;
                // Actualizar el TextView con el nuevo valor del dinero
                textNumberTotal.setText(String.valueOf(money));
            }
        });

        // Configurar OnClickListener para imageView4
        ImageView imageView4 = view.findViewById(R.id.imageView4);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sumar 3 al dinero
                money += 1500;
                // Actualizar el TextView con el nuevo valor del dinero
                textNumberTotal.setText(String.valueOf(money));
            }
        });

        // Configurar OnClickListener para imageView5
        ImageView imageView5 = view.findViewById(R.id.imageView5);
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sumar 4 al dinero
                money += 100000;
                // Actualizar el TextView con el nuevo valor del dinero
                textNumberTotal.setText(String.valueOf(money));
            }
        });

        return view;
    }
}
