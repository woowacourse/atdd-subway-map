package wooteco.subway.admin.domain;

import java.util.Arrays;
import java.util.List;

public class Color {
	private final static String FORMAT_HEAD = "bg";
	private final static String FORMAT_TAIL = "-%s";

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

		if (tokens.size() == 2) {
			return new Color(tokens.get(1), "");
		}

		if (tokens.size() == 3) {
			return new Color(tokens.get(1), tokens.get(2));
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
