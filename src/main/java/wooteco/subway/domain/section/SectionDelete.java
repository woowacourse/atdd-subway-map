package wooteco.subway.domain.section;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import wooteco.subway.exception.HttpException;

public class SectionDelete {
    private static final String SECTION_DELETE_ERROR_MESSAGE = "구간 삭제 에러";

    private final List<Section> sectionsWithStationIdToDelete;
    private final Long stationIdToDelete;
    private Section newUpStationSection;
    private Section newDownStationSection;

    public SectionDelete(List<Section> allSectionsInLine, Long stationIdToDelete) {
        this.sectionsWithStationIdToDelete = getSectionsWithStationIdToDelete(stationIdToDelete, allSectionsInLine);
        validateDeleteCondition(allSectionsInLine);
        this.stationIdToDelete = stationIdToDelete;
    }

    private List<Section> getSectionsWithStationIdToDelete(Long stationIdToDelete, List<Section> sections) {
        return sections.stream()
            .filter(section ->
                section.getUpStationId().equals(stationIdToDelete)
                    || section.getDownStationId().equals(stationIdToDelete))
            .collect(Collectors.toList());
    }

    private void validateDeleteCondition(List<Section> sections) {
        if (sectionsWithStationIdToDelete.size() == 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "노선에 삭제할 역이 존재하지 않습니다.");
        }
        if (sections.size() == 1) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "한 개의 구간만 존재하는 노선은 구간을 삭제할 수 없습니다.");
        }
    }

    public boolean isFirstOrLastStationDelete() {
        return sectionsWithStationIdToDelete.size() == 1;
    }

    public Long getSectionIdToDelete() {
        Section sectionToDelete = sectionsWithStationIdToDelete.get(0);
        return sectionToDelete.getId();
    }

    public Long getNewUpStationSectionId() {
        newUpStationSection = getNewUpStationSection();
        return newUpStationSection.getId();
    }

    private Section getNewUpStationSection() {
        return sectionsWithStationIdToDelete.stream()
            .filter(section -> section.getDownStationId().equals(stationIdToDelete))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(SECTION_DELETE_ERROR_MESSAGE));
    }

    public Long getNewDownStationSectionId() {
        newDownStationSection = getNewDownStationSection();
        return newDownStationSection.getId();
    }

    private Section getNewDownStationSection() {
        return sectionsWithStationIdToDelete.stream()
            .filter(section -> section.getUpStationId().equals(stationIdToDelete))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(SECTION_DELETE_ERROR_MESSAGE));
    }

    public Section getNewSection(Long lineId) {
        int newDistance = newUpStationSection.getDistance() + newDownStationSection.getDistance();
        return new Section(lineId, newUpStationSection.getUpStationId(), newDownStationSection.getDownStationId(), newDistance);
    }
}
