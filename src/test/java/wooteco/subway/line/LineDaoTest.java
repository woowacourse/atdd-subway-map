package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedFieldException;

@JdbcTest
class LineDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(namedParameterJdbcTemplate);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void save() {
        final Line line = new Line("2호선", "black");

        final Line createdLine = lineDao.save(line);

        assertThat(createdLine.getName()).isEqualTo(line.getName());
        assertThat(createdLine.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void saveStationWithDuplicateName() {
        final Line line = new Line("2호선", "black");
        lineDao.save(line);

        assertThatThrownBy(() -> lineDao.save(line))
            .hasMessage("중복된 이름의 노선입니다.")
            .isInstanceOf(DuplicatedFieldException.class);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void delete() {
        final Line line = new Line("2호선", "black");
        final Line createdLine = lineDao.save(line);

        assertThatCode(() -> lineDao.deleteById(createdLine.getId()))
            .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 노선의 이름으로 노선을 제거하면 예외가 발생한다.")
    @Test
    void deleteWithAbsentName() {
        assertThatThrownBy(() -> lineDao.deleteById(1L))
            .hasMessage("해당 Id의 노선이 없습니다.")
            .isInstanceOf(DataNotFoundException.class);
    }

    @DisplayName("전체 노선을 조회한다.")
    @Test
    void findAll() {
        final List<String> lineNames = Arrays.asList("1호선", "2호선", "3호선");
        lineNames.stream()
            .map(name -> new Line(name, "black"))
            .forEach(lineDao::save);

        assertThat(lineDao.findAll()).extracting("name").isEqualTo(lineNames);
    }

    @DisplayName("특정 이름의 노선을 조회한다.")
    @Test
    void findByName() {
        final String name = "2호선";
        final Line createdLine = lineDao.save(new Line(name, "black"));

        final Line line = lineDao.findByName(name).get();

        assertThat(line.getId()).isEqualTo(createdLine.getId());
        assertThat(line.getName()).isEqualTo(createdLine.getName());
        assertThat(line.getColor()).isEqualTo(createdLine.getColor());
    }

    @DisplayName("특정 id의 지하철역을 조회한다.")
    @Test
    void findById() {
        final String name = "잠실역";
        final Line createdLine = lineDao.save(new Line(name, "black"));

        final Line line = lineDao.findById(createdLine.getId()).get();

        assertThat(line.getId()).isEqualTo(createdLine.getId());
        assertThat(line.getName()).isEqualTo(createdLine.getName());
        assertThat(line.getColor()).isEqualTo(createdLine.getColor());
    }
}
