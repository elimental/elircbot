package ru.elimental.elircbot.service.messagehandlers;

import ru.elimental.elircbot.bot.Bot;
import ru.elimental.elircbot.repository.DataProvider;
import ru.elimental.elircbot.service.MessageProcessor;

public abstract class AbstractHandler {

    protected final DataProvider dataProvider;
    protected final Bot bot;

    AbstractHandler(DataProvider dataProvider, Bot bot, MessageProcessor messageProcessor) {
        this.dataProvider = dataProvider;
        this.bot = bot;
        messageProcessor.addHandler(this);
    }

    public abstract boolean handle(String channel, String sender, String message);
}
