package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
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

    @DisplayName("노선을 생성한다.")
    @Test
    void create() {
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Station 선정릉역 = stationRepository.save(new Station("선정릉역"));

        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600", 선릉역.getId(), 선정릉역.getId(), 5);
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
        lineRepository.save(new Line("분당선", "bg-red-600"));
        assertThatThrownBy(() -> lineService.create(new LineRequest("분당선", "bg-red-600")))
                .isInstanceOf(NameDuplicatedException.class);
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void showLines() {
        lineRepository.save(new Line("분당선", "bg-red-600"));
        lineRepository.save(new Line("신분당선", "bg-yellow-600"));

        List<LineResponse> lineResponses = lineService.showLines();
        assertThat(lineResponses).hasSize(2);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        Long id = lineRepository.save(new Line("분당선", "bg-red-600"));
        LineResponse lineResponse = lineService.showLine(id);

        assertAll(
            () -> assertThat(lineResponse.getName()).isEqualTo("분당선"),
            () -> assertThat(lineResponse.getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("노선을 업데이트 한다.")
    @Test
    void update() {
        Long id = lineRepository.save(new Line("분당선", "bg-red-600"));
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
        Long id = lineRepository.save(new Line("분당선", "bg-red-600"));
        lineService.delete(id);

        assertThat(lineRepository.findAll()).isEmpty();
    }

}
