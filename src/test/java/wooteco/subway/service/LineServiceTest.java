package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineService lineService;

    private Line line;

    @BeforeEach
    void setUp() {
        line = lineDao.save(new Line("2호선", "bg-green-600"));
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void lineCreateTest() {
        final Line line = new Line("5호선", "bg-purple-600");
        final Station upStation = stationDao.save(new Station("아차산역"));
        final Station downStation = stationDao.save(new Station("군자역"));
        final Section section = new Section(upStation, downStation, 3);

        final Line savedLine = lineService.create(line, section);

        assertAll(
                () -> assertThat(savedLine.getId()).isNotNull(),
                () -> assertThat(savedLine.getName()).isEqualTo(line.getName()),
                () -> assertThat(savedLine.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void queryAllTest() {
        lineDao.save(new Line("5호선", "bg-purple-600"));

        final List<Line> lines = lineService.getAll();

        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void queryByIdTest() {
        final Line foundLine = lineService.getById(line.getId());

        assertThat(line).isEqualTo(foundLine);
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    void modifyTest() {
        lineService.modify(line.getId(), new Line("5호선", "bg-green-600"));

        final Line foundLine = lineService.getById(line.getId());

        assertThat(foundLine).isEqualTo(new Line(line.getId(), "5호선", "bg-green-600"));
    }

    @DisplayName("존재하지 않는 노선을 수정하려고 하면 예외가 발생한다.")
    @Test
    void modifyWithExceptionTest() {
        assertThatThrownBy(() -> lineService.modify(100L, new Line("5호선", "bg-purple-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("특정 노선을 삭제한다.")
    @Test
    void removeTest() {
        lineService.remove(line.getId());

        assertThatThrownBy(() -> lineService.getById(line.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("존재하지 않는 노선을 삭제하려고 하면 예외가 발생한다.")
    @Test
    void removeWithExceptionTest() {
        assertThatThrownBy(() -> lineService.remove(100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선이 존재하지 않습니다.");
    }
}
