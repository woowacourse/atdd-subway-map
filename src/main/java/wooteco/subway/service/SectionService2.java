package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao2;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain2.section.Section;
import wooteco.subway.domain2.section.Sections2;
import wooteco.subway.domain2.station.Station;
import wooteco.subway.entity.SectionEntity2;
import wooteco.subway.exception.NotFoundException;

@Service
public class SectionService2 {

    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "존재하지 않는 역을 입력하였습니다.";

    private final SectionDao2 sectionDao;
    private final StationDao stationDao;

    public SectionService2(SectionDao2 sectionDao,
                           StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections2 sections = Sections2.of(findValidSections(lineId));
        Sections2 updatedSections = sections.delete(findExistingStation(stationId));

        for (Section deletedSection : updatedSections.extractDeletedSections(sections)) {
            sectionDao.delete(deletedSection.toEntity(lineId));
        }
        for (Section deletedSection : updatedSections.extractNewSections(sections)) {
            sectionDao.save(deletedSection.toEntity(lineId));
        }
    }

    private List<Section> findValidSections(Long lineId) {
        return sectionDao.findAllByLineId(lineId)
                .stream()
                .map(SectionEntity2::toDomain)
                .collect(Collectors.toList());
    }

    private Station findExistingStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND_EXCEPTION_MESSAGE))
                .toDomain();
    }
}
