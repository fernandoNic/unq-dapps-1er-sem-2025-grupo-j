package unq.dapp._5._ercuatri.grupoj.SoccerGenius.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@ToString
@Setter
@Getter
public class Team {
    private String name;
    private List<Player> playersList;
}
