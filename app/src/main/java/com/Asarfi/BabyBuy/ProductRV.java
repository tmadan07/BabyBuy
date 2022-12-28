package com.Asarfi.BabyBuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductRV extends RecyclerView.Adapter<ProductRV.ProductViewHolder>{

    private ArrayList<ProductModal> productModalArrayList;
    private Context context;
    private ProductClickInterface productClickInterface;
    int lastPos = -1;

    // creating a constructor.
    public ProductRV(ArrayList<ProductModal> productModalList, Context context, ProductClickInterface productClickInterface) {
        this.productModalArrayList = productModalList;
        this.context = context;
        this.productClickInterface = productClickInterface;
    }

    @NonNull
    @Override
    public ProductRV.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_rv, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ProductModal productModal = productModalArrayList.get(position);
        holder.productTV.setText(productModal.getProductName());
        holder.productPriceTV.setText("Rs. " + productModal.getProductPrice());
        Picasso.get().load(productModal.getProductImg()).into(holder.productIV);
        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productClickInterface.onProductClick(position);
            }
        });
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            // setting animation.
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }


        @Override
    public int getItemCount() {
        return productModalArrayList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productIV;
        private TextView productTV, productPriceTV;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing all our variables
            productIV = itemView.findViewById(R.id.IVProduct);
            productTV = itemView.findViewById(R.id.TVProductName);
            productPriceTV = itemView.findViewById(R.id.TVProductPrice);
        }
    }


    // creating a interface
    public interface ProductClickInterface {
        void onProductClick(int position);
    }
}
