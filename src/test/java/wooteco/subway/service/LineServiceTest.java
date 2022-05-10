package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequestDto;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.mockDao.MockLineDao;

class LineServiceTest {

    private final LineService service = new LineService(new MockLineDao());

    @BeforeEach
    void initStore() {
        MockLineDao.removeAll();
    }

    @DisplayName("노선 이름과 색깔을 입력받아서 해당 이름과 색깔을 가진 노선을 등록한다.")
    @Test
    void register() {
        LineRequestDto lineRequestDto = new LineRequestDto("2호선", "bg-green-600", null, null, 0);
        Line created = service.register(lineRequestDto);

        assertAll(
                () -> assertThat(created.getName()).isEqualTo("2호선"),
                () -> assertThat(created.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("이미 존재하는 노선이름으로 등록하려할 시 예외가 발생한다.")
    @Test
    void registerDuplicateName() {
        LineRequestDto lineRequestDto = new LineRequestDto("2호선", "bg-green-600", null, null, 0);
        service.register(lineRequestDto);

        assertThatThrownBy(() -> service.register(lineRequestDto))
                .isInstanceOf(DuplicateLineNameException.class)
                .hasMessage("[ERROR] 이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("등록된 모든 노선 리스트를 조회한다.")
    @Test
    void searchAll() {
        LineRequestDto lineRequestDto1 = new LineRequestDto("2호선", "bg-green-600", null, null, 0);
        LineRequestDto lineRequestDto2 = new LineRequestDto("신분당선", "bg-red-600", null, null, 0);
        LineRequestDto lineRequestDto3 = new LineRequestDto("분당선", "bg-yellow-600", null, null, 0);
        service.register(lineRequestDto1);
        service.register(lineRequestDto2);
        service.register(lineRequestDto3);

        List<Line> lines = service.searchAll();
        List<String> names = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        List<String> colors = lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(names).isEqualTo(List.of("2호선", "신분당선", "분당선")),
                () -> assertThat(colors).isEqualTo(List.of("bg-green-600", "bg-red-600", "bg-yellow-600"))
        );
    }

    @DisplayName("id 로 노선을 조회한다.")
    @Test
    void searchById() {
        LineRequestDto lineRequestDto = new LineRequestDto("2호선", "bg-green-600", null, null, 0);
        Line savedLine = service.register(lineRequestDto);

        Line searchedLine = service.searchById(savedLine.getId());

        assertAll(
                () -> assertThat(searchedLine.getName()).isEqualTo(savedLine.getName()),
                () -> assertThat(searchedLine.getColor()).isEqualTo(savedLine.getColor())
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void modify() {
        LineRequestDto lineRequestDto = new LineRequestDto("2호선", "bg-green-600", null, null, 0);
        Line savedLine = service.register(lineRequestDto);

        LineRequestDto newLineRequestDto = new LineRequestDto("신분당선", "bg-red-600", null, null, 0);
        service.modify(savedLine.getId(), newLineRequestDto);
        Line searchedLine = service.searchById(savedLine.getId());

        assertAll(
                () -> assertThat(searchedLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(searchedLine.getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("id 로 노선을 삭제한다.")
    @Test
    void removeById() {
        LineRequestDto lineRequestDto1 = new LineRequestDto("2호선", "bg-green-600", null, null, 0);
        LineRequestDto lineRequestDto2 = new LineRequestDto("신분당선", "bg-red-600", null, null, 0);
        service.register(lineRequestDto1);
        Line line = service.register(lineRequestDto2);

        service.remove(line.getId());

        assertThat(service.searchAll().size()).isEqualTo(1);
    }
}
