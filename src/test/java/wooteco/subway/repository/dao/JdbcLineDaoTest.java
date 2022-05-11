package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.entity.LineEntity;

@JdbcTest
class JdbcLineDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static LineDao lineDao;

    private Station station_GN = new Station(1L, "강남역");
    private Station station_YS = new Station(2L, "역삼역");
    private Line line_2H = new Line("2호선", "bg-green-600", List.of(station_GN, station_YS));
    private Line line_SBD = new Line("신분당선", "bg-red-600", List.of(station_GN, station_YS));
    private LineEntity savedLineEntity_2H;
    private LineEntity savedLineEntity_SBD;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(namedParameterJdbcTemplate);

        savedLineEntity_2H = lineDao.save(new LineEntity(line_2H));
        savedLineEntity_SBD = lineDao.save(new LineEntity(line_SBD));
    }

    @DisplayName("노선을 저장하고 id로 노선을 찾는다.")
    @Test
    void saveAndFindById() {
        LineEntity lineEntity = lineDao.findById(savedLineEntity_2H.getId());

        assertAll(
                () -> assertThat(lineEntity.getName()).isEqualTo("2호선"),
                () -> assertThat(lineEntity.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void findAll() {
        assertThat(lineDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("id 로 노선을 삭제한다.")
    @Test
    void deleteById() {
        lineDao.deleteById(savedLineEntity_2H.getId());

        assertThat(lineDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        LineEntity newLineEntity = new LineEntity(savedLineEntity_2H.getId(), "3호", "bg-yellow-600");

        lineDao.update(newLineEntity);

        LineEntity updatedLineEntity = lineDao.findById(savedLineEntity_2H.getId());
        assertAll(
                () -> assertThat(updatedLineEntity.getName()).isEqualTo("3호"),
                () -> assertThat(updatedLineEntity.getColor()).isEqualTo("bg-yellow-600")
        );
    }
}
