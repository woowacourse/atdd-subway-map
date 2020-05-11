package wooteco.subway.admin.util;

public class EasyExceptionMaker {

	public static void validateThrowIAE(boolean condition, String exceptionMessage) {
		if (condition) {
			throw new IllegalArgumentException(exceptionMessage);
		}
	}
}
