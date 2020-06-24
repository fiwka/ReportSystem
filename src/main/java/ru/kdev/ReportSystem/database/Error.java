package ru.kdev.ReportSystem.database;
import ru.kdev.ReportSystem.ReportSystem;

import java.util.logging.Level;

public class Error {
    public static void execute(ReportSystem plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(ReportSystem plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
