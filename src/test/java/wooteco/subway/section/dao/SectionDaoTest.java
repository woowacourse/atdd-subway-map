package wooteco.subway.section.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.section.api.dto.SectionDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionDaoTest {

    private SectionDao sectionDao;

    @BeforeEach
    void setUp(@Autowired JdbcTemplate jdbcTemplate) {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("특정 노선에 포함되는 section들을 조회하는 기능")
    @Test
    void findSectionsByLineId() {
        //given
        LineRequest request = new LineRequest("2호선", "green", 1L, 2L, 10);
        LineRequest request2 = new LineRequest("2호선", "green", 2L, 3L, 10);
        long lineId = 1L;
        sectionDao.save(lineId, request);
        sectionDao.save(lineId, request2);
        //when
        List<SectionDto> sections = sectionDao.findSectionsByLineId(lineId);
        //then
        assertThat(sections).hasSize(2);
    }
}
