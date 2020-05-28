package ru.elimental.elircbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.elimental.elircbot.model.Channel;
import ru.elimental.elircbot.model.Message;
import ru.elimental.elircbot.model.User;
import ru.elimental.elircbot.repository.ChannelRepository;
import ru.elimental.elircbot.repository.MessageRepository;
import ru.elimental.elircbot.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Transactional
public class DataProvider {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Autowired
    public DataProvider(MessageRepository messageRepository, UserRepository userRepository,
                        ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public void saveMessage(String channel, String sender, String message) {
        messageRepository.save(new Message(LocalDateTime.now(), getChannel(channel), getUser(sender), message));

    }

    private Channel getChannel(String channelName) {
        Optional<Channel> channel = channelRepository.getChannelByName(channelName);
        return channel.orElseGet(() -> channelRepository.save(new Channel(channelName)));
    }

    private User getUser(String userNick) {
        Optional<User> user = userRepository.getUserByNick(userNick);
        return user.orElseGet(() -> userRepository.save(new User(userNick)));
    }
}
