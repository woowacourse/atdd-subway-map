package wooteco.subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.section.NotEnoughSectionException;
import wooteco.subway.exception.station.StationNotFoundException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SectionService {

    private static final int MIN_SECTION_COUNT = 1;

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public Section create(Section section, Long lineId) {
        Sections sections = sectionDao.findById(lineId);
        Section sectionWithLineId = Section.of(section, lineId);
        Optional<Section> affectedSection = sections.changeSection(sectionWithLineId);

        sections.add(sectionWithLineId);
        return saveAffectedSections(sections, sectionWithLineId, affectedSection);
    }

    private Section saveAffectedSections(Sections sections, Section section, Optional<Section> affectedSection) {
        affectedSection.ifPresent(received -> {
            sections.getSections().stream()
                    .filter(exist -> exist.getId().equals(received.getId()))
                    .findAny()
                    .ifPresent(exist -> {
                        sectionDao.deleteById(exist.getId());
                        sectionDao.save(received, section.getLineId());
                    });
        });
        return sectionDao.save(section, section.getLineId());
    }

    public void delete(Long lineId, Long stationId) {
        validate(lineId, stationId);

        List<Section> sections = sectionDao.findContainsStationId(lineId, stationId);
        final Sections foundSections = Sections.from(sections);

        sectionDao.deleteStations(lineId, sections);
        Optional<Section> affectedSection = foundSections.transformSection(stationId);
        affectedSection.ifPresent(section -> sectionDao.insertSection(section, lineId));
    }

    private void validate(Long lineId, Long stationId) {
        lineDao.findById(lineId).orElseThrow(LineNotFoundException::new);
        stationDao.findById(stationId).orElseThrow(StationNotFoundException::new);
        if (sectionDao.findById(lineId).hasSize(MIN_SECTION_COUNT)) {
            throw new NotEnoughSectionException();
        }
    }
}
