package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static wooteco.subway.TestFixtures.LINE_COLOR;
import static wooteco.subway.TestFixtures.LINE_SIX;
import static wooteco.subway.TestFixtures.STANDARD_DISTANCE;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.신당역;
import static wooteco.subway.TestFixtures.창신역;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.utils.exception.SectionCreateException;
import wooteco.subway.utils.exception.SectionDeleteException;

@Transactional
@SpringBootTest
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private LineService lineService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("하행선에 구간을 추가한다.")
    @Test
    void create() {
        Station saved_신당역 = createStation(신당역);
        Station saved_동묘앞역 = createStation(동묘앞역);
        Station saved_창신역 = createStation(창신역);
        Line lineSix = createLine(LINE_SIX, LINE_COLOR);
        sectionRepository.save(new Section(lineSix.getId(), saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));

        sectionService.create(lineSix.getId(),
                new SectionRequest(saved_동묘앞역.getId(), saved_창신역.getId(), STANDARD_DISTANCE - 1));
        LineResponse lineResponse = lineService.showLine(lineSix.getId());
        assertThat(lineResponse.getStations())
                .extracting(StationResponse::getId, StationResponse::getName)
                .containsExactly(
                        tuple(saved_신당역.getId(), saved_신당역.getName()),
                        tuple(saved_동묘앞역.getId(), saved_동묘앞역.getName()),
                        tuple(saved_창신역.getId(), saved_창신역.getName())
                );
    }

    @DisplayName("중간에 구간을 추가한다.")
    @Test
    void createCutIn() {
        Station saved_신당역 = createStation(신당역);
        Station saved_동묘앞역 = createStation(동묘앞역);
        Station saved_창신역 = createStation(창신역);
        Line lineSix = createLine(LINE_SIX, LINE_COLOR);
        sectionRepository.save(new Section(lineSix.getId(), saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));

        sectionService.create(lineSix.getId(),
                new SectionRequest(saved_신당역.getId(), saved_창신역.getId(), STANDARD_DISTANCE - 1));
        LineResponse lineResponse = lineService.showLine(lineSix.getId());
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(saved_신당역),
                new StationResponse(saved_창신역),
                new StationResponse(saved_동묘앞역));
    }

    @DisplayName("중간에 구간 추가시 거리로 인해 실패한다.")
    @Test
    void createCutInDistanceException() {
        Station saved_신당역 = createStation(신당역);
        Station saved_동묘앞역 = createStation(동묘앞역);
        Station saved_창신역 = createStation(창신역);
        Line lineSix = createLine(LINE_SIX, LINE_COLOR);
        sectionRepository.save(new Section(lineSix.getId(), saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));

        assertThatThrownBy(() -> sectionService.create(lineSix.getId(),
                new SectionRequest(saved_신당역.getId(), saved_창신역.getId(), STANDARD_DISTANCE + 1)))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("구간 추가시 중복 구간으로 실패한다.")
    @Test
    void createDuplicateException() {
        Station saved_신당역 = createStation(신당역);
        Station saved_동묘앞역 = createStation(동묘앞역);
        Line lineSix = createLine(LINE_SIX, LINE_COLOR);
        sectionRepository.save(new Section(lineSix.getId(), saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));

        assertThatThrownBy(() -> sectionService.create(lineSix.getId(),
                new SectionRequest(saved_신당역.getId(), saved_동묘앞역.getId(), STANDARD_DISTANCE)))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("이미 연결된 구간 추가시 실패한다.")
    @Test
    void createConnectedException() {
        Station saved_신당역 = createStation(신당역);
        Station saved_동묘앞역 = createStation(동묘앞역);
        Station saved_창신역 = createStation(창신역);
        Line lineSix = createLine(LINE_SIX, LINE_COLOR);
        sectionRepository.save(new Section(lineSix.getId(), saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));

        sectionService.create(lineSix.getId(),
                new SectionRequest(saved_동묘앞역.getId(), saved_창신역.getId(), STANDARD_DISTANCE));
        assertThatThrownBy(() -> sectionService.create(lineSix.getId(),
                new SectionRequest(saved_신당역.getId(), saved_창신역.getId(), STANDARD_DISTANCE)))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("역 삭제시 앞, 뒤 구간을 연결한다.")
    @Test
    void deleteAndConnect() {
        Station saved_신당역 = createStation(신당역);
        Station saved_동묘앞역 = createStation(동묘앞역);
        Station saved_창신역 = createStation(창신역);
        Line lineSix = createLine(LINE_SIX, LINE_COLOR);
        sectionRepository.save(new Section(lineSix.getId(), saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));

        sectionService.create(lineSix.getId(),
                new SectionRequest(saved_동묘앞역.getId(), saved_창신역.getId(), STANDARD_DISTANCE));
        sectionService.delete(lineSix.getId(), saved_동묘앞역.getId());

        LineResponse lineResponse = lineService.showLine(lineSix.getId());
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(saved_신당역),
                new StationResponse(saved_창신역));
    }

    @DisplayName("하행역을 삭제한다.")
    @Test
    void delete() {
        Station saved_신당역 = createStation(신당역);
        Station saved_동묘앞역 = createStation(동묘앞역);
        Station saved_창신역 = createStation(창신역);
        Line lineSix = createLine(LINE_SIX, LINE_COLOR);
        sectionRepository.save(new Section(lineSix.getId(), saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));

        sectionService.create(lineSix.getId(),
                new SectionRequest(saved_동묘앞역.getId(), saved_창신역.getId(), STANDARD_DISTANCE));
        sectionService.delete(lineSix.getId(), saved_창신역.getId());

        LineResponse lineResponse = lineService.showLine(lineSix.getId());
        assertThat(lineResponse.getStations()).containsExactly(new StationResponse(saved_신당역),
                new StationResponse(saved_동묘앞역));
    }

    @DisplayName("구간이 하나일 떼 역 삭제에 실패한다.")
    @Test
    void deleteOneSectionException() {
        Station saved_신당역 = createStation(신당역);
        Station saved_동묘앞역 = createStation(동묘앞역);
        Station saved_창신역 = createStation(창신역);
        Line lineSix = createLine(LINE_SIX, LINE_COLOR);
        sectionRepository.save(new Section(lineSix.getId(), saved_신당역, saved_동묘앞역, STANDARD_DISTANCE));

        assertThatThrownBy(() -> sectionService.delete(lineSix.getId(), saved_동묘앞역.getId()))
                .isInstanceOf(SectionDeleteException.class);

    }

    private Line createLine(String lineName, String lineColor) {
        Long id = lineRepository.save(new Line(lineName, lineColor));
        return new Line(id, lineName, lineColor);
    }

    private Station createStation(Station station) {
        return stationRepository.save(station);
    }
}
