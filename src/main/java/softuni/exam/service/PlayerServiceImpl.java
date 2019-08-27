package softuni.exam.service;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import softuni.exam.domain.dtos.json.PlayerDto;
import softuni.exam.domain.entities.Picture;
import softuni.exam.domain.entities.Player;
import softuni.exam.domain.entities.Team;
import softuni.exam.repository.PictureRepository;
import softuni.exam.repository.PlayerRepository;
import softuni.exam.repository.TeamRepository;
import softuni.exam.util.FileUtil;
import softuni.exam.util.ValidatorUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final static String PLAYERS_JSON_FILE_PATH =
            System.getProperty("user.dir")+"/src/main/resources/files/json/players.json";

    private final PlayerRepository playerRepository;
    private final PictureRepository pictureRepository;
    private final TeamRepository teamRepository;
    private final ValidatorUtil validationUtil;
    private final FileUtil fileUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, PictureRepository pictureRepository, TeamRepository teamRepository, ValidatorUtil validationUtil, FileUtil fileUtil,
                             Gson gson, ModelMapper modelMapper) {
        this.playerRepository = playerRepository;
        this.pictureRepository = pictureRepository;
        this.teamRepository = teamRepository;
        this.validationUtil = validationUtil;
        this.fileUtil = fileUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    @Override
    public String importPlayers() throws IOException {
        StringBuilder sb = new StringBuilder();

        PlayerDto[] playerDtos = this.gson.fromJson(readPlayersJsonFile(), PlayerDto[].class);

        for (PlayerDto playerDto : playerDtos) {
            Player player = this.modelMapper.map(playerDto, Player.class);
            Picture picture = this.pictureRepository.findByUrl(player.getPicture().getUrl());
            Team team = this.teamRepository.findByName(player.getTeam().getName());
            player.setPicture(picture);
            player.setTeam(team);
            if (!this.validationUtil.isValid(player)){
                sb.append("Invalid player").append(System.lineSeparator());
                continue;
            }

            this.playerRepository.saveAndFlush(player);
            sb.append(String.format("Successfully imported player: %s %s", player.getFirstName(), player.getLastName()))
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public boolean areImported() {
        return this.playerRepository.count() > 0;
    }

    @Override
    public String readPlayersJsonFile() throws IOException {
        return this.fileUtil.readFile(PLAYERS_JSON_FILE_PATH);
    }

    @Override
    public String exportPlayersWhereSalaryBiggerThan() {
        StringBuilder sb = new StringBuilder();

        List<Player> players = this.playerRepository.findAllBySalaryGreaterThanOrderBySalaryDesc(BigDecimal.valueOf(100000));

        for (Player player : players) {
            sb.append(String.format("Player name: %s", player.getFirstName() + " " + player.getLastName())).append(System.lineSeparator());
            sb.append(String.format("    Number: %d", player.getNumber())).append(System.lineSeparator());
            sb.append(String.format("    Salary: %.2f", player.getSalary())).append(System.lineSeparator());
            sb.append(String.format("    Team: %s", player.getTeam().getName())).append(System.lineSeparator());

        }
        return sb.toString();
    }

    @Override
    public String exportPlayersInATeam() {
        StringBuilder sb = new StringBuilder();

        List<Player> players = this.playerRepository.findPlayersInTeam();

        sb.append("Team: North Hub")
                .append(System.lineSeparator());
        for (Player player : players) {
            sb.append(String.format("    Player name: %s - %s", player.getFirstName() + " " + player.getLastName(),
                    player.getPosition().toString())).append(System.lineSeparator());
            sb.append(String.format("    Number: %d", player.getNumber())).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
