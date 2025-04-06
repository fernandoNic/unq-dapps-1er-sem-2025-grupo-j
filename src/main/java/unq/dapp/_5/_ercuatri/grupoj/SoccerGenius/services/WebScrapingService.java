package unq.dapp._5._ercuatri.grupoj.SoccerGenius.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.Player;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebScrapingService {
    private String URL = "https://es.whoscored.com/search/?t=";

    public List<Player> scrapeWebsite(String teamName) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);

        List<Player> scrapedData = new ArrayList<>();

        try {
            String urlTMP = URL + teamName;
            driver.navigate().to(urlTMP);

            WebElement divResult = driver.findElement(By.className("search-result"));
            WebElement table = divResult.findElement(By.xpath("./*[2]")).findElement(By.tagName("tbody"));
            String teamUrl = table.findElement(By.xpath("./*[2]")).findElement(By.tagName("td")).findElement(By.tagName("a")).getAttribute("href");;

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
            // Close the browser
            driver.quit();
        }
        return scrapedData;
    }
}
