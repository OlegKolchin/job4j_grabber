package ru.job4j.grabber;

import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.SqlRuParse;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                cnn.prepareStatement("insert into post(name, text, link, created) values (?, ?, ?, ?)")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setDate(4, Date.valueOf(post.getCreated().toLocalDate()));
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(new Post(resultSet.getInt("id"), resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            new Timestamp(resultSet.getDate("created").getTime()).toLocalDateTime()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        String s = String.format("select * from post where id=%s", id);
        try (PreparedStatement statement = cnn.prepareStatement(s)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    return new Post(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            new Timestamp(resultSet.getDate("created").getTime()).toLocalDateTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            SqlRuParse parse = new SqlRuParse(new SqlRuDateTimeParser());
            Post post = parse.detail("https://www.sql.ru/forum/1337903/integracionnyy-razrabotchik-sql");
            Properties config = new Properties();
            config.load(in);
            PsqlStore store = new PsqlStore(config);
            store.save(post);
            System.out.println(store.findById(1).getTitle());
            System.out.println(store.getAll().get(0).getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}