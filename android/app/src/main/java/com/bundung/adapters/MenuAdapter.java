package com.bundung.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bundung.R;
import com.bundung.models.MenuItem;
import com.bundung.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onQuantityChanged(MenuItem item, int newQuantity);
    }

    public MenuAdapter(Context context, OnMenuItemClickListener listener) {
        this.context = context;
        this.menuItems = new ArrayList<>();
        this.listener = listener;
    }

    public void setMenuItems(List<MenuItem> items) {
        this.menuItems = items;
        notifyDataSetChanged();
    }

    public List<MenuItem> getSelectedItems() {
        List<MenuItem> selected = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (item.getQuantity() > 0) {
                selected.add(item);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(Constants.formatPrice(item.getPrice()));
        holder.tvCategory.setText(item.getCategory());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        
        // Note field
        holder.etNote.setText(item.getNote());
        if (item.getQuantity() > 0) {
            holder.etNote.setVisibility(View.VISIBLE);
        } else {
            holder.etNote.setVisibility(View.GONE);
        }
        
        // Plus button
        holder.btnPlus.setOnClickListener(v -> {
            item.incrementQuantity();
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            holder.etNote.setVisibility(View.VISIBLE);
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity());
            }
        });
        
        // Minus button
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.decrementQuantity();
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                if (item.getQuantity() == 0) {
                    holder.etNote.setVisibility(View.GONE);
                    item.setNote("");
                    holder.etNote.setText("");
                }
                if (listener != null) {
                    listener.onQuantityChanged(item, item.getQuantity());
                }
            }
        });
        
        // Note change listener
        holder.etNote.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                item.setNote(holder.etNote.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvCategory, tvQuantity;
        Button btnPlus, btnMinus;
        EditText etNote;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivMenuImage);
            tvName = itemView.findViewById(R.id.tvMenuName);
            tvPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvCategory = itemView.findViewById(R.id.tvMenuCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            etNote = itemView.findViewById(R.id.etNote);
        }
    }
}