package ru.elimental.elircbot.remote;

import ru.elimental.elircbot.bot.Bot;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class RemoteWorkerThread extends Thread {

    private final Bot bot;
    private final String secret;
    private final Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;

    public RemoteWorkerThread(Socket socket, Bot bot, String secret) {
        this.socket = socket;
        this.bot = bot;
        this.secret = secret;
    }

    @Override
    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ioException) {
                // ignore
            }
            return;
        }
        handleCommand();
    }

    private void handleCommand() {
        try {
            while (true) {
                String input = br.readLine();
                String[] msg = input.split(" ");
                if (msg.length < 3) {
                    bw.write("Wrong input line\n");
                    bw.flush();
                    continue;
                }
                if (!msg[0].equals(secret)) {
                    bw.write("Wrong secret\n");
                    bw.flush();
                    continue;
                }
                String channel = msg[1];
                List<String> channels = Arrays.asList(bot.getChannels());
                if (!channels.contains("#" + channel)) {
                    bw.write("Wrong channel\n");
                    bw.flush();
                    continue;
                }
                bot.sendAndSaveMessage(channel, getMsg(msg));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMsg(String[] msg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < msg.length; i++) {
            sb.append(msg[i]).append(" ");
        }
        return sb.toString().trim();
    }
}
