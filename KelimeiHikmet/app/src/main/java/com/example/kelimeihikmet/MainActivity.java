package com.example.kelimeihikmet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelimeihikmet.ui.FavoritesActivity;
import com.example.kelimeihikmet.ui.QuoteListActivity;
import com.example.kelimeihikmet.utils.LocaleHelper; // 🌍 Yerelleştirme sınıfı (hazırsa kullanılır)

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    // 🗂️ Kategori anahtarlarını tanımlarız
    private List<String> categoryList = Arrays.asList(
            "category_baris", "category_bilgelik", "category_erdem", "category_motivasyon",
            "category_ask", "category_yasam", "category_ozgurluk", "category_toplum",
            "category_adalet", "category_doga", "category_inanc", "category_diger"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 🌓 Tema tercihini uygula
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String themePref = prefs.getString("theme", "light");
        if (themePref.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 🌍 Kaydedilmiş dil tercihini uygula
        String savedLang = prefs.getString("language", "tr");
        LocaleHelper.setLocale(this, savedLang); // LocaleHelper yardımıyla dil değişimi

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔳 Kategorileri gösteren RecyclerView ayarı
        recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // 🔧 Adapter ayarı ve kategori tıklamasında ilgili alıntı sayfasına yönlendirme
        CategoryAdapter adapter = new CategoryAdapter(categoryList, category -> {
            Intent intent = new Intent(MainActivity.this, QuoteListActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // ❤️ Favoriler ekranına geçiş butonu
        Button favoriteButton = findViewById(R.id.favorite);
        favoriteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        // 🌗 Gece modu geçiş anahtarı
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(themePref.equals("dark"));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putString("theme", "dark");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putString("theme", "light");
            }
            editor.apply();
        });

        // 🔄 DİL DEĞİŞTİRME BUTONU: tr ↔ en arasında geçiş sağlar
        Button btnChangeLanguage = findViewById(R.id.btnChangeLanguage);
        btnChangeLanguage.setOnClickListener(v -> {
            String currentLang = prefs.getString("language", "tr"); // Şu anki dil
            String newLang = currentLang.equals("tr") ? "en" : "tr"; // Yeni dil

            prefs.edit().putString("language", newLang).apply(); // Yeni dili kaydet
            LocaleHelper.setLocale(this, newLang); // Dili uygulamaya bildir
            recreate(); // Ekranı yeniden yarat, dil değişikliği anında geçerli olsun
        });
    }
}
