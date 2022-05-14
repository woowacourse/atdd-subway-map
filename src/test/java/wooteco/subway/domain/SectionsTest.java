package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.bind.annotation.DeleteMapping;

class SectionsTest {

    @DisplayName("저장된 구간을 상행부터 하행까지 정렬하여 출력한다")
    @Test
    void getSortedStationId() {
        // given
        Section section1 = new Section(1L, 3L, 2L, 5);
        Section section2 = new Section(1L, 1L, 3L, 5);
        Sections sections = new Sections(List.of(section1, section2));

        // when
        List<Long> sortedStationId = sections.getSortedStationId();

        // then
        assertAll(
                () -> assertThat(sortedStationId).hasSize(3),
                () -> assertThat(sortedStationId.get(0)).isEqualTo(1L),
                () -> assertThat(sortedStationId.get(1)).isEqualTo(3L),
                () -> assertThat(sortedStationId.get(2)).isEqualTo(2L)
        );
    }

    @DisplayName("파라미터로 받은 역 id가 종점인지 확인한다.")
    @Test
    void isTerminal() {
        // given
        Section section1 = new Section(1L, 3L, 2L, 5);
        Section section2 = new Section(1L, 1L, 3L, 5);
        Sections sections = new Sections(List.of(section1, section2));

        // when
        boolean result1 = sections.isTerminal(1L);
        boolean result2 = sections.isTerminal(2L);
        boolean result3 = sections.isTerminal(3L);

        // then
        assertAll(
                () -> assertThat(result1).isTrue(),
                () -> assertThat(result2).isTrue(),
                () -> assertThat(result3).isFalse()
        );
    }
}
