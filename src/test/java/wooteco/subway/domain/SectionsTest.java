package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private final Long lineId = 1L;
    private final Long lastUpStationId = 1L;
    private final Long middleStationId = 2L;
    private final Long lastDownStationId = 3L;
    private final Long newStationId = 4L;

    private final Section section1 = new Section(1L, lineId, lastUpStationId, middleStationId, 10);
    private final Section section2 = new Section(2L, lineId, middleStationId, lastDownStationId, 10);
    private final Sections sections = new Sections(List.of(section1, section2));

    @DisplayName("새로운 구간의 하행역이 현재의 상행 종점일경우 true를 반환한다.")
    @Test
    void isLastUpStation() {
        boolean isLastStation = sections.isLastStation(newStationId, lastUpStationId);

        assertThat(isLastStation).isTrue();
    }

    @DisplayName("새로운 구간의 상행역이 현재의 하행 종점일경우 true를 반환한다.")
    @Test
    void isLastDownStation() {
        boolean isLastStation = sections.isLastStation(lastDownStationId, newStationId);

        assertThat(isLastStation).isTrue();
    }

    @DisplayName("새로 생성하려는 구간에 붙어있는 기존 구간을 구한다.")
    @Test
    void findExistSection() {
        Section existSection = sections.findExistSection(lastUpStationId, newStationId);

        assertThat(existSection).isEqualTo(section1);
    }

    @DisplayName("해당 역이 구간들 안에 존재할 경우 true를 반환한다.")
    @Test
    void hasStation() {
        boolean expected = sections.hasStation(lastUpStationId);
        boolean actual = true;

        assertThat(expected).isEqualTo(actual);
    }

    @DisplayName("구간이 1개 이하일 경우 true, 더 많을 경우 false를 반환한다.")
    @Test
    void hasOneSection() {
        boolean expected = sections.hasOneSection();
        boolean actual = false;

        assertThat(expected).isEqualTo(actual);
    }

    @DisplayName("해당 역이 상행 종점인 경우 마지막 상행 구간을 가져온다.")
    @Test
    void checkAndExtractLastUpStation() {
        Optional<Section> section = sections.checkAndExtractLastStation(lastUpStationId);

        assertThat(section.get().getId()).isEqualTo(section1.getId());
    }

    @DisplayName("해당 역이 하행 종점인 경우 마지막 하행 구간을 가져온다.")
    @Test
    void checkAndExtractLastDownStation() {
        Optional<Section> section = sections.checkAndExtractLastStation(lastDownStationId);

        assertThat(section.get().getId()).isEqualTo(section2.getId());
    }

    @DisplayName("해당 역이 종점이 아닌 경우 Optional.Empty를 반환한다.")
    @Test
    void checkAndExtractStation() {
        Optional<Section> section = sections.checkAndExtractLastStation(middleStationId);

        assertThat(section.isEmpty()).isTrue();
    }

    @DisplayName("해당 역의 상행(앞) 구간을 추출한다.")
    @Test
    void extractUpSideStation() {
        Section expected = sections.extractUpSideStation(middleStationId);

        assertThat(expected).isEqualTo(section1);
    }

    @DisplayName("해당 역의 하행(뒤) 구간을 추출한다.")
    @Test
    void extractDownSideStation() {
        Section expected = sections.extractDownSideStation(middleStationId);

        assertThat(expected).isEqualTo(section2);
    }
}
