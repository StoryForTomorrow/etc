package de.janmm14.customskins;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.NonNull;

public abstract class PlayerOnlyCmdPart extends CmdPart {

	public PlayerOnlyCmdPart(@NonNull String permission, @NonNull String... commandParts) {
		super(permission, commandParts);
	}

	@Override
	public final void onCommand(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		if (cs instanceof Player) {
			onPlayerCommand((Player) cs, restArgs);
		} else {
			cs.sendMessage("§4This command may only be used by players.");
		}
	}

	public abstract void onPlayerCommand(@NonNull Player p, @NonNull String[] restArgs);

}
