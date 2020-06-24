package ru.kdev.ReportSystem.database;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.kdev.ReportSystem.ReportSystem;
import ru.kdev.ReportSystem.utils.ThrowableConsumer;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SQLite implements Database {
    String dbname;
    ReportSystem plugin;
    Connection connection;
    String table = "reports";
    public SQLite(ReportSystem plugin){
        this.plugin = plugin;
        dbname = "reports"; // Set the table name here e.g player_kills
    }

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS reports (" + // make sure to put your table name in here too.
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`player` text NOT NULL," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
            "`report_type` text NOT NULL," +
            "`who` text NOT NULL" +// This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
            ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.


    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
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

    private void async(ThrowableConsumer<PreparedStatement, SQLException> result, String sql, Object... objects) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
           try(PreparedStatement statement = createStatement(sql, objects)) {
               result.accept(statement);
           } catch (SQLException e) {
               e.printStackTrace();
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

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }

    @Override
    public void init() {

    }

    public void addReport(Player player, String type, Player who) {
        connection = getSQLConnection();
        execute("INSERT INTO reports (player, report_type, who) VALUES (?, ?, ?)",
                player.getName(), type, who.getName());
    }

    public Map<Integer, String> getReports() {
        connection = getSQLConnection();
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
        connection = getSQLConnection();
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
        connection = getSQLConnection();
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
        connection = getSQLConnection();
        try {
            execute("DELETE FROM reports WHERE id = ?",
                    id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(ConfigurationSection configurationSection) {

    }

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM reports WHERE player = ?");
            ps.setString(1, "te");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}

