package wooteco.subway.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.fake.FakeLineDao;
import wooteco.subway.service.fake.FakeSectionDao;
import wooteco.subway.service.fake.FakeStationDao;

class SectionServiceTest {

    private SectionService sectionService;
    private LineService lineService;
    private StationService stationService;

    @BeforeEach
    void setUp() {
        final FakeSectionDao fakeSectionDao = new FakeSectionDao();
        final FakeStationDao fakeStationDao = new FakeStationDao();
        sectionService = new SectionService(fakeSectionDao);
        lineService = new LineService(new FakeLineDao(), fakeSectionDao, fakeStationDao);
        stationService = new StationService(fakeStationDao, fakeSectionDao);
    }

    @DisplayName("구간을 등록할 수 있다.")
    @Test
    public void save() {
        //given
        final StationRequest a = new StationRequest("a");
        final StationRequest b = new StationRequest("b");
        final StationRequest c = new StationRequest("c");

        final StationResponse response1 = stationService.save(a);
        final StationResponse response2 = stationService.save(b);
        final StationResponse response3 = stationService.save(c);

        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", response1.getId(), response3.getId(), 10);
        final Long lineId = lineService.save(lineRequest).getId();

        final SectionRequest request = new SectionRequest(response1.getId(), response2.getId(), 4);

        // when & then
        assertDoesNotThrow(() -> sectionService.save(lineId, request));
    }

    @DisplayName("구간을 제거할 수 있다.")
    @Test
    public void delete() {
        //given
        final StationRequest a = new StationRequest("a");
        final StationRequest b = new StationRequest("b");
        final StationRequest c = new StationRequest("c");

        final StationResponse response1 = stationService.save(a);
        final StationResponse response2 = stationService.save(b);
        final StationResponse response3 = stationService.save(c);

        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", response1.getId(), response2.getId(), 10);
        final Long lineId = lineService.save(lineRequest).getId();

        final SectionRequest request = new SectionRequest(response2.getId(), response3.getId(), 10);

        sectionService.save(lineId, request);

        //when & then
        assertDoesNotThrow(() -> sectionService.delete(lineId, response2.getId()));
    }
}