package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.section.Sections;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wooteco.subway.DummyData.*;

@DisplayName("지하철 노선 도메인 테스트")
class LineTest {

    @DisplayName("같은 이름인지 비교")
    @Test
    void isSameName() {
        // given
        Line 분당선 = new Line(1L, "분당선", "bg-red-600");
        Line 신분당선 = new Line(2L, "신분당선", "bg-yellow-600");

        // when
        boolean sameName = 분당선.isSameName("분당선");
        boolean notSameName = 신분당선.isSameName("분당선");

        // then
        assertTrue(sameName);
        assertFalse(notSameName);
    }

    @DisplayName("같은 아이디인지 비교")
    @Test
    void isSameId() {
        // given
        Line 분당선 = new Line(1L, "분당선", "bg-red-600");
        Line 신분당선 = new Line(2L, "신분당선", "bg-yellow-600");

        // when
        boolean sameId = 분당선.isSameId(1L);
        boolean notSameId = 신분당선.isSameId(1L);

        // then
        assertTrue(sameId);
        assertFalse(notSameId);
    }

    @DisplayName("지하철 노선 정보 수정")
    @Test
    void update() {
        // given
        Line 분당선 = new Line(1L, "분당선", "bg-red-600");
        LineUpdateRequest 신분당선 = new LineUpdateRequest("신분당선", "bg-yellow-600");

        // when
        분당선.update(신분당선.getName(), 신분당선.getColor());

        // then
        assertThat(분당선).usingRecursiveComparison()
                .isEqualTo(new Line(1L, "신분당선", "bg-yellow-600"));
    }

    @DisplayName("지하철 노선의 구간 정보 수정")
    @Test
    void updateSections() {
        // given
        Line 이호선 = new Line(2L, "2호선", "green", new Sections());
        Sections sections = new Sections(
                Arrays.asList(이호선_왕십리_잠실_거리10, 이호선_잠실_강남_거리5)
        );

        // when
        이호선.updateSections(sections);

        // then
        assertThat(이호선.getSections().getSections()).hasSize(2);
    }

    @DisplayName("구간에서 지하철 역 제거")
    @Test
    void deleteStationInSection() {
        // given
        Sections sections = new Sections(
                Arrays.asList(이호선_왕십리_잠실_거리10, 이호선_잠실_강남_거리5)
        );
        Line 이호선 = new Line(2L, "2호선", "green", sections);

        // when
        이호선.deleteStationInSection(강남역);

        // then
        assertThat(이호선.getSections().getSections()).hasSize(1);
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        Sections sections = new Sections(Arrays.asList(
                이호선_왕십리_잠실_거리10,
                이호선_잠실_강남_거리5
        ));
        Line 이호선 = new Line(2L, "2호선", "green", sections);

        // when
        이호선.addSection(이호선_강남_구의_거리7);

        // then
        assertThat(이호선.getSections().getSections()).hasSize(3);
    }
}