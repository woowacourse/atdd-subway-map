package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.LineDaoImpl;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.SectionDaoImpl;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.ui.dto.SectionDeleteRequest;
import wooteco.subway.ui.dto.SectionRequest;

@JdbcTest
public class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionService sectionService;
    private SectionDao sectionDao;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDaoImpl(jdbcTemplate);
        sectionService = new SectionService(sectionDao);
        lineDao = new LineDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("상행점 구간을 저장한다.")
    void saveFirstStation() {
        // given
        Long lineId = lineDao.save(new Line("name", "color"));
        sectionDao.save(new Section(lineId, 2L, 3L, 3));
        sectionDao.save(new Section(lineId, 3L, 4L, 4));
        sectionDao.save(new Section(lineId, 4L, 5L, 5));

        // when
        sectionService.save(new SectionRequest(lineId, 1L, 2L, 2));
        List<Section> sections = sectionDao.findByLineId(lineId);

        // then
        assertThat(sections).hasSize(4);
    }

    @Test
    @DisplayName("하행점 구간을 저장한다.")
    void saveLastStation() {
        // given
        Long lineId = lineDao.save(new Line("name", "color"));
        sectionDao.save(new Section(lineId, 2L, 3L, 3));
        sectionDao.save(new Section(lineId, 3L, 4L, 4));
        sectionDao.save(new Section(lineId, 4L, 5L, 5));

        // when
        sectionService.save(new SectionRequest(lineId, 5L, 6L, 3));
        List<Section> sections = sectionDao.findByLineId(lineId);

        // then
        assertThat(sections).hasSize(4);
    }

    @Test
    @DisplayName("중간 지점 구간을 저장한다.")
    void saveMiddleStation() {
        // given
        Long lineId = lineDao.save(new Line("name", "color"));
        sectionDao.save(new Section(lineId, 2L, 3L, 3));
        sectionDao.save(new Section(lineId, 3L, 4L, 4));
        sectionDao.save(new Section(lineId, 4L, 5L, 5));

        // when
        sectionService.save(new SectionRequest(lineId, 3L, 6L, 2));
        List<Section> sections = sectionDao.findByLineId(lineId);

        // then
        assertThat(sections).hasSize(4);
    }

    @Test
    @DisplayName("생성할 중간 지점 구간의 길이가 기존 구간의 길이보다 길거나 같은 경우 예외가 발생한다.")
    void validateMiddleStationDistance() {
        // given
        Long lineId = lineDao.save(new Line("name", "color"));
        sectionDao.save(new Section(lineId, 2L, 3L, 3));
        sectionDao.save(new Section(lineId, 3L, 4L, 4));
        sectionDao.save(new Section(lineId, 4L, 5L, 5));

        assertThatThrownBy(() ->
            sectionService.save(new SectionRequest(lineId, 3L, 6L, 4)))
            .hasMessage("등록할 구간의 길이가 기존 역 사이의 길이보다 길거나 같으면 안됩니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("중간 지점 구간 제거")
    void deleteMiddleSection() {
        // given
        Long lineId = lineDao.save(new Line("name", "color"));
        sectionDao.save(new Section(lineId, 2L, 3L, 3));
        sectionDao.save(new Section(lineId, 3L, 4L, 4));
        sectionDao.save(new Section(lineId, 4L, 5L, 5));

        // when
        boolean result = sectionService.removeSection(new SectionDeleteRequest(lineId, 3L));

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("중간 지점 구간 제거 시, 종점일 경우 예외가 발생한다.")
    void validateDeleteEndStationSection() {
        // given
        Long lineId = lineDao.save(new Line("name", "color"));
        sectionDao.save(new Section(lineId, 2L, 3L, 3));
        sectionDao.save(new Section(lineId, 3L, 4L, 4));

        // when
        assertThatThrownBy(() ->
            sectionService.removeSection(new SectionDeleteRequest(lineId, 2L)))
            .hasMessage("종점은 제거할 수 없습니다.")
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
            sectionService.removeSection(new SectionDeleteRequest(lineId, 4L)))
            .hasMessage("종점은 제거할 수 없습니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }
}
