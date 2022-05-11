package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public Section addSection(final long lineId, final Section section) {
        final Station upStation = stationDao.findById(section.getUpStation().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철역 입니다."));
        final Station downStation = stationDao.findById(section.getDownStation().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철역 입니다."));

        Section newSection = new Section(upStation, downStation, section.getDistance(), lineId);

        final List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        final Sections sections = new Sections(lineSections);
        sections.add(newSection);

        lineSections.add(newSection);
        List<Section> sectionsToUpdate = sections.extract(lineSections);
        for (Section sectionToUpdate : sectionsToUpdate) {
            sectionDao.update(sectionToUpdate.getId(), sectionToUpdate);
        }

        return sectionDao.save(newSection);
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        final List<Section> sectionsToDelete = sections.pop(stationId);
        final Optional<Section> mergedSection = sections.findMergedSection(sectionsToDelete);
        sectionDao.deleteAll(sectionsToDelete);
        mergedSection.ifPresent(sectionDao::save);
    }

    @Transactional(readOnly = true)
    public List<Section> getSectionsByLine(final long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }

    @Transactional(readOnly = true)
    public List<Station> getStationsByLine(final long lineId) {
        final List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        final Sections sections = new Sections(lineSections);
        return sections.extractStations();
    }
}
