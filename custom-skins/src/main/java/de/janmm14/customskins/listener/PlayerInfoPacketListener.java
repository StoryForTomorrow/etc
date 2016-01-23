package de.janmm14.customskins.listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.janmm14.customskins.CustomSkins;
import de.janmm14.customskins.data.Skin;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Supplier;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.NonNull;

public class PlayerInfoPacketListener extends PacketAdapter implements Listener {

	private final CustomSkins plugin;

	public PlayerInfoPacketListener(@NonNull CustomSkins plugin) {
		super(new AdapterParameteters()
			.plugin(plugin)
			.gamePhase(GamePhase.BOTH)
			.serverSide()
			.optionAsync()
			.listenerPriority(ListenerPriority.HIGHEST)
			.types(PacketType.Play.Server.PLAYER_INFO));
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * shownTo, shown -> display name
	 */
	private final HashBasedTable<UUID, UUID, WrappedChatComponent> displayNameCache = HashBasedTable.create();
	private final ReentrantLock displayNameCacheLock = new ReentrantLock();

	@Override
	public void onPacketSending(PacketEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.PLAYER_INFO) {
			return;
		}
		WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());
		if (packet.getAction() == EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME) {
			for (PlayerInfoData pd : packet.getData()) {
				if (pd.getDisplayName() == null) {
					displayNameCacheLock.lock();
					displayNameCache.remove(event.getPacket(), pd.getProfile().getUUID());
					displayNameCacheLock.unlock();
				} else {
					displayNameCacheLock.lock();
					displayNameCache.put(event.getPlayer().getUniqueId(), pd.getProfile().getUUID(), pd.getDisplayName());
					displayNameCacheLock.unlock();
				}
			}
			return;
		} else if (packet.getAction() != EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
			return;
		}
		try {
			List<PlayerInfoData> data = packet.getData();
			List<PlayerInfoData> ndata = new ArrayList<>(data.size());
			Supplier<List<WrappedSignedProperty>> listSupplier = new Supplier<List<WrappedSignedProperty>>() {
				@Override
				public List<WrappedSignedProperty> get() {
					return Lists.newArrayList();
				}
			};
			for (PlayerInfoData pd : data) {
				WrappedGameProfile profile = pd.getProfile();
				Multimap<String, WrappedSignedProperty> res = Multimaps.newListMultimap(
					Maps.<String, Collection<WrappedSignedProperty>>newHashMapWithExpectedSize(profile.getProperties().size()),
					listSupplier);
				for (Map.Entry<String, WrappedSignedProperty> entry : profile.getProperties().entries()) {
					if (entry.getKey().equalsIgnoreCase("textures")) {
						String skinName = plugin.getData().getSkinNameIdByUUID(profile.getUUID());
						if (skinName != null && !skinName.isEmpty()) {
							Skin skin = plugin.getData().getCachedSkin(skinName);
							if (skin != null) {
								res.put(entry.getKey(), new WrappedSignedProperty(
									entry.getValue().getName(),
									skin.getData(),
									skin.getSignature()));
								continue;
							}
						}
					}
					res.put(entry.getKey(), entry.getValue());
				}
				if (res.isEmpty()) {
					String skinName = plugin.getData().getSkinNameIdByUUID(profile.getUUID());
					if (skinName != null && !skinName.isEmpty()) {
						Skin skin = plugin.getData().getCachedSkin(skinName);
						if (skin != null) {


							res.put("textures", new WrappedSignedProperty(

								"textures"
								,
								skin.getData()
								,
								skin.getSignature()));
						} else {
							res.putAll(profile.getProperties());
						}
					} else {
						res.putAll(profile.getProperties());
					}
				}
				profile.getProperties().clear();
				profile.getProperties().putAll(res);
				WrappedChatComponent displayName = pd.getDisplayName();
				if (displayName == null) {
					displayName = WrappedChatComponent.fromText(profile.getName());
				} else {
					displayNameCacheLock.lock();
					displayNameCache.put(event.getPlayer().getUniqueId(), profile.getUUID(), displayName);
					displayNameCacheLock.unlock();
				}
				ndata.add(new PlayerInfoData(profile, pd.getPing(), pd.getGameMode(), displayName));
			}

			try {
				packet.setData(ndata);
			} catch (RuntimeException e) {
				e.getCause().printStackTrace();
			}
			event.setPacket(packet.getHandle());
		} catch (Throwable t) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (PlayerInfoData playerInfoData : packet.getData()) {
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(playerInfoData.getProfile().getName());
			}
			plugin.getLogger().severe("Could not transform player list packet for player " + event.getPlayer().getName() + " about player's " + sb.toString());
			t.printStackTrace();
		}
	}

	public void updatePlayerSkin(@NonNull Player player) {
		try {
			WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
			packet.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
			WrappedGameProfile profile = new WrappedGameProfile(player.getUniqueId(), null);
			packet.setData(Lists.newArrayList(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(""))));
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (!p.spigot().getHiddenPlayers().contains(player)) {
					packet.sendPacket(p);
				}
			}
		} catch (Throwable t) {
			plugin.getLogger().severe("Could not update player skin for player " + player.getName() + ": Sending remove old skin failed!");
			t.printStackTrace();
		}
		try {
			WrapperPlayServerPlayerInfo basePacket = new WrapperPlayServerPlayerInfo();
			basePacket.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
			WrappedGameProfile profile = new WrappedGameProfile(player.getUniqueId(), player.getName());
			List<PlayerInfoData> playerInfoDatas = new ArrayList<>(1);
			final EnumWrappers.NativeGameMode nativeGameMode = EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode());

			int ping;
			Class<?> craftPlayer = player.getClass();
			try {
				Method getHandle = craftPlayer.getMethod("getHandle", (Class[]) null);
				Object entityPlayer = getHandle.invoke(player);
				Field pingField = entityPlayer.getClass().getField("ping");
				ping = (int) pingField.get(entityPlayer);
			} catch (Throwable t) {
				plugin.getLogger().warning("Could not get ping of player " + player.getName() + ". Error:");
				t.printStackTrace();
				ping = 0;
			}
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (!p.spigot().getHiddenPlayers().contains(player)) {
					WrappedChatComponent displayName = null;

					displayNameCacheLock.lock();
					if (displayNameCache.contains(p, player))
						displayName = displayNameCache.get(p, player);
					displayNameCacheLock.unlock();

					if (displayName == null) {
						displayName = WrappedChatComponent.fromText(player.getName());
					}
					playerInfoDatas.add(new PlayerInfoData(profile, ping, nativeGameMode, displayName));

					WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(basePacket.getHandle());
					packet.setData(playerInfoDatas);
					packet.sendPacket(p);
				}
			}
		} catch (Throwable t) {
			plugin.getLogger().severe("Could not update player skin for player " + player.getName() + ": Sending new skin data failed!");
			t.printStackTrace();
		}
		// TODO test if that works and if that makes the code above unneccessary
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (!p.spigot().getHiddenPlayers().contains(player)) {
				p.hidePlayer(player);
				p.showPlayer(player);
			}
		}
	}

	private void removeCachedDisplayNames(@NonNull UUID uuid) {
		displayNameCacheLock.lock();
		Map<UUID, WrappedChatComponent> row = new HashMap<>(displayNameCache.row(uuid));
		for (UUID shown : row.keySet()) {
			displayNameCache.remove(uuid, shown);
		}
		Map<UUID, WrappedChatComponent> column = new HashMap<>(displayNameCache.column(uuid));
		for (UUID shownTo : column.keySet()) {
			displayNameCache.remove(shownTo, uuid);
		}
		displayNameCacheLock.unlock();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		removeCachedDisplayNames(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		removeCachedDisplayNames(event.getPlayer().getUniqueId());
	}
}
