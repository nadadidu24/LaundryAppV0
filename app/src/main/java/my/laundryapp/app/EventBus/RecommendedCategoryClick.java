package my.laundryapp.app.EventBus;

import my.laundryapp.app.Model.RecommendedServicesModel;

public class RecommendedCategoryClick {
    private RecommendedServicesModel recommendedServicesModel;

    public RecommendedCategoryClick(RecommendedServicesModel recommendedServicesModel) {
        this.recommendedServicesModel = recommendedServicesModel;
    }

    public RecommendedServicesModel getRecommendedServicesModel() {
        return recommendedServicesModel;
    }

    public void setRecommendedServicesModel(RecommendedServicesModel recommendedServicesModel) {
        this.recommendedServicesModel = recommendedServicesModel;
    }
}
