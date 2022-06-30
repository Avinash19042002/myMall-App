package com.example.android.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.mymall.Model.AdminOrders;
import com.example.android.mymall.Model.Cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class AdminNewOrdersActivity extends AppCompatActivity {
private RecyclerView ordersList;
private DatabaseReference ordersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef, AdminOrders.class)
                        .build();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder>adapter= new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull AdminOrdersViewHolder holder, int position, @NonNull @NotNull AdminOrders model) {
             holder.userName.setText("Name: "+model.getName());
             holder.userPhoneNumber.setText("Phone: "+model.getPhone());
             holder.userTotalPrice.setText(model.getTotalAmount());
             holder.userShippingAddress.setText("Shipping Address: "+model.getAddress()+","+model.getCity());
             holder.userDateTime.setText("Ordered at: "+model.getDate()+" "+model.getTime());
             holder.ShowOrdersButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     String Uid = getRef(position).getKey();
                     Intent intent = new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
                     intent.putExtra("uid",Uid);
                     startActivity(intent);
                 }
             });
             holder.itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     CharSequence options[]=new CharSequence[]{
                       "Yes","No"
                     };
                     AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                     builder.setTitle("Have you shipped this order?");
                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int i) {
                           if(i==0){
                               String uid = getRef(position).getKey();
                               RemoveOrder(uid);
                           }
                           else {
                               finish();
                           }
                         }



                     }).show();
                 }
             });

            }

            @NonNull
            @NotNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                return new AdminOrdersViewHolder(view);
            }
        };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{
          public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress;
          public Button ShowOrdersButton;
        public AdminOrdersViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice= itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            ShowOrdersButton =itemView.findViewById(R.id.show_all_products_btn);
        }
    }
    private void RemoveOrder(String uid) {
        ordersRef.child(uid).removeValue();
    }



}