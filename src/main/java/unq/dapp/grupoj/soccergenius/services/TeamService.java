package unq.dapp.grupoj.soccergenius.services;

import unq.dapp.grupoj.soccergenius.model.Player;

import java.util.List;

public interface TeamService {

    public List<Player> getTeamPlayers(String teamName, String country);
}
