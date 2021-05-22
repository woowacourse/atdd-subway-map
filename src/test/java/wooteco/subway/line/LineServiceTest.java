package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.UnitTest;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistItemException;
import wooteco.subway.line.dto.LineOnlyDataResponse;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("LineService 테스트")
class LineServiceTest extends UnitTest {

    private static final Station GANGNAM_STATION = new Station(1L, "강남역");
    private static final Station JAMSIL_STATION = new Station(2L, "잠실역");
    private static final Station YEOKSAM_STATION = new Station(3L, "역삼역");
    private static final Station SILLIM_STATION = new Station(4L, "신림역");
    private static final Map<Long, StationResponse> NUMBER_TO_STATION = new HashMap<>();
    private static final LineRequest LINE_2_REQUEST = new LineRequest("2호선", "bg-green-600", 1L, 2L,
        10);
    private static final LineRequest LINE_3_REQUEST = new LineRequest("3호선", "bg-orange-600", 1L,
        3L, 13);

    private final LineService lineService;
    private final StationDao stationDao;

    public LineServiceTest(LineService lineService, StationDao stationDao) {
        this.lineService = lineService;
        this.stationDao = stationDao;
    }

    @BeforeAll
    static void BeforeAllSetUp() {
        NUMBER_TO_STATION.put(1L, new StationResponse(GANGNAM_STATION));
        NUMBER_TO_STATION.put(2L, new StationResponse(JAMSIL_STATION));
        NUMBER_TO_STATION.put(3L, new StationResponse(YEOKSAM_STATION));
        NUMBER_TO_STATION.put(4L, new StationResponse(SILLIM_STATION));
    }

    @BeforeEach
    void setUp() {
        stationDao.save(GANGNAM_STATION);
        stationDao.save(JAMSIL_STATION);
        stationDao.save(YEOKSAM_STATION);
        stationDao.save(SILLIM_STATION);
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void create() {
        //given

        //when
        LineResponse lineResponse = lineService.create(LINE_2_REQUEST);

        //then
        checkedThen(LINE_2_REQUEST, lineResponse);
    }

    @Test
    @DisplayName("중복된 이름의 노선을 생성하면 에러가 발생한다.")
    void createWithDuplicateName() {
        //given
        //when
        lineService.create(LINE_2_REQUEST);

        //then
        assertThatThrownBy(() -> lineService.create(LINE_2_REQUEST))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 찾는다.")
    void findById() {
        //given
        lineService.create(LINE_2_REQUEST);

        //when
        LineResponse lineResponse = lineService.findById(1L);

        //then
        checkedThen(LINE_2_REQUEST, lineResponse);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 찾는다.")
    void findByIdWithNotExistItemException() {
        assertThatThrownBy(() -> lineService.findById(1L))
            .isInstanceOf(NotExistItemException.class);
    }

    @Test
    @DisplayName("노선 전체를 가져온다.")
    void findAll() {
        //given
        lineService.create(LINE_2_REQUEST);
        lineService.create(LINE_3_REQUEST);

        //when
        List<LineOnlyDataResponse> lines = lineService.findAll();

        //then
        assertThat(lines).hasSize(2);
        checkedThenOnlyLineData(LINE_2_REQUEST, lines.get(0));
        checkedThenOnlyLineData(LINE_3_REQUEST, lines.get(1));
    }

    @Test
    @DisplayName("노선의 정보를 수정한다.")
    void update() {
        //given
        lineService.create(LINE_2_REQUEST);

        //when
        lineService.update(1L, LINE_3_REQUEST);

        //then
        checkedThen(new LineRequest("3호선", "bg-orange-600", 1L, 2L, 10), lineService.findById(1L));
    }

    @Test
    @DisplayName("없는 노선의 정보를 수정하면 에러가 발생한다.")
    void updateWithNotExistItem() {
        //given

        //when

        //then
        assertThatThrownBy(() -> lineService.update(1L, LINE_2_REQUEST))
            .isInstanceOf(NotExistItemException.class);
    }

    @Test
    @DisplayName("기존에 있는 이름으로 노선을 수정시 에러가 발생한다.")
    void updateWithDuplicateName() {
        //given
        lineService.create(LINE_2_REQUEST);
        lineService.create(LINE_3_REQUEST);

        //when, then
        assertThatThrownBy(() -> lineService.update(1L, LINE_3_REQUEST))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 삭제한다.")
    void delete() {
        //given
        lineService.create(LINE_2_REQUEST);

        //when
        lineService.delete(1L);

        //then
        assertThat(lineService.findAll()).hasSize(0);
    }

    private void checkedThen(LineRequest lineRequest, LineResponse lineResponse) {
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
        List<StationResponse> stations = Arrays.asList(
            NUMBER_TO_STATION.get(lineRequest.getUpStationId()),
            NUMBER_TO_STATION.get(lineRequest.getDownStationId())
        );
        assertThat(lineResponse.getStations()).usingRecursiveComparison().isEqualTo(stations);
    }

    private void checkedThenOnlyLineData(LineRequest lineRequest,
        LineOnlyDataResponse lineResponse) {
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }
}