package unq.dapp.grupoj.soccergenius.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import unq.dapp.grupoj.soccergenius.model.Player;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class WebScrapingService {
    private static final String URL             = "https://es.whoscored.com/search/?t=";
    private static final String USER_AGENT      = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";
    private static final String CUSTOMCACHEPATH = "/tmp/wdm_cache";

    public List<Player> scrapeWebsite(String teamName, String country) {
        WebDriverManager.chromedriver().cachePath(CUSTOMCACHEPATH).setup();
        WebDriver driver = createWebDriver();

        List<Player> scrapedData = new ArrayList<>();

        try {
            String urlTMP = URL + teamName;
            driver.navigate().to(urlTMP);

            WebElement divResult = driver.findElement(By.className("search-result"));
            WebElement teamsTable = divResult.findElement(By.xpath("./table[1]"));
            WebElement tbody = teamsTable.findElement(By.tagName("tbody"));
            List<WebElement> teamsList = tbody.findElements(By.xpath("./tr[position()>1]"));

            String teamUrl = "";
            for (WebElement team : teamsList) {
                WebElement linkEquipo = team.findElement(By.xpath("./td[1]/a"));
                String teamNameSource = linkEquipo.getText();

                WebElement spanPais = team.findElement(By.xpath("./td[2]/span"));
                String countryName = spanPais.getText();

                if (teamNameSource.toLowerCase().contains(teamName) && countryName.equalsIgnoreCase(country)){
                    teamUrl = linkEquipo.getAttribute("href");
                    break;
                }
            }

            driver.navigate().to(teamUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")));
            WebElement playerList = driver.findElement(By.id("player-table-statistics-body"));
            List<WebElement> playerList2 = playerList.findElements(By.xpath("./*"));

            for (WebElement player : playerList2) {
                String name        = player.findElement(By.className("player-link")).findElement(By.xpath("./*[2]")).getText();
                String gamesPlayed = player.findElement(By.xpath("./*[5]")).getText();
                String goals       = player.findElement(By.className("goal")).getText().equals("-") ? "0" : player.findElement(By.className("goal")).getText();
                String assists     = player.findElement(By.className("assistTotal")).getText();
                String rating      = player.findElement(By.xpath("./*[15]")).getText();

                Player p = new Player(name,gamesPlayed,goals,assists,rating);
                scrapedData.add(p);
            }
        } finally {
            driver.quit();
        }
        return scrapedData;
    }

    private WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Modo headless moderno
        options.addArguments("--disable-gpu"); // Necesario a veces en headless
        options.addArguments("--window-size=1920,1080"); // Definir tamaño puede ayudar
        options.addArguments("--no-sandbox"); // A veces necesario en entornos Linux/Docker
        options.addArguments("--disable-dev-shm-usage"); // A veces necesario en entornos Linux/Docker
        options.addArguments("user-agent=" + USER_AGENT); // Usar constante

        try {
            // Crear un path único para el directorio de datos de usuario en /tmp
            Path userDataDir = Paths.get("./tmp", "chrome_user_data_" + UUID.randomUUID().toString());
            // Asegurarse de que el directorio exista (aunque Chrome a menudo lo crea)
            Files.createDirectories(userDataDir) ;
            // Añadir el argumento a las opciones
            options.addArguments("--user-data-dir=" + userDataDir.toAbsolutePath().toString());
        } catch (IOException e) {
            System.err.println("Error al crear el directorio temporal para user-data-dir: " + e.getMessage());
            // Considera lanzar una excepción o manejar el error como prefieras
            // Si esto falla, el inicio de Chrome probablemente también fallará.
            throw new RuntimeException("No se pudo crear el directorio temporal para Chrome", e);
        }
        return new ChromeDriver(options);
    }
}
