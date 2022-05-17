package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class LineDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
        lineDao = new LineDaoImpl(jdbcTemplate);
        stationDao = new StationDaoImpl(jdbcTemplate);
        sectionDao = new SectionDaoImpl(jdbcTemplate);

        station1 = stationDao.save(new Station("강남역"));
        station2 = stationDao.save(new Station("선릉역"));

        station3 = stationDao.save(new Station("교대역"));
        station4 = stationDao.save(new Station("잠실역"));

        List<Line> lines = lineDao.findAll();
        List<Long> lineIds = lines.stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        for (Long lineId : lineIds) {
            lineDao.deleteById(lineId);
        }
    }

    @Test
    void save() {
        // given
        Line line = new Line("1호선", "bg-red-600");

        // when
        Long savedId = lineDao.save(line);

        // then
        assertThat(savedId).isPositive();
    }

    @Test
    void validateDuplication() {
        // given
        Line line1 = new Line("1호선", "bg-red-600");
        Line line2 = new Line("1호선", "bg-red-600");

        // when
        lineDao.save(line1);

        // then
        assertThatThrownBy(() -> lineDao.save(line2))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("id로 지하철 노선을 조회할 수 있어야 한다.")
    void findById() {
        // given
        Line line = new Line("1호선", "bg-red-600");

        // when
        Long savedId = lineDao.save(line);
        sectionDao.save(new Section(station1, station2, 10), savedId);
        Line findLine = lineDao.findById(savedId).get();

        // then
        assertThat(findLine.getName()).isEqualTo(line.getName());
    }

    @Test
    @DisplayName("없는 id값으로 조회할 경우 Optional.empty()를 반환해야 한다.")
    void findByWrongId() {
        Optional<Line> line = lineDao.findById(0L);
        assertThat(line.isEmpty()).isTrue();
    }

    @Test
    void findAll() {
        // given
        Line line1 = new Line("1호선", "bg-red-600");
        Line line2 = new Line("2호선", "bg-green-600");

        // when
        Long savedId1 = lineDao.save(line1);
        sectionDao.save(new Section(station1, station2, 10), savedId1);
        Long savedId2 = lineDao.save(line2);
        sectionDao.save(new Section(station3, station4, 10), savedId2);

        // then
        List<String> names = lineDao.findAll()
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
        Line line = new Line("1호선", "bg-red-600");
        Long savedId = lineDao.save(line);

        // when
        lineDao.deleteById(savedId);

        // then
        List<Long> lineIds = lineDao.findAll()
            .stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        assertThat(lineIds)
            .hasSize(0)
            .doesNotContain(savedId);
    }

    @Test
    void update() {
        // given
        Line originLine = new Line("1호선", "bg-red-600");
        Long savedId = lineDao.save(originLine);

        // when
        Line newLine = new Line("2호선", "bg-green-600");
        lineDao.updateById(savedId, newLine);
        sectionDao.save(new Section(station1, station2, 10), savedId);
        Line line = lineDao.findById(savedId).get();

        // then
        assertThat(line).isEqualTo(newLine);
    }
}
