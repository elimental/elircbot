package ru.elimental.elircbot.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class WeatherForecast {

    private String city;
    private String description;
    private String temperature;
    private int pressure;
    private String humidity;
    private int windDirection;
    private String windSpeed;

    @JsonProperty("weather")
    private void unpackWeather(List<Map<String, Object>> weather) {
        description = weather.get(0).get("description").toString();
    }

    @JsonProperty("main")
    private void unpackMain(Map<String, Object> main) {
        temperature = main.get("temp").toString();
        pressure = (int) (0.75 * (int) main.get("pressure"));
        humidity = main.get("humidity").toString();
    }

    @JsonProperty("wind")
    private void unpackWind(Map<String, Object> wind) {
        windDirection = (int) wind.get("deg");
        windSpeed = wind.get("speed").toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Погода в ")
                .append(city)
                .append(": ")
                .append(description)
                .append(", ")
                .append("температура ")
                .append(temperature)
                .append("C, ")
                .append("влажность ")
                .append(humidity)
                .append("%, ")
                .append("атмосферное давление ")
                .append(pressure)
                .append(" мм.рт.ст, ")
                .append("ветер ")
                .append(WindDirection.getDirection(windDirection))
                .append(" скорость ")
                .append(windSpeed)
                .append(" м/с.");
        return sb.toString();
    }

    enum WindDirection {

        N("С"),
        NNE("ССВ"),
        NE("СВ"),
        ENE("ВВС"),
        E("В"),
        ESE("ВЮВ"),
        SE("ЮВ"),
        SSE("ЮЮВ"),
        S("Ю"),
        SSW("ЮЮЗ"),
        SW("ЮЗ"),
        WSW("ЗЮЗ"),
        W("З"),
        WNW("ЗСЗ"),
        NW("СЗ"),
        NNW("ССЗ");

        private static final double STEP = 22.5;
        private static final int MASK = 16;
        private String value;

        WindDirection(String value) {
            this.value = value;
        }

        static String getDirection(int degrees) {
            int index = ((int)(degrees/STEP)) % MASK;
            return values()[index].value;
        }
    }
}


