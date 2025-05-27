package tgBt.learn;

import tgBt.question.Question;
import tgBt.question.QuestionSet;
import tgBt.StateSession;

public class LearnSession implements StateSession {
    private QuestionSet questionSet;
    private State state;
    private Question currentQ;
    private int correctAnswers;
    private int totalQuestions;

    public LearnSession(QuestionSet questionSet) {
        this.questionSet = questionSet;
        this.state = State.INIT;
        this.correctAnswers = 0;
        this.totalQuestions = 0;
    }

    @Override
    public Question action() {
        currentQ = questionSet.getRandomQuestion();
        state = State.ACTION;
        totalQuestions++;
        return currentQ;
    }

    @Override
    public boolean check(String answer) {
        boolean isCorrect = false;

        try {
            // Добавляем отладочный вывод
            System.out.println("User answer: '" + answer + "'");
            System.out.println("Options: " + currentQ.getOptions());
            System.out.println("Correct answers: " + currentQ.getCorrectAnswers());

            isCorrect = currentQ.isCorrect(answer);

            if (isCorrect) {
                correctAnswers++;
                state = State.END;
            } else {
                state = State.CHECK;
            }

            System.out.println("Is correct: " + isCorrect);
        } catch (Exception e) {
            System.err.println("Error checking answer: " + e.getMessage());
            state = State.CHECK;
        }

        return isCorrect;
    }

    @Override
    public String end() {
        state = State.END;
        return "✅ Правильно!\n" +
                "Правильный ответ: " + currentQ.getCorrectAnswer() + "\n" +
                "Статистика: " + correctAnswers + " из " + totalQuestions + "\n" +
                "Введите /learn для нового вопроса.";
    }

    @Override
    public State getState() {
        return state;
    }

    public String getCorrectAnswer() {
        return currentQ.getCorrectAnswer();
    }
}