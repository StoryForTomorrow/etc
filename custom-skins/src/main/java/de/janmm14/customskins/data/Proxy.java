package de.janmm14.customskins.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import de.janmm14.customskins.CustomSkins;

import org.jetbrains.annotations.Contract;
import lombok.NonNull;
import lombok.ToString;

@ToString
public class Proxy {

	public static final Proxy NO_PROXY = new Proxy("", "", Integer.MIN_VALUE + 8156, null, null);
	private final CustomSkins plugin = CustomSkins.getPlugin();
	@NonNull
	private final String id;
	@NonNull
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final transient Map<UUID, Long> lastUsedMillis = new HashMap<>();

	@Contract(
		"_,_,_,!null,null -> fail;" +
			"null,_,_,_,_ -> fail;" +
			"_,null,_,_,_ -> fail;" +
			"!null,!null,_,null,null -> !null;" +
			"!null,!null,_,!null,!null -> !null;")
	public static Proxy create(@NonNull String id, @NonNull String host, int port, @Nullable String username, @Nullable String password) {
		return new Proxy(id, host, port, username, password);
	}

	@NonNull
	public static Proxy create(@NonNull String id, @NonNull String host, int port) {
		return new Proxy(id, host, port, null, null);
	}

	private Proxy(@NonNull String id, @NonNull String host, int port, @Nullable String username, @Nullable String password) {
		if ((username == null || username.trim().isEmpty()) && (password != null && !password.trim().isEmpty())) {
			throw new IllegalArgumentException("proxy " + id + ": the username has to be set if the password is set and vice versa!");
		}
		this.id = id;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public boolean isNoProxy() {
		return this == NO_PROXY;
	}

	@NonNull
	public String getId() {
		return id;
	}

	@NonNull
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Nullable
	public String getUsername() {
		return username;
	}

	@Nullable
	public String getPassword() {
		return password;
	}

	public void setLastUsedMillis(@NonNull UUID uuid, long millis) {
		lastUsedMillis.put(uuid, millis);
		plugin.saveConfig();
	}

	public long getLastUsedMillis(@NonNull UUID uuid) {
		if (lastUsedMillis.containsKey(uuid))
			return lastUsedMillis.get(uuid);
		else {
			lastUsedMillis.put(uuid, 0L);
			return 0;
		}
	}

	@SuppressWarnings({"ConstantConditions", "RedundantIfStatement"})
	public boolean equals(Object o) {
		if (o == this)
			return true;
		else if (o == NO_PROXY)
			return false;
		if (!(o instanceof Proxy))
			return false;
		final Proxy other = (Proxy) o;
		if (!other.canEqual(this))
			return false;
		final Object this$plugin = this.plugin;
		final Object other$plugin = other.plugin;
		if (this$plugin == null ? other$plugin != null : !this$plugin.equals(other$plugin))
			return false;
		final Object this$id = this.getId();
		final Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id))
			return false;
		final Object this$host = this.getHost();
		final Object other$host = other.getHost();
		if (this$host == null ? other$host != null : !this$host.equals(other$host))
			return false;
		if (this.getPort() != other.getPort())
			return false;
		final Object this$username = this.getUsername();
		final Object other$username = other.getUsername();
		if (this$username == null ? other$username != null : !this$username.equals(other$username))
			return false;
		final Object this$password = this.getPassword();
		final Object other$password = other.getPassword();
		if (this$password == null ? other$password != null : !this$password.equals(other$password))
			return false;
		return true;
	}

	@SuppressWarnings("ConstantConditions")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $plugin = this.plugin;
		result = result * PRIME + ($plugin == null ? 0 : $plugin.hashCode());
		final Object $id = this.getId();
		result = result * PRIME + ($id == null ? 0 : $id.hashCode());
		final Object $host = this.getHost();
		result = result * PRIME + ($host == null ? 0 : $host.hashCode());
		result = result * PRIME + this.getPort();
		final Object $username = this.getUsername();
		result = result * PRIME + ($username == null ? 0 : $username.hashCode());
		final Object $password = this.getPassword();
		result = result * PRIME + ($password == null ? 0 : $password.hashCode());
		return result;
	}

	protected boolean canEqual(Object other) {
		return other instanceof Proxy;
	}
}
