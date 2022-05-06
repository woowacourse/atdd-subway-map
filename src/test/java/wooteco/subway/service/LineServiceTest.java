package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

class LineServiceTest {

    private final LineDao lineDao = new FakeLineDao();
    private final LineService lineService = new LineService(lineDao);

    @BeforeEach
    void setUp() {
        List<Line> lines = lineDao.findAll();
        List<Long> stationIds = lines.stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            lineDao.deleteById(stationId);
        }
    }

    @Test
    void save() {
        // given
        Line line = new Line("1호선", "bg-red-600");

        // when
        Long savedId = lineService.save(line);
        Line line1 = lineDao.findById(savedId);

        // then
        assertThat(line.getName()).isEqualTo(line1.getName());
    }

    @Test
    void validateDuplication() {
        // given
        Line line1 = new Line("1호선", "bg-red-600");
        Line line2 = new Line("1호선", "bg-red-600");

        // when
        lineService.save(line1);

        // then
        assertThatThrownBy(() -> lineService.save(line2))
            .hasMessage("이미 존재하는 데이터 입니다.")
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        // given
        Line line1 = new Line("1호선", "bg-red-600");
        Line line2 = new Line("2호선", "bg-green-600");

        // when
        lineService.save(line1);
        lineService.save(line2);

        // then
        List<String> names = lineService.findAll()
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
        Long savedId = lineService.save(line);

        // when
        lineService.deleteById(savedId);

        // then
        List<Long> lineIds = lineService.findAll()
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
        Long savedId = lineService.save(originLine);

        // when
        Line newLine = new Line("2호선", "bg-green-600");
        lineService.updateById(savedId, newLine);
        Line line = lineDao.findById(savedId);

        // then
        assertThat(line).isEqualTo(newLine);
    }
}
