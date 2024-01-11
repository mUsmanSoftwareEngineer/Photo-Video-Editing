package collagestudio.photocollage.collagemaker.model;

public class TemplateFirebaseImage {
    String name;
    String uri;
    TemplateFirebaseImage(){

    }

    public TemplateFirebaseImage(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
