package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.신당역;
import static wooteco.subway.TestFixtures.창신역;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
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

    private Line lineSix;
    private Station saved_신당역;
    private Station saved_동묘앞역;
    private Station saved_창신역;

    @BeforeEach
    void setUp() {
        saved_신당역 = stationRepository.save(신당역);
        saved_동묘앞역 = stationRepository.save(동묘앞역);
        saved_창신역 = stationRepository.save(창신역);
        Long lineId = lineRepository.save(new Line("6호선", "bg-red-500"));
        sectionRepository.save(new Section(lineId, saved_신당역, saved_동묘앞역, 10));
        lineSix = new Line(lineId, "6호선", "bg-red-500");
    }

    @DisplayName("하행선에 구간을 추가한다.")
    @Test
    void create() {
        sectionService.create(lineSix.getId(), new SectionRequest(saved_동묘앞역.getId(), saved_창신역.getId(), 5));
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
        sectionService.create(lineSix.getId(), new SectionRequest(saved_신당역.getId(), saved_창신역.getId(), 5));
        LineResponse lineResponse = lineService.showLine(lineSix.getId());
        assertThat(lineResponse.getStations())
                .extracting(StationResponse::getId, StationResponse::getName)
                .containsExactly(
                        tuple(saved_신당역.getId(), saved_신당역.getName()),
                        tuple(saved_창신역.getId(), saved_창신역.getName()),
                        tuple(saved_동묘앞역.getId(), saved_동묘앞역.getName())
                );
    }

    @DisplayName("중간에 구간 추가시 거리로 인해 실패한다.")
    @Test
    void createCutInDistanceException() {
        assertThatThrownBy(() -> sectionService.create(lineSix.getId(),
                new SectionRequest(saved_신당역.getId(), saved_창신역.getId(), 11)))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("구간 추가시 중복 구간으로 실패한다.")
    @Test
    void createDuplicateException() {
        assertThatThrownBy(() -> sectionService.create(lineSix.getId(),
                new SectionRequest(saved_신당역.getId(), saved_동묘앞역.getId(), 10)))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("이미 연결된 구간 추가시 실패한다.")
    @Test
    void createConnectedException() {
        sectionService.create(lineSix.getId(), new SectionRequest(saved_동묘앞역.getId(), saved_창신역.getId(), 10));
        assertThatThrownBy(() -> sectionService.create(lineSix.getId(),
                new SectionRequest(saved_신당역.getId(), saved_창신역.getId(), 10)))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("역 삭제시 앞, 뒤 구간을 연결한다.")
    @Test
    void deleteAndConnect() {
        sectionService.create(lineSix.getId(), new SectionRequest(saved_동묘앞역.getId(), saved_창신역.getId(), 10));
        sectionService.delete(lineSix.getId(), saved_동묘앞역.getId());

        LineResponse lineResponse = lineService.showLine(lineSix.getId());
        assertThat(lineResponse.getStations()).extracting("id", "name")
                .containsExactly(
                        tuple(saved_신당역.getId(), saved_신당역.getName()),
                        tuple(saved_창신역.getId(), saved_창신역.getName())
                );
    }

    @DisplayName("하행역을 삭제한다.")
    @Test
    void delete() {
        sectionService.create(lineSix.getId(), new SectionRequest(saved_동묘앞역.getId(), saved_창신역.getId(), 10));
        sectionService.delete(lineSix.getId(), saved_창신역.getId());

        LineResponse lineResponse = lineService.showLine(lineSix.getId());
        assertThat(lineResponse.getStations()).extracting("id", "name")
                .containsExactly(
                        tuple(saved_신당역.getId(), saved_신당역.getName()),
                        tuple(saved_동묘앞역.getId(), saved_동묘앞역.getName())
                );
    }

    @DisplayName("구간이 하나일 떼 역 삭제에 실패한다.")
    @Test
    void deleteOneSectionException() {
        assertThatThrownBy(() -> sectionService.delete(lineSix.getId(), saved_동묘앞역.getId()))
                .isInstanceOf(SectionDeleteException.class);

    }
}
