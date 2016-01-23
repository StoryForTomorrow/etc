package de.janmm14.customskins.data;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Skin { // TODO add source

	private final String skinName;
	@NonNull
	private final String data;
	@NonNull
	private final String signature;

	public Skin(String skinName, String data, String signature) {
		this.skinName = skinName;
		this.data = data;
		this.signature = signature;
	}

	public String getSkinName() {
		return skinName;
	}

	public String getData() {
		return data;
	}

	public String getSignature() {
		return signature;
	}
}
