package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@DisplayName("지하철 노선 관련 DAO 테스트")
@JdbcTest
class LineDaoTest {

    private static final Line LINE = new Line("신분당선", "bg-red-600");

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        lineDao.save(LINE);

        Integer count = jdbcTemplate.queryForObject("select count(*) from LINE", Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("중복된 아이디의 지하철 노선이 있다면 true 를 반환한다.")
    @Test
    void existLineById() {
        long lineId = lineDao.save(LINE);

        assertThat(lineDao.existLineById(lineId)).isTrue();
    }

    @DisplayName("중복된 이름의 지하철 노선이 있다면 true 를 반환한다.")
    @Test
    void existLineByName() {
        lineDao.save(LINE);

        assertThat(lineDao.existLineByName("신분당선")).isTrue();
    }

    @DisplayName("중복된 색상의 지하철 노선이 있다면 true 를 반환한다.")
    @Test
    void existLineByColor() {
        lineDao.save(LINE);

        assertThat(lineDao.existLineByColor("bg-red-600")).isTrue();
    }

    @DisplayName("지하철 노선의 목록을 조회한다.")
    @Test
    void findAll() {
        lineDao.save(LINE);
        lineDao.save(new Line("다른분당선", "bg-green-600"));

        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(2);
    }

    @DisplayName("지하철 노선에 포함되어 있는 지하철역 목록을 조회한다.")
    @Test
    void findStations() {
        // given
        long stationId1 = stationDao.save(new Station("강남역"));
        long stationId2 = stationDao.save(new Station("역삼역"));
        long stationId3 = stationDao.save(new Station("삼성역"));

        sectionDao.save(1L, new Section(stationId2, stationId1, 5));
        sectionDao.save(1L, new Section(stationId1, stationId3, 5));

        // when
        List<Station> stations = lineDao.findStations(1L);
        List<String> stationNames = stations.stream()
                .map(Station::getName)
                .collect(Collectors.toUnmodifiableList());

        // then
        assertThat(stationNames).containsExactly("강남역", "역삼역", "삼성역");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void find() {
        long lineId = lineDao.save(LINE);

        Optional<Line> line = lineDao.find(lineId);

        assertThat(line).isNotNull();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        long lineId = lineDao.save(LINE);
        Line updatedLine = new Line("다른분당선", "bg-red-600");

        lineDao.update(lineId, updatedLine);

        assertThat(lineDao.find(lineId).orElseThrow().getName()).isEqualTo("다른분당선");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        long lineId = lineDao.save(LINE);

        lineDao.delete(lineId);

        Integer count = jdbcTemplate.queryForObject("select count(*) from LINE", Integer.class);
        assertThat(count).isEqualTo(0);
    }
}
