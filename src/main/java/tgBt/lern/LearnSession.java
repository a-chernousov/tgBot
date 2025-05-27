package tgBt.lern;

import tgBt.question.Question;
import tgBt.question.QuestionSet;
import tgBt.StateSession;

public class LearnSession implements StateSession {
    private QuestionSet questionSet;
    private State state;
    private Question currentQ;

    public LearnSession(QuestionSet questionSet) {
        this.questionSet = questionSet;
        this.state = State.INIT;
    }

    @Override
    public Question action() {
        currentQ = questionSet.getRandomQuestion();
        state = State.ACTION;
        return currentQ;
    }

    @Override
    public boolean check(String answer) {
        boolean isCorrect = currentQ.isCorrect(answer);
        state = isCorrect ? State.END : State.CHECK;
        return isCorrect;
    }

    @Override
    public String end() {
        state = State.END;
        return "Правильный ответ: " + currentQ.getCorrectAnswer() +
                "\nВведите /learn для нового вопроса.";
    }

    @Override
    public State getState() {
        return state;
    }
}