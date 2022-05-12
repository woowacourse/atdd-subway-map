package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.dao.entity.EntityAssembler;
import wooteco.subway.repository.dao.entity.line.LineEntity;

@JdbcTest
class LineDaoTest {

    private static final List<Section> sections = List.of(
            new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 3)
    );
    
    @Autowired
    private DataSource dataSource;
    private wooteco.subway.repository.dao.LineDao lineDao;

    @BeforeEach
    void setUp() {
        this.lineDao = new LineDao(dataSource);
    }

    @DisplayName("지하철노선을 저장한다.")
    @Test
    void save() {
        LineEntity lineEntity = EntityAssembler.lineEntity(new Line(sections, "신분당선", "bg-red-600"));
        assertThat(lineDao.save(lineEntity)).isGreaterThan(0);
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void findAll() {
        List<LineEntity> lines = List.of(
                EntityAssembler.lineEntity(new Line(sections, "신분당선", "bg-red-600")),
                EntityAssembler.lineEntity(new Line(sections, "1호선", "bg-red-601")),
                EntityAssembler.lineEntity(new Line(sections, "2호선", "bg-red-602"))
        );
        lines.forEach(lineDao::save);
        assertThat(lineDao.findAll()).hasSize(3);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findById() {
        LineEntity expected = EntityAssembler.lineEntity(new Line(sections, "신분당선", "bg-red-600"));
        Long lineId = lineDao.save(expected);
        Optional<LineEntity> lineEntity = lineDao.findById(lineId);
        assertAll(
                () -> assertThat(lineEntity.isPresent()).isTrue(),
                () -> assertThat(lineEntity.get()).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(expected)
        );
    }

    @DisplayName("존재하지 않는 지하철노선을 조회한다.")
    @Test
    void findWithNonexistentId() {
        Optional<LineEntity> lineEntity = lineDao.findById(1L);
        assertThat(lineEntity.isEmpty()).isTrue();
    }

    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void update() {
        Long lineId = lineDao.save(EntityAssembler.lineEntity(new Line(sections, "신분당선", "bg-red-600")));
        LineEntity expected = EntityAssembler.lineEntity(new Line(lineId, sections, "분당선", "bg-blue-600"));
        lineDao.update(expected);
        Optional<LineEntity> actual = lineDao.findById(lineId);
        assertAll(
                () -> assertThat(actual.isPresent()).isTrue(),
                () -> assertThat(actual.get()).usingRecursiveComparison()
                        .isEqualTo(expected)
        );
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void remove() {
        Long lineId = lineDao.save(EntityAssembler.lineEntity(new Line(sections, "신분당선", "bg-red-600")));
        lineDao.remove(lineId);
        assertThat(lineDao.findAll()).isEmpty();
    }

    @DisplayName("해당 이름의 노선이 존재하는지 확인한다.")
    @Test
    void existsByName() {
        LineEntity lineEntity = EntityAssembler.lineEntity(new Line(sections, "신분당선", "bg-red-600"));
        lineDao.save(lineEntity);
        assertThat(lineDao.existsByName(lineEntity.getName())).isTrue();
    }

    @DisplayName("해당 색상의 노선이 존재하는지 확인한다.")
    @Test
    void existsByColor() {
        LineEntity lineEntity = EntityAssembler.lineEntity(new Line(sections, "신분당선", "bg-red-600"));
        lineDao.save(lineEntity);
        assertThat(lineDao.existsByColor(lineEntity.getColor())).isTrue();
    }
}
