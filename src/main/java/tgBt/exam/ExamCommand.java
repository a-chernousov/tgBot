package tgBt.exam;

import tgBt.Command;
import tgBt.question.QuestionSet;
import tgBt.Sender;

public class ExamCommand extends Command {
    public ExamCommand(QuestionSet questionSet) {
        super(questionSet);
    }

    @Override
    public Sender execute(long chatId) {
        return new ExamSender(chatId, questionSet);
    }
}