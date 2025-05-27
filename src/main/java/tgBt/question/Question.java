package tgBt.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Question {
    @JsonProperty("question")
    private String questionText;

    @JsonProperty("correct_answers")
    private List<String> correctAnswers;

    @JsonProperty("wrong_answers")
    private List<String> wrongAnswers;

    private List<String> cachedOptions;

    public Question() {
        // Обязательный конструктор для Jackson
    }

    public Question(String questionText, List<String> correctAnswers, List<String> wrongAnswers) {
        this.questionText = questionText;
        this.correctAnswers = new ArrayList<>(correctAnswers);
        this.wrongAnswers = new ArrayList<>(wrongAnswers);
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }

    public List<String> getWrongAnswers() {
        return wrongAnswers;
    }

    public List<String> getOptions() {
        if (cachedOptions == null) {
            // Генерируем варианты только один раз
            cachedOptions = new ArrayList<>();
            if (!correctAnswers.isEmpty()) {
                cachedOptions.add(correctAnswers.get(0)); // Берём первый правильный
            }
            cachedOptions.addAll(wrongAnswers);
            Collections.shuffle(cachedOptions);
        }
        return cachedOptions;
    }

    public boolean isCorrect(String answer) {
        if (answer == null) return false;

        // Нормализуем ввод (регистр + пробелы)
        String normalizedAnswer = answer.trim().toLowerCase();

        // Проверяем, является ли ответ числом (выбор по индексу)
        if (normalizedAnswer.matches("\\d+")) {
            int optionIndex = Integer.parseInt(normalizedAnswer) - 1;
            if (optionIndex >= 0 && optionIndex < getOptions().size()) {
                String selectedOption = getOptions().get(optionIndex).trim().toLowerCase();
                return correctAnswers.stream()
                        .anyMatch(correct -> correct.trim().toLowerCase().equals(selectedOption));
            }
            return false;
        }

        // Если ответ не число - сравниваем текст напрямую
        return correctAnswers.stream()
                .anyMatch(correct -> correct.trim().toLowerCase().equals(normalizedAnswer));
    }

    public String getCorrectAnswer() {
        return correctAnswers.isEmpty() ? "" : correctAnswers.get(0);
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionText='" + questionText + '\'' +
                ", correctAnswers=" + correctAnswers +
                ", wrongAnswers=" + wrongAnswers +
                '}';
    }
}
