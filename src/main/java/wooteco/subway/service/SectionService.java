package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.SectionDaoV2;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionV2;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.SectionsV2;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private static final int SECTION_MERGE_SIZE = 2;
    private static final int FIRST_SECTION = 0;
    private static final int TWICE_SECTION = 1;

    private final SectionDaoV2 sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDaoV2 sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void add(Long lineId, SectionRequest sectionRequest) {
        final SectionsV2 sections = new SectionsV2(sectionDao.findByLineId(lineId));

        final Station upStation = stationDao.findById(sectionRequest.getUpStationId());
        final Station downStation = stationDao.findById(sectionRequest.getDownStationId());
        final SectionV2 section = new SectionV2(lineId, upStation, downStation, sectionRequest.getDistance());
        sections.add(section);

        sectionDao.save(section);
        sections.findUpdate(sectionDao.findByLineId(lineId))
                .ifPresent(sectionDao::update);
    }

    public void delete(Long lineId, Long stationId) {
        final SectionsV2 sections = new SectionsV2(sectionDao.findByLineId(lineId));
        final Station station = stationDao.findById(stationId);

        final List<SectionV2> deletedSections = sections.delete(station);
        sectionDao.deleteSections(deletedSections);

        if (deletedSections.size() == SECTION_MERGE_SIZE) {
            final SectionV2 mergedSection = deletedSections.get(FIRST_SECTION).merge(deletedSections.get(TWICE_SECTION));
            sectionDao.save(mergedSection);
        }
    }
}
