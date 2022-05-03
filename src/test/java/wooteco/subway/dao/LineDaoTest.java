package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

class LineDaoTest {
    @Test
    void save() {
        // given
        String name = "1호선";
        String color = "빨강";
        List<Station> stations = List.of(new Station("교대역"), new Station("강남역"));
        Line line = new Line(name, color, stations);

        // when
        Long savedId = LineDao.save(line);

        // then
        assertThat(savedId).isEqualTo(1L);
    }
}
