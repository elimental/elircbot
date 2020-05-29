package ru.elimental.elircbot.service.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.elimental.elircbot.bot.Bot;
import ru.elimental.elircbot.model.dto.WeatherForecast;
import ru.elimental.elircbot.service.DataProvider;
import ru.elimental.elircbot.service.MessageProcessor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class WeatherForecastHandler extends AbstractHandler {

    private static final String WEATHER_FORECAST_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String URL_LANGUAGE_PARAM = "&lang=ru";
    private static final String URL_UNIT_PARAM = "&units=metric";
    private static final String API_KEY_PARAM_PREFIX = "&APPID=";
    private static final String ERROR_MESSAGE = "Что-то пошло не так. Варианты: 1.Такого города нет. " +
            "2.Синоптики еще спят. 3.Бот пьян.";
    private static final String ERROR_EMPTY_CITY = "Треба указать город";
    private static final String ERROR_OVER_LIMIT_REQUEST_MESSAGE = "Слишком дофига запросов...треба подождать минутку";
    private static final int REQUEST_LIMIT = 50;
    private static final int REQUEST_LIMIT_PERIOD = 60;

    private final AtomicInteger requestCount = new AtomicInteger(0);
    private volatile LocalDateTime lastRequestCountReset = LocalDateTime.now();

    @Value("${openweathermapApiKey}")
    private String apiKey;

    @Autowired
    public WeatherForecastHandler(DataProvider dataProvider, Bot bot, MessageProcessor messageProcessor) {
        super(dataProvider, bot, messageProcessor);
    }

    @Override
    public boolean handle(String channel, String sender, String message) {
        if (!message.startsWith("!погода")) {
            return false;
        }
        doRequest(channel, sender, message);
        return true;
    }

    void doRequest(String channel, String sender, String message) {
        new Thread(() -> {
            String city = message.substring(7).trim();
            String senderString = sender + ": ";
            if (city.isEmpty()) {
                sendMessage(channel, senderString + ERROR_EMPTY_CITY);
                return;
            }
            LocalDateTime current = LocalDateTime.now();
            Duration duration = Duration.between(lastRequestCountReset, current);
            long diff = duration.getSeconds();
            if (diff > REQUEST_LIMIT_PERIOD) {
                setLastRequestCountReset(current);
                requestCount.set(0);
            } else if (requestCount.get() >= REQUEST_LIMIT) {
                sendMessage(channel, senderString + ERROR_OVER_LIMIT_REQUEST_MESSAGE);
                return;
            }
            RestTemplate restTemplate = new RestTemplate();
            String url = WEATHER_FORECAST_URL + city + URL_LANGUAGE_PARAM + URL_UNIT_PARAM
                    + API_KEY_PARAM_PREFIX + apiKey;
            ResponseEntity<String> response = null;
            try {
                response = restTemplate.getForEntity(url, String.class);
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                sendMessage(channel, senderString + ERROR_MESSAGE);
                return;
            } finally {
                requestCount.incrementAndGet();
            }
            String body = response.getBody();
            if (body == null) {
                sendMessage(channel, senderString + ERROR_MESSAGE);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            WeatherForecast weatherForecast;
            try {
                weatherForecast = objectMapper.readValue(response.getBody(), WeatherForecast.class);
            } catch (Exception e) {
                sendMessage(channel, senderString + ERROR_MESSAGE);
                return;
            }
            weatherForecast.setCity(city);
            sendMessage(channel, senderString + weatherForecast.toString());
        }).start();
    }

    private synchronized void setLastRequestCountReset(LocalDateTime dateTime) {
        this.lastRequestCountReset = dateTime;
    }
}
