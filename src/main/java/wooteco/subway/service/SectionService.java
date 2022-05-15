package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public Section create(long lineId, SectionRequest sectionRequest) {
        final Section section = sectionRequest.toEntity();
        final Section newSection = new Section(findUpStation(section), findDownStation(section), section.getDistance(), lineId);

        final List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        final Sections sections = new Sections(lineSections);
        sections.add(newSection);

        lineSections.add(newSection);
        modify(sections, lineSections);

        return sectionDao.save(newSection);
    }

    private void modify(final Sections sections, final List<Section> lineSections) {
        final List<Section> sectionsToUpdate = sections.extract(lineSections);
        for (Section sectionToUpdate : sectionsToUpdate) {
            sectionDao.update(sectionToUpdate.getId(), sectionToUpdate);
        }
    }

    private Station findUpStation(final Section section) {
        return stationDao.findById(section.getUpStation().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철역 입니다."));
    }

    private Station findDownStation(final Section section) {
        return stationDao.findById(section.getDownStation().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철역 입니다."));
    }

    @Transactional
    public void remove(final Long lineId, final Long stationId) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        final List<Section> sectionsToDelete = sections.pop(stationId);
        final Optional<Section> mergedSection = sections.findMergedSection(sectionsToDelete);
        sectionDao.deleteAll(sectionsToDelete);
        mergedSection.ifPresent(sectionDao::save);
    }
}
