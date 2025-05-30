package tgBt.testUnit;

import java.util.logging.*;
import java.io.IOException;

public class LoggerUtil {
    private static final String LOG_FILE = "bot_logs.txt";
    private static Logger logger;

    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(LoggerUtil.class.getName());
            logger.setUseParentHandlers(false); // Отключаем стандартные обработчики

            try {
                // Настройка вывода в файл
                FileHandler fileHandler = new FileHandler(LOG_FILE, true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);

                // Настройка вывода в консоль
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(consoleHandler);

                logger.setLevel(Level.ALL); // Уровень логирования - ALL (все сообщения)
            } catch (IOException e) {
                System.err.println("Ошибка при настройке логгера: " + e.getMessage());
            }
        }
        return logger;
    }
}