package ru.itis.words.controllers.impl;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import ru.itis.words.controllers.Controller;
import ru.itis.words.controllers.impl.bot.TelegramBot;
import ru.itis.words.services.Service;

public class ControllerBotImpl implements Controller {
    private final Logger logger = Logger.getLogger(ControllerBotImpl.class);
    private final Service service;

    public ControllerBotImpl(Service service) {
        this.service = service;
    }

    @Override
    public void startApplication() {
        String botToken = System.getProperty("BOT_TOKEN");
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()
        ) {
            botsApplication.registerBot(botToken, new TelegramBot(service, botToken));
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
