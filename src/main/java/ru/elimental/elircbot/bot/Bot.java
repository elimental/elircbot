package ru.elimental.elircbot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.elimental.elircbot.service.MessageProcessor;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "irc")
@Getter
@Setter
@Slf4j
public class Bot extends PircBot {

    private static final String VERSION = "ELIrcBot v.1.0";
    public static final String CHANNEL_PREFIX = "#";

    private String serverAddress;
    private int serverPort;
    private List<String> channelsToJoin;
    private String nickName;
    private final MessageProcessor messageProcessor;

    @Autowired
    public Bot(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @PostConstruct
    public void init() {
        setName(nickName);
        setAutoNickChange(true);
        setVersion(VERSION);
        setFinger(BotMessages.REPLY_FINGER);
        setLogin(nickName);
        setVerbose(true);
    }

    public void start() {
        try {
            connect(serverAddress, serverPort);
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onConnect() {
        channelsToJoin.forEach(s -> joinChannel(CHANNEL_PREFIX + s.trim()));
    }

    @Override
    protected void onDisconnect() {
        try {
            reconnect();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        messageProcessor.handleMessage(channel, sender, message);
    }
}
