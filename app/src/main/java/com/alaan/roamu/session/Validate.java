package com.alaan.roamu.session;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class Validate {
	
	public static boolean isNull(@Nullable String str) {
		return str == null || str.equalsIgnoreCase("null") || str.trim().length() == 0;
	}
	
	@NonNull
	public static boolean isNotNull(@Nullable String str) {
		return !(str == null || str.equalsIgnoreCase("null") || str.trim().length() == 0);
	}
}
