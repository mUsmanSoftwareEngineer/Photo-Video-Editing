package collagestudio.photocollage.collagemaker.cross_promotion;

public class SliderResponse {

    private String slideImage;
    private String appUrl;

    public SliderResponse(String slideImage, String appUrl) {
        this.slideImage = slideImage;
        this.appUrl = appUrl;
    }

    public String getSlideImage() {
        return slideImage;
    }

    public void setSlideImage(String slideImage) {
        this.slideImage = slideImage;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
}
