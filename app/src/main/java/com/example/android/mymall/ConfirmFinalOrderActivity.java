package com.example.android.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mymall.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfirmFinalOrderActivity extends AppCompatActivity implements PaymentResultListener {
private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
private Button confirmOrderBtn,payBtn;
private String totalAmount="";
private TextView paymentDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("TotalCost");


        paymentDetail = findViewById(R.id.payment_detail);
        payBtn = (Button)findViewById(R.id.pay_btn);

       Toast.makeText(ConfirmFinalOrderActivity.this,"Total cost = "+totalAmount,Toast.LENGTH_SHORT).show();
        payBtn.setText("You have to pay Rs."+totalAmount);

        nameEditText = findViewById(R.id.shippment_name);
        phoneEditText=findViewById(R.id.shippment_phone_number);
        addressEditText=findViewById(R.id.shippment_address);
        cityEditText=findViewById(R.id.shippment_city);
        confirmOrderBtn=findViewById(R.id.confirm_final_order_btn);
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
        confirmOrderBtn.setVisibility(View.GONE);

        paymentDetail.setVisibility(View.GONE);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    payment();
            }
        });

    }
    private void payment() {
        int amount = Math.round(Float.parseFloat(totalAmount) * 100);

                Checkout checkout = new Checkout();
              checkout.setKeyID("rzp_test_cBYi4AZakdT77z");

                checkout.setImage(R.drawable.mymalllogo);

                JSONObject object = new JSONObject();
                try {
                    object.put("name", "MyMall");
                    object.put("description", "Cart Payment");
                    object.put("theme.color", "");
                    object.put("amount", amount);
                    object.put("prefill.contact", phoneEditText.getText().toString());
                    object.put("prefill.email", "avinashdhn1904@gmail.com");
                    checkout.open(ConfirmFinalOrderActivity.this, object);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
    }
    @Override
    public void onPaymentSuccess(String s) {
        payBtn.setVisibility(View.GONE);
        confirmOrderBtn.setVisibility(View.VISIBLE);
        paymentDetail.setVisibility(View.VISIBLE);
        paymentDetail.setText("Order Successfully. Transaction No :"+s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        paymentDetail.setVisibility(View.VISIBLE);
        paymentDetail.setText("Something went wrong"+s);

    }

    private void check() {
        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this,"Please enter your full name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this,"Please enter your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this,"Please enter your address",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this,"Please enter your city name",Toast.LENGTH_SHORT).show();
        }
        else{
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
         final String saveCurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        final HashMap<String,Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",nameEditText.getText().toString());
        orderMap.put("phone",phoneEditText.getText().toString());
        orderMap.put("address",addressEditText.getText().toString());
        orderMap.put("city",cityEditText.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("state","Not shipped");
        orderMap.put("paydetails",paymentDetail.getText().toString());
        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                 if(task.isSuccessful()){
                     FirebaseDatabase.getInstance().getReference().child("cartList").child("UserView")
                             .child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull @NotNull Task<Void> task) {
                                     if(task.isSuccessful()){
                                         Toast.makeText(ConfirmFinalOrderActivity.this, "Order is placed succeefully", Toast.LENGTH_SHORT).show();
                                         Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(intent);
                                         finish();
                                     }
                                 }
                             });
                 }
            }
        });

    }
}