package com.example.kelimeihikmet.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelimeihikmet.R;
import com.example.kelimeihikmet.data.Quote;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    private List<Quote> quoteList; // 🔹 Quote nesnelerini tutan liste
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // 🔥 Firestore bağlantısı
    private boolean isEnglish = false; // 🌍 İngilizce gösterim durumu (false = Türkçe, true = İngilizce)

    // 🔹 Constructor - Quote listesini alır
    public QuoteAdapter(List<Quote> quoteList) {
        this.quoteList = quoteList;
    }

    // 🌐 Dil geçişi için dışarıdan çağrılabilecek metod
    public void setLanguage(boolean isEnglish) {
        this.isEnglish = isEnglish; // İngilizce gösterilsin mi?
        notifyDataSetChanged(); // Tüm listeyi yenile
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🔹 Her bir kart görünümünü oluşturur
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        // 🔹 İlgili pozisyondaki Quote nesnesini al
        Quote quote = quoteList.get(position);

        // 🌐 Kullanıcının seçtiği dile göre gösterilecek metni belirle
        String displayText;
        if (isEnglish) {
            displayText = (quote.getTextEn() != null && !quote.getTextEn().isEmpty())
                    ? quote.getTextEn()
                    : quote.getText();
        } else {
            displayText = quote.getText();
        }

        // 🖋️ Metni ve yazarı ViewHolder'a yaz
        holder.textViewQuote.setText("\"" + displayText + "\"");
        holder.textViewAuthor.setText("- " + quote.getAuthor());

        // ❤️ Favori simgesini güncelle
        updateFavoriteIcon(holder.buttonFavorite, quote.isFavorite());

        // 🔁 Favori butonuna tıklanınca durum değiştir
        holder.buttonFavorite.setOnClickListener(v -> {
            boolean newState = !quote.isFavorite(); // ✅ Yeni durum belirleniyor
            quote.setFavorite(newState); // 📝 Durum güncelleniyor
            updateFavoriteIcon(holder.buttonFavorite, newState);
            notifyItemChanged(position); // ♻️ UI yenileniyor

            // 🔥 Favori durumu Firestore'da quote ID üzerinden güncelleniyor
            if (quote.getId() != null) {
                db.collection("quotes").document(quote.getId())
                        .update("isFavorite", newState)
                        .addOnSuccessListener(aVoid ->
                                Log.d("QuoteAdapter", "Favori güncellendi: " + newState))
                        .addOnFailureListener(e ->
                                Log.e("QuoteAdapter", "Firestore hatası: " + e.getMessage()));
            } else {
                Log.e("QuoteAdapter", "Quote ID bulunamadı, güncelleme yapılamadı.");
            }
        });

    }

    @Override
    public int getItemCount() {
        return quoteList.size(); // 🔢 Liste uzunluğu
    }

    // ❤️ Favori ikonunu ayarlayan yardımcı metod
    private void updateFavoriteIcon(ImageButton button, boolean isFavorite) {
        button.setImageResource(isFavorite
                ? R.drawable.ic_favorite // Dolu kalp
                : R.drawable.ic_favorite_border); // Boş kalp
    }

    // 📦 ViewHolder sınıfı - her bir öğeyi temsil eder
    public static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewQuote, textViewAuthor;
        ImageButton buttonFavorite;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuote = itemView.findViewById(R.id.textViewQuote); // Alıntı metni
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor); // Yazar
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite); // Kalp butonu
        }
    }
}
