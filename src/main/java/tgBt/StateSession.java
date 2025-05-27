package tgBt;

public interface StateSession {
    enum State {INIT, ACTION, CHECK, END, ERROR};

    Question action();
    boolean check(String answer);
    String end();
    State getState();
}
