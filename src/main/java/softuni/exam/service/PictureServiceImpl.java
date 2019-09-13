package softuni.exam.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import softuni.exam.domain.dtos.xml.PictureDto;
import softuni.exam.domain.dtos.xml.PictureRootDto;
import softuni.exam.domain.entities.Picture;
import softuni.exam.repository.PictureRepository;
import softuni.exam.util.FileUtil;
import softuni.exam.util.ValidatorUtil;
import softuni.exam.util.XmlParser;
import javax.xml.bind.JAXBException;
import java.io.IOException;


@Service
@Transactional
public class PictureServiceImpl implements PictureService {

    private final static String PICTURE_XML_FILE_PATH =
            System.getProperty("user.dir")+"/src/main/resources/files/xml/pictures.xml";

    private final PictureRepository pictureRepository;
    private final ValidatorUtil validationUtil;
    private final FileUtil fileUtil;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository, ValidatorUtil validationUtil, FileUtil fileUtil,
                              XmlParser xmlParser, ModelMapper modelMapper) {
        this.pictureRepository = pictureRepository;
        this.validationUtil = validationUtil;
        this.fileUtil = fileUtil;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
    }

    @Override
    public String importPictures() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        PictureRootDto pictureRootDto = this.xmlParser.parseXml(PictureRootDto.class, PICTURE_XML_FILE_PATH);

        for (PictureDto pictureDto : pictureRootDto.getPictureDtos()) {
            Picture picture = this.modelMapper.map(pictureDto, Picture.class);
            if (!this.validationUtil.isValid(picture) || this.pictureRepository.findByUrl(pictureDto.getUrl()) != null){
                sb.append("Invalid picture").append(System.lineSeparator());
                continue;
            }

            this.pictureRepository.saveAndFlush(picture);
            sb.append(String.format("Successfully imported picture - %s", picture.getUrl())).append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readPicturesXmlFile() throws IOException {
        return this.fileUtil.readFile(PICTURE_XML_FILE_PATH);
    }

}
