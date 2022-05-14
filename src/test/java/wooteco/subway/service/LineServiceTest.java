package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataDuplicationException;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.service.dto.LineDto;

class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        StationService stationService = new StationService(new FakeStationDao());
        stationService.createStation(new Station("선릉"));
        stationService.createStation(new Station("강남"));
        lineService = new LineService(new FakeLineDao(),
            new SectionService(new FakeSectionDao(), stationService), stationService);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void create() {
        //given
        LineDto lineDto = new LineDto("7호선", "khaki", 1L, 2L, 5);

        //when
        Line actual = lineService.createLine(lineDto);

        //then
        assertThat(isSameNameAndColor(actual, lineDto.toLine())).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"3:2", "1:3"}, delimiter = ':')
    @DisplayName("등록하려는 노선의 역이 목록에 존재하지 않으면 예외를 던진다(stationId 3은 존재하지 않음).")
    void createWithStationNotExists(long upStationId, long downStationId) {
        //when, then
        assertThatThrownBy(() -> lineService.createLine(
            new LineDto("7호선", "khaki", upStationId, downStationId, 5)))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessageContaining("존재하지 않는 역입니다.");
    }

    private boolean isSameNameAndColor(Line lineA, Line lineB) {
        return lineA.getName().equals(lineB.getName()) && lineA.getColor().equals(lineB.getColor());
    }

    @Test
    @DisplayName("중복된 노선을 저장할 수 없다.")
    void createDuplicateName() {
        //given
        LineDto lineDto = new LineDto("7호선", "khaki", 1L, 2L, 5);
        lineService.createLine(lineDto);

        //then
        assertThatThrownBy(() -> lineService.createLine(lineDto))
            .isInstanceOf(DataDuplicationException.class)
            .hasMessageContaining("이미 등록된 노선입니다.");
    }

    @Test
    @DisplayName("전체 Line 목록을 조회한다.")
    void findAll() {
        //given
        Line line1 = lineService.createLine(new LineDto("2호선", "green", 1L, 2L, 5));
        Line line2 = lineService.createLine(new LineDto("7호선", "khaki", 1L, 2L, 5));

        //when
        List<Line> actual = lineService.findAll();

        //then
        assertAll(
            () -> assertThat(isSameNameAndColor(actual.get(0), line1)).isTrue(),
            () -> assertThat(isSameNameAndColor(actual.get(1), line2)).isTrue()
        );
    }

    @Test
    @DisplayName("id로 노선을 조회한다.")
    void findById() {
        //given
        Line expected = lineService.createLine(new LineDto("7호선", "khaki", 1L, 2L, 5));

        //when
        Line actual = lineService.findById(expected.getId());

        //then
        assertThat(isSameNameAndColor(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 id 를 조회할 경우 예외를 던진다.")
    void findByIdNotExists() {
        //given
        Line line = new Line("4호선", "sky-blue");
        Long id = lineService.createLine(new LineDto(line.getName(), line.getColor(), 1L, 2L, 5)).getId();

        //then
        assertThatThrownBy(() -> lineService.findById(id + 1))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        //given
        Line createdLine = lineService.createLine(new LineDto("4호선", "sky-blue", 1L, 2L, 5));

        //when
        Line expected = new Line(createdLine.getId(), "4호선", "khaki");
        lineService.update(expected);
        Line actual = lineService.findById(createdLine.getId());

        //then
        assertThat(isSameNameAndColor(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("중복된 이름으로 수정하면 예외를 던진다.")
    void updateWithDuplicatedName() {
        //given
        lineService.createLine(new LineDto("2호선", "sky-blue", 1L, 2L, 5));
        Line line = lineService.createLine(new LineDto("4호선", "sky-blue", 1L, 2L, 5));
        Line duplicatedLine = new Line(line.getId(), "2호선", line.getColor());

        //then
        assertThatThrownBy(() -> lineService.update(duplicatedLine))
            .isInstanceOf(DataDuplicationException.class)
            .hasMessage("이미 등록된 노선입니다.");
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        //given
        Long id = lineService.createLine(new LineDto("4호선", "sky-blue", 1L, 2L, 5)).getId();

        //when
        lineService.deleteById(id);

        //then
        assertThatThrownBy(() -> lineService.findById(id))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 id 로 삭제할 경우 예외를 던진다.")
    void deleteByIdWithIdNotExists() {
        //then
        assertThatThrownBy(() -> lineService.deleteById(1L))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessageContaining("존재하지 않는 노선입니다.");
    }
}
