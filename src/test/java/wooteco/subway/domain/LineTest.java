package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private Long lineSeq = 0L;
    private Long stationSeq = 0L;
    private static final Long LINE_ID = 1L;

    @DisplayName("저장되어 있는 구간들을 바탕으로 역 목록을 구한다.")
    @Test
    void getStations() {
        Station station1 = new Station(++stationSeq, "강남역");
        Station station2 = new Station(++stationSeq, "역삼역");
        Station station3 = new Station(++stationSeq, "선릉역");

        Section section1 = new Section(++lineSeq, station1, station2, 1);
        Section section2 = new Section(++lineSeq, station2, station3, 1);

        Line line = new Line(LINE_ID, "2호선", "green", List.of(section1, section2));

        List<Station> stations = line.getStations();

        assertThat(stations).containsOnly(station1, station2, station3);
    }

}
