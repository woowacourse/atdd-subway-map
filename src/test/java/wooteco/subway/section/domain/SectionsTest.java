package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.exception.SectionsIllegalArgumentException;
import wooteco.subway.section.exception.SectionsSizeTooSmallException;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 일급 컬렉션 기능 테스트")
class SectionsTest {
    private final Station GANGNAM_STATION = new Station(1L, "강남역");
    private final Station JAMSIL_STATION = new Station(2L, "잠실역");
    private final Station JONGHAP_STATION = new Station(3L, "종합운동장역");
    private final Station SADANG_STATION = new Station(4L, "사당역");
    private final Section FIRST_SECTION = new Section(GANGNAM_STATION, JAMSIL_STATION, 10);
    private final Section SECOND_SECTION = new Section(JAMSIL_STATION, JONGHAP_STATION, 10);
    private final Section THIRD_SECTION = new Section(JONGHAP_STATION, SADANG_STATION, 10);
    private final Section DOUBLE_END_UPSTATION_SECTION = new Section(JONGHAP_STATION, JAMSIL_STATION, 10);

    @DisplayName("노선구간의 생성")
    @Test
    void createSections() {
        //given
        //when
        //then
        assertThat(new Sections(Arrays.asList(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION)))
                .isNotNull();
    }

    @DisplayName("사이즈가 2보다 작은 구간 일급 컬렉션을 만들려 하면 예외")
    @Test
    void whenCreateTooSmallSections() {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Sections(Collections.emptyList()))
                .isInstanceOf(SectionsSizeTooSmallException.class);
    }

    @DisplayName("노선구간의 종점이 1개가 아니면 예외")
    @Test
    void whenEndStationsSizeNotOne() {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Sections(Arrays.asList(FIRST_SECTION, DOUBLE_END_UPSTATION_SECTION)))
                .isInstanceOf(SectionsIllegalArgumentException.class);
    }
}