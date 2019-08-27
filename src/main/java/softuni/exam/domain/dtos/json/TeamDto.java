package softuni.exam.domain.dtos.json;

import com.google.gson.annotations.Expose;
import softuni.exam.domain.dtos.xml.PictureDto;

public class TeamDto {

    @Expose
    private String name;
    @Expose
    private PictureImportDto picture;

    public TeamDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PictureImportDto getPictureDto() {
        return picture;
    }

    public void setPictureDto(PictureImportDto picture) {
        this.picture = picture;
    }
}
