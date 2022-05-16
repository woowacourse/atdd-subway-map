package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;
import wooteco.subway.repository.entity.LineEntity;

@Sql("/jdbcLineDaoTest.sql")
@JdbcTest
class JdbcLineDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(namedParameterJdbcTemplate);
    }

    @DisplayName("노선을 저장하고 id로 노선을 찾는다.")
    @Test
    void saveAndFindById() {
        LineEntity lineEntity = new LineEntity(Line.ofNullId("1호선", "bg-yellow-600", null));

        Long lineId = lineDao.save(lineEntity).getId();
        LineEntity savedLineEntity = lineDao.findById(lineId);

        assertAll(
                () -> assertThat(savedLineEntity.getName()).isEqualTo("1호선"),
                () -> assertThat(savedLineEntity.getColor()).isEqualTo("bg-yellow-600")
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
        lineDao.deleteById(1L);

        assertThat(lineDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        LineEntity newLineEntity = new LineEntity(1L, "1호", "bg-yellow-600");

        lineDao.update(newLineEntity);

        LineEntity updatedLineEntity = lineDao.findById(1L);
        assertAll(
                () -> assertThat(updatedLineEntity.getName()).isEqualTo("1호"),
                () -> assertThat(updatedLineEntity.getColor()).isEqualTo("bg-yellow-600")
        );
    }
}
