package de.janmm14.customskins.cmdparts;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;

import com.google.common.collect.Lists;
import lombok.NonNull;

public class DeleteFull extends CmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins deletefull <skinname>").color(ChatColor.DARK_GRAY).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins deletefull "))
		.append(" - ").color(ChatColor.DARK_GRAY)
		.append("delets the skin with the provided skin name fully").color(ChatColor.DARK_GRAY).create();

	public DeleteFull() {
		super("customskins.deletefull", "deletefull");
	}

	@Override
	public void onCommand(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		cs.sendMessage("This command is not available yet. Look for future updates of this plugin.");
		// TODO implement
	}

	@NonNull
	@Override
	public List<String> onTabComplete(@NonNull CommandSender cs, @NonNull String[] restArgs) {
		return Lists.newArrayList();
	}

	@Override
	protected void sendUsageToNonPlayer(@NonNull CommandSender cs) {
		cs.sendMessage("§8/customskins deletefull <skinname> - delets the skin with the provided skin id fully");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
