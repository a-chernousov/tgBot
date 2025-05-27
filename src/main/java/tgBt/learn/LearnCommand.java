package tgBt.learn;

import tgBt.Command;
import tgBt.question.QuestionSet;
import tgBt.Sender;

public class LearnCommand extends Command {
    public LearnCommand(QuestionSet questionSet) {
        super(questionSet);
    }

    @Override
    public Sender execute(long chatId) {
        return new LearnSender(chatId, questionSet);
    }
}
