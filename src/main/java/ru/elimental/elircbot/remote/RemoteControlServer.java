package ru.elimental.elircbot.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.elimental.elircbot.bot.Bot;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Service
public class RemoteControlServer {

    private static final int MAXIMUM_CONNECTION = 2;

    private final Bot bot;

    @Value("${remote.server.port}")
    private int port;

    @Value("${remote.server.secret}")
    private String secret;

    @Autowired
    public RemoteControlServer(Bot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void init() {
        Thread listener = new Listener();
        listener.setDaemon(true);
        listener.start();
    }

    class Listener extends Thread {

        @Override
        public void run() {
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(port, MAXIMUM_CONNECTION);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    new RemoteWorkerThread(socket, bot, secret).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
