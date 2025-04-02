package unq.dapp._5._ercuatri.grupoj.SoccerGenius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SoccerGeniusApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoccerGeniusApplication.class, args);
	}

}
