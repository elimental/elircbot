package ru.elimental.elircbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.elimental.elircbot.bot.Bot;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

@SpringBootApplication
public class ElircbotApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ElircbotApplication.class, args);
        Bot bot = ctx.getBean(Bot.class);
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        bot.start();
    }
}
