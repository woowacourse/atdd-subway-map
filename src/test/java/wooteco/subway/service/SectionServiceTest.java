package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;

@JdbcTest
public class SectionServiceTest {

    private SectionService sectionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        SectionDao sectionDao = new SectionDao(jdbcTemplate);
        sectionService = new SectionService(sectionDao);

        sectionDao.save(new Section(1L, 1L, 2L, 10));
    }

    @DisplayName("지하철 구간을 추가한다. - 끝에 삽입")
    @Test
    void saveEndLocation() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThatCode(() -> sectionService.save(section))
            .doesNotThrowAnyException();
    }

    @DisplayName("지하철 구간을 추가한다. - 중간에 삽입")
    @Test
    void saveMiddleLocation() {
        Section section = new Section(1L, 1L, 3L, 4);

        assertThatCode(() -> sectionService.save(section))
            .doesNotThrowAnyException();
    }

    @DisplayName("지하철 구간을 추가한다. - 같은 길이의 구간이면 에러")
    @Test
    void saveSameDistance() {
        Section section = new Section(1L, 3L, 2L, 10);

        assertThatThrownBy(() -> sectionService.save(section))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("추가하려는 구간이 기존 역 사이 길이보다 크거나 같습니다.");
    }

    @DisplayName("지하철 구간을 추가한다. - 모두 등록되어 있는 역일 경우 에러")
    @Test
    void saveAlreadyExistStation() {
        Section section = new Section(1L, 1L, 2L, 10);

        assertThatThrownBy(() -> sectionService.save(section))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @DisplayName("지하철 구간을 추가한다. - 모두 등록되지 않아있는 역일 경우 에러")
    @Test
    void saveNotExistStations() {
        Section section = new Section(1L, 3L, 4L, 10);

        assertThatThrownBy(() -> sectionService.save(section))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
    }

    @DisplayName("지하철 구간을 삭제한다. - 종단점 삭제")
    @Test
    void deleteEndLocation() {
        saveEndLocation();

        assertThatCode(() -> sectionService.delete(1L, 1L))
            .doesNotThrowAnyException();
    }

    @DisplayName("지하철 구간을 삭제한다. - 구간이 하나 남았을 때 삭제할 경우 에러")
    @Test
    void deleteOneSectionException() {
        assertThatCode(() -> sectionService.delete(1L, 2L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("구간이 하나 남아서 삭제 할 수 없음");
    }

    @DisplayName("지하철 구간을 삭제한다. - 없는 역을 삭제할 경우 에러")
    @Test
    void deleteNotExistLocationException() {
        saveEndLocation();

        assertThatThrownBy(() -> sectionService.delete(1L, 4L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("역이 존재하지 않습니다.");
    }
}
