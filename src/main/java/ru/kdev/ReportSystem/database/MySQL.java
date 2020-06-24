package ru.kdev.ReportSystem.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.kdev.ReportSystem.ReportSystem;
import ru.kdev.ReportSystem.utils.ThrowableConsumer;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQL implements Database {
    private ReportSystem plugin;
    private Connection connection;

    public String createTableSQL = "CREATE TABLE IF NOT EXISTS reports (" + // make sure to put your table name in here too.
            "`id` int PRIMARY KEY AUTO_INCREMENT," +
            "`player` varchar(24) NOT NULL," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
            "`report_type` varchar(24) NOT NULL," +
            "`who` varchar(24) NOT NULL" +// This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
            ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.

    public MySQL(ReportSystem plugin) {
        this.plugin = plugin;
    }

    private PreparedStatement createStatement(String query, Object... objects) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        ps.setQueryTimeout(5);

        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];

                if (object == null) {
                    ps.setNull(i + 1, Types.VARCHAR);
                } else {
                    ps.setObject(i + 1, objects[i]);
                }
            }
        }

        if (objects == null || objects.length == 0) {
            ps.clearParameters();
        }

        return ps;
    }

    public void connect(ConfigurationSection section) {
        connect(
                section.getString("host"),
                section.getInt("port"),
                section.getString("database"),
                section.getString("user"),
                section.getString("password")
        );
    }

    @Override
    public void load() {

    }

    public void init() {
        execute(createTableSQL);
    }

    private void handleError(SQLException e) {
        e.printStackTrace();
    }

    public void connect(String host, int port, String database, String user, String password) {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(host);
            dataSource.setPort(port);
            dataSource.setDatabaseName(database);
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setServerTimezone("UTC");

            connection = dataSource.getConnection();
        } catch (SQLException e) {
            handleError(e);
        }
    }

    private void async(ThrowableConsumer<PreparedStatement, SQLException> result,
                       String query, Object... objects) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement ps = createStatement(query, objects)) {
                result.accept(ps);
            } catch (SQLException e) {
                handleError(e);
            }
        });
    }

    public void executeQuery(ThrowableConsumer<ResultSet, SQLException> result, String query, Object... objects) {
        async(ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                result.accept(rs);
            }
        }, query, objects);
    }

    public void execute(String query, Object... objects) {
        async(PreparedStatement::execute, query, objects);
    }

    public void addReport(Player player, String type, Player who) {
        execute("INSERT INTO reports (player, report_type, who) VALUES (?, ?, ?)",
                player.getName(), type, who.getName());
    }

    public Map<Integer, String> getReports() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports");
            ResultSet rs = statement.executeQuery();
            Map<Integer, String> map = new HashMap<>();
            while (rs.next()) {
                map.put(rs.getInt("id"), rs.getString("report_type"));
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Player getWho(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports WHERE id = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            Player player = null;
            while (rs.next()) {
                player = Bukkit.getPlayer(rs.getString("who"));
            }
            return player;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getReportsCount() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM reports");
            ResultSet rs = statement.executeQuery();
            int count = 0;
            while (rs.next()) {
                count = rs.getInt("count(*)");
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void deleteReport(int id) {
        try {
            execute("DELETE FROM reports WHERE id = ?",
                    id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
