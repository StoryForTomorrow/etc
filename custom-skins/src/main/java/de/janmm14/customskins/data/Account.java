package de.janmm14.customskins.data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Account {

	@NonNull
	private final String name;
	@NonNull
	private final UUID uuid;
	@NonNull
	private final String email;
	@NonNull
	private final String password;
	private final AtomicBoolean used = new AtomicBoolean(false);

	Account(@NonNull String name, @NonNull UUID uuid, @NonNull String email, @NonNull String password) {
		this.name = name;
		this.uuid = uuid;
		this.email = email;
		this.password = password;
	}

	@NonNull
	public String getName() {
		return name;
	}

	@NonNull
	public UUID getUuid() {
		return uuid;
	}

	@NonNull
	public String getEmail() {
		return email;
	}

	@NonNull
	public String getPassword() {
		return password;
	}

	@NonNull
	public AtomicBoolean getUsed() {
		return used;
	}
}
