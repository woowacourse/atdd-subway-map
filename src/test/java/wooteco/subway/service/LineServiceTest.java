package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.EmptyResultException;

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
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);
        LineRequest lineRequest2 = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);

        // when
        lineService.save(lineRequest1);

        // then
        assertThatThrownBy(() -> lineService.save(lineRequest2))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        // given
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);
        LineRequest lineRequest2 = new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 5);

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
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);
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
        LineRequest originLine = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);
        LineResponse lineResponse = lineService.save(originLine);

        // when
        LineRequest newLine = new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 5);
        lineService.updateById(lineResponse.getId(), newLine);
        Line line = lineDao.findById(lineResponse.getId()).get();

        // then
        assertThat(line.getName()).isEqualTo(newLine.getName());
    }

    @Test
    void insertSection() {
        // given
        LineRequest originLine = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);
        LineResponse lineResponse = lineService.save(originLine);

        // when
        Long newDownStationId = stationDao.save(new Station("교대역")).getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, newDownStationId, 3);

        // then
        lineService.insertSection(lineResponse.getId(), sectionRequest);
        LineResponse newLineResponse = lineService.findById(lineResponse.getId());
        assertAll(
            () -> assertThat(lineResponse.getId()).isEqualTo(newLineResponse.getId()),
            () -> assertThat(lineResponse.getName()).isEqualTo(newLineResponse.getName()),
            () -> assertThat(lineResponse.getColor()).isEqualTo(newLineResponse.getColor()),
            () -> assertThat(newLineResponse.getStations())
                .hasSize(3)
                .contains(StationResponse.from(new Station("강남역")),
                    StationResponse.from(new Station("교대역")),
                    StationResponse.from(new Station("선릉역")))
        );
    }

    @Test
    void insertInvalidSection() {
        // given
        LineRequest originLine = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);
        LineResponse lineResponse = lineService.save(originLine);

        // when
        Long newUpStationId = stationDao.save(new Station("잠실역")).getId();
        Long newDownStationId = stationDao.save(new Station("교대역")).getId();
        SectionRequest sectionRequest = new SectionRequest(newUpStationId, newDownStationId, 3);

        // then
        assertThatThrownBy(
            () -> lineService.insertSection(lineResponse.getId(), sectionRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStation() {
        // given
        LineRequest originLine = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);
        LineResponse lineResponse = lineService.save(originLine);
        Long newDownStationId = stationDao.save(new Station("교대역")).getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, newDownStationId, 3);
        lineService.insertSection(lineResponse.getId(), sectionRequest);

        // when
        lineService.deleteStation(lineResponse.getId(), newDownStationId);

        // then
        LineResponse newLineResponse = lineService.findById(lineResponse.getId());
        assertAll(
            () -> assertThat(lineResponse.getId()).isEqualTo(newLineResponse.getId()),
            () -> assertThat(lineResponse.getName()).isEqualTo(newLineResponse.getName()),
            () -> assertThat(lineResponse.getColor()).isEqualTo(newLineResponse.getColor()),
            () -> assertThat(newLineResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList()))
                .hasSize(2)
                .contains("강남역", "선릉역")
        );
    }

    @Test
    @DisplayName("삭제할 구간을 찾지 못했을 경우 예외를 반환해야 합니다.")
    void deleteNone() {
        // given
        LineRequest originLine = new LineRequest("1호선", "bg-red-600", upStationId, downStationId, 5);
        LineResponse lineResponse = lineService.save(originLine);
        Long newDownStationId = stationDao.save(new Station("교대역")).getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, newDownStationId, 3);
        lineService.insertSection(lineResponse.getId(), sectionRequest);

        // when
        Long notInLineStationId = stationDao.save(new Station("xx")).getId();

        // then
        assertThatThrownBy(() -> lineService.deleteStation(lineResponse.getId(), notInLineStationId))
            .hasMessage("삭제할 구간을 찾지 못했습니다.")
            .isInstanceOf(EmptyResultException.class);
    }
}
