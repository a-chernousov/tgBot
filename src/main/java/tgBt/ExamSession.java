package tgBt;

import java.util.ArrayList;
import java.util.List;

public class ExamSession implements StateSession {
    private QuestionSet questionSet;
    private int totalQuestions;
    private int currentQuestion;
    private int correctAnswers;
    private State state;
    private Question currentQ;

    public ExamSession(QuestionSet questionSet, int totalQuestions) {
        this.questionSet = questionSet;
        this.totalQuestions = totalQuestions;
        this.currentQuestion = 0;
        this.correctAnswers = 0;
        this.state = State.INIT;
    }

    @Override
    public Question action() {
        if (currentQuestion >= totalQuestions) {
            state = State.END;
            return null;
        }

        currentQ = questionSet.getRandomQuestion();
        currentQuestion++;
        state = State.ACTION;
        return currentQ;
    }

    @Override
    public boolean check(String answer) {
        boolean isCorrect = currentQ.isCorrect(answer);
        if (isCorrect) {
            correctAnswers++;
        }
        state = State.CHECK;
        return isCorrect;
    }

    @Override
    public String end() {
        state = State.END;
        return String.format("Экзамен завершен!\nПравильных ответов: %d из %d (%.1f%%)",
                correctAnswers, totalQuestions, (correctAnswers * 100.0 / totalQuestions));
    }

    @Override
    public State getState() {
        return state;
    }
}
