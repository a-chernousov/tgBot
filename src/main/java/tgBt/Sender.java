package tgBt;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public abstract class Sender {
    protected StateSession stateSession;
    protected long chatId;

    public Sender(long chatId) {
        this.chatId = chatId;
    }

    public abstract void onMessageReceived(String message);
    public abstract SendMessage createSendMessage();
}