package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class SectionDaoTest {
    SectionDao sectionDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("섹션 추가")
    void insert() {
        //given
        Long lineId = 1L;
        Long upStationId = 2L;
        Long downStationId = 3L;
        int distance = 7;

        //when
        Section 강남_잠실 = sectionDao.insert(new Section(lineId, upStationId, downStationId, distance));

        //then
        assertThat(강남_잠실.getLineId()).isEqualTo(lineId);
        assertThat(강남_잠실.getUpStationId()).isEqualTo(upStationId);
        assertThat(강남_잠실.getDownStationId()).isEqualTo(downStationId);
    }
}
