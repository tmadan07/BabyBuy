package com.Asarfi.BabyBuy;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductModal implements Parcelable {

     private String productName;
   private  String productDesc;
    private String productPrice;
    private String productSuited;
    private String productImg;
    private String productID;
    private boolean isPurchased;

    public boolean Purchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        this.isPurchased = purchased;
    }

    public ProductModal(){

    }

    public String getProductName() {
        return productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductSuited() {
        return productSuited;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductSuited(String productSuited) {
        this.productSuited = productSuited;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductID() {
        return productID;
    }

    protected ProductModal(Parcel in) {
        productName = in.readString();
        productID = in.readString();
        productDesc = in.readString();
        productPrice = in.readString();
        productSuited = in.readString();
        productImg = in.readString();
        isPurchased = in.readByte() != 0;
    }

    public static final Creator<ProductModal> CREATOR = new Creator<ProductModal>() {
        @Override
        public ProductModal createFromParcel(Parcel in) {
            return new ProductModal(in);
        }

        @Override
        public ProductModal[] newArray(int size) {
            return new ProductModal[size];
        }
    };

    public ProductModal(String productID, String productName, String productDesc, String productPrice, String productSuited, String image, Boolean purchased) {
        this.productName = productName;
        this.productID = productID;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.productSuited = productSuited;
        this.productImg = image;
        this.isPurchased = purchased;
    }

    @Override
    public String toString() {
        return "ProductModal{" +
                "productName='" + productName + '\'' +
                ", productDesc='" + productDesc + '\'' +
                ", productPrice='" + productPrice + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(productName);
        dest.writeString(productID);
        dest.writeString(productDesc);
        dest.writeString(productPrice);
        dest.writeString(productSuited);
        dest.writeString(productImg);
        dest.writeByte((byte) (isPurchased ? 1 : 0));
    }
}
