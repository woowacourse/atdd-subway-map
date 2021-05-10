package wooteco.subway.section;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.InvalidDeleteSectionException;
import wooteco.subway.exception.NoLineException;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineH2Dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final SectionH2Dao sectionH2Dao; //TODO 인터페이스 추출

    private SectionService(LineDao lineDao, SectionH2Dao sectionH2Dao) {
        this.lineDao = lineDao;
        this.sectionH2Dao = sectionH2Dao;
    }

    public Section add(Long lineId, Long upStationId, Long downStationId, int distance) {
        validateLineId(lineId);
        Section section = new Section(upStationId, downStationId, distance);
        Section newSection = sectionH2Dao.save(lineId, section);
        Optional<Section> overlappedSection = sectionH2Dao.findBySameUpOrDownId(lineId, newSection);
        overlappedSection.ifPresent(updateIntermediate(newSection));
        return newSection;
    }

    private Consumer<Section> updateIntermediate(Section newSection) {
        return originalSection -> {
            int newDistance = originalSection.getDistance() - newSection.getDistance();
            if (originalSection.isUpStation(newSection.getUpStationId())) {
                sectionH2Dao.updateUpStation(originalSection.getId(), newSection.getDownStationId(), newDistance);
                return;
            }
            sectionH2Dao.updateDownStation(originalSection.getId(), newSection.getUpStationId(), newDistance);
        };
    }

    public void delete(Long lineId, Long stationId) {
        validateLineId(lineId);
        validateRemovableSize(lineId);
        Sections sections = new Sections(sectionH2Dao.findByStation(lineId, stationId));
        merge(lineId, stationId, sections);
        for (Long sectionId : sections.sectionIds()) {
            sectionH2Dao.delete(sectionId);
        }
    }

    private void validateLineId(Long lineId) {
        lineDao.findById(lineId)
                .orElseThrow(NoLineException::new);
    }

    private void validateRemovableSize(Long lineId) {
        validateLineId(lineId);
        Sections sections = new Sections(sectionH2Dao.findByLineId(lineId));
        if (sections.isOne()) {
            throw new InvalidDeleteSectionException();
        }
    }

    private void merge(Long lineId, Long stationId, Sections sections) {
        if (sections.isBiggerThanOne()) {
            Section mergedSection = sections.merge(stationId);
            sectionH2Dao.save(lineId, mergedSection);
        }
    }
}