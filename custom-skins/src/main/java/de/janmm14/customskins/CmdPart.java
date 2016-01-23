package de.janmm14.customskins;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.NonNull;

public abstract class CmdPart {

	@NonNull
	private final String[] commandParts;
	@NonNull
	private final String permission;

	public CmdPart(@NonNull String permission, @NonNull String... commandParts) {
		this.commandParts = commandParts;
		this.permission = permission;
	}

	public abstract void onCommand(@NonNull CommandSender cs, @NonNull String[] restArgs);

	@NonNull
	public abstract List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs);

	protected abstract void sendUsageToNonPlayer(@NonNull CommandSender cs);

	protected abstract void sendUsageToPlayer(@NonNull Player p);

	public final void sendUsage(@NonNull CommandSender cs) {
		if (cs instanceof Player) {
			sendUsageToPlayer((Player) cs);
		} else {
			sendUsageToNonPlayer(cs);
		}
	}

	@NonNull
	public final String[] getCommandParts() {
		return commandParts;
	}

	@NonNull
	public final String getPermission() {
		return permission;
	}
}
