package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateLineException;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionDao sectionDao;

    private Station gangNam;
    private Station yeokSam;
    private Station seonNeung;

    @BeforeEach
    void setUpStation() {
        gangNam = stationDao.save(new Station("강남역"));
        yeokSam = stationDao.save(new Station("역삼역"));
        seonNeung = stationDao.save(new Station("선릉역"));
    }

    @DisplayName("지하철 노선과 포함된 구간을 저장한다.")
    @Test
    void createLineAndSection() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", gangNam.getId(), yeokSam.getId(), 1);

        LineResponse actual = lineService.create(lineRequest);
        List<StationResponse> actualStations = actual.getStations();

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(lineRequest.getName());
        assertThat(actualStations).extracting(StationResponse::getId, StationResponse::getName)
                .containsOnly(
                        tuple(gangNam.getId(), gangNam.getName()),
                        tuple(yeokSam.getId(), yeokSam.getName())
                );
    }

    @DisplayName("이미 저장된 노선과 중복된 이름의 노선을 저장하려 하면 예외가 발생한다.")
    @Test
    void createDuplicateName() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", gangNam.getId(), yeokSam.getId(), 1);
        LineRequest duplicateRequest = new LineRequest("2호선", "빨간색", gangNam.getId(), yeokSam.getId(), 1);

        lineService.create(lineRequest);

        assertThatThrownBy(() -> lineService.create(duplicateRequest))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("이미 저장된 노선과 중복된 색상의 노선을 저장하려 하면 예외가 발생한다.")
    @Test
    void createDuplicateColor() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", gangNam.getId(), yeokSam.getId(), 1);
        LineRequest duplicateRequest = new LineRequest("성수지선", "초록색", gangNam.getId(), yeokSam.getId(), 1);

        lineService.create(lineRequest);

        assertThatThrownBy(() -> lineService.create(duplicateRequest))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("이미 존재하는 노선 색상입니다.");
    }

    @DisplayName("저장된 노선을 모두 조회한다.")
    @Test
    void findAll() {
        LineRequest request1 = new LineRequest("1호선", "군청색", gangNam.getId(), yeokSam.getId(), 1);
        LineRequest request2 = new LineRequest("2호선", "초록색", gangNam.getId(), yeokSam.getId(), 1);
        lineService.create(request1);
        lineService.create(request2);

        List<LineResponse> lineResponses = lineService.findAll();

        assertThat(lineResponses).hasSize(2)
                .extracting(LineResponse::getName, LineResponse::getColor)
                .containsOnly(
                        tuple(request1.getName(), request1.getColor()),
                        tuple(request2.getName(), request2.getColor())
                );
    }

    @DisplayName("지정한 id에 해당하는 노선을 조회한다.")
    @Test
    void findById() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", gangNam.getId(), yeokSam.getId(), 1);
        Long id = lineService.create(lineRequest).getId();

        LineResponse actual = lineService.find(id);
        LineResponse expected = new LineResponse(id, "2호선", "초록색");

        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @DisplayName("지정한 id에 해당하는 노선이 없으면 예외가 발생한다.")
    @Test
    void findNotExist() {
        assertThatThrownBy(() -> lineService.find(Long.MAX_VALUE))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @Disabled
    @DisplayName("지정한 id에 해당하는 노선 정보를 수정한다.")
    @Test
    void update() {
        // TODO: 업데이트 시 다른 requestDto 필요
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        Long id = lineService.create(lineRequest).getId();

        LineRequest updateRequest = new LineRequest("1호선", "군청색");
        lineService.update(id, updateRequest);
        LineResponse expected = new LineResponse(id, "1호선", "군청색");
        LineResponse actual = lineService.find(id);

        assertEquals(expected, actual);
    }

    @Disabled
    @DisplayName("수정하려는 노선이 없으면 예외가 발생한다.")
    @Test
    void updateNotExist() {
        // TODO: 업데이트 시 다른 requestDto 필요
        LineRequest updateRequest = new LineRequest("2호선", "초록색");

        assertThatThrownBy(() -> lineService.update(Long.MAX_VALUE, updateRequest))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @Disabled
    @DisplayName("이미 존재하는 노선의 이름으로 수정하려고 하면 예외가 발생한다.")
    @Test
    void updateToDuplicateName() {
        // TODO: 업데이트 시 다른 requestDto 필요
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        Long id = lineService.create(lineRequest).getId();
        lineRequest = new LineRequest("8호선", "분홍색");
        lineService.create(lineRequest);

        LineRequest updateRequest = new LineRequest("8호선", "초록색");

        assertThatThrownBy(() -> lineService.update(id, updateRequest))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("이미 존재하는 노선 이름입니다.");
    }

    @Disabled
    @DisplayName("이미 존재하는 노선의 색상으로 수정하려고 하면 예외가 발생한다.")
    @Test
    void updateToDuplicateColor() {
        // TODO: 업데이트 시 다른 requestDto 필요
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        Long id = lineService.create(lineRequest).getId();
        lineRequest = new LineRequest("8호선", "분홍색");
        lineService.create(lineRequest);

        LineRequest updateRequest = new LineRequest("2호선", "분홍색");

        assertThatThrownBy(() -> lineService.update(id, updateRequest))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("이미 존재하는 노선 색상입니다.");
    }

    @DisplayName("지정한 id에 해당하는 노선을 삭제한다.")
    @Test
    void delete() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", gangNam.getId(), yeokSam.getId(), 1);
        Long id = lineService.create(lineRequest).getId();

        lineService.delete(id);
        List<LineResponse> lineResponses = lineService.findAll();

        assertThat(lineResponses).isEmpty();
    }

    @DisplayName("삭제하려는 노선이 없으면 예외가 발생한다.")
    @Test
    void deleteNotExist() {
        assertThatThrownBy(() -> lineService.delete(Long.MAX_VALUE))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @DisplayName("특정 노선에 새 구간을 추가한다.")
    @Test
    void addSection() {
        LineRequest lineRequest = new LineRequest("2호선", "green", gangNam.getId(), seonNeung.getId(), 2);
        LineResponse lineResponse = lineService.create(lineRequest);
        Line line = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
        SectionRequest sectionRequest = new SectionRequest(gangNam.getId(), yeokSam.getId(), 1);

        lineService.createSectionBySectionRequest(line.getId(), sectionRequest);

        List<Section> sections = sectionDao.findAllByLine(line);

        assertThat(sections).hasSize(2)
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .contains(
                        tuple(gangNam, yeokSam, 1),
                        tuple(yeokSam, seonNeung, 1)
                );
    }

    @DisplayName("특정 노선에서 요청으로 받은 역을 포함하는 구간을 제거한다.")
    @Test
    void deleteSection() {
        LineRequest lineRequest = new LineRequest("2호선", "green", gangNam.getId(), yeokSam.getId(), 1);
        LineResponse lineResponse = lineService.create(lineRequest);
        Line line = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());

        SectionRequest newSectionRequest = new SectionRequest(yeokSam.getId(), seonNeung.getId(), 1);

        lineService.createSectionBySectionRequest(line.getId(), newSectionRequest);

        lineService.deleteSection(line.getId(), yeokSam.getId());

        List<Section> sections = sectionDao.findAllByLine(line);

        assertThat(sections).hasSize(1)
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .containsOnly(
                        tuple(gangNam, seonNeung, 2)
                );
    }

    private void assertEquals(LineResponse expected, LineResponse actual) {
        assertThat(expected.getId()).isEqualTo(actual.getId());
        assertThat(expected.getName()).isEqualTo(actual.getName());
        assertThat(expected.getColor()).isEqualTo(actual.getColor());
    }

}
