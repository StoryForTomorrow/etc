package de.janmm14.customskins.cmdparts;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.PlayerOnlyCmdPart;

import com.google.common.collect.Lists;
import lombok.NonNull;

public class ResetMe extends PlayerOnlyCmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins resetme").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins resetme"))
		.append(" - ").color(ChatColor.GRAY)
		.append("resets your skin to the default minecraft.net one").color(ChatColor.YELLOW).create();

	public ResetMe() {
		super("customskins.resetme", "resetme");
	}

	@Override
	public void onPlayerCommand(@NonNull Player p, @NonNull String[] restArgs) {
		p.sendMessage("This command is not available yet. Look for future updates of this plugin.");
		// TODO implement
	}

	@NonNull
	@Override
	public java.util.List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		return Lists.newArrayList();
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§6/customskins resetme§7 - §eresets your skin to the default minecraft.net one");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
