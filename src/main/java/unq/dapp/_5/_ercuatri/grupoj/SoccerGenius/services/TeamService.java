package unq.dapp._5._ercuatri.grupoj.SoccerGenius.services;

import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.Player;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.Team;

import java.util.List;

public interface TeamService {

    public List<Player> getTeamPlayers(String teamName);
}
