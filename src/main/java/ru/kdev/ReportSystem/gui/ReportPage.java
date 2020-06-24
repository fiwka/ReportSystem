package ru.kdev.ReportSystem.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ru.kdev.ReportSystem.ReportSystem;
import ru.kdev.ReportSystem.utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class ReportPage implements InventoryProvider {
    public static ReportSystem plugin;
    private static int id;
    private static Player who;
    private static String type;
    private static ReportPage reportPage;

    public static void setArguments(ReportSystem plugin, Player who, String type, int id) {
        if(reportPage == null)
            reportPage = new ReportPage();
        if(ReportPage.plugin == null)
            ReportPage.plugin = plugin;
        ReportPage.who = who;
        ReportPage.type = type;
        ReportPage.id = id;
    }

    public static SmartInventory REPORT_INVENTORY() {
        return SmartInventory
                .builder()
                .id("reportPage")
                .provider(new ReportPage())
                .manager(plugin.getInventoryManager())
                .size(3, 9)
                .title(MessageUtil.getLocaleMessage("reports-title"))
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(who);
        meta.setDisplayName(ChatColor.GREEN + who.getName());
        List<String> list = plugin.getConfig().getStringList("locale.report-page-head-lore");
        List<String> tmp_list = new ArrayList<>();
        list.stream().forEach(item -> tmp_list.add(ChatColor.translateAlternateColorCodes('&', item.replace("%type%", type))));
        meta.setLore(tmp_list);
        itemStack.setItemMeta(meta);

        contents.fill(ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15)));

        contents.set(0, 4, ClickableItem.empty(itemStack));

        ItemStack itemStack1 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta1 = itemStack1.getItemMeta();
        meta1.setDisplayName(MessageUtil.getLocaleMessage("teleport"));
        itemStack1.setItemMeta(meta1);

        contents.set(2, 2, ClickableItem.of(itemStack1, e -> player.teleport(who)));

        ItemStack itemStack2 = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta2 = itemStack2.getItemMeta();
        meta2.setDisplayName(MessageUtil.getLocaleMessage("back"));
        itemStack2.setItemMeta(meta2);

        contents.set(2, 4, ClickableItem.of(itemStack2, e -> player.performCommand("reports")));

        ItemStack itemStack3 = new ItemStack(Material.BARRIER);
        ItemMeta meta3 = itemStack3.getItemMeta();
        meta3.setDisplayName(MessageUtil.getLocaleMessage("delete"));
        itemStack3.setItemMeta(meta3);

        contents.set(2, 6, ClickableItem.of(itemStack3, e -> { plugin.getDatabase().deleteReport(id); player.closeInventory(); }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
