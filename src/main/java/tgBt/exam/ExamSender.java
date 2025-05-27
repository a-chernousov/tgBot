package tgBt.exam;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tgBt.question.Question;
import tgBt.question.QuestionSet;
import tgBt.Sender;

public class ExamSender extends Sender {
    private static final int EXAM_QUESTIONS = 5;

    public ExamSender(long chatId, QuestionSet questionSet) {
        super(chatId);
        this.stateSession = new ExamSession(questionSet, EXAM_QUESTIONS);
    }

    @Override
    public void onMessageReceived(String message) {
        stateSession.check(message); // проверяем ответ и обновляем состояние
    }

    @Override
    public SendMessage createSendMessage() {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        switch (stateSession.getState()) {
            case INIT:
            case ACTION: {
                Question question = stateSession.action();
                if (question == null) {
                    message.setText(stateSession.end());
                } else {
                    message.setText(formatQuestion(question));
                }
                break;
            }

            case CHECK: {
                Question nextQuestion = stateSession.action();
                if (nextQuestion == null) {
                    message.setText(stateSession.end());
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ответ принят. Следующий вопрос:\n\n");
                    sb.append(formatQuestion(nextQuestion));
                    message.setText(sb.toString());
                }
                break;
            }

            case END:
                message.setText(stateSession.end() + "\n\nЧто дальше?\n" +
                        "/exam — пройти тест заново\n" +
                        "/help — помощь\n" +
                        "/cancel — отмена и выход");

                break;

            case ERROR:
                message.setText("Произошла ошибка. Попробуйте снова.");
                break;
        }

        return message;
    }

    private String formatQuestion(Question question) {
        StringBuilder sb = new StringBuilder();
        sb.append("Вопрос: ").append(question.getQuestionText()).append("\n\n");
        sb.append("Варианты ответов:\n");

        int i = 1;
        for (String option : question.getOptions()) {
            sb.append(i++).append(". ").append(option).append("\n");
        }

        return sb.toString();
    }
}
