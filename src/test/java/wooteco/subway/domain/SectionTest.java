package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    @DisplayName("[상행]기존에 존재하던 구간과 요청한 구간으로 새 구간을 만든다.")
    @Test
    void createUpSectionBySections() {
        Section existed = new Section(10, 2L, 5L, 4L);
        Section inserted = new Section(4, 2L, 5L, 6L);

        Section generated = existed.createSection(inserted);

        assertAll(() -> {
            assertThat(generated.getDistance()).isEqualTo(6);
            assertThat(generated.getUpStationId()).isEqualTo(6L);
            assertThat(generated.getDownStationId()).isEqualTo(4L);
        });
    }

    @DisplayName("[하행]기존에 존재하던 구간과 요청한 구간으로 새 구간을 만든다.")
    @Test
    void createDownSectionBySections() {
        Section existed = new Section(10, 2L, 5L, 4L);
        Section inserted = new Section(4, 2L, 6L, 4L);

        Section generated = existed.createSection(inserted);

        assertAll(() -> {
            assertThat(generated.getDistance()).isEqualTo(6);
            assertThat(generated.getUpStationId()).isEqualTo(5L);
            assertThat(generated.getDownStationId()).isEqualTo(6L);
        });
    }

    @DisplayName("[종점 구간 추가] 새 종점 구간으로 추가할 수 있다.")
    @ParameterizedTest
    @CsvSource({"3,1", "2,3"})
    void canAddAsLastStation(Long upStationId, Long downStationId){
        Section section = new Section(10, 2L, 1L, 2L);

        boolean actual = section.canAddAsLastStation(upStationId, downStationId);

        assertThat(actual).isTrue();
    }

    @DisplayName("[종점 구간 추가 불가] 새 종점 구간으로 추가할 수 없다.")
    @ParameterizedTest
    @CsvSource({"1,3", "3,2", "4,5", "2,1"})
    void cantAddAsLastStation(Long upStationId, Long downStationId){
        Section section = new Section(10, 2L, 1L, 2L);

        boolean actual = section.canAddAsLastStation(upStationId, downStationId);

        assertThat(actual).isFalse();
    }
}
