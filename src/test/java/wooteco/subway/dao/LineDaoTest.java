package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

class LineDaoTest {

    @BeforeEach
    void setUp() {
        List<Line> lines = LineDao.findAll();
        List<Long> lineIds = lines.stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        for (Long lineId : lineIds) {
            LineDao.deleteById(lineId);
        }
    }

    @Test
    void save() {
        // given
        List<Station> stations = List.of(new Station("교대역"), new Station("강남역"));
        Line line = new Line("1호선", "빨강", stations);

        // when
        Long savedId = LineDao.save(line);
        Line line1 = LineDao.findById(savedId);

        // then
        assertThat(line.getName()).isEqualTo(line1.getName());
    }

    @Test
    void findAll() {
        // given
        List<Station> stations = List.of(new Station("교대역"), new Station("강남역"));
        Line line1 = new Line("1호선", "빨강", stations);
        Line line2 = new Line("2호선", "초록", stations);

        // when
        LineDao.save(line1);
        LineDao.save(line2);

        // then
        List<String> names = LineDao.findAll()
            .stream()
            .map(Line::getName)
            .collect(Collectors.toList());

        assertThat(names)
            .hasSize(2)
            .contains(line1.getName(), line2.getName());
    }

    @Test
    void delete() {
        // given
        List<Station> stations = List.of(new Station("교대역"), new Station("강남역"));
        Line line = new Line("1호선", "빨강", stations);
        Long savedId = LineDao.save(line);

        // when
        LineDao.deleteById(savedId);

        // then
        List<Long> lineIds = LineDao.findAll()
            .stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        assertThat(lineIds)
            .hasSize(0)
            .doesNotContain(savedId);
    }
}
