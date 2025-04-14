package unq.dapp.grupoj.soccergenius.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unq.dapp.grupoj.soccergenius.model.Player;
import unq.dapp.grupoj.soccergenius.services.TeamService;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService){
        this.teamService = teamService;
    }

    @GetMapping("/{teamName}/players")
    @Operation(summary = "retorna información de los jugadores de un equipo, incluyendo nombre, partidos jugados, goles, asistencias y rating.")
    public ResponseEntity<List<Player>> getTeamPlayers (@PathVariable String teamName){
        List<Player> players = this.teamService.getTeamPlayers(teamName);
        return ResponseEntity.status(HttpStatus.OK).body(players);
    }
}
