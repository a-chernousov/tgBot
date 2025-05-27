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
        System.out.println("Все варианты: " + currentQ.getOptions());
        System.out.println("Выбрано: " + answer);
        System.out.println("Правильные ответы: " + currentQ.getCorrectAnswers());
        boolean isCorrect = false;

        try {
            int choice = Integer.parseInt(answer.trim()) - 1;
            List<String> options = currentQ.getOptions();

            if (choice >= 0 && choice < options.size()) {
                String selectedOption = options.get(choice);
                isCorrect = currentQ.isCorrect(selectedOption);

                // Увеличиваем счётчик только если ответ правильный!
                if (isCorrect) {
                    correctAnswers++;  // <- Вот здесь исправление
                }
            }
        } catch (NumberFormatException e) {
            return false;
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
