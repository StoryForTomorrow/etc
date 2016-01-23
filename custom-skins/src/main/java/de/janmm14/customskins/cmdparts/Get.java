package de.janmm14.customskins.cmdparts;

import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;
import de.janmm14.customskins.CustomSkins;
import de.janmm14.customskins.data.Proxy;
import de.janmm14.customskins.data.Skin;
import de.janmm14.customskins.util.Network;

import com.google.common.collect.Lists;
import lombok.NonNull;

public class Get extends CmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins get <playername>").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins get "))
		.append(" - ").color(ChatColor.GRAY)
		.append("prepares the skin of the given player to be set").color(ChatColor.YELLOW).create();

	public Get() {
		super("customskins.get", "get", "g");
	}

	private CustomSkins plugin = CustomSkins.getPlugin();

	@Override
	public void onCommand(@NonNull final CommandSender cs, @NonNull final String[] args) {
		if (args.length < 1) {
			sendUsage(cs);
			return;
		}
		// TODO better chat messages
		Bukkit.getScheduler().runTaskAsynchronously(CustomSkins.getPlugin(), new Runnable() {
			@Override
			public void run() {
				cs.sendMessage("Retrieving skin's data...");
				Proxy proxy = null;
				String skinId = args[0];
				String name = skinId.toLowerCase();
				UUID uuid = Network.getUuidsOfNames(name).values().iterator().next();

				for (Proxy p : plugin.getData().getProxies()) {
					if ((p.getLastUsedMillis(uuid) + (1000 * 75)) < System.currentTimeMillis()) {
						proxy = p;
						break;
					}
				}

				if (proxy == null) {
					cs.sendMessage("Currently is no proxy available to handle your request.");
					return;
				}

				Skin skin = Network.getSkin(uuid, proxy, name);
				if (skin == null) {
					cs.sendMessage("An error occurred while trying to recieve the skin's data.");
					return;
				}
				proxy.setLastUsedMillis(uuid, System.currentTimeMillis());
				plugin.getData().setCachedSkin(skin, "from_user: " + skinId);
				cs.sendMessage("Skin data loaded. Skin " + name + " is ready for usage with /customskins set");
			}
		});
	}

	@NonNull
	@Override
	public List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		return Lists.newArrayList();
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§6/customskins get <playername>&7 - &eprepares the skin of the given player to be set");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
