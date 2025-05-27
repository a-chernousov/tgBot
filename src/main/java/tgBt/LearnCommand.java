package tgBt;

public class LearnCommand extends Command {
    public LearnCommand(QuestionSet questionSet) {
        super(questionSet);
    }

    @Override
    public Sender execute(long chatId) {
        return new LearnSender(chatId, questionSet);
    }
}
