package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.ExistKeyException;
import wooteco.subway.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
@SuppressWarnings("NonAsciiCharacters")
class LineServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    private LineService lineService;

    private StationResponse 선릉역;
    private StationResponse 선정릉역;
    private StationResponse 모란역;
    private StationResponse 기흥역;
    private LineResponse 분당선;

    @BeforeEach
    void setUp() {
        StationService stationService = new StationService(new StationDao(jdbcTemplate, dataSource));
        lineService = new LineService(new LineDao(jdbcTemplate, dataSource),
                new StationDao(jdbcTemplate, dataSource), new SectionDao(jdbcTemplate, dataSource));
        선릉역 = stationService.create(new StationRequest("선릉역"));
        선정릉역 = stationService.create(new StationRequest("선정릉역"));
        모란역 = stationService.create(new StationRequest("모란역"));
        기흥역 = stationService.create(new StationRequest("기흥역"));

        분당선 = lineService.create(new LineRequest("분당선", "yellow", 선릉역.getId(),
                선정릉역.getId(), 10));
    }

    @DisplayName("새로운 노선 셍성 정보를 이용해 노선을 생성한다.")
    @Test
    void create() {
        LineRequest request =
                new LineRequest("신분당선", "red", 선릉역.getId(), 선정릉역.getId(), 10);

        LineResponse response = lineService.create(request);

        assertAll(
                () -> assertThat(response.getName()).isEqualTo(request.getName()),
                () -> assertThat(response.getColor()).isEqualTo(request.getColor()),
                () -> assertThat(response.getStations()).hasSize(2)
        );
    }

    @DisplayName("존재하는 노선 이름으로 새로운 노선을 생성하려 시도하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenCreateLineWithExistName() {
        LineRequest request =
                new LineRequest("신분당선", "red", 선릉역.getId(), 선정릉역.getId(), 10);
        lineService.create(request);

        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(ExistKeyException.class)
                .hasMessageMatching("요청하신 노선의 이름은 이미 존재합니다.");
    }

    @DisplayName("존재하지 않는 역으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenCreateLineWithNotExistsStation() {
        LineRequest request =
                new LineRequest("신분당선", "red", 선릉역.getId(), 100L, 10);

        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행,하행 중복된 역으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenCreateLineWithDuplicateStation() {
        LineRequest request =
                new LineRequest("신분당선", "red", 선릉역.getId(), 선릉역.getId(), 10);

        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("거리가 0이하인 구간으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenCreateLineWithZeroDistance() {
        LineRequest request =
                new LineRequest("신분당선", "red", 선릉역.getId(), 선릉역.getId(), 10);

        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선 id에 따른 노선 정보를 조회한다.")
    @Test
    void findById() {
        LineResponse response = lineService.findById(분당선.getId());

        List<StationResponse> expectedStations = List.of(선릉역, 선정릉역);
        boolean hasAllStation = hasAllStation(response.getStations(), expectedStations);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(분당선.getId()),
                () -> assertThat(response.getName()).isEqualTo(분당선.getName()),
                () -> assertThat(response.getColor()).isEqualTo(분당선.getColor()),
                () -> assertThat(hasAllStation).isTrue()
        );
    }

    @DisplayName("구간 목록들을 모두 읽어온다.")
    @Test
    void findAll() {
        LineResponse 새로운_분당선 = lineService.create(new LineRequest("새로운 분당선", "yellow", 기흥역.getId(),
                모란역.getId(), 10));

        List<LineResponse> response = lineService.findAll();

        LineResponse 분당선_응답 = response.stream()
                .filter(r -> r.getId().equals(분당선.getId()))
                .findFirst().orElseThrow();
        List<StationResponse> 분당선_포함역 = List.of(선릉역, 선정릉역);
        boolean 분당선_모든역_포함여부 = hasAllStation(분당선_응답.getStations(), 분당선_포함역);


        LineResponse 새로운_분당선_응답 = response.stream()
                .filter(r -> r.getId().equals(새로운_분당선.getId()))
                .findFirst().orElseThrow();
        List<StationResponse> 새로운_분당선_포함역 = List.of(기흥역, 모란역);
        boolean 새로운_분당선_모든역_포함여부 = hasAllStation(새로운_분당선_응답.getStations(), 새로운_분당선_포함역);

        assertAll(
                () -> assertThat(분당선_응답.getId()).isEqualTo(분당선.getId()),
                () -> assertThat(분당선_응답.getName()).isEqualTo(분당선.getName()),
                () -> assertThat(분당선_응답.getColor()).isEqualTo(분당선.getColor()),
                () -> assertThat(분당선_모든역_포함여부).isTrue(),

                () -> assertThat(새로운_분당선_응답.getId()).isEqualTo(새로운_분당선.getId()),
                () -> assertThat(새로운_분당선_응답.getName()).isEqualTo(새로운_분당선.getName()),
                () -> assertThat(새로운_분당선_응답.getColor()).isEqualTo(새로운_분당선.getColor()),
                () -> assertThat(새로운_분당선_모든역_포함여부).isTrue()
        );
    }

    private boolean hasAllStation(List<StationResponse> responseStations, List<StationResponse> expected) {
        boolean result = true;
        for (StationResponse station : expected) {
            boolean hasMatchStation = responseStations.stream()
                    .anyMatch(s -> s.getId().equals(station.getId()));
            if (!hasMatchStation) {
                result = false;
            }
        }
        return result;
    }

    @DisplayName("노선 정보를 업데이트한다.")
    @Test
    void update() {
        LineRequest request = new LineRequest("신분당선", "red", 0L, 0L, 0);
        lineService.update(분당선.getId(), request);
        LineResponse response = lineService.findById(분당선.getId());

        assertAll(
                () -> assertThat(request.getName()).isEqualTo(response.getName()),
                () -> assertThat(request.getColor()).isEqualTo(response.getColor())
        );
    }

    @DisplayName("존재하지 않는 노선 정보 업데이트를 시도하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenUpdateNotExistLine() {
        LineRequest request = new LineRequest("신분당선", "red", 0L, 0L, 0);
        assertThatThrownBy(() -> lineService.update(100L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageMatching("접근하려는 노선이 존재하지 않습니다.");
    }

    @DisplayName("노선 정보를 삭제한다.")
    @Test
    void delete() {
        lineService.delete(분당선.getId());

        List<LineResponse> responses = lineService.findAll();
        boolean hasLine = responses.stream()
                .noneMatch(r -> r.getId().equals(분당선.getId()));

        assertThat(hasLine).isTrue();
    }

    @DisplayName("존재하지 않는 노선 삭제를 시도하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenDeleteNotExistLine() {
        assertThatThrownBy(() -> lineService.delete(100L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageMatching("접근하려는 노선이 존재하지 않습니다.");
    }
}
