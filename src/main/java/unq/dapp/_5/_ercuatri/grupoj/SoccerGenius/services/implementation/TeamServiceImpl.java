package unq.dapp._5._ercuatri.grupoj.SoccerGenius.services.implementation;

import org.springframework.stereotype.Service;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.Player;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.Team;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.services.TeamService;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.services.WebScrapingService;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final WebScrapingService WebScrapingService;

    public TeamServiceImpl(WebScrapingService webScrapingService) {
        WebScrapingService = webScrapingService;
    }

    @Override
    public List<Player> getTeamPlayers(String teamName) {
        List<Player> resultSearch =  this.WebScrapingService.scrapeWebsite(teamName);

        return resultSearch;
    }
}
