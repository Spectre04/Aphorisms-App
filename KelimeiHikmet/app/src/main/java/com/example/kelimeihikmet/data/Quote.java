package com.example.kelimeihikmet.data;

import com.google.firebase.firestore.PropertyName; // 🔧 Firestore ile alan adlarını eşlemek için

public class Quote {

    // 🔹 Türkçe söz metni
    private String text;

    // 🔹 İngilizce söz metni (Firestore'da "text_en" ama burada camelCase)
    private String textEn;

    // 🔹 Yazar adı
    private String author;

    // 🔹 Kategori (örneğin: Barış, Adalet)
    private String category;

    // 🔹 Kategori anahtar değeri (örnek: category_baris)
    private String categoryKey;

    // 🔹 Favori olup olmadığını tutan bayrak
    private boolean isFavorite;

    // 🔹 Firestore doküman ID'sini saklamak için alan
    private String id;

    // 🛠️ Boş yapıcı (Firestore tarafından otomatik kullanılır)
    public Quote() {}

    // 🛠️ Parametreli yapıcı (manuel oluşturulan nesneler için)
    public Quote(String text, String textEn, String author, String category, String categoryKey, boolean isFavorite) {
        this.text = text;
        this.textEn = textEn;
        this.author = author;
        this.category = category;
        this.categoryKey = categoryKey;
        this.isFavorite = isFavorite;
    }

    // 📌 GETTER ve SETTER METOTLARI

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // 🌐 "textEn" alanı Firestore'daki "text_en" alanıyla eşleştirilir
    @PropertyName("text_en")
    public String getTextEn() {
        return textEn;
    }

    @PropertyName("text_en")
    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // 🔧 Firestore'daki belge ID'si (opsiyonel ama kullanışlı)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
