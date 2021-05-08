package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.exception.SectionsSizeTooSmallException;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 일급 컬렉션 기능 테스트")
class SectionsTest {
    private final Section section1 = new Section(new Station("강남역"), new Station("잠실역"), 10);
    private final Section section2 = new Section(new Station("잠실역"), new Station("역삼역"), 10);

    @DisplayName("사이즈가 2보다 작은 구간 일급 컬렉션을 만들려 하면 예외")
    @Test
    void whenCreateTooSmallSections() {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Sections(Arrays.asList(section1)))
                .isInstanceOf(SectionsSizeTooSmallException.class);
    }
}