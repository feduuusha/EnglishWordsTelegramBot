# English Words Telegram Bot

## Table Of Contents

* [About the Project](#about-the-project)
* [Built With](#built-with)
* [Functionality](#functionality)
* [Application requirements](#application-requirements)
* [Usage](#usage)
* [How to use the application](#how-to-use-the-application)
* [License](#license)

## About the Project

Telegram bot that can help you learn English. 

The bot generates cards with a random English word, and the user must choose the correct translation into Russian. 

After choosing the answer, the bot will output the correct translation, as well as the definitions of the hidden word.

The application was created in order to study the interaction with the API of Internet services, as well as with databases.
I also gained experience in deploying the application on the server. The server on which the bot is running is paid for until August 19, you can safely test it in your [Telegram application](https://t.me/bot_for_education_project_bot).

## Built With
- Java 17
- [Free Dictionary API](https://dictionaryapi.dev)
- [Google Translation API](https://cloud.google.com/translate/docs/reference/rest)
- [Random word API](https://random-word-api.herokuapp.com/home)
- [Jackson](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core)
- [Telegram Bots](https://mvnrepository.com/artifact/org.telegram/telegrambots)
- [PostgreSQL](https://mvnrepository.com/artifact/org.postgresql/postgresql)
- [HikariCP](https://mvnrepository.com/artifact/com.zaxxer/HikariCP)
- [Log4j](https://mvnrepository.com/artifact/log4j/log4j )

## Functionality 

* Generating cards with an English word
* Storing statistics of correct and incorrect answers

## Application requirements
* Java 17+
* Telegram API Key from BotFather

## Usage
* Start screen
  ![alt text](https://i.ibb.co/CVNF54y/start-img.jpg)

* Start menu
  ![alt text](https://i.ibb.co/7vDh4xJ/start-menu.jpg)


* New card
  ![alt text](https://i.ibb.co/9Wjxh18/new-card.jpg)

* Response to the card
  ![alt text](https://i.ibb.co/tJWddff/bot-answer.jpg)

* Profile
  ![alt text](https://i.ibb.co/K64bvz9/profile.jpg)

## How to use the application
1. Download .jar from [here](https://drive.google.com/file/d/1m2e9DqFf6m4ECqU66It5smFmH3XQzrlz/view?usp=sharing):
2. Create config.txt file (in folder with .jar) in this format:
        
   BOT_TOKEN~{Your telegram api key from botfather}
   
   JDBC_URL~jdbc:postgresql://localhost:5432/{table_name}
   
   DATA_BASE_USERNAME~{user_name}
   
   DATA_BASE_PASSWORD~{password}
   
4. Open terminal in folder with .jar
5. Run the command: java -jar EnglishWordsTelegramBot.jar
6. Use the Bot

## License
Distributed under the MIT License. [Click](https://github.com/feduuusha/BooleanOperationsApp?tab=MIT-1-ov-file#readme) for more information.
