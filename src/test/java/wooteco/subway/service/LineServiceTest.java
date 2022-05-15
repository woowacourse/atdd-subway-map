package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;

@SpringBootTest
@Transactional
class LineServiceTest {

    private LineRequest LINE_FIXTURE;
    private LineRequest LINE_FIXTURE2;
    private LineRequest LINE_FIXTURE3;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void setup() {
        LINE_FIXTURE = makeLineRequest("a", "b", "2호선", "bg-color-700");;
        LINE_FIXTURE2 = makeLineRequest("c", "e", "3호선", "bg-color-800");
        LINE_FIXTURE3 = makeLineRequest("f", "g", "4호선", "bg-color-900");
    }


    @Nested
    @DisplayName("새로운 노선을 저장할 때")
    class SaveTest {

        @Test
        @DisplayName("노선 이름이 중복되지 않으면 저장할 수 있다.")
        void save_Success_If_Not_Exists() {
            assertThatCode(() -> lineService.saveLine(LINE_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("노선 이름이 중복되면 예외가 발생한다.")
        void save_Fail_If_Exists() {
            lineService.saveLine(LINE_FIXTURE);
            assertThatThrownBy(() -> lineService.saveLine(LINE_FIXTURE))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("이미 존재하는 노선입니다. " + LINE_FIXTURE);
        }
    }

    @Test
    @DisplayName("전체 지하철 노선을 조회할 수 있다")
    void findAll() {
        lineService.saveLine(LINE_FIXTURE);
        lineService.saveLine(LINE_FIXTURE2);
        lineService.saveLine(LINE_FIXTURE3);

        assertThat(lineService.findAll()).extracting("name")
                .isEqualTo(List.of(LINE_FIXTURE.getName(), LINE_FIXTURE2.getName(), LINE_FIXTURE3.getName()));
    }

    private LineRequest makeLineRequest(final String stationName1, final String stationName2,
                                               final String lineName, final String color) {
        final Long upStationId2 = stationDao.save(new Station(stationName1)).getId();
        final Long downStationId2 = stationDao.save(new Station(stationName2)).getId();
        return new LineRequest(lineName, color, upStationId2, downStationId2, 3);
    }

    @Test
    @DisplayName("아이디로 지하철 노선을 조회할 수 있다")
    void findById() {
        final LineResponse line = lineService.saveLine(LINE_FIXTURE);
        final LineResponse found = lineService.findById(line.getId());
        assertThat(line.getId()).isEqualTo(found.getId());
    }

    @Nested
    @DisplayName("아이디로 지하철노선을 삭제할 때")
    class DeleteLineTest {

        @Test
        @DisplayName("아이디가 존재하면 아이디로 지하철노선을 삭제할 수 있다.")
        void deleteById() {
            final LineResponse line = lineService.saveLine(LINE_FIXTURE);
            final List<Line> lines = lineService.findAll();
            lineService.deleteById(line.getId());
            final List<Line> afterDelete = lineService.findAll();

            assertThat(lines).isNotEmpty();
            assertThat(afterDelete).isEmpty();
        }

        @Test
        @DisplayName("아이디가 존재하지 않는다면 예외를 던진다.")
        void delete_By_Id_Fail() {
            assertThatThrownBy(() -> lineService.deleteById(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("요청한 노선이 존재하지 않습니다. id=1");
        }
    }

    @Nested
    @DisplayName("노선 이름과 색상을 변경하려할 때")
    class UpdateLineTest {

        @Test
        @DisplayName("노선이 존재하면 노선 이름과 색상을 변경할 수 있다")
        void update_Line_Success() {
            final LineResponse line = lineService.saveLine(LINE_FIXTURE);
            final Long id = line.getId();
            final LineRequest lineRequest = new LineRequest("22호선", "bg-color-777", 1L, 2L, 3);

            lineService.updateLine(id, lineRequest);
            final LineResponse updated = lineService.findById(id);

            assertAll(
                    () -> assertThat(updated.getId()).isEqualTo(id),
                    () -> assertThat(updated.getName()).isEqualTo("22호선"),
                    () -> assertThat(updated.getColor()).isEqualTo("bg-color-777")
            );
        }

        @Test
        @DisplayName("노선이 존재하지 않으면 예외를 던진다.")
        void update_Line_Fail() {
            assertThatThrownBy(() -> lineService.updateLine(1L, new LineRequest("a", "b", 10L, 11L, 12)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("요청한 노선이 존재하지 않습니다. id=1 Line{name='a', color='b'}");

        }
    }

}
