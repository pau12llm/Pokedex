package com.example.pokedex;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tab_bar);
// Obtener TabLayout y ViewPager desde el layout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        // Configurar el adaptador de fragments y el ViewPager
        viewPager.setAdapter(new ViewPagerFragmentAdapter(this));

        // Conectar el TabLayout con el ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("Tab " + (position + 1))
        ).attach();
    }


    private static class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
            // Devuelve el fragment correspondiente a la posición
            switch (position) {
                case 0:
                    return new PokedexFragment();
                case 1:
                    return new TrainerFragment();
                case 2:
                    return new ShopFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            // Devuelve el número total de fragments
            return 3;
        }
    }
}