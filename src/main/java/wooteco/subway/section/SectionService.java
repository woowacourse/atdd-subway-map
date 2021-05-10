package wooteco.subway.section;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.section.NotEnoughSectionException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public Section createSection(Section section, Long lineId) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        Optional<Section> affectedSection = sections.affectedSection(section);
        sections.add(section);

        sectionDao.change(sections, affectedSection);
        return sectionDao.saveAffectedSections(section, affectedSection, lineId);
    }

    public void removeSection(Long lineId, Long stationId) {
        lineDao.findById(lineId).orElseThrow(LineNotFoundException::new);
        stationDao.findStationById(stationId).orElseThrow(StationNotFoundException::new);
        if (sectionDao.findSectionsByLineId(lineId).hasSize(1)) {
            throw new NotEnoughSectionException();
        }

        List<Section> sections = sectionDao.findSectionContainsStationId(lineId, stationId);
        final Sections foundSections = Sections.from(sections);

        //TODO : 라인 아이디가 없을 시 예외처리
        sectionDao.deleteStations(lineId, sections);

        // TODO : 연결하자.
        Optional<Section> affectedSection = foundSections.transformSection(stationId);

        affectedSection.ifPresent(section -> sectionDao.insertSection(section, lineId));
    }
}
