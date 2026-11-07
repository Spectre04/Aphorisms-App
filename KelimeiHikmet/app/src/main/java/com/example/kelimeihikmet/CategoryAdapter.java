package com.example.kelimeihikmet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryKey); // Firestore için KEY gönderilecek
    }

    private final List<String> categories; // Örn: "category_erdem"
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String categoryKey = categories.get(position); // Örn: "category_erdem"
        Context context = holder.itemView.getContext();

        // Çevrilmiş kategori adını göster
        int resId = context.getResources().getIdentifier(categoryKey, "string", context.getPackageName());
        String categoryDisplay = resId != 0 ? context.getString(resId) : categoryKey;

        holder.textViewCategory.setText(categoryDisplay);

        // Tıklandığında KEY gönderilecek
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(categoryKey));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
        }
    }
}
