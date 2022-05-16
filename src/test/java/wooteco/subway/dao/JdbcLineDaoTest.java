package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.testutils.Fixture.LINE_1_BLUE;
import static wooteco.subway.testutils.Fixture.LINE_2_GREEN;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;

@JdbcTest
public class JdbcLineDaoTest {

    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;


    @BeforeEach
    void set() {
        lineDao = new JdbcLineDao(dataSource);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        //given
        final Line actual = lineDao.save(LINE_2_GREEN);

        //when
        String actualName = actual.getName();

        //then
        assertThat(actualName).isEqualTo(LINE_2_GREEN.getName());

        lineDao.deleteById(actual.getId());
    }

    @Test
    @DisplayName("중복된 노선을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        //given
        final Line saved = lineDao.save(LINE_2_GREEN);

        //when & then
        assertThatThrownBy(() -> lineDao.save(LINE_2_GREEN))
            .isInstanceOf(DuplicateKeyException.class);

        lineDao.deleteById(saved.getId());
    }

    @Test
    @DisplayName("모든 노선을 조회한다")
    void findAll() {
        //given
        final Line line1 = lineDao.save(LINE_2_GREEN);
        final Line line2 = lineDao.save(LINE_1_BLUE);

        //when
        List<Line> lines = lineDao.findAll();

        //then
        assertThat(lines).hasSize(2);

        lineDao.deleteById(line1.getId());
        lineDao.deleteById(line2.getId());
    }

    @Test
    @DisplayName("입력된 id의 노선을 삭제한다")
    void deleteById() {
        //given
        final Line created = lineDao.save(LINE_2_GREEN);

        //when
        lineDao.deleteById(created.getId());

        //then
        assertThat(lineDao.findAll()).isEmpty();
    }

    @Test
    @DisplayName("입력된 id의 노선을 수정한다.")
    void update() {
        //given
        final Line created = lineDao.save(LINE_2_GREEN);
        final LineRequest lineRequest = new LineRequest("1호선", "green", 1L, 2L, 10);
        final Line updated = lineRequest.toEntity(created.getId());

        //when
        lineDao.update(updated);
        final Line updateLine = lineDao.findById(created.getId())
            .orElseThrow();
        
        //then
        assertThat(updateLine.getName()).isEqualTo(updateLine.getName());

        lineDao.deleteById(updateLine.getId());
    }
}
