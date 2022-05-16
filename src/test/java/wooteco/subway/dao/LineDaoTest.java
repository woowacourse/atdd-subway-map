package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineDto;

@DisplayName("Line Dao를 통해서")
@JdbcTest
@Transactional
class LineDaoTest {

    private static final LineDto LINE_FIXTURE = new LineDto("a", "b", 1L, 2L, 3);
    private static final LineDto LINE_FIXTURE2 = new LineDto("c", "d", 3L, 4L, 5);
    private static final LineDto LINE_FIXTURE3 = new LineDto("e", "f", 6L, 7L, 8);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private DataSource dataSource;

    private CommonLineDao lineDao;

    @BeforeEach
    void setup() {
        lineDao = new LineDao(namedParameterJdbcTemplate, dataSource);
    }

    @Nested
    @DisplayName("새로운 노선을 저장할 때")
    class SaveTest {

        @Test
        @DisplayName("노선 이름이 중복되지 않으면 저장할 수 있다.")
        void save_Success_If_Not_Exists() {
            assertThatCode(() -> lineDao.save(LINE_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("노선 이름이 중복되면 예외가 발생한다.")
        void save_Fail_If_Exists() {
            lineDao.save(LINE_FIXTURE);

            assertThatThrownBy(() -> lineDao.save(LINE_FIXTURE))
                    .isInstanceOf(DuplicateKeyException.class);
        }
    }


    @Test
    @DisplayName("전체 지하철 노선을 조회할 수 있다")
    void findAll() {
        lineDao.save(LINE_FIXTURE);
        lineDao.save(LINE_FIXTURE2);
        lineDao.save(LINE_FIXTURE3);

        assertThat(lineDao.findAll()).extracting("name")
                .isEqualTo(List.of(LINE_FIXTURE.getName(), LINE_FIXTURE2.getName(), LINE_FIXTURE3.getName()));
    }

    @Test
    @DisplayName("아이디로 지하철 노선을 조회할 수 있다")
    void findById() {
        final Line line = lineDao.save(LINE_FIXTURE);
        final Line found = lineDao.findById(line.getId());

        assertThat(line).isEqualTo(found);
    }

    @Test
    @DisplayName("아이디가 존재한다면 지하철노선을 삭제할 수 있다.")
    void deleteById() {
        final Line line = lineDao.save(LINE_FIXTURE);
        final List<Line> lines = lineDao.findAll();
        lineDao.deleteById(line.getId());
        final List<Line> afterDelete = lineDao.findAll();

        assertThat(lines).isNotEmpty();
        assertThat(afterDelete).isEmpty();
    }

    @Test
    @DisplayName("노선이 존재하면 노선 이름과 색상을 변경할 수 있다.")
    void update_Line_Success() {
        final Line line = lineDao.save(LINE_FIXTURE);
        final Long id = line.getId();

        lineDao.update(id, new Line("22호선", "bg-color-777"));
        final Line updated = lineDao.findById(id);

        assertAll(
                () -> assertThat(updated.getId()).isEqualTo(id),
                () -> assertThat(updated.getName()).isEqualTo("22호선"),
                () -> assertThat(updated.getColor()).isEqualTo("bg-color-777")
        );
    }

}
