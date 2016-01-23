package de.janmm14.customskins.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

	public static String insertDashUUID(@NonNull String dashFreeUuid) {
		return dashFreeUuid.substring(0, 8) + '-' + dashFreeUuid.substring(8, 12) + '-' + dashFreeUuid.substring(12, 16) + '-' + dashFreeUuid.substring(16, 20) + '-' + dashFreeUuid.substring(20, 32);

	}
}
