package wooteco.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringFormatTest {

    @Test
    @DisplayName("String, String을 넣어주면 에러메세지를 만들어준다")
    void errorMessage() {
        assertThat(StringFormat.errorMessage("kbsat", "hello")).isEqualTo("kbsat : hello");
    }

    @Test
    @DisplayName("Long, message를 넣어주면 에러메세지를 만들어준다")
    void errorMessage_withLong() {
        assertThat(StringFormat.errorMessage(100L, "hello")).isEqualTo("100 : hello");
    }
}
