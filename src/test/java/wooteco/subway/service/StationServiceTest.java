package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.TestFixtures.LINE_COLOR;
import static wooteco.subway.TestFixtures.LINE_SIX;
import static wooteco.subway.TestFixtures.STANDARD_DISTANCE;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.신당역;
import static wooteco.subway.TestFixtures.창신역;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.utils.exception.NameDuplicatedException;
import wooteco.subway.utils.exception.SubwayException;

@Transactional
@SpringBootTest
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;

    @DisplayName("역 요청을 받아 저장한다.")
    @Test
    void save() {
        StationResponse stationResponse = stationService.save(new StationRequest("신림역"));
        Station findStation = stationRepository.findById(stationResponse.getId());

        assertThat(stationResponse.getId()).isEqualTo(findStation.getId());
    }

    @DisplayName("동일한 이름의 역이 있으면 에러를 발생한다.")
    @Test
    void saveDuplicateName() {
        stationRepository.save(new Station("신림역"));
        assertThatThrownBy(() -> stationService.save(new StationRequest("신림역"))
        ).isInstanceOf(NameDuplicatedException.class);
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void showStations() {
        stationRepository.save(new Station("신림역"));
        stationRepository.save(new Station("신대방역"));
        List<StationResponse> stationResponses = stationService.showStations();
        assertThat(stationResponses).hasSize(2);
    }

    @DisplayName("역을 삭제한다.")
    @Test
    void deleteStation() {
        Station station = stationRepository.save(new Station("신림역"));
        stationService.deleteStation(station.getId());
        assertThat(stationRepository.findAll()).isEmpty();
    }

    @DisplayName("구간에 역이 존재하여 삭제에 실패한다.")
    @Test
    void deleteStationException() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);
        Long lineId = lineRepository.save(new Line(LINE_SIX, LINE_COLOR));
        sectionRepository.save(new Section(lineId, saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));
        assertThatThrownBy(() -> stationService.deleteStation(saved_신당역.getId()))
                .isInstanceOf(SubwayException.class)
                .hasMessage("[ERROR] 역이 구간에 존재하여 삭제할 수 없습니다.");
    }

}
