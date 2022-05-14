package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.SectionEntity;

@JdbcTest
class JdbcSectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;
    private Long savedLineId;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);
        lineDao = new JdbcLineDao(jdbcTemplate);
        savedLineId = lineDao.save(new Line("2호선", "green")).getId();
    }

    @Test
    @DisplayName("구간을 저장한다.")
    void save() {
        //when
        Long sectionId = sectionDao.save(new SectionEntity(savedLineId, 1L, 2L, 5));

        //then
        List<SectionEntity> actual = sectionDao.findByLineId(savedLineId);
        List<SectionEntity> expected = List.of(new SectionEntity(sectionId, savedLineId, 1L, 2L, 5));

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("노선 id 로 구간을 조회한다.")
    void findByLineId() {
        //given
        Long sectionIdA = sectionDao.save(new SectionEntity(savedLineId, 1L, 2L, 5));
        Long sectionIdB = sectionDao.save(new SectionEntity(savedLineId, 2L, 3L, 5));

        //when
        List<SectionEntity> actual = sectionDao.findByLineId(savedLineId);
        List<SectionEntity> expected = List.of(new SectionEntity(sectionIdA, savedLineId, 1L, 2L, 5),
            new SectionEntity(sectionIdB, savedLineId, 2L, 3L, 5));

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("구간 정보를 수정한다.")
    void update() {
        //given
        Long sectionId = sectionDao.save(new SectionEntity(savedLineId, 1L, 2L, 5));

        //when
        sectionDao.update(new SectionEntity(sectionId, savedLineId, 3L, 2L, 4));

        //then
        List<SectionEntity> actual = sectionDao.findByLineId(savedLineId);
        SectionEntity expected = new SectionEntity(sectionId, savedLineId, 3L, 2L, 4);
        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("id 에 해당하는 구간을 삭제한다.")
    void deleteById() {
        //given
        Long sectionId = sectionDao.save(new SectionEntity(savedLineId, 1L, 2L, 5));

        //when
        sectionDao.deleteById(sectionId);

        //then
        List<SectionEntity> actual = sectionDao.findByLineId(savedLineId);
        SectionEntity expected = new SectionEntity(sectionId, savedLineId, 1L, 2L, 5);
        assertThat(actual).doesNotContain(expected);
    }

    @Test
    @DisplayName("특정 노선을 삭제할 경우 그에 속한 구간이 모두 삭제되는지 확인한다.")
    void onDeleteByLineId() {
        //given
        sectionDao.save(new SectionEntity(savedLineId, 1L, 2L, 5));

        //when
        lineDao.deleteById(savedLineId);

        //then
        assertThat(sectionDao.findByLineId(savedLineId)).isEmpty();
    }
}
