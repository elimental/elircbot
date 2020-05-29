package ru.elimental.elircbot.service.handlers;

import ru.elimental.elircbot.bot.Bot;
import ru.elimental.elircbot.service.DataProvider;
import ru.elimental.elircbot.service.MessageProcessor;

import static ru.elimental.elircbot.bot.Bot.CHANNEL_PREFIX;

public abstract class AbstractHandler {

    protected final DataProvider dataProvider;
    private final Bot bot;

    AbstractHandler(DataProvider dataProvider, Bot bot, MessageProcessor messageProcessor) {
        this.dataProvider = dataProvider;
        this.bot = bot;
        messageProcessor.addHandler(this);
    }

    public abstract boolean handle(String channel, String sender, String message);

    protected void sendMessage(String channel, String message) {
        bot.sendMessage(CHANNEL_PREFIX + channel, message);
    }
}
