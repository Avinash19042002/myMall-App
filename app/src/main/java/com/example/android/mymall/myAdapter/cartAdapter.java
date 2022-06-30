package com.example.android.mymall.myAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.mymall.Model.Cart;
import com.example.android.mymall.R;
import com.example.android.mymall.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.jetbrains.annotations.NotNull;

public class cartAdapter extends FirebaseRecyclerAdapter<Cart, CartViewHolder> {


    public cartAdapter(@NonNull @NotNull FirebaseRecyclerOptions<Cart> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int position, @NonNull @NotNull Cart model) {
holder.txtProductNameCart.setText(model.getPname());
holder.txtProductPriceCart.setText(model.getPrice());
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

    }
});
    }

    @NonNull
    @NotNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
       return new CartViewHolder(view);
    }
}
