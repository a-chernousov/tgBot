package tgBt;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class ExamSender extends Sender {
    private static final int EXAM_QUESTIONS = 5;

    public ExamSender(long chatId, QuestionSet questionSet) {
        super(chatId);
        this.stateSession = new ExamSession(questionSet, EXAM_QUESTIONS);
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
                if (qt == null) {
                    message.setText(stateSession.end());
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Вопрос: ").append(qt.getQuestionText()).append("\n\n");
                    sb.append("Варианты ответов:\n");

                    int i = 1;
                    for (String option : qt.getOptions()) {
                        sb.append(i++).append(". ").append(option).append("\n");
                    }

                    message.setText(sb.toString());
                }
                break;

            case CHECK:
                message.setText("Ответ принят. Следующий вопрос:");
                break;

            case END:
                message.setText(stateSession.end());
                break;

            case ERROR:
                message.setText("Произошла ошибка. Попробуйте снова.");
                break;
        }

        return message;
    }
}
