package my.laundryapp.app.Model;

public class RecommendedServicesModel {
    private String catalog_id,services_id,name,image;

    public RecommendedServicesModel() {
    }

    public RecommendedServicesModel(String catalog_id, String services_id, String name, String image) {
        this.catalog_id = catalog_id;
        this.services_id = services_id;
        this.name = name;
        this.image = image;
    }

    public String getCatalog_id() {
        return catalog_id;
    }

    public void setCatalog_id(String catalog_id) {
        this.catalog_id = catalog_id;
    }

    public String getServices_id() {
        return services_id;
    }

    public void setServices_id(String services_id) {
        this.services_id = services_id;
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
}
