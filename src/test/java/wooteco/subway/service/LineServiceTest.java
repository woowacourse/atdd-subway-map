package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.dao.FakeSectionDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

class LineServiceTest {

    private final LineDao lineDao = new FakeLineDao();
    private final SectionDao sectionDao = new FakeSectionDao();
    private final LineService lineService = new LineService(lineDao, sectionDao);

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
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", null, null, 0);

        // when
        LineResponse lineResponse = lineService.save(lineRequest);

        // then
        assertAll(
            () -> assertThat(lineRequest.getName()).isEqualTo(lineResponse.getName()),
            () -> assertThat(lineRequest.getColor()).isEqualTo(lineResponse.getColor())
        );

    }

    @Test
    void validateDuplication() {
        // given
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-red-600", null, null, 0);
        LineRequest lineRequest2 = new LineRequest("1호선", "bg-red-600", null, null, 0);

        // when
        lineService.save(lineRequest1);

        // then
        assertThatThrownBy(() -> lineService.save(lineRequest2))
            .hasMessage("이미 존재하는 데이터 입니다.")
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        // given
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-red-600", null, null, 0);
        LineRequest lineRequest2 = new LineRequest("2호선", "bg-green-600", null, null, 0);

        // when
        lineService.save(lineRequest1);
        lineService.save(lineRequest2);

        // then
        List<String> names = lineService.findAll()
            .stream()
            .map(LineResponse::getName)
            .collect(Collectors.toList());

        assertThat(names)
            .hasSize(2)
            .contains(lineRequest1.getName(), lineRequest2.getName());
    }

    @Test
    void delete() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", null, null, 0);
        LineResponse lineResponse = lineService.save(lineRequest);

        // when
        lineService.deleteById(lineResponse.getId());

        // then
        List<Long> lineIds = lineService.findAll()
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(lineIds)
            .hasSize(0)
            .doesNotContain(lineResponse.getId());
    }

    @Test
    void update() {
        // given
        LineRequest originLine = new LineRequest("1호선", "bg-red-600", null, null, 0);
        LineResponse lineResponse = lineService.save(originLine);

        // when
        LineRequest newLine = new LineRequest("2호선", "bg-green-600", null, null, 0);
        lineService.updateById(lineResponse.getId(), newLine);
        Line line = lineDao.findById(lineResponse.getId()).get();

        // then
        assertThat(line.getName()).isEqualTo(newLine.getName());
    }
}
