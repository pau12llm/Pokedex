package com.example.pokedex;

import android.os.Bundle;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends Fragment {
    private static final String TAG = "ShopFragment";

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }


}
