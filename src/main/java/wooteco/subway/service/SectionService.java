package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.section.NoneOfSectionIncludedInLine;
import wooteco.subway.exception.section.SectionDistanceMismatchException;
import wooteco.subway.exception.section.SectionsAlreadyExistException;
import wooteco.subway.domain.SimpleSection;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public List<Section> findAllByLineId(Long id) {
        return sectionDao.findAllByLineId(id);
    }

    public void checkEndStationsAreIncluded(Long lineId, SectionInsertRequest sectionInsertRequest) {
        if (sectionDao.isIncludeAllEndStations(lineId, sectionInsertRequest)) {
            throw new SectionsAlreadyExistException();
        }
    }

    public void insertSections(Long lineId, SectionInsertRequest sectionInsertRequest) {
        final Optional<Section> optionalSection = sectionDao.findOneIfInclude(lineId, sectionInsertRequest);
        final Section section = optionalSection.orElseThrow(NoneOfSectionIncludedInLine::new);
        validateCanBeInserted(section, sectionInsertRequest);
        final SimpleSection updatedSection = updateOriginalSectionToMakeOneLine(section, sectionInsertRequest);
        sectionDao.update(lineId, updatedSection); // 기존 섹션을 업데이트함. 삽입된 구간을 포함하여.
        sectionDao.insert(lineId, sectionInsertRequest.toSimpleSectionDto()); // 추가된 섹션을 삽입함.
    }

    private SimpleSection updateOriginalSectionToMakeOneLine(Section section, SectionInsertRequest insertedSection) {
        if (section.getUpStationId().equals(insertedSection.getUpStationId())) {
            return new SimpleSection(insertedSection.getDownStationId(),
                    section.getDownStationId(),
                    updateDistance(section, insertedSection));
        }

        // downStation 이 같다면
        return new SimpleSection(section.getUpStationId(),
                section.getUpStationId(),
                updateDistance(section, insertedSection));
    }

    private int updateDistance(Section section, SectionInsertRequest insertedSection) {
        final int maxDistance = Math.max(section.getDistance(), insertedSection.getDistance());
        final int minDistance = Math.min(section.getDistance(), insertedSection.getDistance());
        return maxDistance - minDistance;
    }

    private void validateCanBeInserted(Section section, SectionInsertRequest insertedSection) {
        if (!section.compareDistance(insertedSection)) {
            throw new SectionDistanceMismatchException();
        }
    }
}
