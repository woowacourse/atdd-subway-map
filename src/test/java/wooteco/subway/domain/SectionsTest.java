package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.SectionRequest;

class SectionsTest {

    private final Sections sections = new Sections(List.of(new Section(0L, 2L, 4L, 2)
            , new Section(0L, 3L, 2L, 2)
            , new Section(0L, 1L, 3L, 2)));


    @DisplayName("section 의 순서에 따라 station 의 id 들을 찾는다.")
    @Test
    void findStationIdInOrder() {
        //given

        //when
        List<Long> stationIdsInOrder = sections.findStationIdsInOrder();
        //then
        assertThat(stationIdsInOrder).isEqualTo(List.of(1L, 3L, 2L, 4L));
    }

    @DisplayName("section 을 이용하여 sections 에 연결된 section 이 존재하지 않는 경우, 예외를 발생시킨다.")
    @Test
    void validNonLinkSection() {
        //given
        SectionRequest request = new SectionRequest(6L, 5L, 1);
        //when
        assertThatThrownBy(() -> sections.validNonLinkSection(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연결할 section 이 존재하지 않습니다.");
    }

    @DisplayName("section 을 이용하여 sections 에 상행역과 하행역이 겹치는 section 이 있는 경우, 예외를 발생시킨다.")
    @Test
    void validSameStations() {
        //given
        SectionRequest request = new SectionRequest(1L, 3L, 1);
        //when
        assertThatThrownBy(() -> sections.validSameStations(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 노선에 이미 존재합니다.");
    }

}