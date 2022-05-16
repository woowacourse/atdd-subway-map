package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.duplicate.DuplicateLineException;
import wooteco.subway.exception.notfound.LineNotFoundException;
import wooteco.subway.exception.notfound.SectionNotFoundException;
import wooteco.subway.exception.notfound.StationNotFoundException;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionDao sectionDao;

    private Station gangnam;
    private Station yeoksam;
    private Station seolleung;

    @BeforeEach
    void setUpStation() {
        gangnam = stationDao.save(new Station("강남역"));
        yeoksam = stationDao.save(new Station("역삼역"));
        seolleung = stationDao.save(new Station("선릉역"));
    }

    @DisplayName("지하철 노선과 포함된 구간을 저장한다.")
    @Test
    void createLineAndSection() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);

        LineResponse actual = lineService.save(lineCreateRequest);
        List<StationResponse> actualStations = actual.getStations();

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(lineCreateRequest.getName());
        assertThat(actualStations).extracting(StationResponse::getId, StationResponse::getName)
                .containsOnly(
                        tuple(gangnam.getId(), gangnam.getName()),
                        tuple(yeoksam.getId(), yeoksam.getName())
                );
    }

    @DisplayName("이미 저장된 노선과 중복된 이름의 노선을 저장하려 하면 예외가 발생한다.")
    @Test
    void createDuplicateName() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);
        LineCreateRequest duplicateRequest = new LineCreateRequest("2호선", "빨간색", gangnam.getId(), yeoksam.getId(), 1);

        lineService.save(lineCreateRequest);

        assertThatThrownBy(() -> lineService.save(duplicateRequest))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("이미 저장된 노선과 중복된 색상의 노선을 저장하려 하면 예외가 발생한다.")
    @Test
    void createDuplicateColor() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);
        LineCreateRequest duplicateRequest = new LineCreateRequest("성수지선", "초록색", gangnam.getId(), yeoksam.getId(), 1);

        lineService.save(lineCreateRequest);

        assertThatThrownBy(() -> lineService.save(duplicateRequest))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("이미 존재하는 노선 색상입니다.");
    }

    @DisplayName("저장된 노선을 모두 조회한다.")
    @Test
    void findAll() {
        LineCreateRequest request1 = new LineCreateRequest("1호선", "군청색", gangnam.getId(), yeoksam.getId(), 1);
        LineCreateRequest request2 = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);
        lineService.save(request1);
        lineService.save(request2);

        List<LineResponse> lineResponses = lineService.findAll();

        assertThat(lineResponses).hasSize(2)
                .extracting(LineResponse::getName, LineResponse::getColor)
                .containsOnly(
                        tuple(request1.getName(), request1.getColor()),
                        tuple(request2.getName(), request2.getColor())
                );
        lineResponses.forEach(lineResponse -> assertThat(lineResponse.getStations())
                .extracting(StationResponse::getId, StationResponse::getName)
                .containsOnly(
                        tuple(gangnam.getId(), gangnam.getName()),
                        tuple(yeoksam.getId(), yeoksam.getName())
                )
        );
    }

    @DisplayName("지정한 id에 해당하는 노선을 조회한다.")
    @Test
    void findById() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);
        Long id = lineService.save(lineCreateRequest).getId();

        LineResponse actual = lineService.find(id);
        LineResponse expected = new LineResponse(new Line(id, "2호선", "초록색"), List.of(gangnam, yeoksam));

        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @DisplayName("지정한 id에 해당하는 노선이 없으면 예외가 발생한다.")
    @Test
    void findNotExist() {
        assertThatThrownBy(() -> lineService.find(Long.MAX_VALUE))
                .isInstanceOf(LineNotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @DisplayName("지정한 id에 해당하는 노선 정보를 수정한다.")
    @Test
    void update() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);
        Long id = lineService.save(lineCreateRequest).getId();

        LineUpdateRequest updateRequest = new LineUpdateRequest("1호선", "군청색");
        lineService.update(id, updateRequest);
        LineResponse expected = new LineResponse(new Line(id, "1호선", "군청색"), List.of(gangnam, yeoksam));
        LineResponse actual = lineService.find(id);

        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @DisplayName("수정하려는 노선이 없으면 예외가 발생한다.")
    @Test
    void updateNotExist() {
        LineUpdateRequest updateRequest = new LineUpdateRequest("2호선", "초록색");

        assertThatThrownBy(() -> lineService.update(Long.MAX_VALUE, updateRequest))
                .isInstanceOf(LineNotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @DisplayName("이미 존재하는 노선의 이름으로 수정하려고 하면 예외가 발생한다.")
    @Test
    void updateToDuplicateName() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);
        Long id = lineService.save(lineCreateRequest).getId();
        lineCreateRequest = new LineCreateRequest("8호선", "분홍색", gangnam.getId(), yeoksam.getId(), 1);
        lineService.save(lineCreateRequest);

        LineUpdateRequest updateRequest = new LineUpdateRequest("8호선", "초록색");

        assertThatThrownBy(() -> lineService.update(id, updateRequest))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("이미 존재하는 노선의 색상으로 수정하려고 하면 예외가 발생한다.")
    @Test
    void updateToDuplicateColor() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);
        Long id = lineService.save(lineCreateRequest).getId();
        lineCreateRequest = new LineCreateRequest("8호선", "분홍색", gangnam.getId(), yeoksam.getId(), 1);
        lineService.save(lineCreateRequest);

        LineUpdateRequest updateRequest = new LineUpdateRequest("2호선", "분홍색");

        assertThatThrownBy(() -> lineService.update(id, updateRequest))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("이미 존재하는 노선 색상입니다.");
    }

    @DisplayName("지정한 id에 해당하는 노선을 삭제한다.")
    @Test
    void delete() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "초록색", gangnam.getId(), yeoksam.getId(), 1);
        Long id = lineService.save(lineCreateRequest).getId();

        lineService.delete(id);
        List<LineResponse> lineResponses = lineService.findAll();

        assertThat(lineResponses).isEmpty();
    }

    @DisplayName("삭제하려는 노선이 없으면 예외가 발생한다.")
    @Test
    void deleteNotExist() {
        assertThatThrownBy(() -> lineService.delete(Long.MAX_VALUE))
                .isInstanceOf(LineNotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @DisplayName("특정 노선에 새 구간을 추가한다.")
    @Test
    void addSection() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "green", gangnam.getId(), seolleung.getId(),
                2);
        LineResponse lineResponse = lineService.save(lineCreateRequest);
        Line line = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
        SectionRequest sectionRequest = new SectionRequest(gangnam.getId(), yeoksam.getId(), 1);
        lineService.saveSectionBySectionRequest(line.getId(), sectionRequest);

        List<Section> sections = sectionDao.findAllByLine(line);

        assertThat(sections).hasSize(2)
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .contains(
                        tuple(gangnam, yeoksam, 1),
                        tuple(yeoksam, seolleung, 1)
                );
    }

    @DisplayName("없는 역으로 구간을 생성하려고 하면 예외를 반환한다.")
    @Test
    void addSectionWithNotExistStation() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "green", gangnam.getId(), seolleung.getId(),
                2);
        LineResponse lineResponse = lineService.save(lineCreateRequest);
        Line line = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
        SectionRequest sectionRequest = new SectionRequest(seolleung.getId(), Long.MAX_VALUE, 2);

        assertThatThrownBy(() -> lineService.saveSectionBySectionRequest(line.getId(), sectionRequest))
                .isInstanceOf(StationNotFoundException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }

    @DisplayName("특정 노선에서 요청으로 받은 역을 포함하는 구간을 제거한다.")
    @Test
    void deleteSection() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "green", gangnam.getId(), yeoksam.getId(),
                1);
        LineResponse lineResponse = lineService.save(lineCreateRequest);
        Line line = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
        SectionRequest newSectionRequest = new SectionRequest(yeoksam.getId(), seolleung.getId(), 1);
        lineService.saveSectionBySectionRequest(line.getId(), newSectionRequest);

        lineService.deleteSection(line.getId(), yeoksam.getId());

        List<Section> sections = sectionDao.findAllByLine(line);

        assertThat(sections).hasSize(1)
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .containsOnly(
                        tuple(gangnam, seolleung, 2)
                );
    }

    @DisplayName("없는 노선으로 구간을 제거하려고 하면 예외가 발생한다.")
    @Test
    void deleteSectionWithInvalidLine() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "green", gangnam.getId(), yeoksam.getId(),
                1);
        LineResponse lineResponse = lineService.save(lineCreateRequest);

        assertThatThrownBy(() -> lineService.deleteSection(lineResponse.getId() + 1, yeoksam.getId()))
                .isInstanceOf(LineNotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @DisplayName("노선에 존재하지 않는 역으로 구간을 제거하려고 하면 예외가 발생한다.")
    @Test
    void deleteSectionWithInvalidStation() {
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "green", gangnam.getId(), yeoksam.getId(),
                1);
        LineResponse lineResponse = lineService.save(lineCreateRequest);

        assertThatThrownBy(() -> lineService.deleteSection(lineResponse.getId(), seolleung.getId()))
                .isInstanceOf(SectionNotFoundException.class)
                .hasMessage("요청하는 역을 포함하는 구간이 없습니다.");
    }

}
