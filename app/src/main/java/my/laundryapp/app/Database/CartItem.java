package my.laundryapp.app.Database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity(tableName = "Cart",primaryKeys = {"custUid","categoryId","servicesId","servicesAddon","servicesSize"})

public class CartItem {

    @NonNull
    @ColumnInfo(name = "categoryId")
    private String categoryId;

    @NonNull
    @ColumnInfo(name = "servicesId")
    private String servicesId;

    @ColumnInfo(name = "servicesName")
    private String servicesName;

    @ColumnInfo(name = "servicesImage")
    private String servicesImage;

    @ColumnInfo(name = "servicesPrice")
    private Double servicesPrice;

    @ColumnInfo(name = "servicesQuantity")
    private int servicesQuantity;

    @ColumnInfo(name = "custPhone")
    private String custPhone;

    @ColumnInfo(name = "servicesExtraPrice")
    private Double servicesExtraPrice;

    @NonNull
    @ColumnInfo(name = "servicesAddon")
    private String servicesAddon;

    @NonNull
    @ColumnInfo(name = "servicesSize")
    private String servicesSize;

    @NonNull
    @ColumnInfo(name = "custUid")
    private String custUid;

    @NonNull
    public String getServicesId() {
        return servicesId;
    }

    public void setServicesId(@NonNull String servicesId) {
        this.servicesId = servicesId;
    }

    public String getServicesName() {
        return servicesName;
    }

    public void setServicesName(String servicesName) {
        this.servicesName = servicesName;
    }

    public String getServicesImage() {
        return servicesImage;
    }

    public void setServicesImage(String servicesImage) {
        this.servicesImage = servicesImage;
    }

    public Double getServicesPrice() {
        return servicesPrice;
    }

    public void setServicesPrice(Double servicesPrice) {
        this.servicesPrice = servicesPrice;
    }

    public int getServicesQuantity() {
        return servicesQuantity;
    }

    public void setServicesQuantity(int servicesQuantity) {
        this.servicesQuantity = servicesQuantity;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public Double getServicesExtraPrice() {
        return servicesExtraPrice;
    }

    public void setServicesExtraPrice(Double servicesExtraPrice) {
        this.servicesExtraPrice = servicesExtraPrice;
    }

    public String getServicesAddon() {
        return servicesAddon;
    }

    public void setServicesAddon(String servicesAddon) {
        this.servicesAddon = servicesAddon;
    }

    public String getServicesSize() {
        return servicesSize;
    }

    public void setServicesSize(String servicesSize) {
        this.servicesSize = servicesSize;
    }

    @NonNull
    public String getCustUid() {
        return custUid;
    }

    public void setCustUid(@NonNull String custUid) {
        this.custUid = custUid;
    }

    @NonNull
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        this.categoryId = categoryId;
    }

    //Ctrl + o


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof CartItem))
            return false;
        CartItem cartItem = (CartItem)obj;
        return cartItem.getServicesId().equals(this.servicesId) &&
                cartItem.getServicesAddon().equals(this.servicesAddon) &&
                cartItem.getServicesSize().equals(this.servicesSize);
    }
}
