package ru.kdev.ReportSystem.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ru.kdev.ReportSystem.ReportSystem;
import ru.kdev.ReportSystem.utils.MessageUtil;

import java.util.*;

public class Reports implements InventoryProvider {
    private static ReportSystem plugin;
    private static Reports reports;

    public static void setPlugin(ReportSystem plugin){
        if(reports == null)
            reports = new Reports();
        if(Reports.plugin == null)
            Reports.plugin = plugin;

    }

    public static SmartInventory REPORTS_INVENTORY() {
        return SmartInventory
                .builder()
                .id("reports")
                .provider(reports)
                .manager(plugin.getInventoryManager())
                .size(3, 9)
                .title(MessageUtil.getLocaleMessage("reports-title"))
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Map<Integer, String> map = plugin.getDatabase().getReports();
        Pagination pagination = contents.pagination();
        ClickableItem[] items = new ClickableItem[plugin.getDatabase().getReportsCount()];

        int slotId = 0;
        for(Map.Entry<Integer, String> entry : map.entrySet()) {
            Player who = plugin.getDatabase().getWho(entry.getKey());
            ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setOwningPlayer(who);
            meta.setDisplayName(ChatColor.GREEN + who.getName());
            List<String> list = plugin.getConfig().getStringList("locale.reports-head-lore");
            List<String> tmp_list = new ArrayList<>();
            list.stream().forEach(item -> tmp_list.add(ChatColor.translateAlternateColorCodes('&', item.replace("%type%", MessageUtil.getLocaleMessage(entry.getValue())))));
            meta.setLore(tmp_list);
            itemStack.setItemMeta(meta);
            items[slotId] = ClickableItem.of(itemStack, e -> {
                ReportPage.setArguments(plugin, who, MessageUtil.getLocaleMessage(entry.getValue()), entry.getKey());
                ReportPage.REPORT_INVENTORY().open(player);
            });
            slotId++;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(7);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));

        ItemStack next = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(MessageUtil.getLocaleMessage("forward"));
        next.setItemMeta(nextMeta);

        ItemStack back = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(MessageUtil.getLocaleMessage("back"));
        back.setItemMeta(backMeta);

        contents.set(2, 3, ClickableItem.of(back,
                e -> REPORTS_INVENTORY().open(player, pagination.previous().getPage())));
        contents.set(2, 5, ClickableItem.of(next,
                e -> REPORTS_INVENTORY().open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
