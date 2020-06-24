package ru.kdev.ReportSystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kdev.ReportSystem.ReportSystem;
import ru.kdev.ReportSystem.gui.ReportBook;
import ru.kdev.ReportSystem.utils.MessageUtil;

public class Report implements CommandExecutor {
    ReportSystem plugin;

    public Report(ReportSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command only for player!");
            return true;
        }
        if(args.length < 1) {
            sender.sendMessage(MessageUtil.getLocaleMessage("usage"));
        } else {
            Player player = (Player) sender;
            Player who = Bukkit.getPlayer(args[0]);
            if(args.length == 1) {
                ReportBook reportBook = new ReportBook(plugin, who, player);
                reportBook.createBook();
                reportBook.show();
            } else if(args.length == 2) {
                if(args[1].equals("cheating") || args[1].equals("chat") || args[1].equals("bad-name") || args[1].equals("bad-skin")) {
                    plugin.getDatabase().addReport(player, args[1], who);
                    ReportBook reportBook = new ReportBook(plugin, who, player);
                    reportBook.createSuccessBook();
                    reportBook.show();
                } else {
                    sender.sendMessage(MessageUtil.getLocaleMessage("usage"));
                }
            } else {
                sender.sendMessage(MessageUtil.getLocaleMessage("usage"));
            }
        }
        return false;
    }
}
