package tgBt.exam;

import tgBt.question.Question;
import tgBt.question.QuestionSet;
import tgBt.StateSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExamSession implements StateSession {
    private QuestionSet questionSet;
    private int totalQuestions;
    private int currentQuestion;
    private int correctAnswers;
    private State state;
    private Question currentQ;
    private List<Question> remainingQuestions;
    private final Random random = new Random();

    public ExamSession(QuestionSet questionSet, int totalQuestions) {
        this.questionSet = questionSet;
        this.totalQuestions = totalQuestions;
        this.currentQuestion = 0;
        this.correctAnswers = 0;
        this.state = State.INIT;

        // создаём копию всех вопросов и будем их оттуда удалять по мере использования
        this.remainingQuestions = new ArrayList<>(questionSet.getQuestions());
    }

    @Override
    public Question action() {
        if (currentQuestion >= totalQuestions || remainingQuestions.isEmpty()) {
            state = State.END;
            return null;
        }

        // выбираем случайный вопрос из оставшихся
        int index = random.nextInt(remainingQuestions.size());
        currentQ = remainingQuestions.remove(index); // удаляем, чтобы не повторялся

        currentQuestion++;
        state = State.ACTION;
        return currentQ;
    }

    @Override
    public boolean check(String answer) {
        boolean isCorrect = false;

        try {
            int optionIndex = Integer.parseInt(answer.trim()) - 1;
            if (optionIndex >= 0 && optionIndex < currentQ.getOptions().size()) {
                String selectedOption = currentQ.getOptions().get(optionIndex);
                isCorrect = currentQ.isCorrect(selectedOption);
            }
        } catch (NumberFormatException e) {
            isCorrect = false;
        }

        if (isCorrect) {
            correctAnswers++;
        }

        state = State.CHECK;
        return isCorrect;
    }

    @Override
    public String end() {
        state = State.END;
        return String.format(
                "Экзамен завершен!\nПравильных ответов: %d из %d (%.1f%%)\n\nВведите /start для возврата в главное меню.",
                correctAnswers, totalQuestions, (correctAnswers * 100.0 / totalQuestions));
    }

    @Override
    public State getState() {
        return state;
    }
}
