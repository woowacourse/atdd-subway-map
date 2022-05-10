package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 리스트 관리 기능")
class SectionsTest {

    @DisplayName("노선 전체에서 종점 역 아이디를 찾아낸다.")
    @Test
    void findLastStopId() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        List<Long> lastStopStationIds = sections.getLastStopStationIds();

        assertThat(lastStopStationIds.size()).isEqualTo(2);
        assertThat(lastStopStationIds).isEqualTo(List.of(1L, 4L));
    }
}
