package com.Asarfi.BabyBuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    Button addProductBtn;
    private TextInputEditText ProductName, ProductDesc, ProductPrice, ProductSuited;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    StorageReference storageReference;
    ProductModal modal;
    Uri productImage;
    StorageTask uploadTask;
    private String productID;
    private final int GALLERY_REQ_CODE = 1;
    ImageView uploadImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        uploadImageView = findViewById(R.id.uploadImageView);
        addProductBtn = findViewById(R.id.BtnAddProduct);
        ProductName = findViewById(R.id.ProductName);
        ProductDesc = findViewById(R.id.ProductDescription);
        ProductPrice = findViewById(R.id.ProductPrice);
        ProductSuited = findViewById(R.id.SuitedFor);
        loadingPB = findViewById(R.id.PBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();

        uploadImageView.setOnClickListener(this);
        storageReference = FirebaseStorage.getInstance().getReference("Products");
        addProductBtn.setOnClickListener(this);

        databaseReference = firebaseDatabase.getReference("Products");

    }

    // get file extension
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.BtnAddProduct:
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(this, "Upload is in progress", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (productImage != null) {

                    // add unique name to file so that same file would not be overwrite
                    StorageReference fileRef = storageReference
                            .child(System.currentTimeMillis() + "."+ getFileExtension(productImage));

                    uploadTask = fileRef.putFile(productImage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    // to delay the progressbar
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingPB.setProgress(0);
                                        }
                                    }, 500);
                                    Toast.makeText(AddProductActivity.this, "Add successful", Toast.LENGTH_LONG).show();

                                    // get download url
                                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            uploadTextualData(taskSnapshot, uri);
                                        }
                                    });
                                    // work with other textual data

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progress = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                    loadingPB.setProgress((int)progress);

                                }
                            });

                } else {
                    Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.uploadImageView:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQ_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE && data != null && data.getData() !=null) {
                // for image gallery
                productImage = data.getData();
                uploadImageView.setImageURI(productImage);
            }
        }
    }


    public void uploadTextualData(UploadTask.TaskSnapshot taskSnapshot,  Uri uri) {

        String productName = ProductName.getText().toString().trim();
        String productDesc = ProductDesc.getText().toString().trim();
        String productPrice = ProductPrice.getText().toString().trim();
        String productSuited = ProductSuited.getText().toString().trim();
        productID = productName;

        if (TextUtils.isEmpty(productSuited) || TextUtils.isEmpty(productDesc) || TextUtils.isEmpty(productPrice) || TextUtils.isEmpty(productName)) {
            Toast.makeText(this, "Some of the fields are empty", Toast.LENGTH_SHORT).show();
        } else {
            ProductModal modal = new ProductModal(productID, productName,
                    productPrice, productSuited, productDesc, uri.toString(), false);
            databaseReference.child(productID).setValue(modal).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddProductActivity.this, "Add successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddProductActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AddProductActivity.this, "Failed adding", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }



}