package unq.dapp.grupoj.soccergenius.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unq.dapp.grupoj.soccergenius.model.Player;
import unq.dapp.grupoj.soccergenius.security.JwtTokenProvider;
import unq.dapp.grupoj.soccergenius.services.TeamService;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);
    private final TeamService teamService;

    public TeamController(TeamService teamService){
        this.teamService = teamService;
    }

    @GetMapping("/{teamName}/{country}/players")
    @Operation(summary = "retorna información de los jugadores de un equipo, incluyendo nombre, partidos jugados, goles, asistencias y rating.")
    public ResponseEntity<List<Player>> getTeamPlayers (@PathVariable String teamName, @PathVariable String country, @RequestHeader HttpHeaders header){
        JwtTokenProvider.validateToken(header.getFirst("Authorization"));

        long startTime = System.currentTimeMillis();
        logger.info("Request received to get players for team {} in country {}", teamName, country);

        try {
            List<Player> players = this.teamService.getTeamPlayers(teamName, country);
            long endTime = System.currentTimeMillis();
            logger.info("Successfully retrieved {} players for team {} in {} ms",
                    players.size(), teamName, (endTime - startTime));
            return ResponseEntity.status(HttpStatus.OK).body(players);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("Error fetching players for team {}: {} (execution time: {} ms)",
                    teamName, e.getMessage(), (endTime - startTime), e);
            throw e;
        }
    }

    @GetMapping("/locationServer")
    public ResponseEntity<Map<String, Object>> getPathServerLocation(){
        Map<String, Object> response = new HashMap<>();
        try {
            String applicationPath = System.getProperty("user.dir");
            response.put("applicationPath", applicationPath);
            logger.info("Obteniendo directorios dentro de: {}", applicationPath);

            File directory = new File(applicationPath);
            List<String> subDirectories = List.of(); // Lista vacía por defecto

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    // Filtrar para obtener solo directorios y mapear a sus nombres
                    subDirectories = Arrays.stream(files)
                            .filter(File::isDirectory) // Filtra solo directorios
                            .map(File::getName)        // Obtiene el nombre de cada directorio
                            .collect(Collectors.toList()); // Recolecta en una lista
                    logger.info("Se encontraron {} subdirectorios.", subDirectories.size());
                } else {
                    logger.warn("No se pudo listar el contenido del directorio: {}", applicationPath);
                    response.put("error", "No se pudo listar el contenido del directorio.");
                }
            } else {
                logger.error("La ruta {} no existe o no es un directorio.", applicationPath);
                response.put("error", "La ruta de la aplicación no existe o no es un directorio.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            response.put("subDirectories", subDirectories);
            return ResponseEntity.ok(response);

        } catch (SecurityException se) {
            logger.error("Error de seguridad al intentar acceder a {}: {}", System.getProperty("user.dir"), se.getMessage(), se);
            response.put("error", "Permiso denegado para acceder al directorio.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            logger.error("Error inesperado al listar directorios: {}", e.getMessage(), e);
            response.put("error", "Ocurrió un error inesperado.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}