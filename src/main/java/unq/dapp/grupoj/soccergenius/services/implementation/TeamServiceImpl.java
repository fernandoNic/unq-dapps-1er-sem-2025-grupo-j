package unq.dapp.grupoj.soccergenius.services.implementation;

import org.springframework.stereotype.Service;
import unq.dapp.grupoj.soccergenius.model.Player;
import unq.dapp.grupoj.soccergenius.services.TeamService;
import unq.dapp.grupoj.soccergenius.services.WebScrapingService;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final WebScrapingService WebScrapingService;

    public TeamServiceImpl(WebScrapingService webScrapingService) {
        WebScrapingService = webScrapingService;
    }

    @Override
    public List<Player> getTeamPlayers(String teamName, String country) {
        List<Player> resultSearch =  this.WebScrapingService.scrapeWebsite(teamName,country);

        return resultSearch;
    }
}
