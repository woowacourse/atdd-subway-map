package wooteco.subway.domain.section;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.section.type.Direction;
import wooteco.subway.exception.HttpException;

public class SectionCreate {
    private static final int VALID_NUMBER_OF_INSERT_CRITERIA_STATION = 1;
    private static final int NUMBER_OF_SECTIONS_IN_LINE_HAVING_INSERT_CRITERIA_STATION_WHEN_INSERT_FIRST_OR_LAST = 1;
    private static final String CREATE_SECTION_ERROR_MESSAGE = "구간 추가 에러";

    private final Section newSection;
    private final List<Long> stationIdsOfNewSection;
    private List<Section> sectionsHavingStationIdsOfNewSection;
    private Long sectionInsertCriteriaStationId;
    private List<Section> sectionsInLineHavingCriteriaStation;
    private Direction directionOfInsertCriteriaStationInNewSection;
    private Section sectionToBeSplit;

    public SectionCreate(Section newSection, List<Section> allSectionsOfLine) {
        this.newSection = newSection;
        this.stationIdsOfNewSection = Arrays.asList(newSection.getUpStationId(), newSection.getDownStationId());
        validateNewSectionStations(allSectionsOfLine);
    }

    private void validateNewSectionStations(List<Section> allSectionsOfLine) {
        List<Long> sectionInsertCriteriaStationIds = getSectionInsertCriteriaStationIds(allSectionsOfLine);
        validateNumberOfSectionInsertCriteriaStations(sectionInsertCriteriaStationIds);
        this.sectionInsertCriteriaStationId = sectionInsertCriteriaStationIds.get(0);
    }

    private List<Long> getSectionInsertCriteriaStationIds(List<Section> allSectionsOfLine) {
        this.sectionsHavingStationIdsOfNewSection = getSectionsHavingStationIdsOfNewSection(allSectionsOfLine);
        Set<Long> stationIdsInSectionsHavingStationIdsOfNewSection = getStationIdsInSectionsHavingStationIdsOfNewSection();
        return stationIdsInSectionsHavingStationIdsOfNewSection.stream()
            .filter(stationId ->
                stationId.equals(newSection.getUpStationId())
                    || stationId.equals(newSection.getDownStationId()))
            .collect(Collectors.toList());
    }

    private List<Section> getSectionsHavingStationIdsOfNewSection(List<Section> allSectionsOfLine) {
        return allSectionsOfLine.stream()
            .filter(section ->
                stationIdsOfNewSection.contains(section.getUpStationId())
                    || stationIdsOfNewSection.contains(section.getDownStationId()))
            .collect(Collectors.toList());
    }

    private Set<Long> getStationIdsInSectionsHavingStationIdsOfNewSection() {
        Set<Long> stationIdsInSectionsHavingStationIdsOfNewSection = new HashSet<>();
        for (Section section : sectionsHavingStationIdsOfNewSection) {
            stationIdsInSectionsHavingStationIdsOfNewSection.add(section.getUpStationId());
            stationIdsInSectionsHavingStationIdsOfNewSection.add(section.getDownStationId());
        }
        return stationIdsInSectionsHavingStationIdsOfNewSection;
    }


    private void validateNumberOfSectionInsertCriteriaStations(List<Long> sectionInsertCriteriaStationIds) {
        if (sectionInsertCriteriaStationIds.size() != VALID_NUMBER_OF_INSERT_CRITERIA_STATION) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "추가할 구간의 한 개의 역만 기존 노선에 존재해야 합니다.");
        }
    }

    public boolean isConditionOfFirstOrLastInsert() {
        this.sectionsInLineHavingCriteriaStation = getSectionsInLineHavingCriteriaStation();
        if (sectionsInLineHavingCriteriaStation.size() != NUMBER_OF_SECTIONS_IN_LINE_HAVING_INSERT_CRITERIA_STATION_WHEN_INSERT_FIRST_OR_LAST) {
            return false;
        }
        Section sectionInLineHavingCriteriaStation = sectionsInLineHavingCriteriaStation.get(0);
        return isFirstInsert(sectionInLineHavingCriteriaStation)
            || isLastInsert(sectionInLineHavingCriteriaStation);
    }

    private List<Section> getSectionsInLineHavingCriteriaStation() {
        return sectionsHavingStationIdsOfNewSection.stream()
            .filter(sectionInLine ->
                sectionInLine.getUpStationId().equals(sectionInsertCriteriaStationId)
                    || sectionInLine.getDownStationId().equals(sectionInsertCriteriaStationId))
            .collect(Collectors.toList());
    }

    private boolean isFirstInsert(Section sectionInLineHavingCriteriaStation) {
        return sectionInsertCriteriaStationId.equals(sectionInLineHavingCriteriaStation.getUpStationId())
            && sectionInsertCriteriaStationId.equals(newSection.getDownStationId());
    }

    private boolean isLastInsert(Section sectionInLineHavingCriteriaStation) {
        return sectionInsertCriteriaStationId.equals(newSection.getUpStationId())
            && sectionInsertCriteriaStationId.equals(sectionInLineHavingCriteriaStation.getDownStationId());
    }

    private void validateDistance() {
        this.directionOfInsertCriteriaStationInNewSection = newSection.getDirectionOf(sectionInsertCriteriaStationId);
        this.sectionToBeSplit = getSectionToBeSplit();
        if (!sectionToBeSplit.canBeSplitBy(newSection)) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "추가할 구간의 길이가 너무 큽니다.");
        }
    }

    private Section getSectionToBeSplit() {
        return sectionsInLineHavingCriteriaStation.stream()
            .filter(section -> section.getDirectionOf(sectionInsertCriteriaStationId) == directionOfInsertCriteriaStationInNewSection)
            .findFirst()
            .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, CREATE_SECTION_ERROR_MESSAGE));
    }

    public Section getNewSectionWhenInsertNewSectionToMiddleOfSection() {
        validateDistance();
        Long newStationId = getNewStationId();
        return sectionToBeSplit.getNewSplitSectionBy(newSection, newStationId, directionOfInsertCriteriaStationInNewSection);
    }

    private Long getNewStationId() {
        List<Long> stationIdsOfNewSection = Arrays.asList(newSection.getUpStationId(), newSection.getDownStationId());
        return stationIdsOfNewSection.stream()
            .filter(stationId -> !stationId.equals(sectionInsertCriteriaStationId))
            .findFirst()
            .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, CREATE_SECTION_ERROR_MESSAGE));
    }

    public Long getOldSplitSectionId() {
        return sectionToBeSplit.getId();
    }

    public Section getNewSection() {
        return newSection;
    }
}
