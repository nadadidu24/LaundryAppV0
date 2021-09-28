package my.laundryapp.app.Model;

public class ShippingOrderModel {

    private String key, runnerPhone,runnerName;
    private double currentLat,currentLng;
    private Order orderModel;
    private boolean isStartTrip;

    public ShippingOrderModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRunnerPhone() {
        return runnerPhone;
    }

    public void setRunnerPhone(String runnerPhone) {
        this.runnerPhone = runnerPhone;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public Order getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(Order orderModel) {
        this.orderModel = orderModel;
    }

    public boolean isStartTrip() {
        return isStartTrip;
    }

    public void setStartTrip(boolean startTrip) {
        isStartTrip = startTrip;
    }
}
