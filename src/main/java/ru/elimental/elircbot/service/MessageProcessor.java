package ru.elimental.elircbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.elimental.elircbot.service.handlers.AbstractHandler;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageProcessor {

    private final DataProvider dataProvider;
    private final List<AbstractHandler> handlers = new ArrayList<>();

    @Autowired
    public MessageProcessor(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void handleMessage(String channel, String sender, String message) {
        String channelName = channel.substring(1);
        dataProvider.saveMessage(channelName, sender, message);

        for (AbstractHandler handler : handlers) {
            if (handler.handle(channelName, sender, message)) {
                break;
            }
        }
    }

    public void addHandler(AbstractHandler messageHandler) {
        handlers.add(messageHandler);
    }
}
