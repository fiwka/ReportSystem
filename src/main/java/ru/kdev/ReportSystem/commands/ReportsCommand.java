package ru.kdev.ReportSystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kdev.ReportSystem.ReportSystem;
import ru.kdev.ReportSystem.gui.Reports;
import ru.kdev.ReportSystem.utils.MessageUtil;

public class ReportsCommand implements CommandExecutor {
    private ReportSystem plugin;

    public ReportsCommand(ReportSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command only for player!");
            return true;
        }
        Player player = (Player) commandSender;
        if(player.hasPermission("report.admin")) {
            if(plugin.getDatabase().getReportsCount() == 0) {
                player.sendMessage(MessageUtil.getLocaleMessage("no-reports"));
                return false;
            }
            Reports.setPlugin(plugin);
            Reports.REPORTS_INVENTORY().open(player);
        } else {
            player.sendMessage(MessageUtil.getLocaleMessage("no-perm"));
        }
        return false;
    }
}
