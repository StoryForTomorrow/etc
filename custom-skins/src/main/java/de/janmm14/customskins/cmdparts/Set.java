package de.janmm14.customskins.cmdparts;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;
import de.janmm14.customskins.CustomSkins;

import com.google.common.collect.Lists;
import lombok.NonNull;

public class Set extends CmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins set <name> <skinname>").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins set "))
		.append(" - ").color(ChatColor.GRAY)
		.append("sets the skin of the player with the given name to the skin of the given playername or the downloaded skin").color(ChatColor.YELLOW).create();

	public Set() {
		super("customskins.set", "set");
	}

	@Override
	public void onCommand(@NonNull final CommandSender cs, @NonNull final String[] args) {
		if (args.length < 2) {
			sendUsage(cs);
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(CustomSkins.getPlugin(), new Runnable() {
			@Override
			public void run() {
				CustomSkins plugin = CustomSkins.getPlugin();
				Player p = Bukkit.getPlayerExact(args[0]);
				if (p == null) {
					cs.sendMessage("Player not found / not online. Setting skins of offline players is currently unsupported.");
					return;
				}
				String skinId = args[1].toLowerCase();
				if (plugin.getData().getCachedSkin(skinId) == null) {
					cs.sendMessage("Skin " + skinId + "not found!");
				}
				plugin.getData().setUsedSkin(p.getUniqueId(), skinId);
				plugin.getListener().updatePlayerSkin(p);
				cs.sendMessage("Skin of " + p.getName() + " changed!");
				if (cs instanceof ConsoleCommandSender) {
					p.sendMessage("Your skin was changed by the mysterious one sitting in front of my heard!");
				} else
					p.sendMessage("Your skin was changed by " + cs.getName());
			}
		});
	}

	@NonNull
	@Override
	public List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] args) {
		// TODO provide tab complete for skin names
		if (args.length != 1)
			return Lists.newArrayList();

		String s = args[0].toLowerCase();
		List<String> l = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			String name = p.getName();
			if (name.toLowerCase().startsWith(s)) {
				l.add(name);
			}
		}
		return l;
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§6/customskins set <name> <id/name>§7 - §esets the skin of the player with the given name to the skin of the given playername or the downloaded skin");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
