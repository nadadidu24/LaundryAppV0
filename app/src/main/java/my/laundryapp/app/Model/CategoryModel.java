package my.laundryapp.app.Model;

import java.util.List;

public class CategoryModel {
    private String catalog_id,name,image;
    List<LaundryServicesModel> services;

    public CategoryModel() {
    }

    public String getCatalog_id() {
        return catalog_id;
    }

    public void setCatalog_id(String catalog_id) {
        this.catalog_id = catalog_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<LaundryServicesModel> getServices() {
        return services;
    }

    public void setServices(List<LaundryServicesModel> services) {
        this.services = services;
    }
}
