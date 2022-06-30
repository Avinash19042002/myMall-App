package com.example.android.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {
private Button applyChangesBtn,deleteBtn;
private EditText name,price,description;
private ImageView imageView;
    private String productId="";
    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productId = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        applyChangesBtn= findViewById(R.id.apply_changes_btn);
        deleteBtn = findViewById(R.id.delete_product_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }


        });

        name=findViewById(R.id.product_name_maintain);
        price=findViewById(R.id.product_price_maintain);


        description=findViewById(R.id.product_description_maintain);
        imageView=findViewById(R.id.product_image_maintain);
        
        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }


        });
    }
    private void deleteThisProduct() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
              if(task.isSuccessful()){
                  startActivity(new Intent(AdminMaintainProductsActivity.this,AdminCategoryActivity.class));
                  finish();
                  Toast.makeText(AdminMaintainProductsActivity.this,"Product Deleted Successfully",Toast.LENGTH_SHORT).show();
              }
            }
        });
    }


    private void applyChanges() {
        String pName= name.getText().toString();
        String pPrice= price.getText().toString();
        String pDescription= description.getText().toString();

        if(pName.equals("")){
            Toast.makeText(AdminMaintainProductsActivity.this,"write product name",Toast.LENGTH_SHORT).show();
        }
        else if(pPrice.equals("")){
            Toast.makeText(AdminMaintainProductsActivity.this,"write product price",Toast.LENGTH_SHORT).show();
        }
        else if(pDescription.equals("")){
            Toast.makeText(AdminMaintainProductsActivity.this,"write product description",Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productId);
            productMap.put("description", pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                 if(task.isSuccessful()){
                     Toast.makeText(AdminMaintainProductsActivity.this,"Changes Applied Successfully",Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(AdminMaintainProductsActivity.this,AdminCategoryActivity.class));
                     finish();
                 }
                }
            });
        }
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
              if(snapshot.exists()){
                  String pName= snapshot.child("pname").getValue().toString();
                  String pPrice= snapshot.child("price").getValue().toString();
                  String pDescription= snapshot.child("description").getValue().toString();
                  String pImage= snapshot.child("image").getValue().toString();

                  name.setText(pName);
                  price.setText(pPrice);
                  description.setText(pDescription);
                  Picasso.get().load(pImage).into(imageView);
              }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}