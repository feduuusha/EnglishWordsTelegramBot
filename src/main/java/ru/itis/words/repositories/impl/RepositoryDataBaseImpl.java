package ru.itis.words.repositories.impl;

import org.apache.log4j.Logger;
import ru.itis.words.models.User;
import ru.itis.words.repositories.Repository;

import javax.sql.DataSource;
import java.sql.*;

public class RepositoryDataBaseImpl implements Repository {
    private final Logger logger = Logger.getLogger(RepositoryDataBaseImpl.class);
    private final DataSource dataSource;
    // language=sql
    private static final String SELECT_FROM_USER_WHERE_TG_ID = "select * from \"user\" where tg_id = ?";
    // language=sql
    private static final String SQL_SELECT_USER_WHERE_ID = "select * from \"user\" where tg_id = ?";
    // language=sql
    private static final String SQL_UPDATE_TABLE_USER_SET_NUMBER_OF_CARDS = "update \"user\" set number_of_cards = number_of_cards + 1 where tg_id = ?";
    // language=sql
    private static final String SQL_UPDATE_TABLE_USER_SET_NUMBER_OF_CORRECT_CARDS = "update \"user\" set number_of_correct_cards = number_of_correct_cards + 1 where tg_id = ?";
    // language=sql
    private static final String SQL_UPDATE_TABLE_USER_SET_NUMBER_OF_INCORRECT_CARDS = "update \"user\" set number_of_incorrect_cards = number_of_incorrect_cards + 1 where tg_id = ?";
    // language=sql
    private static final String SQL_INSERT_INTO_USER = "insert into \"user\" (tg_id) values (?)";
    // language=sql
    private static final String SQL_CREATE_TABLE = "create table \"user\" (\n" +
            "    \"tg_id\" bigint primary key not null,\n" +
            "    \"number_of_cards\" integer default 0,\n" +
            "    \"number_of_correct_cards\" integer default 0,\n" +
            "    \"number_of_incorrect_cards\" integer default 0\n" +
            ")";
    //language=sql
    private static final String SQL_DROP_If_EXIST = "drop table if exists \"user\"";

    public RepositoryDataBaseImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection connection = dataSource.getConnection();
             Statement statementForDrop = connection.createStatement();
             Statement statementForCreate = connection.createStatement()) {
            statementForDrop.executeUpdate(SQL_DROP_If_EXIST);
            statementForCreate.executeUpdate(SQL_CREATE_TABLE);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }


    @Override
    public User findUserByTgId(long tgId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_USER_WHERE_ID)) {
            preparedStatement.setLong(1, tgId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return new User(resultSet.getLong("tg_id"), resultSet.getInt("number_of_cards"),
                    resultSet.getInt("number_of_correct_cards"), resultSet.getInt("number_of_incorrect_cards"));
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void incrementNumberOfCards(long tgId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_TABLE_USER_SET_NUMBER_OF_CARDS)) {
            preparedStatement.setLong(1, tgId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void incrementNumberOfCorrectCards(long tgId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_TABLE_USER_SET_NUMBER_OF_CORRECT_CARDS)) {
            preparedStatement.setLong(1, tgId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void incrementNumberOfIncorrectCards(long tgId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_TABLE_USER_SET_NUMBER_OF_INCORRECT_CARDS)) {
            preparedStatement.setLong(1, tgId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void addUser(long tgId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(SELECT_FROM_USER_WHERE_TG_ID);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_INTO_USER)) {
            selectStatement.setLong(1, tgId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (!resultSet.next()) {
                preparedStatement.setLong(1, tgId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
