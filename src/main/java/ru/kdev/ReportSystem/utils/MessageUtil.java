package ru.kdev.ReportSystem.utils;

import org.bukkit.ChatColor;
import ru.kdev.ReportSystem.ReportSystem;

public class MessageUtil {
    public static String getLocaleMessage(String name) {
        ReportSystem reportSystem = ReportSystem.getPlugin(ReportSystem.class);
        return ChatColor.translateAlternateColorCodes('&', reportSystem.getConfig().getString("locale." + name));
    }
}
