package de.janmm14.customskins.cmdparts;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CustomSkins;
import de.janmm14.customskins.PlayerOnlyCmdPart;

import lombok.NonNull;

public class SetMe extends PlayerOnlyCmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins setme <id/name>").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins setme "))
		.append(" - ").color(ChatColor.GRAY)
		.append("sets your skin to the skin of the given playername or the downloaded skin").color(ChatColor.YELLOW).create();

	public SetMe() {
		super("customskins.setme", "setme");
	}

	@Override
	public void onPlayerCommand(@NonNull final Player p, @NonNull final String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(CustomSkins.getPlugin(), new Runnable() {
			@Override
			public void run() {
				CustomSkins plugin = CustomSkins.getPlugin();
				String skinId = args[0].toLowerCase();
				if (plugin.getData().getCachedSkin(skinId) == null) {
					p.sendMessage("Skin " + skinId + "not found!");
				}
				plugin.getData().setUsedSkin(p.getUniqueId(), skinId);
				plugin.getListener().updatePlayerSkin(p);
				p.sendMessage("Skin updated!");
			}
		});
	}

	@NonNull
	@Override
	public List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		// TODO implement
		return null;
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§6/customskins setme <id/name>§7 - §esets your skin to the skin of the given playername or the downloaded skin");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
