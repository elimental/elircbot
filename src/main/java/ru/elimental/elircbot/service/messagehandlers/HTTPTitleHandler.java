package ru.elimental.elircbot.service.messagehandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.elimental.elircbot.bot.Bot;
import ru.elimental.elircbot.model.json.google.YouTubeReply;
import ru.elimental.elircbot.repository.DataProvider;
import ru.elimental.elircbot.service.MessageProcessor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class HTTPTitleHandler extends AbstractHandler {

    private final static String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet" +
            "&key=AIzaSyDDmNKXo6fz3BoBllqjGc9ugxErq606iRM&id=";

    @Autowired
    HTTPTitleHandler(DataProvider dataProvider, Bot bot, MessageProcessor messageProcessor) {
        super(dataProvider, bot, messageProcessor);
    }

    @Override
    public boolean handle(String channel, String sender, String message) {
        if (message.contains("http://") || message.contains("https://")) {
            doRequest(channel, message);
            return true;
        }
        return false;
    }

    private void doRequest(String channel, String message) {
        new Thread(() -> {
            int start = message.contains("http://") ? message.indexOf("http://") : message.indexOf("https://");
            int end = message.indexOf(" ", start);
            String url = end == -1 ? message.substring(start) : message.substring(start, end);
            String replyMessage = null;
            if (url.contains("youtube.com") || url.contains("youtu.be")) {
                try {
                    String videoId = getYouTubeVideoId(url);
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response;
                    response = restTemplate.getForEntity(YOUTUBE_API_URL + videoId, String.class);
                    ObjectMapper objectMapper = new ObjectMapper();
                    YouTubeReply youTubeReply = objectMapper.readValue(response.getBody(), YouTubeReply.class);
                    String videoTitle = youTubeReply.getItems()[0].getSnippet().getTitle();
                    replyMessage = "Видосик на YouTube: " + videoTitle;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Document httpDoc = Jsoup.connect(url).get();
                    replyMessage = httpDoc.title();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (replyMessage != null && !replyMessage.isEmpty()) {
                bot.sendAndSaveMessage(channel, replyMessage);
            }
        }).start();
    }

    private String getYouTubeVideoId(String url) throws MalformedURLException {
        String videoId;
        if (url.contains("youtube.com")) {
            URL youTubeUrl = new URL(url);
            String query = youTubeUrl.getQuery();
            int start = query.indexOf("v=");
            int end = query.indexOf("&", start);
            videoId = end == -1 ? query.substring(start + 2) : query.substring(start + 2, end);
        } else {
            int start = url.indexOf("://youtu.be/");
            int end = url.indexOf(" ", start);
            videoId = end == -1 ? url.substring(start + 12) : url.substring(start, end);
        }
        return videoId;
    }
}
