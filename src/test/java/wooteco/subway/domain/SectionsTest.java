package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.Fixtures.SECTION;
import static wooteco.subway.Fixtures.SECTION_3;
import static wooteco.subway.Fixtures.SECTION_4;
import static wooteco.subway.Fixtures.STATION;
import static wooteco.subway.Fixtures.STATION_2;
import static wooteco.subway.Fixtures.STATION_3;
import static wooteco.subway.Fixtures.STATION_4;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("역의 노선들을 순서대로 반환한다.")
    @Test
    void calculateStations() {
        Section Section = new Section(1L, 1L, STATION, STATION_2, 5);
        Section Section2 = new Section(3L, 1L, STATION_2, STATION_3, 10);
        Section Section3 = new Section(2L, 1L, STATION_3, STATION_4, 6);
        Sections sections = new Sections(List.of(Section2, Section, Section3));
        assertThat(sections.calculateStations())
                .containsOnly(STATION, STATION_2, STATION_3, STATION_4);
    }

    @DisplayName("해당 구간이 노선에 있는지 검증한다.")
    @Test
    void checkUniqueSection() {
        Sections sections = new Sections(List.of(SECTION));
        assertThatThrownBy(() -> sections.validateSave(SECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 구간입니다.");
    }

    @DisplayName("해당 구간이 노선과 연결 가능한지 검증한다.")
    @Test
    void checkIsLinked() {
        assertDoesNotThrow(() -> new Sections(List.of()).validateSave(SECTION));
        Sections sections = new Sections(List.of(SECTION));
        assertThatThrownBy(() -> sections.validateSave(SECTION_4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 구간은 역과 연결될 수 없습니다.");
    }
}
