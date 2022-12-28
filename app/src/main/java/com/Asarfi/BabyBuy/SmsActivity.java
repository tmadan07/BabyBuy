package com.Asarfi.BabyBuy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class SmsActivity extends AppCompatActivity {

    ProductModal productModal;
    TextView name, price, desc;
    Button sendSms;
    SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        // get view id
        name = findViewById(R.id.share_product_name);
        price = findViewById(R.id.share_product_price);
        desc = findViewById(R.id.share_product_desc);
        sendSms = findViewById(R.id.send_share_button);

        productModal = getIntent().getParcelableExtra("Products");
        if (productModal != null ) {
            name.setText(productModal.getProductName());
            price.setText(productModal.getProductPrice());
            desc.setText(productModal.getProductDesc());
        }

        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SmsActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
                String message = "[ name: " + productModal.getProductName() + ", Description: " + productModal.getProductDesc() + ", price: " + productModal.getProductPrice()+"]";

                smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(null, null, message, null, null);
                Toast.makeText(SmsActivity.this, "Successfully shared", Toast.LENGTH_SHORT).show();

                // direct to home
                startActivity(new Intent(SmsActivity.this, MainActivity.class));
                finish();

            }
        });

    }
}

