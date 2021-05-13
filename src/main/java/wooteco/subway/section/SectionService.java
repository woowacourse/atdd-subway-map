package wooteco.subway.section;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.section.SectionLastRemainedException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private static final int WHEN_BETWEEN_SECTIONS = 2;
    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Transactional
    public Section create(Section newSection, Long lineId) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        Section modifiedSection = sections.addAndThenGetModifiedAdjacent(newSection);

        return sectionDao.saveModified(newSection, modifiedSection, lineId);
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        validateExistLine(lineId);
        validateExistStation(stationId);
        validateIsLastRemainedSection(lineId);

        Station station = stationDao.findById(stationId);
        List<Section> effectiveSections = sectionDao.findAdjacentByStationId(lineId, stationId);
        sectionDao.removeSections(lineId, effectiveSections);

        if (effectiveSections.size() == WHEN_BETWEEN_SECTIONS) {
            Section section = Sections.create(effectiveSections).removeStationInBetween(station);
            sectionDao.insertSection(section, lineId);
        }
    }

    private void validateIsLastRemainedSection(Long lineId) {
        if (sectionDao.findAllByLineId(lineId).hasSize(1)) {
            throw new SectionLastRemainedException();
        }
    }

    private void validateExistStation(Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new StationNotFoundException();
        }
    }

    private void validateExistLine(Long lineId) {
        if (!lineDao.existById(lineId)) {
            throw new LineNotFoundException();
        }
    }
}
