package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.ExceptionMessage;

class NearSectionsTest {

    @Test
    @DisplayName("특정 역에 근접한 구간이 2개 이상이면 예외를 반환")
    void createNearSections() {
        // given
        List<Section> sections = List.of(new Section(1L, 2L, 3L, 4),
                new Section(1L, 3L, 4L, 5),
                new Section(1L, 4L, 5L, 6));

        // then
        assertThatThrownBy(() -> new NearSections(sections))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.NEAR_SECTIONS_OVER_SIZE.getContent());
    }
}
