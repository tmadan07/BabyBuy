package com.Asarfi.BabyBuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private TextInputEditText ProductName, ProductDesc, ProductPrice, ProductSuited;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private String productID;
    ProductModal productModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Button addProduct = findViewById(R.id.BtnAdd);
        ProductName = findViewById(R.id.ProductName);
        ProductDesc = findViewById(R.id.ProductDescription);
        ProductPrice = findViewById(R.id.ProductPrice);
        ProductSuited = findViewById(R.id.SuitedFor);
        loadingPB = findViewById(R.id.PBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();

        productModal = getIntent().getParcelableExtra("product");

        if (productModal != null) {
            //setting data to our edit text from modal class
            ProductName.setText(productModal.getProductName());
            ProductDesc.setText(productModal.getProductDesc());
            ProductPrice.setText(productModal.getProductPrice());
            ProductSuited.setText(productModal.getProductSuited());
            productID = productModal.getProductID();
        }

        //initialing database reference and adding a child as product id.
        databaseReference = firebaseDatabase.getReference("Products").child(productID);
        // on below line we are adding click listener for our add course button.
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // making progress bar as visible.
                loadingPB.setVisibility(View.VISIBLE);

                String productName = ProductName.getText().toString();
                String productDesc = ProductDesc.getText().toString();
                String productPrice = ProductPrice.getText().toString();
                String productSuited = ProductSuited.getText().toString();

                // creating a map for passing a data using key and value pair.
                Map<String, Object> map = new HashMap<>();
                map.put("productName", productName);
                map.put("productDescription", productDesc);
                map.put("productPrice", productPrice);
                map.put("productSuitedFor", productSuited);
                map.put("productID", productID);


                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingPB.setVisibility(View.GONE);
                        databaseReference.updateChildren(map);
                        Toast.makeText(EditProductActivity.this, "Product Updated..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditProductActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // displaying a failure message on toast
                        Toast.makeText(EditProductActivity.this, "Fail to update product..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}