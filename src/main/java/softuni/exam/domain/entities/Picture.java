package softuni.exam.domain.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "pictures")
public class Picture extends BaseEntity{

    private String url;
    private List<Team> teams;
    private List<Player> players;

    public Picture() {
    }

    @Column(name = "url", nullable = false)
    @NotNull
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @OneToMany(targetEntity = Team.class, mappedBy = "picture")
    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    @OneToMany(targetEntity = Player.class, mappedBy = "picture")
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
