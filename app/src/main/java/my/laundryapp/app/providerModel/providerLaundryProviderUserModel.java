package my.laundryapp.app.providerModel;

public class providerLaundryProviderUserModel {

    private String Capital,ConfirmPassword,Email,Name,Password,PhoneNumber,laundryProviderUid;

    public providerLaundryProviderUserModel() {
    }

    public providerLaundryProviderUserModel(String capital, String confirmPassword, String email, String name, String password, String phoneNumber, String laundryProviderUid) {
        Capital = capital;
        ConfirmPassword = confirmPassword;
        Email = email;
        Name = name;
        Password = password;
        PhoneNumber = phoneNumber;
        this.laundryProviderUid = laundryProviderUid;
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

    public String getLaundryProviderUid() {
        return laundryProviderUid;
    }

    public void setLaundryProviderUid(String laundryProviderUid) {
        this.laundryProviderUid = laundryProviderUid;
    }
}
