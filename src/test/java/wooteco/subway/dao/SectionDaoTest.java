package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@JdbcTest
class SectionDaoTest {

    private Long savedId1;
    private Long lineId;

    private SectionDao sectionDao;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        LineDao lineDao = new LineDao(jdbcTemplate);
        lineId = lineDao.save(new Line("2호선", "green"));
        savedId1 = sectionDao.save(new Section(lineId, 1L, 2L, 10));
        sectionDao.save(new Section(lineId, 2L, 3L, 5));
    }

    @DisplayName("section 을 저장한다.")
    @Test
    void save() {
        //given
        Section section = new Section(lineId, 1L, 2L, 10);
        //when
        Long sectionId = sectionDao.save(section);
        //then
        assertThat(sectionId).isNotNull();
    }

    @DisplayName("Line_id 를 이용하여 section 을 조회한다.")
    @Test
    void findByLineId() {
        //given

        //when
        List<Section> sections = sectionDao.findByLineId(lineId);
        //then
        long expectedIdCount = sections.stream()
                .filter(section -> section.getLineId().equals(lineId))
                .count();
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(expectedIdCount).isEqualTo(2)
        );
    }

    @DisplayName("id 를 이용하여 distance 를 수정한다.")
    @Test
    void updateDistanceById() {
        //given
        int updateDistance = 1;
        //when
        sectionDao.updateDistanceById(savedId1, updateDistance);
        //then
        List<Section> sections = sectionDao.findByLineId(lineId);
        sections.stream()
                .filter(section -> section.getId().equals(savedId1))
                .findFirst()
                .ifPresent(section -> assertThat(section.getDistance()).isEqualTo(updateDistance));

    }

    @DisplayName("id 를 이용하여 section 을 삭제한다.")
    @Test
    void deleteById() {
        //given

        //when
        sectionDao.deleteById(savedId1);
        //then
        List<Section> sections = sectionDao.findByLineId(lineId);
        assertThat(sections.size()).isEqualTo(1);
    }

}