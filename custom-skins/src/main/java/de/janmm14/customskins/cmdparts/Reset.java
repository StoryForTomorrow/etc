package de.janmm14.customskins.cmdparts;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;

import lombok.NonNull;

public class Reset extends CmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins reset <name>").color(ChatColor.DARK_GRAY).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins reset "))
		.append(" - ").color(ChatColor.DARK_GRAY)
		.append("resets the skin of the given player to the default minecraft.net one").color(ChatColor.DARK_GRAY).create();

	public Reset() {
		super("customskins.reset", "reset");
	}

	@Override
	public void onCommand(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		cs.sendMessage("This command is not available yet. Look for future updates of this plugin.");
		// TODO implement
	}

	@NonNull
	@Override
	public java.util.List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		// TODO implement
		return null;
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§8/customskins reset <name> - resets the skin of the given player to the default minecraft.net one");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
