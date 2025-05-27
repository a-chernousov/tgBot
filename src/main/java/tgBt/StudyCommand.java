package tgBt;

public class StudyCommand extends Command {
    public StudyCommand(QuestionSet questionSet) {
        super(questionSet);
    }

    @Override
    public Sender execute(long chatId) {
        return new StudySender(chatId, questionSet);
    }
}