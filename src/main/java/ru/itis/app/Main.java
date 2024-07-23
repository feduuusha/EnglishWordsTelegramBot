package ru.itis.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;
import ru.itis.words.controllers.Controller;
import ru.itis.words.controllers.impl.ControllerBotImpl;
import ru.itis.words.repositories.Repository;
import ru.itis.words.repositories.impl.RepositoryDataBaseImpl;
import ru.itis.words.services.Service;
import ru.itis.words.services.dictionaries.Dictionary;
import ru.itis.words.services.dictionaries.impl.DictionaryApiImpl;
import ru.itis.words.services.generators.Generator;
import ru.itis.words.services.generators.impl.GeneratorApiImpl;
import ru.itis.words.services.impl.ServiceWordImpl;
import ru.itis.words.services.translators.Translator;
import ru.itis.words.services.translators.impl.TranslatorApiImpl;
import ru.itis.words.util.configurators.Configurator;
import ru.itis.words.util.configurators.impl.ConfiguratorPropertyImpl;

import java.io.File;
import java.net.URISyntaxException;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
//        Получаем путь до директории jar файла
        String pathToJarDirectory;
        try {
            pathToJarDirectory = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
//        Записываем данные из конфига в переменные окружения
        Configurator configurator = new ConfiguratorPropertyImpl();
        configurator.setConfig(pathToJarDirectory + "/config.txt");
//        Настраимаем CP к нашей базе данных
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(System.getProperty("JDBC_URL"));
        hikariConfig.setUsername(System.getProperty("DATA_BASE_USERNAME"));
        hikariConfig.setPassword(System.getProperty("DATA_BASE_PASSWORD"));
        hikariConfig.setMaximumPoolSize(20);
//        Инициализируем объекты и запускаем приложение
        Repository repository = new RepositoryDataBaseImpl(new HikariDataSource(hikariConfig));
        Generator generator = new GeneratorApiImpl();
        Translator translator = new TranslatorApiImpl();
        Dictionary dictionary = new DictionaryApiImpl();
        Service service = new ServiceWordImpl(dictionary, generator, translator, repository);
        Controller controller = new ControllerBotImpl(service);
        controller.startApplication();
    }
}

