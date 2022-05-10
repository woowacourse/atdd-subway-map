package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 DAO 테스트")
@JdbcTest
class SectionDaoTest {

    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void save() {
        sectionDao.save(1L, new Section(1L, 2L, 10));

        Integer count = jdbcTemplate.queryForObject("select count(*) from SECTION", Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("특정 지하철 노선에 구간의 상행역과 하행역이 모두 일치하면 true 를 반환한다.")
    @Test
    void equalAllStation() {
        // given
        sectionDao.save(1L, new Section(1L, 2L, 10));

        // when & then
        assertThat(sectionDao.equalAllStation(1L, new Section(1L, 2L, 10))).isTrue();
    }

    @DisplayName("특정 지하철 노선에 특정 상행역 아이디가 존재하면 true 를 반환한다.")
    @Test
    void existUpStation() {
        // given
        sectionDao.save(1L, new Section(1L, 2L, 10));

        // when & then
        assertThat(sectionDao.existUpStation(1L, 1L)).isTrue();
    }

    @DisplayName("특정 지하철 노선에 특정 하행역 아이디가 존재하면 true 를 반환한다.")
    @Test
    void existDownStation() {
        // given
        sectionDao.save(1L, new Section(1L, 2L, 10));

        // when & then
        assertThat(sectionDao.existDownStation(1L, 2L)).isTrue();
    }

    @DisplayName("특정 지하철 노선에 포함되어 있는 구간을 모두 삭제한다.")
    @Test
    void delete() {
        // given
        sectionDao.save(1L, new Section(1L, 2L, 10));

        // when
        sectionDao.delete(1L);

        // then
        Integer count = jdbcTemplate.queryForObject("select count(*) from SECTION where line_id = ?", Integer.class, 1L);
        assertThat(count).isEqualTo(0);
    }
}
