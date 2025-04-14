package unq.dapp.grupoj.SoccerGenius.services;

import unq.dapp.grupoj.SoccerGenius.model.Player;

import java.util.List;

public interface TeamService {

    List<Player> getTeamPlayers(String teamName);
}
