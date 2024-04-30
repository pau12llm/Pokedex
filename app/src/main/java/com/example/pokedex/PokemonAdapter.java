package com.example.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PokemonAdapter extends BaseAdapter {

    private List<String> pokemonNames;
    private Context context;

    public PokemonAdapter(Context context, List<String> pokemonNames) {
        this.context = context;
        this.pokemonNames = pokemonNames;
    }

    @Override
    public int getCount() {
        return pokemonNames.size();
    }

    @Override
    public Object getItem(int position) {
        return pokemonNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textViewName = convertView.findViewById(R.id.textViewName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String pokemonName = pokemonNames.get(position);
        viewHolder.textViewName.setText(pokemonName);

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewName;
    }
}