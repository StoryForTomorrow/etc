package de.janmm14.customskins.cmdparts;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;
import de.janmm14.customskins.CustomSkins;

import com.google.common.collect.Lists;
import lombok.NonNull;

public class Reload extends CmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins reload").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins reload"))
		.append(" - ").color(ChatColor.GRAY)
		.append("reloads the configuration").color(ChatColor.YELLOW)
		.create();

	public Reload() {
		super("customskins.reload", "reload");
	}

	@Override
	public void onCommand(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		CustomSkins plugin = CustomSkins.getPlugin();
		plugin.getData().reloadConfig();
		cs.sendMessage("Configuration reloaded!");
	}

	@Override
	@NonNull
	public List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		return Lists.newArrayList();
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§6/customskins reload§7 - §ereloads the configuration");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
