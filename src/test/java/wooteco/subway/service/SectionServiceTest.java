package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;
import wooteco.subway.repository.entity.StationEntity;
import wooteco.subway.service.dto.SectionDeleteRequest;
import wooteco.subway.service.dto.SectionSaveRequest;

@SpringBootTest
@Transactional
class SectionServiceTest {

    StationEntity first;
    StationEntity second;
    StationEntity third;
    StationEntity fourth;
    StationEntity add;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineDao lineDao;
    private Long lineId;

    @BeforeEach
    void setUp() {
        LineEntity firstLine = lineDao.save(new LineEntity(null, "1호선", "red"));
        lineId = firstLine.getId();

        first = stationDao.save(new StationEntity(null, "first"));
        second = stationDao.save(new StationEntity(null, "second"));
        third = stationDao.save(new StationEntity(null, "third"));
        fourth = stationDao.save(new StationEntity(null, "fourth"));
        add = stationDao.save(new StationEntity(null, "add"));

        sectionDao.save(new SectionEntity(null, lineId, first.getId(), second.getId(), 3));
        sectionDao.save(new SectionEntity(null, lineId, second.getId(), third.getId(), 4));
        sectionDao.save(new SectionEntity(null, lineId, third.getId(), fourth.getId(), 5));
    }

    @Test
    @DisplayName("구간 등록하기")
    void saveSection() {
        // given
        SectionSaveRequest request = new SectionSaveRequest(lineId, second.getId(), add.getId(), 1);

        assertThatCode(() -> sectionService.save(request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구간 삭제하기")
    void deleteSection() {
        // given
        SectionDeleteRequest request = new SectionDeleteRequest(lineId, first.getId());
        // then
        assertThatCode(() -> sectionService.delete(request))
                .doesNotThrowAnyException();
    }
}
