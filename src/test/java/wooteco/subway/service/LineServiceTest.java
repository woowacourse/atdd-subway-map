package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.TestFixtures.LINE_COLOR;
import static wooteco.subway.TestFixtures.STANDARD_DISTANCE;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.보문역;
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
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.utils.exception.NameDuplicatedException;

@Transactional
@SpringBootTest
public class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @DisplayName("노선을 생성한다.")
    @Test
    void create() {
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Station 선정릉역 = stationRepository.save(new Station("선정릉역"));

        LineRequest lineRequest = new LineRequest("분당선", LINE_COLOR, 선릉역.getId(), 선정릉역.getId(), STANDARD_DISTANCE);
        LineResponse lineResponse = lineService.create(lineRequest);

        assertAll(
                () -> assertThat(lineResponse.getId()).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor())
        );
    }

    @DisplayName("노선 생성시 이름이 존재할 경우 예외 발생")
    @Test
    void createDuplicateName() {
        lineRepository.save(new Line("분당선", LINE_COLOR));
        assertThatThrownBy(() -> lineService.create(new LineRequest("분당선", LINE_COLOR)))
                .isInstanceOf(NameDuplicatedException.class);
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void showLines() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);
        Line line1 = new Line("분당선", LINE_COLOR);
        Long id1 = lineRepository.save(line1);
        Section section1 = new Section(id1, saved_신당역, saved_동묘앞역, STANDARD_DISTANCE);
        sectionRepository.save(section1);

        Station saved_보문역 = stationRepository.save(보문역);
        Station saved_창신역 = stationRepository.save(창신역);
        Line line2 = new Line("신분당선", LINE_COLOR);
        Long id2 = lineRepository.save(line2);
        Section section2 = new Section(id2, saved_보문역, saved_창신역, STANDARD_DISTANCE);
        sectionRepository.save(section2);

        List<LineResponse> lineResponses = lineService.showLines();
        assertAll(
                () -> assertThat(lineResponses).hasSize(2),
                () -> assertThat(lineResponses.get(0).getStations()).hasSize(2),
                () -> assertThat(lineResponses.get(1).getStations()).hasSize(2)
        );
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);
        Line line1 = new Line("분당선", LINE_COLOR);
        Long id = lineRepository.save(line1);
        Section section1 = new Section(id, saved_신당역, saved_동묘앞역, STANDARD_DISTANCE);
        sectionRepository.save(section1);
        LineResponse lineResponse = lineService.showLine(id);

        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo(LINE_COLOR),
                () -> assertThat(lineResponse.getStations()).hasSize(2),
                () -> assertThat(lineResponse.getStations()).extracting("id", "name")
                        .containsExactly(
                                tuple(saved_신당역.getId(), saved_신당역.getName()),
                                tuple(saved_동묘앞역.getId(), saved_동묘앞역.getName())
                        )
        );
    }

    @DisplayName("노선을 업데이트 한다.")
    @Test
    void update() {
        Long id = lineRepository.save(new Line("분당선", LINE_COLOR));
        lineService.update(id, new LineRequest("신분당선", "bg-yellow-600"));

        Line findUpdateLine = lineRepository.findById(id);
        assertAll(
                () -> assertThat(findUpdateLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(findUpdateLine.getColor()).isEqualTo("bg-yellow-600")
        );
    }


    @DisplayName("노선을 제거 한다.")
    @Test
    void delete() {
        Long id = lineRepository.save(new Line("분당선", LINE_COLOR));
        lineService.delete(id);

        assertThat(lineRepository.findAll()).isEmpty();
    }

}
