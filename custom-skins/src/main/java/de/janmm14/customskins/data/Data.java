package de.janmm14.customskins.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.janmm14.customskins.CustomSkins;
import de.janmm14.customskins.proxyapis.com.yasakvar.YasakvarApi;
import de.janmm14.customskins.util.Util;

import org.jetbrains.annotations.Contract;
import lombok.NonNull;

public class Data { //TODO config option to toggle caching of uuidSkinIdCache, skinCache, availableSkinNamesCache

	private final CustomSkins plugin;
	private FileConfiguration cfg;

	// caches
	//private final Map<UUID, String> uuidSkinIdCache = new HashMap<>();
	//private final Map<String, Skin> skinCache = new HashMap<>();

	private HashSet<String> availableSkinNamesCache;
	private HashSet<Account> accountCache;

	private HashSet<Proxy> proxyCache;
	private YasakvarApi yasakvarApi;
	private boolean yasakvarApiEnabled = false;

	public Data(@NonNull CustomSkins plugin) {
		this.plugin = plugin;
		reloadConfig();
	}

	public long getAccountCooldown() {
		return cfg.getLong("mojangaccountcooldown");
	}

	public boolean isDebug() {
		return cfg.getBoolean("debug");
	}

	public void reloadConfig() {
		accountCache = null;
		availableSkinNamesCache = null;
		proxyCache = null;
		//skinCache.clear();
		//uuidSkinIdCache.clear();

		plugin.reloadConfig();
		cfg = plugin.getConfig();
		cfg.options().copyDefaults(true).copyHeader(true).pathSeparator('.');

		cfg.addDefault("debug", false);
		cfg.addDefault("mojangaccountcooldown", 4);
		cfg.set("mojangaccountcooldowninfo", "In Seconds. 0 to disable.");
		cfg.set("mojangaccounts.dummyaccount.uuid", "the uuid of dummyaccount");
		cfg.set("mojangaccounts.dummyaccount.email", "dummy@example.com");
		cfg.set("mojangaccounts.dummyaccount.password", "password");
		cfg.set("mojangaccounts.dummyaccount.info", Arrays.asList("You should not delete this, any accounts with the email dummy@example.com are ignored.", "NEVER OVERRIDE THE EXAMPLES! THEY ARE RESET EVERY PLUGIN / SERVER RELOAD!"));
		cfg.addDefault("proxyapis.yasakvar.enabled", false);
		cfg.addDefault("proxyapis.yasakvar.countries", Arrays.asList("GERMANY", "EUROPE", "USA"));
		cfg.set("proxyapis.yasakvar.info", Arrays.asList("See www.yasakvar.com for a full list of countries.", "You should just use proxies near your server's location if you care about very high speed.", "Every country is requested separate. The API just returns up to 20 proxies per request, so you should specify at least some countries to have a decent amount of proxies."));
		cfg.set("proxies.configexample.host", "proxy.example.com");
		cfg.set("proxies.configexample.port", 8080);
		cfg.set("proxies.configexamplewithcredentials.host", "127.0.0.1");
		cfg.set("proxies.configexamplewithcredentials.port", 39124);
		cfg.set("proxies.configexamplewithcredentials.username", "username");
		cfg.set("proxies.configexamplewithcredentials.password", "password");
		cfg.set("proxies.configexample.info", "Any proxy with any of the hosts of the examples are ignored. NEVER OVERRIDE THE EXAMPLES! THEY ARE RESET EVERY PLUGIN / SERVER RELOAD!");
		saveConfig();

		yasakvarApiEnabled = cfg.getBoolean("proxyapis.yasakvar.enabled");
		if (yasakvarApiEnabled) {
			yasakvarApi = new YasakvarApi(plugin);
		}
	}

	public void saveConfig() {
		plugin.saveConfig();
	}

	@NonNull
	public HashSet<Account> getAccounts() {
		if (accountCache == null) {
			HashSet<Account> set = new HashSet<>();
			for (String accName : cfg.getConfigurationSection("mojangaccounts").getKeys(false)) {
				String email = cfg.getString("mojangaccounts." + accName + ".email");
				if (!"dummy@example.com".equalsIgnoreCase(email)) {
					String uuid = cfg.getString("mojangaccounts." + accName + ".uuid");
					if (!uuid.contains("-")) {
						uuid = Util.insertDashUUID(uuid);
					}
					String password = cfg.getString("mojangaccounts." + accName + ".password");
					set.add(new Account(accName, UUID.fromString(uuid), email, password));
				}
			}
			return set;
		} else {
			return accountCache;
		}
	}

