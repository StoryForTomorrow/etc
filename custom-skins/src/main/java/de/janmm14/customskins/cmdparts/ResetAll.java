package de.janmm14.customskins.cmdparts;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;

import com.google.common.collect.Lists;
import lombok.NonNull;

public class ResetAll extends CmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins resetall").color(ChatColor.DARK_GRAY).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins resetall"))
		.append(" - ").color(ChatColor.DARK_GRAY)
		.append("resets ").color(ChatColor.DARK_GRAY)
		.append("ALL").color(ChatColor.DARK_GRAY).bold(true).underlined(true).
			event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("WARNING!!!").color(ChatColor.DARK_RED).bold(true).underlined(true).create()))
		.append(" skins to the default minecraft.net ones").color(ChatColor.DARK_GRAY).create();

	public ResetAll() {
		super("customskins.reset", "reset");
	}

	@Override
	public void onCommand(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		cs.sendMessage("This command is not available yet. Look for future updates of this plugin.");
		// TODO implement
		// TODO double-check/ask if the player really wants it
	}

	@NonNull
	@Override
	public java.util.List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		return Lists.newArrayList();
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§8/customskins reset <name> - resets §4ALL §8skins to the default minecraft.net one");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
