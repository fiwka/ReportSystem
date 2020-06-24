package ru.kdev.ReportSystem.database;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Database {
    void addReport(Player player, String type, Player who);
    Map<Integer, String> getReports();
    Player getWho(int id);
    int getReportsCount();
    void deleteReport(int id);
    void connect(ConfigurationSection configurationSection);
    void load();
    void init();
}
