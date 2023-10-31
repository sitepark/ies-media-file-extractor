package com.sitepark.extractor.types;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EXIF Orientation
 *	Value	Row #0 is:	Column #0 is:
 *	1		Top			Left side
 *	2		Top			Right side
 *	3		Bottom		Right side
 *	4		Bottom		Left side
 *	5		Left side	Top
 *	6		Right side	Top
 *	7		Right side	Bottom
 *	8		Left side	Bottom
 *
 * see:
 * https://www.daveperrett.com/articles/2012/07/28/exif-orientation-handling-is-a-ghetto/
 * https://www.impulseadventure.com/photo/exif-orientation.html
 * https://www.impulseadventure.com/photo/images/orient_flag.gif
 * https://www.imagemagick.org/script/command-line-options.php#auto-orient
 * https://www.imagemagick.org/script/command-line-options.php#repage
 */
public enum Orientation {

	UNDIFINED(0, "Undefined"),
	TOP_LEFT(1, "TopLeft"),
	TOP_RIGHT(2, "TopRight"),
	BOTTOM_RIGHT(3, "BottomRight"),
	BOTTOM_LEFT(4, "BottomLeft"),
	LEFT_TOP(5, "LeftTop"),
	RIGHT_TOP(6, "RightTop"),
	LEFT_BOTTOM(7, "LeftBottom"),
	RIGHT_BOTTOM(8, "RightBottom");

	private int value;
	private String name;

	private static final Map<Integer, Orientation> ENUM_VALUE_MAP;
	private static final Map<String, Orientation> ENUM_NAME_MAP;

	static {
		Map<Integer, Orientation> valueMap = new ConcurrentHashMap<>();
		Map<String, Orientation> nameMap = new ConcurrentHashMap<>();
		for (Orientation instance : Orientation.values()) {
			valueMap.put(instance.getValue(), instance);
			nameMap.put(instance.getName(), instance);
		}
		ENUM_VALUE_MAP = Collections.unmodifiableMap(valueMap);
		ENUM_NAME_MAP = Collections.unmodifiableMap(nameMap);
	}

	public int getValue() {
		return this.value;
	}

	public String getName() {
		return this.name;
	}

	private Orientation(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public static Orientation ofString(String exifOrientation) {

		if (exifOrientation == null) {
			return null;
		}

		if (exifOrientation.equalsIgnoreCase("Undefined")) {
			return null;
		}

		if (exifOrientation.equalsIgnoreCase("TopLeft")) {
			return TOP_LEFT;
		} else if (exifOrientation.equalsIgnoreCase("TopRight")) {
			return TOP_RIGHT;
		} else if (exifOrientation.equalsIgnoreCase("BottomRight")) {
			return BOTTOM_RIGHT;
		} else if (exifOrientation.equalsIgnoreCase("BottomLeft")) {
			return BOTTOM_LEFT;
		} else if (exifOrientation.equalsIgnoreCase("LeftTop")) {
			return LEFT_TOP;
		} else if (exifOrientation.equalsIgnoreCase("RightTop")) {
			return RIGHT_TOP;
		} else if (exifOrientation.equalsIgnoreCase("LeftBottom")) {
			return LEFT_BOTTOM;
		} else if (exifOrientation.equalsIgnoreCase("RightBottom")) {
			return RIGHT_BOTTOM;
		} else {
			throw new IllegalArgumentException("unkown orientation: " + exifOrientation);
		}
	}

	public static Orientation ofName(String name) {
		Orientation orientation = ENUM_NAME_MAP.get(name);
		if (orientation == null) {
			throw new IllegalArgumentException("unkown orientation: " + name);
		} else {
			return orientation;
		}
	}
	public static Orientation ofValue(int value) {
		Orientation orientation = ENUM_VALUE_MAP.get(value);
		if (orientation == null) {
			throw new IllegalArgumentException("expect exif orientation values from 1 to 8: " + value);
		} else {
			return orientation;
		}
	}

	public boolean swapWidthAndHeight() {
		return this == LEFT_BOTTOM || this == LEFT_TOP || this == RIGHT_BOTTOM || this == RIGHT_TOP;
	}
}
