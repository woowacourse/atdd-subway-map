package wooteco.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringFormatTest {

    @Test
    void errorMessage() {
        assertThat(StringFormat.errorMessage("kbsat", "hello")).isEqualTo("kbsat : hello");
    }

    @Test
    void errorMessage_withLong() {
        assertThat(StringFormat.errorMessage(100L, "hello")).isEqualTo("100 : hello");
    }
}
