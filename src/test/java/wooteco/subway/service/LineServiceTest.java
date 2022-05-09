package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.line.LineFindResponse;
import wooteco.subway.service.dto.line.LineSaveRequest;
import wooteco.subway.service.dto.line.LineSaveResponse;

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
        LineSaveRequest line = new LineSaveRequest("1호선", "bg-red-600");

        // when
        LineSaveResponse savedLine = lineService.save(line);
        Line line1 = lineDao.findById(savedLine.getId()).get();

        // then
        assertThat(line.getName()).isEqualTo(line1.getName());
    }

    @Test
    void validateDuplication() {
        // given
        LineSaveRequest line1 = new LineSaveRequest("1호선", "bg-red-600");
        LineSaveRequest line2 = new LineSaveRequest("1호선", "bg-red-600");

        // when
        lineService.save(line1);

        // then
        assertThatThrownBy(() -> lineService.save(line2))
            .hasMessage("중복된 이름이 존재합니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAll() {
        // given
        LineSaveRequest line1 = new LineSaveRequest("1호선", "bg-red-600");
        LineSaveRequest line2 = new LineSaveRequest("2호선", "bg-green-600");

        // when
        lineService.save(line1);
        lineService.save(line2);

        // then
        List<String> names = lineService.findAll()
            .stream()
            .map(LineFindResponse::getName)
            .collect(Collectors.toList());

        assertThat(names)
            .hasSize(2)
            .contains(line1.getName(), line2.getName());
    }

    @Test
    void delete() {
        // given
        LineSaveRequest line = new LineSaveRequest("1호선", "bg-red-600");
        LineSaveResponse savedLine = lineService.save(line);

        // when
        lineService.deleteById(savedLine.getId());

        // then
        List<Long> lineIds = lineService.findAll()
            .stream()
            .map(LineFindResponse::getId)
            .collect(Collectors.toList());

        assertThat(lineIds)
            .hasSize(0)
            .doesNotContain(savedLine.getId());
    }

    @Test
    void update() {
        // given
        LineSaveRequest originLine = new LineSaveRequest("1호선", "bg-red-600");
        LineSaveResponse savedLine = lineService.save(originLine);

        // when
        Line newLine = new Line("2호선", "bg-green-600");
        lineService.updateById(savedLine.getId(), newLine);
        Line line = lineDao.findById(savedLine.getId()).get();

        // then
        assertThat(line).isEqualTo(newLine);
    }
}
