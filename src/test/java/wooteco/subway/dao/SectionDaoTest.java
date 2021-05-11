package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.section.InvalidSectionOnLineException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("classpath:initializeTable.sql")
public class SectionDaoTest {

    private Long lineId;
    private Long stationSinSeolId;
    private Long stationDongMyoId;
    private Long stationDongDaeMoonId;
    private int distance;

    @Autowired
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        lineId = 1L;
        stationSinSeolId = 1L;
        stationDongMyoId = 2L;
        stationDongDaeMoonId = 3L;
        distance = 10;

        Section sinSeolAndDongMyo = new Section(lineId, stationSinSeolId, stationDongMyoId,
            distance);
        Section dongMyoAndDongDaeMoon = new Section(lineId, stationDongMyoId, stationDongDaeMoonId,
            distance);

        sectionDao.save(sinSeolAndDongMyo);
        sectionDao.save(dongMyoAndDongDaeMoon);
    }

    @Test
    @DisplayName("Section 저장 테스트")
    public void save() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        Section targetSection = new Section(lineId, upStationId, downStationId, distance);

        // when
        Section savedSection = sectionDao.save(targetSection);

        // then
        assertThat(savedSection.getLineId()).isEqualTo(lineId);
        assertThat(savedSection.getUpStationId()).isEqualTo(upStationId);
        assertThat(savedSection.getDownStationId()).isEqualTo(downStationId);
        assertThat(savedSection.getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("노선에 있는 Section들 반환 테스트")
    public void findAllByLineId() {
        // given

        // when
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        Section savedSinSeolAndDongMyo = sections.get(0);
        Section savedDongMyoAndDongDaeMoon = sections.get(1);

        // then
        assertThat(savedSinSeolAndDongMyo.getLineId()).isEqualTo(lineId);
        assertThat(savedSinSeolAndDongMyo.getUpStationId()).isEqualTo(stationSinSeolId);
        assertThat(savedSinSeolAndDongMyo.getDownStationId()).isEqualTo(stationDongMyoId);
        assertThat(savedSinSeolAndDongMyo.getDistance()).isEqualTo(distance);
        assertThat(savedDongMyoAndDongDaeMoon.getLineId()).isEqualTo(lineId);
        assertThat(savedDongMyoAndDongDaeMoon.getUpStationId()).isEqualTo(stationDongMyoId);
        assertThat(savedDongMyoAndDongDaeMoon.getDownStationId()).isEqualTo(stationDongDaeMoonId);
        assertThat(savedDongMyoAndDongDaeMoon.getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("노선에 있는 상행역 조회 테스트")
    public void findByLineIdAndUpStationId() {
        // given

        // when
        Section section = sectionDao.findByLineIdAndUpStationId(lineId, stationSinSeolId)
            .orElseThrow(InvalidSectionOnLineException::new);

        // then
        assertThat(section.getLineId()).isEqualTo(lineId);
        assertThat(section.getUpStationId()).isEqualTo(stationSinSeolId);
        assertThat(section.getDownStationId()).isEqualTo(stationDongMyoId);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("노선에 있는 하행역 조회 테스트")
    public void findByLineIdAndDownStationId() {
        // given

        // when
        Section section = sectionDao.findByLineIdAndDownStationId(lineId, stationDongMyoId)
            .orElseThrow(InvalidSectionOnLineException::new);

        // then
        assertThat(section.getLineId()).isEqualTo(lineId);
        assertThat(section.getUpStationId()).isEqualTo(stationSinSeolId);
        assertThat(section.getDownStationId()).isEqualTo(stationDongMyoId);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("노선에 있는 상/하행역 조회 예외처리")
    public void findByLineIdAndStationId() {
        // given

        // when

        // then
        assertThat(sectionDao.findByLineIdAndUpStationId(lineId, 9L)).isEmpty();
        assertThat(sectionDao.findByLineIdAndUpStationId(2L, stationDongMyoId)).isEmpty();
        assertThat(sectionDao.findByLineIdAndDownStationId(2L, stationDongMyoId)).isEmpty();
        assertThat(sectionDao.findByLineIdAndUpStationId(lineId, stationDongDaeMoonId)).isEmpty();
    }

    @Test
    @DisplayName("구간 중 상행역 제거")
    public void deleteByLineIdAndUpStationId() {
        // given
        Long targetRemoveStationId = 1L;

        // when
        List<Section> beforeAllByLineId = sectionDao.findAllByLineId(lineId);
        int result = sectionDao.deleteByLineIdAndUpStationId(lineId, targetRemoveStationId);
        List<Section> afterAllByLindId = sectionDao.findAllByLineId(lineId);

        //then
        assertThat(result).isEqualTo(1);
        assertThat(beforeAllByLineId).hasSize(2);
        assertThat(afterAllByLindId).hasSize(1);
    }

    @Test
    @DisplayName("구간 중 하행역 제거")
    public void deleteByLineIdAndDownStationId() {
        Long targetRemoveStationId = 3L;

        // when
        List<Section> beforeAllByLineId = sectionDao.findAllByLineId(lineId);
        int result = sectionDao.deleteByLineIdAndDownStationId(lineId, targetRemoveStationId);
        List<Section> afterAllByLindId = sectionDao.findAllByLineId(lineId);

        //then
        assertThat(result).isEqualTo(1);
        assertThat(beforeAllByLineId).hasSize(2);
        assertThat(afterAllByLindId).hasSize(1);
    }

    @Test
    @DisplayName("구간 제거")
    public void delete() {
        // given
        Long targetRemoveSectionId = 1L;
        Section section = new Section(1L, 1L, 2L, 3L, 10);

        // when
        List<Section> beforeAllByLineId = sectionDao.findAllByLineId(lineId);
        int result = sectionDao.delete(section);
        List<Section> afterAllByLindId = sectionDao.findAllByLineId(lineId);

        // then
        assertThat(result).isEqualTo(1);
        assertThat(beforeAllByLineId).hasSize(2);
        assertThat(afterAllByLindId).hasSize(1);
    }
}
