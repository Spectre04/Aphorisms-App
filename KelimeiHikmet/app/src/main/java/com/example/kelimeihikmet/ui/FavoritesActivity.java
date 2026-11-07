package com.example.kelimeihikmet.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelimeihikmet.R;
import com.example.kelimeihikmet.adapter.QuoteAdapter;
import com.example.kelimeihikmet.data.Quote;
import com.example.kelimeihikmet.utils.LocaleHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // 🔁 Favori sözleri gösterecek RecyclerView
    private QuoteAdapter adapter; // 🔗 Adapter
    private final ArrayList<Quote> favoriteList = new ArrayList<>(); // 📦 Favori sözler listesi
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // ☁️ Firestore bağlantısı
    private boolean isEnglish; // 🌐 Dil tercihi (true ise İngilizce)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites); // 📄 Layout dosyasını bağla

        // 🌍 Dil ayarını oku
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String lang = prefs.getString("language", "tr");
        LocaleHelper.setLocale(this, lang); // 🌐 Locale'yi uygula
        isEnglish = lang.equals("en"); // ✅ İngilizce mi?

        // 🔗 RecyclerView ayarları
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🧠 Adapter oluşturuluyor ve RecyclerView'a bağlanıyor
        adapter = new QuoteAdapter(favoriteList);
        adapter.setLanguage(isEnglish); // 🌐 Dil durumu adapter'a iletiliyor
        recyclerView.setAdapter(adapter);

        loadFavorites(); // 📥 Favori sözleri yükle
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites(); // ♻️ Sayfa dönüldüğünde favorileri tazele
    }

    // 📦 Firestore'dan favori sözleri çek
    private void loadFavorites() {
        db.collection("quotes")
                .whereEqualTo("isFavorite", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteList.clear(); // 🧹 Eski listeyi temizle
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Quote quote = new Quote();
                        quote.setId(doc.getId()); // 📄 Firestore doküman ID'si
                        quote.setText(doc.getString("text")); // 📝 Türkçe metin
                        quote.setTextEn(doc.getString("text_en")); // 🌐 İngilizce metin
                        quote.setAuthor(doc.getString("author")); // 🖊 Yazar
                        quote.setCategory(doc.getString("category")); // 🏷 Kategori
                        quote.setCategoryKey(doc.getString("categoryKey")); // 🏷 Anahtar kategori
                        quote.setFavorite(Boolean.TRUE.equals(doc.getBoolean("isFavorite"))); // ❤️ Favori mi?

                        // 🌍 Eğer İngilizce görünüm açıksa İngilizce metni göster
                        if (isEnglish && quote.getTextEn() != null && !quote.getTextEn().isEmpty()) {
                            quote.setText(quote.getTextEn()); // 🔁 Text'i İngilizce olarak ayarla
                        }

                        favoriteList.add(quote); // 📥 Listeye ekle
                    }

                    adapter.notifyDataSetChanged(); // 🔄 RecyclerView'u güncelle
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Favoriler alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
