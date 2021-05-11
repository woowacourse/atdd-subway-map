package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.section.DeleteSectionIsNotPermittedException;
import wooteco.subway.exception.section.NoneOfSectionIncludedInLine;
import wooteco.subway.exception.section.SectionDistanceMismatchException;
import wooteco.subway.exception.section.SectionsAlreadyExistException;
import wooteco.subway.domain.SimpleSection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public List<Section> findAllByLineId(Long id) {
        return sectionDao.findAllByLineId(id);
    }

    public void validateEndStationsAreIncluded(Long lineId, SectionInsertRequest sectionInsertRequest) {
        if (sectionDao.isIncludeAllEndStations(lineId, sectionInsertRequest)) {
            throw new SectionsAlreadyExistException();
        }
    }

    public int getSectionCountsByLineId(Long lineId) {
        return sectionDao.countsByLineId(lineId);
    }

    public void insertSections(Long lineId, SectionInsertRequest sectionInsertRequest) {
        final Optional<Section> optionalSectionConversed =
                sectionDao.findOneIfIncludeConversed(lineId, sectionInsertRequest);
        if (optionalSectionConversed.isPresent()) {
            sectionDao.insert(lineId, sectionInsertRequest.toSimpleSectionDto());
            return;
        }

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

    public void validateSectionCount(Long lineId) {
        final int counts = getSectionCountsByLineId(lineId);
        if (counts <= 1) {
            throw new DeleteSectionIsNotPermittedException();
        }
    }

    public void deleteAllSectionByLineId(Long lineId) {
        sectionDao.deleteAllByLineId(lineId);
    }

    public void deleteSection(Long lineId, Long stationId) {
        final List<Section> sections = sectionDao.findAllSectionsIncludeStationId(lineId, stationId);
        if (sections.isEmpty()) {
            throw new DeleteSectionIsNotPermittedException();
        }
        Section section = adjustSection(lineId, sections);
        sectionDao.delete(section);
    }

    private Section adjustSection(Long lineId, List<Section> sections) {
        if (sections.size() == 1) { // 구간이 하나밖에 포함되지 않는 경우 <-> 종점인 경우
            return sections.get(0);
        }

        final int updatedDistance = sections.stream().mapToInt(Section::getDistance).sum();
        final Long upStationId = sections.get(0).getUpStationId();
        final Long downStationId = sections.get(1).getDownStationId();
        sectionDao.update(lineId, new SimpleSection(upStationId, downStationId, updatedDistance));
        return sections.get(1);
    }
}
