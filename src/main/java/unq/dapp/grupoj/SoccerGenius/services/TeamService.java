package unq.dapp.grupoj.SoccerGenius.services;

import unq.dapp.grupoj.SoccerGenius.model.Player;

import java.util.List;

public interface TeamService {

    public List<Player> getTeamPlayers(String teamName, String country);
}
