package tgBt;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;



public class QuestionBot extends TelegramLongPollingBot {
    private Map<Long, Sender> userSessions = new HashMap<>();
    private Map<String, Command> commands = new HashMap<>();
    private QuestionSet questionSet;

    public QuestionBot() {
        questionSet = new QuestionSet();
        try {
            InputStream inputStream = QuestionSet.class.getClassLoader().getResourceAsStream("lotr_questions.json");
            if (inputStream == null) {
                throw new FileNotFoundException("Файл lotr_questions.json не найден в ресурсах.");
            }
            QuestionSet.getInstance().loadFromJson(inputStream);



        } catch (IOException e) {
            System.err.println("Ошибка загрузки вопросов: " + e.getMessage());
        }

        commands.put("/exam", new ExamCommand(questionSet));
        commands.put("/study", new StudyCommand(questionSet));
        commands.put("/learn", new LearnCommand(questionSet));
        commands.put("/lotr", new StudyCommand(questionSet)); // Алиас для тематики
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage reply = new SendMessage();
        try {
            long chatId = update.getMessage().getChatId();
            reply.setChatId(String.valueOf(chatId));
            String text = update.getMessage().getText();

            System.out.println(">>> Получено сообщение: " + text + " от chatId: " + chatId);

            // Загружаем вопросы, если не загружены
            InputStream inputStream = QuestionSet.class.getClassLoader().getResourceAsStream("lotr_questions.json");
            if (inputStream == null) {
                throw new FileNotFoundException("Файл lotr_questions.json не найден в ресурсах.");
            }
            QuestionSet.getInstance().loadFromJson(inputStream);

            // Проверка активной сессии
            Sender sender = userSessions.get(chatId);
            if (sender != null) {
                // Если есть активная сессия, обрабатываем ввод как ответ
                String response = sender.handleInput(text);
                if (sender.isFinished()) {
                    userSessions.remove(chatId);
                }
                reply.setText(response);
            } else {
                // Обработка команды
                Command command = Command.fromText(text);
                if (command == null) {
                    // Неизвестная команда
                    reply.setText(
                            "Выберите путь:\n" +
                                    "⚔️ /exam - Испытание мудрости (5 вопросов)\n" +
                                    "📖 /study - Саги и предания\n" +
                                    "🧙 /learn - Учение у магов\n" +
                                    "❌ /cancel - Покинуть совет"
                    );
                } else {
                    Sender newSender = command.execute(chatId);
                    if (newSender != null) {
                        userSessions.put(chatId, newSender);
                        reply = newSender.createSendMessage();
                    } else {
                        reply.setText("Команда не сработала. Попробуйте позже.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            reply.setText("Что-то пошло не так в Средиземье... Попробуйте позже");
        }

        try {
            execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void handleCommand(long chatId, String commandText, SendMessage reply) {
        switch (commandText.toLowerCase()) {
            case "/start":
                reply.setText("Добро пожаловать в Хранитель Колец!\n\n" +
                        "Выберите путь:\n" +
                        "⚔️ /exam - Испытание мудрости (5 вопросов)\n" +
                        "📖 /study - Саги и предания\n" +
                        "🧙 /learn - Учение у магов\n" +
                        "❌ /cancel - Покинуть совет");
                break;

            case "/cancel":
                userSessions.remove(chatId);
                reply.setText("Совет распущен. Когда будете готовы, выберите новый путь:");
                break;

            default:
                Command command = commands.get(commandText.toLowerCase());
                if (command != null) {
                    Sender sender = command.execute(chatId);
                    if (sender != null) {
                        userSessions.put(chatId, sender);
                        reply = sender.createSendMessage();
                    } else {
                        reply.setText("Команда не сработала. Попробуйте позже.");
                    }
                } else {
                    reply.setText("Такого заклинания нет в моих книгах!\n\n" +
                            "Доступные пути:\n" +
                            "⚔️ /exam - Испытание\n" +
                            "📖 /study - Саги\n" +
                            "🧙 /learn - Учение");
                }
        }
    }

    private void handleMessage(long chatId, String text, SendMessage reply) {
        Sender sender = userSessions.get(chatId);

        if (sender == null) {
            reply.setText("Сначала выберите свой путь, странник:\n" +
                    "⚔️ /exam - Испытание мудрости\n" +
                    "📖 /study - Саги и предания\n" +
                    "🧙 /learn - Учение у магов");
            return;
        }

        try {
            sender.onMessageReceived(text);
            reply = sender.createSendMessage();
        } catch (Exception e) {
            reply.setText("Тьма окутала мой разум... Давайте начнем заново /start");
            userSessions.remove(chatId);
        }
    }

    private void decorateWithLotRTheme(SendMessage message) {
        String text = message.getText();
        List<String> quotes = Arrays.asList(
                "\n\n«Не все те, кто блуждают, потеряны» - Гэндальф",
                "\n\n«Даже самый малый может изменить ход будущего» - Галадриэль",
                "\n\n«Опасное дело — выходить за дверь» - Бильбо Бэггинс"
        );

        if (!text.contains("Гэндальф") && !text.contains("Саурон") &&
                !text.contains("Средиземье") && new Random().nextInt(3) == 0) {
            message.setText(text + quotes.get(new Random().nextInt(quotes.size())));
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
