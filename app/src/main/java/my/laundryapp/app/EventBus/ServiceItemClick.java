package my.laundryapp.app.EventBus;

import my.laundryapp.app.Model.LaundryServicesModel;

public class ServiceItemClick {

    private boolean success;
    private LaundryServicesModel laundryServicesModel;

    public ServiceItemClick(boolean success, LaundryServicesModel laundryServicesModel) {
        this.success = success;
        this.laundryServicesModel = laundryServicesModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LaundryServicesModel getLaundryServicesModel() {
        return laundryServicesModel;
    }

    public void setLaundryServicesModel(LaundryServicesModel laundryServicesModel) {
        this.laundryServicesModel = laundryServicesModel;
    }
}
