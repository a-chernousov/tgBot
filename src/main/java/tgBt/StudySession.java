package tgBt;

public class StudySession implements StateSession {
    private QuestionSet questionSet;
    private State state;
    private Question currentQ;

    public StudySession(QuestionSet questionSet) {
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
        state = State.CHECK;
        return isCorrect;
    }

    @Override
    public String end() {
        state = State.END;
        return "Режим обучения завершен. Введите /study чтобы начать снова.";
    }

    @Override
    public State getState() {
        return state;
    }

    public Question getCurrentQ() {
        return currentQ;
    }
}
