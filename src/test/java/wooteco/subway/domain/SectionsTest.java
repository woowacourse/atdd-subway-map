package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("section의 순서에 따라 station의 id들을 찾는다.")
    void findStationIdInOrder() {
        //given 1-3, 3-2, 2-4
        Sections sections = new Sections(List.of(new Section(0L, 2L, 4L, 2)
                , new Section(0L, 3L, 2L, 2)
                , new Section(0L, 1L, 3L, 2)));
        //when
        List<Long> stationIdsInOrder = sections.findStationIdsInOrder();
        //then
        assertThat(stationIdsInOrder).isEqualTo(List.of(1L, 3L, 2L, 4L));
    }

}