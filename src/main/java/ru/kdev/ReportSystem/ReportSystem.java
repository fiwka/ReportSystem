package ru.kdev.ReportSystem;

import fr.minuskube.inv.InventoryManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kdev.ReportSystem.commands.Report;
import ru.kdev.ReportSystem.commands.ReportsCommand;
import ru.kdev.ReportSystem.database.Database;
import ru.kdev.ReportSystem.database.MySQL;
import ru.kdev.ReportSystem.database.SQLite;

public class ReportSystem extends JavaPlugin {
    private InventoryManager inventoryManager;
    private Database db = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if(getConfig().getBoolean("mysql.enable")) {
            this.db = new MySQL(this);
            this.db.connect(getConfig().getConfigurationSection("mysql"));
            this.db.init();
        } else {
            this.db = new SQLite(this);
            this.db.load();
        }
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
        this.getCommand("report").setExecutor(new Report(this));
        this.getCommand("reports").setExecutor(new ReportsCommand(this));
    }

    public Database getDatabase() {
        return this.db;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
}
