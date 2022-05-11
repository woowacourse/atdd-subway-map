package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@JdbcTest
class JdbcSectionDaoTest {

    private JdbcSectionDao jdbcSectionDao;
    private JdbcLineDao lineDao;
    private Long lineId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcSectionDao = new JdbcSectionDao(jdbcTemplate);
        lineDao = new JdbcLineDao(jdbcTemplate);
        lineId = lineDao.save("hi", "color");
        Section section = new Section(lineId, 2L, 3L, 5);
        jdbcSectionDao.save(section);
    }

    @DisplayName("구간 정보를 등록한다.")
    @Test
    void save() {
        Section section = new Section(lineId, 3L, 4L, 5);
        Long id = jdbcSectionDao.save(section);
        assertThat(id).isNotNull();
    }

    @DisplayName("구간 정보를 제거한다.")
    @Test
    void deleteByLineIdAndStationId() {
        boolean isDeleted = jdbcSectionDao.deleteByLineIdAndStationId(lineId, 2L);
        assertThat(isDeleted).isTrue();
    }

    @DisplayName("db에 stationId가 존재하지 않는 경우 테스트")
    @Test
    void deleteByLineIdAndStationIdIfStationIdIsNotExist() {
        boolean isDeleted = jdbcSectionDao.deleteByLineIdAndStationId(1L, 4L);
        assertThat(isDeleted).isFalse();
    }

    @DisplayName("지하철 호선에 따른 구간을 조회한다.")
    @Test
    void findByLineId() {
        Section section1 = new Section(lineId, 3L, 4L, 5);
        jdbcSectionDao.save(section1);
        Section section2 = new Section(lineId, 4L, 5L, 5);
        jdbcSectionDao.save(section2);

        List<Section> sectionList = jdbcSectionDao.findByLineId(lineId).getSections();
        assertThat(sectionList.size()).isEqualTo(3);
    }

    @DisplayName("지하철 호선과 상행역을 기준으로 구간을 조회한다.")
    @Test
    void findByLineIdAndUpStationId() {
        Section section = new Section(lineId, 3L, 4L, 5);
        jdbcSectionDao.save(section);

        Optional<Section> findSection = jdbcSectionDao.findByLineIdAndUpStationId(lineId, 3L);
        assertThat(findSection.get().getDownStationId()).isEqualTo(4L);
    }

    @DisplayName("지하철 호선과 하행약을 기준으로 구간을 조회한다.")
    @Test
    void findByLineIdAndDownStationId() {
        Optional<Section> findSection = jdbcSectionDao.findByLineIdAndDownStationId(lineId, 3L);
        assertThat(findSection.get().getUpStationId()).isEqualTo(2L);
    }

    @DisplayName("지하철 호선과 상행역을 기준으로 구간을 삭제한다.")
    @Test
    void deleteByLineIdAndUpStationId() {
        boolean isDeleted = jdbcSectionDao.deleteByLineIdAndUpStationId(lineId, 2L);
        assertThat(isDeleted).isTrue();
    }

    @DisplayName("지하철 호선과 상행역을 기준으로 구간의 하행역을 바꾼다")
    @Test
    void updateByLineIdAndStationId() {
        boolean isUpdated = jdbcSectionDao.updateDownStationIdByLineIdAndUpStationId(lineId, 2L, 3L);
        assertThat(isUpdated).isTrue();
    }

    @DisplayName("지하철 호선과 지하철역에 따라 구간을 조회한다.")
    @Test
    void findByLineIdAndStationId() {
        Section section = new Section(lineId, 3L, 4L, 5);
        jdbcSectionDao.save(section);

        Sections sections = jdbcSectionDao.findByLineIdAndStationId(lineId, 3L);
        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("상행과 하행이 db에 있는지 확인한다.")
    @Test
    void isExistByUpStationIdAndDownStationIdForTrue() {
        Section section = new Section(lineId, 3L, 4L, 5);
        jdbcSectionDao.save(section);

        boolean isExist = jdbcSectionDao.isExistByUpStationIdAndDownStationId(3L, 4L);
        assertThat(isExist).isTrue();
    }

    @DisplayName("상행과 하행이 db에 있는지 확인할 때 상행만 있으면 존재하지 않는다.")
    @Test
    void isExistByUpStationIdAndDownStationIdForFalse() {
        Section section = new Section(lineId, 3L, 4L, 5);
        jdbcSectionDao.save(section);

        boolean isExist = jdbcSectionDao.isExistByUpStationIdAndDownStationId(3L, 5L);
        assertThat(isExist).isFalse();
    }

    @DisplayName("상행이 db에 있는지 확인한다.")
    @Test
    void isExistByLineIdAndUpStationId() {
        Section section = new Section(lineId, 3L, 4L, 5);
        jdbcSectionDao.save(section);

        boolean isExist = jdbcSectionDao.isExistByLineIdAndUpStationId(lineId, 3L);
        assertThat(isExist).isTrue();
    }

    @DisplayName("하행이 db에 있는지 확인한다.")
    @Test
    void isExistByLineIdAndDownStationId() {
        Section section = new Section(lineId, 3L, 4L, 5);
        jdbcSectionDao.save(section);

        boolean isExist = jdbcSectionDao.isExistByLineIdAndDownStationId(lineId, 4L);
        assertThat(isExist).isTrue();
    }

    @DisplayName("해당 라인과 지하철역 2개에 관련된 구간들을 조회한다.")
    @Test
    void  findByLineIdAndStationIds() {
        Section section1 = new Section(lineId, 3L, 4L, 5);
        jdbcSectionDao.save(section1);
        Section section2 = new Section(lineId, 4L, 5L, 5);
        jdbcSectionDao.save(section2);

        Sections sections = jdbcSectionDao.findByLineIdAndStationIds(lineId, 4L, 6L);
        assertThat(sections.getSections().size()).isEqualTo(2);
    }
}