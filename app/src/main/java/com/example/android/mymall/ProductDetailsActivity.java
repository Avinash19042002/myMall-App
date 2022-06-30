package com.example.android.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mymall.Model.Products;
import com.example.android.mymall.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
public int quantity;
private TextView incr,decr;
private TextView show_value;

private TextView productPrice,productDescription,productName;
    private Button addToCartBtn;
private ImageView productImage;
private String productId="",state="Normal";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("pid");

  addToCartBtn = (Button)findViewById(R.id.add_product_to_cart_btn);
        productPrice = (TextView)findViewById(R.id.product_price_details);
        productDescription =(TextView)findViewById(R.id.product_description_details);
        productName =(TextView)findViewById(R.id.product_name_details);
        productImage =(ImageView)findViewById(R.id.product_image_details);


        incr = (TextView)findViewById(R.id.inc);
        decr = (TextView)findViewById(R.id.dec);
        show_value=(TextView)findViewById(R.id.quantity_val);
        incr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity<10)quantity++;
                show_value.setText(String.valueOf(quantity));
            }
        });
        decr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>0)quantity--;
                show_value.setText(String.valueOf(quantity));
            }
        });
        getProductDetails(productId);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state=="Order Placed"||state=="Order Shipped"){
                    Toast.makeText(ProductDetailsActivity.this,"You can purchase more prioducts when your previous orders is confirmed or shipped",Toast.LENGTH_LONG).show();
                }
                else {
                    addToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
    }

    private void addToCartList() {
        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

      final DatabaseReference cartListRef =FirebaseDatabase.getInstance().getReference().child("cartList");
        final HashMap<String,Object>cartMap = new HashMap<>();
        cartMap.put("pid",productId);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("discount","");
        String user = Prevalent.currentOnlineUser.getPhone();
        cartListRef.child("UserView").child(Prevalent.currentOnlineUser.getPhone()).child("products")
                .child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                   if(task.isSuccessful()){
                       cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("products")
                               .child(productId)
                               .updateChildren(cartMap)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull @NotNull Task<Void> task) {
                                      if(task.isSuccessful()){
                                          Toast.makeText(ProductDetailsActivity.this,"Product added to Cart",Toast.LENGTH_SHORT).show();
                                  Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                  startActivity(intent);
                                      }
                                   }
                               });
                                 }


                   }

                });


    }

    private void getProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Products products = snapshot.getValue(Products.class);
                        productName.setText(products.getPname());
                        productDescription.setText(products.getDescription());
                        productPrice.setText(products.getPrice());
                        Picasso.get().load(products.getImage()).into(productImage);
                    }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {


            }
        });
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
                      state = "Order Shipped";
                    }
                    else if(shippingState.equals("Not shipped")){
                      state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}