package my.laundryapp.app.Model;

public class CustomerModel {
    private String Capital,ConfirmPassword,Email,Name,Password,PhoneNumber,custUid,Address;
    private double lat,lng;



    public CustomerModel() {
    }

    public CustomerModel(String capital, String confirmPassword, String email, String name, String password, String phoneNumber, String custUid, String address) {
        Capital = capital;
        ConfirmPassword = confirmPassword;
        Email = email;
        Name = name;
        Password = password;
        PhoneNumber = phoneNumber;
        this.custUid = custUid;
        Address = address;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCustUid() {
        return custUid;
    }

    public void setCustUid(String custUid) {
        this.custUid = custUid;
    }

    public String getCapital() {
        return Capital;
    }

    public void setCapital(String capital) {
        Capital = capital;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        ConfirmPassword = confirmPassword;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
