package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.LineEntity;
import wooteco.subway.exception.DataDuplicationException;
import wooteco.subway.exception.DataNotExistException;
import wooteco.subway.service.dto.SectionDto;

class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new FakeLineDao(),
            new SectionService(new FakeSectionDao(), new StationService(new FakeStationDao())));
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void create() {
        //given
        LineEntity line = new LineEntity("7호선", "khaki");

        //when
        LineEntity createdLine = lineService.createLine(line,
            new SectionDto(line.getId(), 1L, 2L, 5));

        //then
        assertThat(isSameName(createdLine, line)).isTrue();
    }

    private boolean isSameName(LineEntity lineA, LineEntity lineB) {
        return lineA.getColor().equals(lineB.getColor()) && lineA.getName().equals(lineB.getName());
    }

    @Test
    @DisplayName("중복된 노선을 저장할 수 없다.")
    void createDuplicateName() {
        //given
        LineEntity line = new LineEntity("7호선", "khaki");
        lineService.createLine(line, new SectionDto(line.getId(), 1L, 2L, 5));

        //then
        assertThatThrownBy(() -> lineService.createLine(line, new SectionDto(line.getId(), 1L, 2L, 5)))
            .isInstanceOf(DataDuplicationException.class)
            .hasMessageContaining("이미 등록된 노선입니다.");
    }

    @Test
    @DisplayName("전체 Line 목록을 조회한다.")
    void findAll() {
        //given
        LineEntity line1 = new LineEntity("2호선", "green");
        LineEntity line2 = new LineEntity("7호선", "khaki");
        lineService.createLine(line1, new SectionDto(line1.getId(), 1L, 2L, 5));
        lineService.createLine(line2, new SectionDto(line2.getId(), 1L, 2L, 5));

        //when
        List<LineEntity> actual = lineService.findAll();

        //then
        assertAll(
            () -> assertThat(isSameName(actual.get(0), line1)).isTrue(),
            () -> assertThat(isSameName(actual.get(1), line2)).isTrue()
        );
    }

    @Test
    @DisplayName("id로 노선을 조회한다.")
    void findById() {
        //given
        LineEntity line = new LineEntity("7호선", "khaki");
        LineEntity createdLine = lineService.createLine(line, new SectionDto(line.getId(), 1L, 2L, 5));

        //when
        LineEntity actual = lineService.findById(createdLine.getId());

        //then
        assertThat(isSameName(actual, line)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 id 를 조회할 경우 예외를 던진다.")
    void findByIdNotExists() {
        //given
        LineEntity line = new LineEntity("4호선", "sky-blue");
        Long id = lineService.createLine(line, new SectionDto(line.getId(), 1L, 2L, 5)).getId();

        //then
        assertThatThrownBy(() -> lineService.findById(id + 1))
            .isInstanceOf(DataNotExistException.class)
            .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        //given
        LineEntity line = new LineEntity("4호선", "sky-blue");
        LineEntity createdLine = lineService.createLine(line, new SectionDto(line.getId(), 1L, 2L, 5));

        //when
        LineEntity expected = new LineEntity(createdLine.getId(), "4호선", "khaki");
        lineService.update(expected);
        LineEntity actual = lineService.findById(createdLine.getId());

        //then
        assertThat(isSameName(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("중복된 이름으로 수정하면 예외를 던진다.")
    void updateWithDuplicatedName() {
        //given
        LineEntity line2 = new LineEntity("2호선", "sky-blue");
        lineService.createLine(line2, new SectionDto(line2.getId(), 1L, 2L, 5));
        LineEntity line4 = new LineEntity("4호선", "sky-blue");
        LineEntity createdLine = lineService.createLine(line4, new SectionDto(line4.getId(), 1L, 2L, 5));
        LineEntity duplicatedLine = new LineEntity(createdLine.getId(), "2호선", "sky-blue");

        //then
        assertThatThrownBy(() -> lineService.update(duplicatedLine))
            .isInstanceOf(DataDuplicationException.class)
            .hasMessage("이미 등록된 노선입니다.");
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        //given
        LineEntity line = new LineEntity("4호선", "sky-blue");
        Long id = lineService.createLine(line, new SectionDto(line.getId(), 1L, 2L, 5)).getId();

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
            .isInstanceOf(DataNotExistException.class)
            .hasMessageContaining("존재하지 않는 노선입니다.");
    }
}
