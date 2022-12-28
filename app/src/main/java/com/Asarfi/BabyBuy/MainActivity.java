package com.Asarfi.BabyBuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ProductRV.ProductClickInterface,View.OnClickListener {

    private FloatingActionButton addProduct;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView productRV;
//    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    private ArrayList<ProductModal> productModalArrayList;
    private ProductRV productAdapter;
    private RelativeLayout bottom_sheet;
    ProductModal modal;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productRV = findViewById(R.id.RVProducts);
        bottom_sheet = findViewById(R.id.idRLBSheet);
        loadingPB = findViewById(R.id.PBLoading);
        addProduct = findViewById(R.id.AddProduct);
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        mAuth = FirebaseAuth.getInstance();
        productModalArrayList = new ArrayList<>();
        productRV.setHasFixedSize(true);
        productRV.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = firebaseDatabase.getReference("Products");

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(i);
            }
        });

        productAdapter = new ProductRV(productModalArrayList, this, this::onProductClick);
        productRV.setAdapter(productAdapter);
        loadingPB.setVisibility(View.INVISIBLE);
        getProducts();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(productRV);

    }


    private void getProducts() {
        productModalArrayList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                productModalArrayList.add(snapshot.getValue(ProductModal.class));
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                productAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // notifying adapter when child is moved.
                productAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingPB.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public void onProductClick(int position) {
        modal = productModalArrayList.get(position);
        displayBottomSheet(productModalArrayList.get(position));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.idLogOut) {
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void displayBottomSheet(ProductModal modal) {
        // creating bottom sheet dialog
        final BottomSheetDialog btmSheetDialog = new BottomSheetDialog(this);

        // inflating layout file for bottom sheet
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, bottom_sheet);
        // setting content view for bottom sheet
        btmSheetDialog.setContentView(layout);
        //setting a cancelable
        btmSheetDialog.setCancelable(false);
        btmSheetDialog.setCanceledOnTouchOutside(true);
        btmSheetDialog.show();

        TextView productPurchased = btmSheetDialog.findViewById(R.id.TVProductPurchased);
        TextView productNameTV = btmSheetDialog.findViewById(R.id.TVProductName);
        TextView productDescTV = btmSheetDialog.findViewById(R.id.TVProductDesc);
        TextView suitedForTV = btmSheetDialog.findViewById(R.id.TVSuitedFor);
        TextView priceTV = btmSheetDialog.findViewById(R.id.TVProductPrice);
        ImageView productIV = btmSheetDialog.findViewById(R.id.IVProduct);
        Button shareBtn = btmSheetDialog.findViewById(R.id.BtnShare);
        Button editBtn = btmSheetDialog.findViewById(R.id.BtnEditProduct);

        // setting data for views
        productNameTV.setText(modal.getProductName());
        priceTV.setText("Price - Rs." + modal.getProductPrice());
        suitedForTV.setText("Suited For - " + modal.getProductSuited());
        productDescTV.setText("Details - " + modal.getProductDesc());

       Picasso.get().load(modal.getProductImg()).into(productIV);

        if (modal.Purchased()) {
            productPurchased.setVisibility(View.VISIBLE);
        } else {
            productPurchased.setVisibility(View.INVISIBLE);
        }

//         edit
        assert editBtn != null;
        editBtn.setOnClickListener(this);
        assert shareBtn != null;
        shareBtn.setOnClickListener(this);

        // adding on click listener for edit button
//        editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // opening  Edit    ProductActivity
//                Intent i = new Intent(MainActivity.this, EditProductActivity.class);
//                // passing product modal
//                i.putExtra("product", modal);
//                startActivity(i);
//            }
//        });


//        shareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
//                String message = "[ name: " + modal.getProductName() + ", Description: " + modal.getProductDesc() + ", price: " + modal.getProductPrice()+"]";
//
//                smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(null, null, message, null, null);
//                Toast.makeText(MainActivity.this,"Successfully shared", Toast.LENGTH_SHORT).show();
//
//                // opening  Edit    ProductActivity
//                Intent i = new Intent(MainActivity.this, MainActivity.class);
//                // passing product modal
//                i.putExtra("product", modal);
//                startActivity(i);
//            }
//        });

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.BtnEditProduct:
                Intent edit = new Intent(MainActivity.this, EditProductActivity.class);
                edit.putExtra("product", modal);
                startActivity(edit);
                break;
            case R.id.BtnShare:
                Intent share = new Intent(MainActivity.this, SmsActivity.class);
                share.putExtra("product", modal);
                startActivity(share);
                finish();
                break;
        }

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            String productId = productModalArrayList.get(position).getProductID();

            switch (direction) {

                case ItemTouchHelper.LEFT:
                    loadingPB.setVisibility(View.VISIBLE);
                    DatabaseReference newDatabaseRef = FirebaseDatabase.getInstance().getReference("Products");
                    newDatabaseRef.child(productId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingPB.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Delete successful", Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed Deletion", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
                case ItemTouchHelper.RIGHT:
                    loadingPB.setVisibility(View.VISIBLE);

                    Map<String, Object> purchasedStatus = new HashMap<>();
                    purchasedStatus.put("purchased", true);
                    DatabaseReference updateDBRef = FirebaseDatabase.getInstance().getReference("Products");
                    updateDBRef.child(productId).updateChildren(purchasedStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingPB.setVisibility(View.INVISIBLE);
                            if(task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Purchase successful", Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);

                            } else {
                                Toast.makeText(MainActivity.this, "Purchase failed ", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    break;
            }

        }
    };



}