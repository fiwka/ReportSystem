package ru.kdev.ReportSystem.gui;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import ru.kdev.ReportSystem.ReportSystem;
import ru.kdev.ReportSystem.utils.BookUtil;
import ru.kdev.ReportSystem.utils.MessageUtil;

public class ReportBook {
    private ReportSystem plugin;
    private ItemStack book;
    private Player who;
    private Player sender;

    public ReportBook(ReportSystem plugin, Player who, Player sender) {
        this.plugin = plugin;
        this.who = who;
        this.sender = sender;
    }

    public void createBook() {
        book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        bookMeta.setTitle("Репорт");
        bookMeta.setAuthor("Fiwka");

        BaseComponent cheating = new TextComponent(MessageUtil.getLocaleMessage("type-character") + " " + MessageUtil.getLocaleMessage("cheating") + "\n");
        BaseComponent chat = new TextComponent(MessageUtil.getLocaleMessage("type-character") + " " + MessageUtil.getLocaleMessage("chat") + "\n");
        BaseComponent badName = new TextComponent(MessageUtil.getLocaleMessage("type-character") + " " + MessageUtil.getLocaleMessage("bad-name") + "\n");
        BaseComponent badSkin = new TextComponent(MessageUtil.getLocaleMessage("type-character") + " " + MessageUtil.getLocaleMessage("bad-skin"));

        cheating.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + who.getName() + " cheating"));
        chat.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + who.getName() + " chat"));
        badName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + who.getName() + " bad-name"));
        badSkin.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + who.getName() + " bad-skin"));

        BaseComponent[] components = {
                new TextComponent(MessageUtil.getLocaleMessage("select-type") + "\n\n"),
                cheating,
                chat,
                badName,
                badSkin
        };

        bookMeta.spigot().addPage(components);
        book.setItemMeta(bookMeta);
    }

    public void createSuccessBook() {
        book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        bookMeta.setTitle("Репорт");
        bookMeta.setAuthor("Fiwka");

        bookMeta.addPage(MessageUtil.getLocaleMessage("success").replace("%player%", who.getName()));
        book.setItemMeta(bookMeta);
    }

    public void show() {
        BookUtil.openBook(book, sender);
    }
}
