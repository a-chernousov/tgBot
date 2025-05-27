package tgBt.learn;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tgBt.question.Question;
import tgBt.question.QuestionSet;
import tgBt.Sender;

public class LearnSender extends Sender {
    public LearnSender(long chatId, QuestionSet questionSet) {
        super(chatId);
        this.stateSession = new LearnSession(questionSet);
    }

    @Override
    public void onMessageReceived(String message) {
        stateSession.check(message);
    }

    @Override
    public SendMessage createSendMessage() {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        switch (stateSession.getState()) {
            case INIT:
            case ACTION:
                Question qt = stateSession.action();
                StringBuilder sb = new StringBuilder();
                sb.append("📚 Вопрос: ").append(qt.getQuestionText()).append("\n\n");
                sb.append("Варианты ответов:\n");

                int i = 1;
                for (String option : qt.getOptions()) {
                    sb.append(i++).append(". ").append(option).append("\n");
                }

                message.setText(sb.toString());
                break;

            case CHECK:
                message.setText("❌ Неверно! Правильный ответ: " +
                        ((LearnSession)stateSession).getCorrectAnswer() +
                        "\n\nВведите /learn для нового вопроса.");
                break;

            case END:
                message.setText(stateSession.end());
                break;

            case ERROR:
                message.setText("⚠ Произошла ошибка. Попробуйте снова.");
                break;
        }

        return message;
    }
}