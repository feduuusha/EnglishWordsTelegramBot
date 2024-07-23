package ru.itis.words.util.configurators.impl;

import org.apache.log4j.Logger;
import ru.itis.words.util.configurators.Configurator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfiguratorPropertyImpl implements Configurator {
    private final Logger logger = Logger.getLogger(ConfiguratorPropertyImpl.class);

    @Override
    public void setConfig(String pathToConfig) {
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToConfig))) {
            while (reader.ready()) {
                String[] property = reader.readLine().split("~");
                System.setProperty(property[0], property[1]);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
