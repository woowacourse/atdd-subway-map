package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;
    @Autowired
    private LineService lineService;
    @Autowired
    private StationDao stationDao;

    @Test
    @DisplayName("상행 종점 구간을 등록할 수 있다.")
    void addUpSection() {
        // given
        Long stationSaveId1 = stationDao.save(new Station("강남역"));
        Long stationSaveId2 = stationDao.save(new Station("역삼역"));
        Long stationSaveId3 = stationDao.save(new Station("선릉역"));

        Long lineId = lineService.save(new LineRequest("신분당선", "bg-red-600", stationSaveId1, stationSaveId2, 10));

        // when
        sectionService.addSection(lineId, new SectionRequest(stationSaveId3, stationSaveId1, 4));

        // then
        final LineResponse response = lineService.findById(lineId);
        assertThat(response.getStations()).extracting("name")
                .containsExactly("선릉역", "강남역", "역삼역");
    }

    @Test
    @DisplayName("하행 종점 구간을 등록할 수 있다.")
    void addDownSection() {
        // given
        Long stationSaveId1 = stationDao.save(new Station("강남역"));
        Long stationSaveId2 = stationDao.save(new Station("역삼역"));
        Long stationSaveId3 = stationDao.save(new Station("선릉역"));
        Long lineId = lineService.save(new LineRequest("신분당선", "bg-red-600", stationSaveId1, stationSaveId2, 10));

        // when
        sectionService.addSection(lineId, new SectionRequest(stationSaveId2, stationSaveId3, 4));

        // then
        final LineResponse response = lineService.findById(lineId);
        assertThat(response.getStations()).extracting("name")
                        .containsExactly("강남역", "역삼역", "선릉역");
    }
}
