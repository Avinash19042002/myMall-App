package com.example.android.mymall.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.mymall.Interface.ItemClickListener;
import com.example.android.mymall.R;

import org.jetbrains.annotations.NotNull;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductNameCart,txtProductPriceCart;
    public ItemClickListener cart_itemClickListener;
    public CartViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        txtProductNameCart= itemView.findViewById(R.id.cart_product_name);
        txtProductPriceCart = itemView.findViewById(R.id.cart_product_price);

    }

    @Override
    public void onClick(View v) {
      cart_itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.cart_itemClickListener = itemClickListener;
    }
}
