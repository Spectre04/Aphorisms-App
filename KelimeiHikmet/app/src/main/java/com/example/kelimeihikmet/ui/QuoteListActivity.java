package com.example.kelimeihikmet.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class QuoteListActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // 🔁 Sözleri gösterecek olan RecyclerView
    private TextView emptyText; // 📭 Boşsa gösterilecek metin
    private QuoteAdapter adapter; // 🔗 Adapter
    private final ArrayList<Quote> quoteList = new ArrayList<>(); // 🔗 Veri kaynağı

    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // ☁️ Firestore bağlantısı
    private String categoryKey; // 🏷 Kategori anahtarı

    private boolean isEnglish; // 🌍 Aktif dil durumu (true ise İngilizce)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_list); // 📄 Layout'u bağla

        // 🔄 Dil tercihine göre Locale ayarla
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String savedLang = prefs.getString("language", "tr");
        LocaleHelper.setLocale(this, savedLang); // 🌍 Dil değişikliğini uygula
        isEnglish = savedLang.equals("en"); // ✅ İngilizce mi kontrol et

        // 📥 MainActivity'den gelen kategori key alınır
        categoryKey = getIntent().getStringExtra("category");

        // 🎭 Kullanıcıya gösterilecek kategori adı
        int resId = getResources().getIdentifier(categoryKey, "string", getPackageName());
        String visibleCategory = resId != 0 ? getString(resId) : categoryKey;
        Log.d("QuoteListActivity", "Gelen kategori key: " + categoryKey + ", Gösterilecek: " + visibleCategory);

        // 🔗 XML bileşenlerini tanımla
        recyclerView = findViewById(R.id.recyclerViewQuotes);
        emptyText = findViewById(R.id.textEmptyQuotes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 🧱 Dikey liste düzeni

        // 🧠 Adapter oluşturuluyor ve dil durumu veriliyor
        adapter = new QuoteAdapter(quoteList);
        adapter.setLanguage(isEnglish); // 🌍 Dil durumu adapter'a iletiliyor
        recyclerView.setAdapter(adapter);

        // ✅ Geçerli kategori varsa veri çek
        if (categoryKey != null && !categoryKey.isEmpty()) {
            loadQuotesByCategory(); // 🔄 Verileri yükle
        } else {
            Toast.makeText(this, R.string.category_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🔄 Sayfaya geri dönüldüğünde verileri tazele
        if (categoryKey != null && !categoryKey.isEmpty()) {
            loadQuotesByCategory();
        }
    }

    // 📦 Firestore'dan kategoriye göre sözleri çeken metod
    private void loadQuotesByCategory() {
        db.collection("quotes")
                .whereEqualTo("categoryKey", categoryKey)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    quoteList.clear(); // 🔄 Eski veriler temizlenir

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Quote quote = new Quote();

                        quote.setId(doc.getId()); // 📄 Firestore döküman ID
                        quote.setText(doc.getString("text")); // 📝 Türkçe söz
                        quote.setTextEn(doc.getString("text_en")); // 🌐 İngilizce söz
                        quote.setAuthor(doc.getString("author"));
                        quote.setCategory(doc.getString("category"));
                        quote.setCategoryKey(doc.getString("categoryKey"));
                        quote.setFavorite(Boolean.TRUE.equals(doc.getBoolean("isFavorite")));

                        quoteList.add(quote); // 📥 Listeye ekle
                    }

                    adapter.notifyDataSetChanged(); // 🔄 Listeyi yenile

                    // 🕳 Liste boşsa mesaj göster
                    if (quoteList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyText.setText(R.string.no_quotes_found);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.GONE);
                    }

                    Log.d("QuoteListActivity", "Toplam söz: " + quoteList.size());
                })
                .addOnFailureListener(e -> {
                    Log.e("QuoteListActivity", "Veri çekme hatası: " + e.getMessage());
                    Toast.makeText(this, "Veri alınamadı", Toast.LENGTH_SHORT).show();
                });
    }
}
