package de.janmm14.customskins;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.janmm14.customskins.cmdparts.DeleteFull;
import de.janmm14.customskins.cmdparts.Get;
import de.janmm14.customskins.cmdparts.Info;
import de.janmm14.customskins.cmdparts.Prepare;
import de.janmm14.customskins.cmdparts.Reload;
import de.janmm14.customskins.cmdparts.Reset;
import de.janmm14.customskins.cmdparts.ResetAll;
import de.janmm14.customskins.cmdparts.ResetMe;
import de.janmm14.customskins.cmdparts.Set;
import de.janmm14.customskins.cmdparts.SetMe;
import de.janmm14.customskins.data.Data;
import de.janmm14.customskins.listener.PlayerInfoPacketListener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.Maps;
import org.mcstats.Metrics;
import lombok.Getter;
import lombok.NonNull;
import com.comphenix.protocol.async.AsyncListenerHandler;
import com.google.common.base.Function;
import com.comphenix.protocol.async.AsyncRunnable;
import org.bukkit.Bukkit;

public class CustomSkins extends JavaPlugin {

	public static final BaseComponent[] USAGE_HEADER = new ComponentBuilder(">>> CustomSkins Help <<<").color(ChatColor.LIGHT_PURPLE).create();
	private final Map<String, CmdPart> cmdParts = Maps.newHashMapWithExpectedSize(16);
	private ProtocolManager protocolManager;
	@Getter
	private Data data;
	@Getter
	private PlayerInfoPacketListener listener;
	@Getter
	private static CustomSkins plugin;

	public void registerCmdPart(@NonNull CmdPart cmdPart) {
		for (String part : cmdPart.getCommandParts()) {
			cmdParts.put(part.trim().toLowerCase(), cmdPart);
		}
	}

	@Override
	public void onEnable() {
		plugin = this;
		new File(getDataFolder(), "cache").mkdirs();
		data = new Data(this);
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			getLogger().warning("PluginMetrics (mcstats.org) error:");
			e.printStackTrace();
		}

		protocolManager = ProtocolLibrary.getProtocolManager();
		AsyncListenerHandler ah = protocolManager.getAsynchronousManager().registerAsyncHandler(listener = new PlayerInfoPacketListener(this));
		ah.start(new Function<AsyncRunnable, Void>() {

				@Override
				public Void apply(AsyncRunnable runnable) {
					Bukkit.getScheduler().runTaskAsynchronously(CustomSkins.this, runnable); 
					return null;
				}
			
		});

		registerCmdPart(new Reload());
		registerCmdPart(new DeleteFull());
		registerCmdPart(new Info());
		registerCmdPart(new Prepare());
		registerCmdPart(new Reload());
		registerCmdPart(new Reset());
		registerCmdPart(new ResetAll());
		registerCmdPart(new ResetMe());
		registerCmdPart(new Set());
		registerCmdPart(new SetMe());
		registerCmdPart(new Get());
	}

	@Override
	public void onDisable() {
		protocolManager.removePacketListeners(
				this);
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("customskins")) {
			cs.sendMessage("§4Unknown command " + cmd.getName() + " attached to CustomSkins!");
			getLogger().warning("CommandSender " + cs + " used unknown command " + cmd.getName() + " which was attached to CustomSkins!");
			return true;
		}
		if (args.length > 0) {
			String args0 = args[0].trim().toLowerCase(Locale.ENGLISH);
			if (cmdParts.containsKey(args0)) {
				CmdPart cmdPart = cmdParts.get(args0.trim().toLowerCase());
				if (cs.hasPermission(cmdPart.getPermission())) {
					cmdPart.onCommand(cs, Arrays.copyOfRange(args, 1, args.length));
					return true;
				} else {
					cs.sendMessage("§4You are not allowed to use this command!");
					return true;
				}
			} else {
				cs.sendMessage("§4Subcommand §6" + args0 + " §4not found!");
			}
		}
		boolean isPlayer = cs instanceof Player;
		if (isPlayer) {
			((Player) cs).spigot().sendMessage(USAGE_HEADER);
		} else {
			cs.sendMessage("§d>>> CustomSkins Help <<<");
		}
		for (CmdPart cmdPart : cmdParts.values()) {
			if (!isPlayer && cmdPart instanceof PlayerOnlyCmdPart) {
				cs.sendMessage("The following command may only be used by players:");
			}
			cmdPart.sendUsage(cs);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("customskins")) {
			return null;
		}
		// TODO implement
		if (data.isDebug()) {
			cs.sendMessage(Arrays.toString(args));
		}
		return null;
	}

}
