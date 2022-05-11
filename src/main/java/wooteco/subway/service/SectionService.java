package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public List<Station> findUniqueSectionStationsByLineId(final Long lineId) {
        return sectionDao.findSectionStationsByLineId(lineId)
            .stream()
            .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
            .distinct()
            .map(id -> stationDao.findById(id).get())
            .sorted()
            .collect(Collectors.toList());
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        final List<Section> sectionsBeforeAddSection = sectionDao.findSectionStationsByLineId(lineId);

        Section newSection = sectionRequest.toEntity(lineId);
        newSection = sectionDao.save(newSection);

        final List<Section> sectionsAfterAddSection = new Sections(sectionsBeforeAddSection).addSection(newSection);

        sectionDao.batchUpdate(sectionsAfterAddSection);
    }
}
