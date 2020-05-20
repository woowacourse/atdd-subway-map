package wooteco.subway.admin.domain;

import java.util.Arrays;
import java.util.List;

public class Color {
	private static final String FORMAT_HEAD = "bg";
	private static final String FORMAT_TAIL = "-%s";
	private static final int NON_VALUE_SIZE = 2;
	private static final int DEFAULT_SIZE = 3;
	private static final int TYPE_INDEX = 1;
	private static final int VALUE_INDEX = 2;
	private static final String EMPTY = "";

	private final String type;
	private final String value;

	public Color(final String type, final String value) {
		this.type = type;
		this.value = value;
	}

	public static Color ofBgColor(String type, String value) {
		return new Color(type, value);
	}

	public static Color ofBgColor(String bgColor) {
		List<String> tokens = Arrays.asList(bgColor.split("-"));

		if (tokens.size() == NON_VALUE_SIZE) {
			return new Color(tokens.get(TYPE_INDEX), EMPTY);
		}

		if (tokens.size() == DEFAULT_SIZE) {
			return new Color(tokens.get(TYPE_INDEX), tokens.get(VALUE_INDEX));
		}

		throw new IllegalArgumentException("적절하지 않은 형식입니다.");
	}

	public String toBgColor() {
		if (value.isEmpty()) {
			return String.format(FORMAT_HEAD + FORMAT_TAIL, type);
		}
		return String.format(FORMAT_HEAD + FORMAT_TAIL + FORMAT_TAIL, type, value);
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}
}
