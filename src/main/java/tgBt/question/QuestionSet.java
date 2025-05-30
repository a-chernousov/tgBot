package tgBt.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import tgBt.testUnit.LoggerUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class QuestionSet {
    private static final Logger logger = LoggerUtil.getLogger();
    private List<Question> questions;

    public QuestionSet() {
        this.questions = new ArrayList<>();
    }

    public List<Question> getQuestions() {
        return questions;
    }


    public void loadFromJson(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        questions = mapper.readValue(inputStream,
                mapper.getTypeFactory().constructCollectionType(List.class, Question.class));
        logger.info("Загружено " + questions.size() + " вопросов из JSON-файла.");
    }

    public void loadFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String questionText = line;
                List<String> correctAnswers = new ArrayList<>();
                List<String> wrongAnswers = new ArrayList<>();

                while ((line = reader.readLine()) != null && line.startsWith("+")) {
                    correctAnswers.add(line.substring(1).trim());
                }

                while (line != null && line.startsWith("-")) {
                    wrongAnswers.add(line.substring(1).trim());
                    line = reader.readLine();
                }

                questions.add(new Question(questionText, correctAnswers, wrongAnswers));
            }
        }
    }

    public Question getRandomQuestion() {
        if (questions.isEmpty()) {
            return null;
        }
        return questions.get((int) (Math.random() * questions.size()));
    }

    public int size() {
        return questions.size();
    }

    // Singleton реализация с загрузкой из ресурсов
    private static QuestionSet instance;

    public static QuestionSet getInstance() {
        if (instance == null) {
            instance = new QuestionSet();
            try (InputStream inputStream = QuestionSet.class.getClassLoader()
                    .getResourceAsStream("lotr_questions.json")) {

                if (inputStream == null) {
                    logger.severe("Файл lotr_questions.json не найден в ресурсах.");
                    throw new FileNotFoundException("Файл lotr_questions.json не найден в ресурсах.");
                }

                instance.loadFromJson(inputStream);
                logger.info("Вопросы успешно загружены из JSON-файла.");

            } catch (IOException e) {
                logger.severe("Ошибка загрузки JSON-файла с вопросами: " + e.getMessage());
            }
        }
        return instance;
    }
}
