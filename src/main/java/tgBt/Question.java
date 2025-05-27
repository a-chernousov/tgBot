package tgBt;

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
        List<String> options = new ArrayList<>();

        // Добавляем до 3 правильных ответов
        int correctToAdd = Math.min(3, correctAnswers.size());
        options.addAll(correctAnswers.subList(0, correctToAdd));

        // Добавляем один неправильный (если есть)
        if (!wrongAnswers.isEmpty()) {
            options.add(wrongAnswers.get(0));
        }

        // Перемешиваем
        Collections.shuffle(options);
        return options;
    }

    public boolean isCorrect(String answer) {
        return correctAnswers.contains(answer);
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
