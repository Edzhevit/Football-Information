package softuni.exam.domain.dtos.json;

import com.google.gson.annotations.Expose;

public class PictureImportDto {

    @Expose
    private String url;

    public PictureImportDto() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
