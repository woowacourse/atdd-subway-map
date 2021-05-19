package wooteco.subway.name.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.common.exception.InvalidNameException;
import wooteco.subway.line.domain.LineName;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineNameTest {
    @Test
    @DisplayName("name 객체를 생성한다.")
    void create() {
        assertThatCode(() -> new LineName("아마찌호선"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"검프", "검 프 선", "gump선"})
    @DisplayName("유효하지 않은 이름의 객체를 가져올 시 예외가 발생한다.")
    void validateName(String name) {
        assertThatThrownBy(() ->
                new LineName(name)
        ).isInstanceOf(InvalidNameException.class);
    }
}
