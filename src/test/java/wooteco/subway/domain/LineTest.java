package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.station.StationNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[도메인] Line")
class LineTest {
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 수서역 = new Station(2L, "수서역");
    private final Station 잠실역 = new Station(3L, "잠실역");
    private final Station 동탄역 = new Station(4L, "동탄역");
    private final Section 강남_수서 = new Section(강남역, 수서역, 10);
    private final Section 수서_잠실 = new Section(수서역, 잠실역, 10);
    private final Section 잠실_동탄 = new Section(잠실역, 동탄역, 10);

    @DisplayName("노선의 역목록 가져오기")
    @Test
    void stations() {
        Line line = new Line("1호선", "bg-green-300");
        List<Section> sections = Arrays.asList(강남_수서, 수서_잠실, 잠실_동탄);
        line.setStationsBySections(new Sections(sections));

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getStations()).containsExactly(강남역, 수서역, 잠실역, 동탄역);
    }

    @DisplayName("같은 이름 확인")
    @Test
    void isSameName() {
        Line line = new Line("1호선", "bg-green-300");

        assertTrue(line.isSameColor("bg-green-300"));
        assertFalse(line.isSameColor("bg-green-310"));
    }

    @DisplayName("같은 색상 확인")
    @Test
    void isSameColor() {
        Line line = new Line("1호선", "bg-green-300");

        assertTrue(line.isSameColor("bg-green-300"));
        assertFalse(line.isSameColor("bg-green-310"));
    }

    @DisplayName("같은 아이디 확인")
    @Test
    void isSameId() {
        Line line = new Line(1L, "1호선", "bg-green-300");

        assertTrue(line.isSameId(1L));
    }

    @DisplayName("구간 셋팅하기")
    @Test
    void setSections() {
        Line line = new Line("1호선", "bg-green-300");
        assertThatThrownBy(line::getStations).isInstanceOf(StationNotFoundException.class);

        line.setStationsBySections(new Sections(강남_수서));

        assertThat(line.getStations()).hasSize(2);
    }

}