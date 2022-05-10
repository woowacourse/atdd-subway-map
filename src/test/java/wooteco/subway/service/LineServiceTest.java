package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
@Transactional
class LineServiceTest {

    private Long upStationId;
    private Long downStationId;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineDao, stationDao, sectionDao);

        upStationId = stationDao.save(new Station("강남역")).getId();
        downStationId = stationDao.save(new Station("선릉역")).getId();
    }

    @Test
    void save() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 1);

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
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 0);
        LineRequest lineRequest2 = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 0);

        // when
        lineService.save(lineRequest1);

        // then
        assertThatThrownBy(() -> lineService.save(lineRequest2))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        // given
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 0);
        LineRequest lineRequest2 = new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 0);

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
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 0);
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
        LineRequest originLine = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 0);
        LineResponse lineResponse = lineService.save(originLine);

        // when
        LineRequest newLine = new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 0);
        lineService.updateById(lineResponse.getId(), newLine);
        Line line = lineDao.findById(lineResponse.getId()).get();

        // then
        assertThat(line.getName()).isEqualTo(newLine.getName());
    }
}
