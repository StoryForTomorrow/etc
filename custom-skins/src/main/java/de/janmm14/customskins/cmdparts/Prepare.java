package de.janmm14.customskins.cmdparts;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CmdPart;
import de.janmm14.customskins.CustomSkins;
import de.janmm14.customskins.data.Account;
import de.janmm14.customskins.data.Proxy;
import de.janmm14.customskins.data.Skin;
import de.janmm14.customskins.util.Network;
import de.janmm14.minecraftchangeskin.api.Callback;
import de.janmm14.minecraftchangeskin.api.SkinChangeParams;
import de.janmm14.minecraftchangeskin.api.SkinChanger;
import de.janmm14.minecraftchangeskin.api.SkinModel;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import lombok.NonNull;

public class Prepare extends CmdPart {

	public static final BaseComponent[] USAGE = new ComponentBuilder("/customskins prepare <skinname> <url>").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/customskins prepare "))
		.append(" - ").color(ChatColor.GRAY)
		.append("prepares the skin at the given url to be set").color(ChatColor.YELLOW).create();

	public Prepare() {
		super("customskins.prepare", "prepare", "p", "prep");
	}

	@Override
	public void onCommand(@NonNull final CommandSender cs, @NonNull final String[] args) {
		// TODO better chat messages
		Bukkit.getScheduler().runTaskAsynchronously(CustomSkins.getPlugin(), new Runnable() {

			@Override
			public void run() {
				if (args.length < 2) {
					sendUsage(cs);
				} else {
					try {
						if (!StringUtils.isAlphanumeric(args[0])) {
							cs.sendMessage("Error: The skin name has to be alphanumeric!");
							return;
						}
						File file = new File(CustomSkins.getPlugin().getDataFolder(), "cache" + File.separator + args[0].toLowerCase() + ".png");
						// TODO check and validate url syntax

						boolean success = Network.downloadSkin(args[1], file);
						// TODO remove success boolean from Network's method and throw any errors, catch these in an inner catch block
						if (!success) {
							cs.sendMessage("Could not download skin. Error unknown.");
							return;
						}
						cs.sendMessage("Skin " + args[0] + " downloaded.");


						cs.sendMessage("Uploading skin " + args[0].toLowerCase() + " to minecraft.net ...");
						final CustomSkins plugin = CustomSkins.getPlugin();
						Account acc_ = null;
						Proxy proxy_ = null;
						boolean found = false;
						HashSet<Account> account = plugin.getData().getAccounts();
						if (account.size() <= 0) {
							cs.sendMessage("Error: No login info specified in config.");
							return;
						}
						for (Proxy p : plugin.getData().getProxies()) {
							for (Account a : account) {
								if (!a.getUsed().get() && (p.getLastUsedMillis(a.getUuid()) + (1000 * 75)) < System.currentTimeMillis()) {
									a.getUsed().set(true);
									proxy_ = p;
									acc_ = a;
									found = true;
									break;
								}
							}
							if (found)
								break;
						}
						if (acc_ == null) {
							cs.sendMessage("Unable to continue because no proxy would be able to recieve the skin data of any player right now. Retry later by reusing the command.");
							return;
						}
						final Account acc = acc_;
						final Proxy proxy = proxy_;
						SkinChanger.changeSkin(
							SkinChangeParams.Builder.create()
								.email(acc.getEmail())
								.password(acc.getPassword())
								.image(file)
								.skinModel(SkinModel.STEVE).build(), // TODO allow change of skin model
							new Callback<Boolean>() {
								@Override
								public void done(Boolean p1, Throwable p2) {
									if (p2 != null) {
										p2.printStackTrace();
										cs.sendMessage("Error while uploading the skin to minecraft.net!");
										return;
									}
									cs.sendMessage("Skin " + args[0].toLowerCase() + " uploaded to minecraft.net!");
									try {
										long millis = CustomSkins.getPlugin().getData().getAccountCooldown() * 1000;
										System.out.println("waiting milliseconds: " + millis);
										Thread.sleep(millis);
									} catch (InterruptedException e) {
										CustomSkins.getPlugin().getLogger().warning("Could not wait the required amount of time for updating the skin.");
										e.printStackTrace();
									}
									cs.sendMessage("Retrieving skin's data...");
									Skin skin = Network.getSkin(acc.getUuid(), proxy, args[0].toLowerCase());
									if (skin == null) {
										cs.sendMessage("An error occurred while trying to recieve the skin's data.");
										return;
									}
									proxy.setLastUsedMillis(acc.getUuid(), System.currentTimeMillis());
									plugin.getData().setCachedSkin(skin, "url: " + args[1]);
									cs.sendMessage("Skin data loaded. Skin " + args[0].toLowerCase() + " is ready for usage with /customskins set");
									if (plugin.getData().getAccountCooldown() != 0) {
										try {
											Thread.sleep(TimeUnit.SECONDS.toMillis(plugin.getData().getAccountCooldown()));
										} catch (InterruptedException ignored) {
										}
									}
									acc.getUsed().set(false);
								}
							});
					} catch (Exception e) {
						cs.sendMessage("Could not prepare skin. Error: " + e.getMessage());
						CustomSkins.getPlugin().getLogger().warning("Could not prepare skin from url " + args[1] + " Error:");
						e.printStackTrace();
					}
				}
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
		cs.sendMessage("§6/customskins prepare <skinname> <url>§7 - §eprepares the skin at the given url to be set");
	}

	@Override
	protected void sendUsageToPlayer(@NonNull Player p) {
		p.spigot().sendMessage(USAGE);
	}
}
