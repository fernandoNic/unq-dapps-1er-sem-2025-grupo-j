package unq.dapp.grupoj.SoccerGenius.services.implementation;

import org.springframework.stereotype.Service;
import unq.dapp.grupoj.SoccerGenius.model.Player;
import unq.dapp.grupoj.SoccerGenius.services.TeamService;
import unq.dapp.grupoj.SoccerGenius.services.WebScrapingService;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final WebScrapingService WebScrapingService;

    public TeamServiceImpl(WebScrapingService webScrapingService) {
        WebScrapingService = webScrapingService;
    }

    @Override
    public List<Player> getTeamPlayers(String teamName) {
        return this.WebScrapingService.scrapeWebsite(teamName);
    }
}
