package ru.elimental.elircbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import ru.elimental.elircbot.bot.Bot;

@SpringBootApplication
public class ElircbotApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(ElircbotApplication.class, args);
		Bot bot = ctx.getBean(Bot.class);
		bot.start();
	}
}
