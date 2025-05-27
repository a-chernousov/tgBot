package tgBt;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public abstract class Command {
    protected QuestionSet questionSet;

    public Command(QuestionSet questionSet) {
        this.questionSet = questionSet;
    }
    public static Command fromText(String text) {
        QuestionSet questionSet = QuestionSet.getInstance();

        switch (text) {
            case "/learn":
                return new LearnCommand(questionSet);
            case "/exam":
                return new ExamCommand(questionSet);
            case "/study":
                return new StudyCommand(questionSet);
            default:
                return null;
        }
    }


    public abstract Sender execute(long chatId);
}