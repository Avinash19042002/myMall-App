package com.example.android.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mymall.Model.Cart;
import com.example.android.mymall.Model.Products;
import com.example.android.mymall.Prevalent.Prevalent;
import com.example.android.mymall.ViewHolder.CartViewHolder;
import com.example.android.mymall.ViewHolder.ProductViewHolder;
import com.example.android.mymall.myAdapter.cartAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount,txtMsg1;
    private DatabaseReference cartListRef;
   private int overAllCost=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = (RecyclerView)findViewById(R.id.cart_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        cartListRef = FirebaseDatabase.getInstance().getReference().child("cartList").child("UserView").child(Prevalent.currentOnlineUser.getPhone()).child("products");
        nextProcessBtn = (Button)findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView)findViewById(R.id.total_price);
        txtMsg1= (TextView)findViewById(R.id.msg1);
       nextProcessBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               txtTotalAmount.setText("Total Price = Rs."+(String.valueOf(overAllCost)));
               Intent intent =new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
               intent.putExtra("TotalCost","Total Amount :Rs."+(String.valueOf(overAllCost)));
               startActivity(intent);
               finish();
           }
       });

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        overAllCost=0;
//    }
    //Display it outside nextButton Click lisner and also include


    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
        FirebaseRecyclerOptions<Cart> cartOptions =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef, Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart,CartViewHolder>adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(cartOptions) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int position, @NonNull @NotNull Cart model) {
                holder.txtProductNameCart.setText(model.getPname());
        holder.txtProductPriceCart.setText(model.getPrice());
       overAllCost=overAllCost+((Integer.valueOf(model.getPrice())));
       holder.itemView.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               CharSequence options[]= new CharSequence[]{
                       "Edit","Remove"
               };
               AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
               builder.setTitle("Cart Options");
               builder.setItems(options, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int i) {
                       if(i==0){
                           Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                           intent.putExtra("pid",model.getPid());
                           startActivity(intent);
                       }
                       if(i==1){
                        cartListRef.child(model.getPid()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     Toast.makeText(CartActivity.this,"Item removed Successfully",Toast.LENGTH_SHORT).show();
                                     Intent intent = new Intent(CartActivity.this,HomeActivity.class);

                                     startActivity(intent);
                                 }
                                    }
                                });
                       }
                   }
               }).show();
           }
       });

            }

            @NonNull
            @NotNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
             View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
             CartViewHolder holder = new CartViewHolder(view);
             return holder;
            }
        };
      //  txtTotalAmount.setText("Total Price = Rs."+(String.valueOf(overAllCost)));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
    private void checkOrderState(){
        DatabaseReference ordersRef;
        ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                String shippingState= snapshot.child("state").getValue().toString();
                 String userName = snapshot.child("name").getValue().toString();
                 if(shippingState.equals("shipped")){
                  txtTotalAmount.setText("Dear "+userName+"\nyour order has been shipped");
                  recyclerView.setVisibility(View.GONE);
                  txtMsg1.setVisibility(View.VISIBLE);
                  txtMsg1.setText("Congratulations!! your order is shipped successfully.Soon it will be at your doorStep");
                  nextProcessBtn.setVisibility(View.GONE);
                  Toast.makeText(CartActivity.this,"You can purchase more products once you recieved your previos order",Toast.LENGTH_SHORT).show();
                 }
                 else if(shippingState.equals("Not shipped")){
                     txtTotalAmount.setText("Your order is not shipped yet");
                     recyclerView.setVisibility(View.GONE);
                     txtMsg1.setVisibility(View.VISIBLE);
                     nextProcessBtn.setVisibility(View.GONE);
                     Toast.makeText(CartActivity.this,"You can purchase more products once you recieved your previos order",Toast.LENGTH_SHORT).show();
                 }
            }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}