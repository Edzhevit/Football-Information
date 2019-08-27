package softuni.exam.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.domain.dtos.xml.PictureDto;
import softuni.exam.domain.dtos.xml.TeamDto;
import softuni.exam.domain.dtos.xml.TeamRootDto;
import softuni.exam.domain.entities.Picture;
import softuni.exam.domain.entities.Team;
import softuni.exam.repository.PictureRepository;
import softuni.exam.repository.TeamRepository;
import softuni.exam.util.FileUtil;
import softuni.exam.util.ValidatorUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Service
public class TeamServiceImpl implements TeamService {

    private final static String TEAMS_XML_FILE_PATH =
            System.getProperty("user.dir") + "/src/main/resources/files/xml/teams.xml";

    private final TeamRepository teamRepository;
    private final PictureRepository pictureRepository;
    private final ValidatorUtil validationUtil;
    private final FileUtil fileUtil;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, PictureRepository pictureRepository, ValidatorUtil validationUtil, FileUtil fileUtil,
                           XmlParser xmlParser, ModelMapper modelMapper) {
        this.teamRepository = teamRepository;
        this.pictureRepository = pictureRepository;
        this.validationUtil = validationUtil;
        this.fileUtil = fileUtil;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
    }

    @Override
    public String importTeams() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        TeamRootDto teamRootDto = this.xmlParser.parseXml(TeamRootDto.class, TEAMS_XML_FILE_PATH);


        for (TeamDto teamDto : teamRootDto.getTeamDtos()) {
            Team team = this.modelMapper.map(teamDto, Team.class);
            Picture picture = this.pictureRepository.findByUrl(teamDto.getPicture().getUrl());
            if (picture == null) {
                sb.append("Invalid team").append(System.lineSeparator());
                continue;
            }

            team.setPicture(picture);
            if (!this.validationUtil.isValid(team)) {

                sb.append("Invalid team").append(System.lineSeparator());
                continue;
            }
            this.teamRepository.saveAndFlush(team);
            sb.append(String.format("Successfully imported team - %s", team.getName())).append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public boolean areImported() {
        return this.teamRepository.count() > 0;
    }

    @Override
    public String readTeamsXmlFile() throws IOException {
        return this.fileUtil.readFile(TEAMS_XML_FILE_PATH);
    }
}
