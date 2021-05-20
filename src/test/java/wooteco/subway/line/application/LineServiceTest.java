package wooteco.subway.line.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.common.exception.AlreadyExistsException;
import wooteco.subway.common.exception.InvalidInputException;
import wooteco.subway.common.exception.NotFoundException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.SectionDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static wooteco.subway.line.LineFactory.구간없는_인천1호선;
import static wooteco.subway.line.LineFactory.인천1호선;
import static wooteco.subway.line.SectionFactory.인천1호선_흑기백기구간;
import static wooteco.subway.station.StationFactory.백기역;
import static wooteco.subway.station.StationFactory.흑기역;

@DisplayName("노선 서비스로직 테스트")
class LineServiceTest {
    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    private Line line;
    private Line noSectionsLine;
    private LineRequest 인천1호선_Request;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        인천1호선_Request = new LineRequest(인천1호선.name(), 인천1호선.color(), 흑기역.id(), 백기역.id(), 7);
        line = new Line(인천1호선.id(), 인천1호선.name(), 인천1호선.color(), 인천1호선.sortedSections());
        noSectionsLine = new Line(인천1호선.id(), 인천1호선.name(), 인천1호선.color());
    }

    @Test
    @DisplayName("노선 정상 저장된다")
    void save() {
        //given
        //when
        LineResponse lineResponse = 노선_저장_요청(인천1호선_Request);

        //then
        노선_정상_저장됨(인천1호선_Request, lineResponse);
    }

    @Test
    @DisplayName("구건 저장 시 상행역과 하행역이 같으면 예와가 발생한다")
    void saveException() {
        //given
        LineRequest request = new LineRequest(인천1호선_Request.getName(), 인천1호선_Request.getColor(), 흑기역.id(), 흑기역.id(), 인천1호선_흑기백기구간.distance());

        //then
        assertThatThrownBy(() -> 노선_저장_요청(request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("상행역과 하행역은 같을 수 없음! ");
    }

    @Test
    @DisplayName("노선에 포함된 구간을 찾는다")
    void findBy() {
        //given
        LineResponse saveResponse = 노선_저장_요청(인천1호선_Request);

        //when
        LineResponse findResponse = 노선단일_조회_요청(line);

        //then
        노선_정상_조회됨(saveResponse, findResponse);
    }

    @Test
    @DisplayName("구간 등록시 상행역과 하행역이 이미 등록 되어있다면 예외가 발생한다. ")
    void registrationDuplicateException() {
        //given
        LineResponse saveResponse = 노선_저장_요청(인천1호선_Request);
        SectionRequest sectionRequest = new SectionRequest(인천1호선_흑기백기구간.upStationId(), 인천1호선_흑기백기구간.downStationId(), 5);

        //when
        //then
        assertThatThrownBy(() ->노선_구간_추가_요청(line, sectionRequest))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    @DisplayName("초기 구간 등록시 상행역과 하행역의 아이디가 없으면 예외가 발생한다. ")
    void registrationNotExistException() {
        //given
        LineRequest request = new LineRequest(인천1호선_Request.getName(), 인천1호선_Request.getColor(), 흑기역.id(), 흑기역.id(), 인천1호선_흑기백기구간.distance());

        //then
        assertThatThrownBy(() -> 노선_저장_요청(request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("상행역과 하행역은 같을 수 없음! ");
    }

    private void 노선_구간_추가_요청(Line line, SectionRequest sectionRequest) {
        노선_저장되어_있음();
        when(sectionDao.findByLineId(line.id())).thenReturn(Collections.singletonList(인천1호선_흑기백기구간));
        when(lineDao.findById(line.id())).thenReturn(Optional.of(line));
        lineService.addSection(line.id(), sectionRequest);
    }

    private LineResponse 노선_저장_요청(LineRequest request) {
        노선_저장되어_있음();
        return lineService.save(request);
    }

    private void 노선_저장되어_있음() {
        when(lineDao.save(any(Line.class))).thenReturn(noSectionsLine);
        when(sectionDao.save(any(Section.class))).thenReturn(인천1호선_흑기백기구간);
        when(stationDao.findById(흑기역.id())).thenReturn(Optional.of(흑기역));
        when(stationDao.findById(백기역.id())).thenReturn(Optional.of(백기역));
    }

    private LineResponse 노선단일_조회_요청(Line line) {
        when(lineDao.findById(line.id())).thenReturn(Optional.of(line));
        return lineService.findLine(line.id());
    }

    private void 노선_정상_저장됨(LineRequest lineRequest, LineResponse lineResponse) {
        assertThat(lineRequest.getName()).isEqualTo(lineResponse.getName());
        assertThat(lineRequest.getColor()).isEqualTo(lineResponse.getColor());
    }

    private void 노선_정상_조회됨(LineResponse saveResponse, LineResponse findResponse) {
        assertThat(saveResponse.getName()).isEqualTo(findResponse.getName());
        노선_정상_포함됨(saveResponse, findResponse);
    }

    private void 노선_정상_포함됨(LineResponse saveResponse, LineResponse findResponse) {
        assertThat(saveResponse.getStations().containsAll(findResponse.getStations())).isEqualTo(true);
    }
}
