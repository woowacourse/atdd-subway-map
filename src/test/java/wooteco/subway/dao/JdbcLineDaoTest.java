package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineEntity;

@JdbcTest
public class JdbcLineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        Line line = new Line("2호선", "green");
        LineEntity createdLineEntity = lineDao.save(line);

        assertThat(createdLineEntity.getId()).isGreaterThan(0);
        assertThat(createdLineEntity.getName()).isEqualTo(line.getName());
        assertThat(createdLineEntity.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("해당 이름을 가진 지하철 노선이 있는지 확인한다.")
    @Test
    void existByName() {
        Line line = new Line("2호선", "green");
        lineDao.save(line);

        assertThat(lineDao.existByName(line.getName())).isTrue();
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAll() {
        Line line = new Line("2호선", "green");
        Line line2 = new Line("3호선", "green");
        lineDao.save(line);
        lineDao.save(line2);

        assertThat(lineDao.findAll()).hasSize(2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void find() {
        Line line = new Line("2호선", "green");
        LineEntity createdLine = lineDao.save(line);
        LineEntity foundLine = lineDao.find(createdLine.getId());

        assertThat(foundLine.getId()).isEqualTo(createdLine.getId());
        assertThat(foundLine.getName()).isEqualTo(createdLine.getName());
        assertThat(foundLine.getColor()).isEqualTo(createdLine.getColor());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        Line line = new Line("2호선", "green");
        LineEntity createdLine = lineDao.save(line);
        Long createdId = createdLine.getId();

        Line lineForUpdateInfo = new Line(createdId, "3호선", "green");

        lineDao.update(lineForUpdateInfo);
        assertThat(lineDao.find(createdId).getName()).isEqualTo(lineForUpdateInfo.getName());
    }

    @DisplayName("해당 id를 가진 지하철 노선이 존재하는지 확인한다.")
    @Test
    void existById() {
        Line line = new Line("2호선", "green");
        LineEntity createdLine = lineDao.save(line);
        Long createdId = createdLine.getId();

        assertThat(lineDao.existById(createdId)).isTrue();
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        Line line = new Line("2호선", "green");
        LineEntity createdLine = lineDao.save(line);
        Long createdId = createdLine.getId();

        lineDao.delete(createdId);

        assertThat(lineDao.existById(createdId)).isFalse();
    }
}
