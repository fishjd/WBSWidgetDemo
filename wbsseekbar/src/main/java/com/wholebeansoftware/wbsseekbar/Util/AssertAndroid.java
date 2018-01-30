package com.wholebeansoftware.wbsseekbar.Util;


import com.wholebeansoftware.wbsseekbar.BuildConfig;

/**
 * A helper class to handle asserts.
 * This is not a jUnit class.
 */
public class AssertAndroid {

	/**
	 * Throws an AssertError if in Debug mode.
	 *
	 * @param message
	 * @param phrase  If false throws Assert
	 */
	public static void assertDebug(String message, Boolean phrase) {
		if (BuildConfig.DEBUG && (false == phrase)) {
			throw new AssertionError(message);
		}
	}

	public static void errorDebug(String message) {
		assertDebug(message, null);
	}

	/**
	 * Throws an AssertError if in Debug mode.
	 *
	 * @param phrase If false throws Assert
	 */
	public static void assertDebug(Boolean phrase) {
		assertDebug("Assertion Error", phrase);
	}

	/**
	 * Throws an AssertError if in Debug mode.
	 *
	 * @param message
	 */
	public static void assertDebug(String message) {
		if (BuildConfig.DEBUG) {
			throw new AssertionError(message);
		}
	}

	public static void assertDebugNotNull(String message, Object o) {
		if (BuildConfig.DEBUG) {
			if (o == null) {
				throw new AssertionError(message);
			}
		}
	}


}
