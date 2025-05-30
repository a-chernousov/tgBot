package tgBt.question;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgBt.Command;
import tgBt.Sender;
import tgBt.testUnit.LoggerUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class QuestionBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerUtil.getLogger();
    private final Map<Long, Sender> userSessions = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage reply = new SendMessage();
        try {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            reply.setChatId(String.valueOf(chatId));

            logger.info("Получено сообщение: " + text + " от chatId: " + chatId);

            // Загрузка вопросов
            InputStream inputStream = QuestionSet.class.getClassLoader().getResourceAsStream("lotr_questions.json");
            if (inputStream == null) {
                logger.severe("Файл lotr_questions.json не найден.");
                throw new RuntimeException("Файл lotr_questions.json не найден.");
            }
            QuestionSet.getInstance().loadFromJson(inputStream);

            Sender sender = userSessions.get(chatId);

            if (text.startsWith("/")) {
                Command command = Command.fromText(text);
                if (command != null) {
                    sender = command.execute(chatId);
                    userSessions.put(chatId, sender);
                    reply = sender.createSendMessage();
                    logger.info("Обработана команда: " + text);
                } else if (text.equals("/start")) {
                    msgHello(reply);
                    logger.info("Пользователь начал взаимодействие с ботом.");
                } else {
                    msgHello(reply);
                    logger.warning("Неизвестная команда: " + text);
                }
            } else {
                if (sender != null) {
                    sender.onMessageReceived(text);
                    reply = sender.createSendMessage();
                    logger.info("Обработан ответ пользователя: " + text);
                } else {
                    msgHello(reply);
                    logger.warning("Пользователь отправил сообщение вне сессии: " + text);
                }
            }
        } catch (Exception e) {
            logger.severe("Ошибка при обработке сообщения: " + e.getMessage());
            reply.setText("Что-то пошло не так в Средиземье... Попробуйте позже.");
        }

        try {
            execute(reply);
        } catch (TelegramApiException e) {
            logger.severe("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "QuestionBot";
    }

    @Override
    public String getBotToken() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("TELEGRAM_BOT_TOKEN");
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить токен из config.properties", e);
        }
    }



    private void msgHello(SendMessage reply){
        reply.setText(
                        "⚔️ /exam - Испытание мудрости (5 вопросов)\n" +
                        "📖 /study - Саги и предания\n" +
                        "🧙 /learn - Учение у магов\n");
    }
}