	@NonNull
	public HashSet<String> getAllSkinNames() {
		if (availableSkinNamesCache == null) {
			HashSet<String> set = new HashSet<>();
			for (String id : cfg.getConfigurationSection("skins").getKeys(false)) {
				set.add(id);
			}
			return set;
		} else {
			return availableSkinNamesCache;
		}
	}

	@Nullable
	public Skin getCachedSkin(@NonNull String skinName) {
		skinName = skinName.toLowerCase();
		//if (!skinCache.containsKey(skinName)) {
		String data = cfg.getString("skins." + skinName + ".data");
		if (data == null || data.trim().isEmpty())
			return null;
		Skin skin = new Skin(skinName, data, cfg.getString("skins." + skinName + ".signature"));
		//skinCache.put(skinName, skin);
		return skin;
		/*} else {
			return skinCache.get(skinName);
		}*/
	}

	public void setCachedSkin(@NonNull Skin skin, @NonNull String source) {
		//skinCache.put(skin.getSkinName(), skin);
		cfg.set("skins." + skin.getSkinName() + ".data", skin.getData());
		cfg.set("skins." + skin.getSkinName() + ".signature", skin.getSignature());
		cfg.set("skins." + skin.getSkinName() + ".source", source);
		saveConfig();
	}

	@Contract("null,_ -> fail; !null,null -> _; !null,!null -> !null")
	public String getSkinSource(@NonNull Skin skin, @Nullable String unknownSource) {
		return cfg.getString("skins." + skin.getSkinName() + ".source", unknownSource);
	}

	public boolean deleteCachedSkin(@NonNull String skinName) {
		if (getCachedSkin(skinName) == null)
			return false;
		//skinCache.remove(skinName);
		cfg.set("skins." + skinName + ".data", null);
		cfg.set("skins." + skinName + ".signature", null);
		cfg.set("skins." + skinName + ".source", null);
		cfg.set("skins." + skinName, null);
		// TODO reset players using the deleted skin (can maybe be done on-the-fly, for example if the data is accessed
		saveConfig();
		return true;
	}

	@NonNull
	public HashSet<Proxy> getProxies() {
		if (proxyCache == null) {
			HashSet<Proxy> set = new HashSet<>();
			set.add(Proxy.NO_PROXY);
			for (String pid : cfg.getConfigurationSection("proxies").getKeys(false)) {
				String host = cfg.getString("proxies." + pid + "host");
				if (host == null || "proxy.example.com".equalsIgnoreCase(host) || "127.0.0.1".equalsIgnoreCase(host)) {
					continue;
				}
				String username = cfg.getString("proxies." + pid + "username");
				int port = cfg.getInt("proxies." + pid + "port");
				if (username == null || username.trim().isEmpty()) {
					plugin.getLogger().info("pid: " + pid + ", host: " + host + ", port: " + port);
					Proxy p = Proxy.create(pid,
						host,
						port);
					set.add(p);
				} else {
					Proxy p = Proxy.create(pid,
						host,
						port,
						username,
						cfg.getString("proxies." + pid + "password"));
					set.add(p);
				}
			}
			if (yasakvarApiEnabled) {
				set.addAll(yasakvarApi.getProxies(new HashSet<>(cfg.getStringList("proxyapis.yasakvar.countries"))));
			}
			proxyCache = set;
		}
		return proxyCache;
	}

	public String getSkinNameIdByUUID(@NonNull UUID uuid) {
		//if (!uuidSkinIdCache.containsKey(uuid)) {
		String skinId = cfg.getString("usedskins." + uuid);
		//uuidSkinIdCache.put(uuid, skinId);
		return skinId;
		/*} else {
			return uuidSkinIdCache.get(uuid);
		}*/
	}

	public void setUsedSkin(@NonNull UUID uuid, @NonNull String skinId) {
		cfg.set("usedskins." + uuid, skinId);
		//uuidSkinIdCache.put(uuid, skinId);
		saveConfig();
	}

	public void resetSkin(@NonNull UUID uuid) {
		cfg.set("usedskins." + uuid, null);
		//uuidSkinIdCache.remove(uuid);
		Player p = Bukkit.getPlayer(uuid);
		if (p != null) {
			plugin.getListener().updatePlayerSkin(p);
		}
		saveConfig();
	}
}
