package collagestudio.photocollage.collagemaker.cross_promotion;

public class LoadPromotionResponse {

    private String appIconStr;
    private String appTitle;
    private String appDescription;
    private String url;
    private String appCoverImage;

    public LoadPromotionResponse() {

    }

    public LoadPromotionResponse(String appIconStr, String appTitle, String appDescription, String url, String appCoverImage) {
        this.appIconStr = appIconStr;
        this.appTitle = appTitle;
        this.appDescription = appDescription;
        this.url = url;
        this.appCoverImage = appCoverImage;
    }

    public String getAppIconStr() {
        return appIconStr;
    }

    public void setAppIconStr(String appIconStr) {
        this.appIconStr = appIconStr;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppCoverImage() {
        return appCoverImage;
    }

    public void setAppCoverImage(String appCoverImage) {
        this.appCoverImage = appCoverImage;
    }
}

