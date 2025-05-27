package tgBt;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QuestionBot extends TelegramLongPollingBot {

    private final Map<Long, Sender> userSessions = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage reply = new SendMessage();
        try {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            reply.setChatId(String.valueOf(chatId));

            System.out.println(">>> Получено сообщение: " + text + " от chatId: " + chatId);

            // Загружаем JSON с вопросами (один раз)
            InputStream inputStream = QuestionSet.class.getClassLoader().getResourceAsStream("lotr_questions.json");
            if (inputStream == null) {
                throw new RuntimeException("Файл lotr_questions.json не найден.");
            }
            QuestionSet.getInstance().loadFromJson(inputStream);

            Sender sender = userSessions.get(chatId);

            if (text.startsWith("/")) {
                // Команда
                Command command = Command.fromText(text);
                if (command != null) {
                    sender = command.execute(chatId);
                    userSessions.put(chatId, sender);
                    reply = sender.createSendMessage();
                } else {
                    reply.setText("Неизвестная команда. Выберите путь:\n" +
                            "⚔️ /exam - Испытание мудрости (5 вопросов)\n" +
                            "📖 /study - Саги и предания\n" +
                            "🧙 /learn - Учение у магов\n" +
                            "❌ /cancel - Покинуть совет");
                }
            } else {
                // Ответ в рамках сессии
                if (sender != null) {
                    sender.onMessageReceived(text);
                    reply = sender.createSendMessage();
                } else {
                    // Не команда и не в сессии
                    reply.setText("Неизвестная команда. Выберите путь:\n" +
                            "⚔️ /exam - Испытание мудрости (5 вопросов)\n" +
                            "📖 /study - Саги и предания\n" +
                            "🧙 /learn - Учение у магов\n" +
                            "❌ /cancel - Покинуть совет");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            reply.setText("Что-то пошло не так в Средиземье... Попробуйте позже.");
        }

        try {
            execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "QuestionBot";
    }

    @Override
    public String getBotToken() {
        return "7856207138:AAHuWf8t8KYm1Oc45SvDzYGnAYJvtmicoGE";
    }
}








