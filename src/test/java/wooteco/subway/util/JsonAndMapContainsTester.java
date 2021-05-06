package wooteco.subway.util;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonAndMapContainsTester {
    public static void test(String json, Map<String, String> params) {
        params.forEach((key, value) -> {
            assertThat(json.replace("\"", "")).contains(key + ":" + value);
        });
    }
}
