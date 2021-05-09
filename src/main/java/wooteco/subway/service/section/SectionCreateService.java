package wooteco.subway.service.section;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.request.section.SectionCreateRequestDto;
import wooteco.subway.controller.dto.response.section.SectionCreateResponseDto;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.type.Direction;
import wooteco.subway.exception.HttpException;

@Transactional
@Service
public class SectionCreateService {
    private static final int VALID_NUMBER_OF_INSERT_CRITERIA_STATION = 1;
    private static final int NUMBER_OF_SECTIONS_WHEN_INSERT_FIRST_OR_LAST = 1;
    private static final String CREATE_SECTION_ERROR_MESSAGE = "구간 추가 에러";

    private final SectionDao sectionDao;

    public SectionCreateService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionCreateResponseDto createSection(Long lineId, SectionCreateRequestDto sectionCreateRequestDto) {
        List<Section> allSectionsOfLine = sectionDao.findAllByLineId(lineId);
        Section newSection = new Section(lineId, sectionCreateRequestDto.getUpStationId(), sectionCreateRequestDto.getDownStationId(), sectionCreateRequestDto.getDistance());
        validateNewSectionStations(newSection, allSectionsOfLine);
        if (isConditionOfFirstOrLastInsert(newSection, allSectionsOfLine)) {
            Section savedSection = sectionDao.save(newSection);
            return new SectionCreateResponseDto(savedSection);
        }
        validateDistance(newSection, allSectionsOfLine);
        Section savedNewSection = insertNewSectionToMiddleOfSectionAndGet(newSection, allSectionsOfLine);
        return new SectionCreateResponseDto(savedNewSection);
    }

    private void validateNewSectionStations(Section newSection, List<Section> allSectionsOfLine) {
        List<Long> sectionInsertCriteriaStationIdsNew = getSectionInsertCriteriaStationIds(newSection, allSectionsOfLine);
        validateNumberOfSectionInsertCriteriaStations(sectionInsertCriteriaStationIdsNew);
    }

    private boolean isConditionOfFirstOrLastInsert(Section newSection, List<Section> allSectionsOfLine) {
        List<Section> sectionsHavingStationIdsOfNewSection = getSectionsHavingStationIdsOfNewSection(allSectionsOfLine, newSection.getAllStationIds());
        List<Long> sectionInsertCriteriaStationIdsNew = getSectionInsertCriteriaStationIds(newSection, allSectionsOfLine);
        Long sectionInsertCriteriaStationId = sectionInsertCriteriaStationIdsNew.get(0);
        List<Section> sectionsInLineHavingCriteriaStation = getSectionsInLineHavingCriteriaStation(sectionsHavingStationIdsOfNewSection, sectionInsertCriteriaStationId);
        if (sectionsInLineHavingCriteriaStation.size() != NUMBER_OF_SECTIONS_WHEN_INSERT_FIRST_OR_LAST) {
            return false;
        }
        Section sectionInLineHavingCriteriaStation = sectionsInLineHavingCriteriaStation.get(0);
        return isFirstInsert(newSection, sectionInsertCriteriaStationId, sectionInLineHavingCriteriaStation)
            || isLastInsert(newSection, sectionInsertCriteriaStationId, sectionInLineHavingCriteriaStation);
    }

    private void validateDistance(Section newSection, List<Section> allSectionsOfLine) {
        List<Section> sectionsHavingStationIdsOfNewSection = getSectionsHavingStationIdsOfNewSection(allSectionsOfLine, newSection.getAllStationIds());
        List<Long> sectionInsertCriteriaStationIds = getSectionInsertCriteriaStationIds(newSection, allSectionsOfLine);
        Long sectionInsertCriteriaStationId = sectionInsertCriteriaStationIds.get(0);
        List<Section> sectionsInLineHavingCriteriaStation = getSectionsInLineHavingCriteriaStation(sectionsHavingStationIdsOfNewSection, sectionInsertCriteriaStationId);
        Direction directionOfInsertCriteriaStationInNewSection = newSection.getDirectionOf(sectionInsertCriteriaStationId);
        Section sectionToBeSplit = getSectionToBeSplit(sectionInsertCriteriaStationId, sectionsInLineHavingCriteriaStation, directionOfInsertCriteriaStationInNewSection);
        if (!sectionToBeSplit.canBeSplitBy(newSection)) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "추가할 구간의 길이가 너무 큽니다.");
        }
    }

    private Section insertNewSectionToMiddleOfSectionAndGet(Section newSection, List<Section> allSectionsOfLine) {
        List<Section> sectionsHavingStationIdsOfNewSection = getSectionsHavingStationIdsOfNewSection(allSectionsOfLine, newSection.getAllStationIds());
        Long sectionInsertCriteriaStationId = getOneSectionInsertCriteriaStationIds(newSection, allSectionsOfLine);
        List<Section> sectionsInLineHavingInsertCriteriaStation = getSectionsInLineHavingCriteriaStation(sectionsHavingStationIdsOfNewSection, sectionInsertCriteriaStationId);
        Direction directionOfInsertCriteriaStationInNewSection = newSection.getDirectionOf(sectionInsertCriteriaStationId);
        Section sectionToBeSplit = getSectionToBeSplit(sectionInsertCriteriaStationId, sectionsInLineHavingInsertCriteriaStation, directionOfInsertCriteriaStationInNewSection);
        Long newStationId = getNewStationId(newSection, sectionInsertCriteriaStationId);
        Section newSplitSection = sectionToBeSplit.getNewSplitSectionBy(newSection, newStationId, directionOfInsertCriteriaStationInNewSection);
        sectionDao.deleteById(Objects.requireNonNull(sectionToBeSplit).getId());
        sectionDao.save(newSplitSection);
        return sectionDao.save(newSection);
    }


    private List<Long> getSectionInsertCriteriaStationIds(Section newSection, List<Section> allSectionsOfLine) {
        List<Long> stationIdsOfNewSection = Arrays.asList(newSection.getUpStationId(), newSection.getDownStationId());
        List<Section> sectionsHavingStationIdsOfNewSection = getSectionsHavingStationIdsOfNewSection(allSectionsOfLine, stationIdsOfNewSection);
        Set<Long> stationIdsInSectionsHavingStationIdsOfNewSection = getStationIdsInSectionsHavingStationIdsOfNewSection(sectionsHavingStationIdsOfNewSection);
        return getSectionInsertCriteriaStationIds(newSection, stationIdsInSectionsHavingStationIdsOfNewSection);
    }

    private Long getOneSectionInsertCriteriaStationIds(Section newSection, List<Section> allSectionsOfLine) {
        List<Long> stationIdsOfNewSection = Arrays.asList(newSection.getUpStationId(), newSection.getDownStationId());
        List<Section> sectionsHavingStationIdsOfNewSection = getSectionsHavingStationIdsOfNewSection(allSectionsOfLine, stationIdsOfNewSection);
        Set<Long> stationIdsInSectionsHavingStationIdsOfNewSection = getStationIdsInSectionsHavingStationIdsOfNewSection(sectionsHavingStationIdsOfNewSection);
        List<Long> sectionInsertCriteriaStationIds = getSectionInsertCriteriaStationIds(newSection, stationIdsInSectionsHavingStationIdsOfNewSection);
        if (sectionInsertCriteriaStationIds.size() != 1) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, CREATE_SECTION_ERROR_MESSAGE);
        }
        return sectionInsertCriteriaStationIds.get(0);
    }

    private Section getSectionToBeSplit(Long sectionInsertCriteriaStationId, List<Section> sectionsInLineHavingCriteriaStation, Direction directionOfInsertCriteriaStationInNewSection) {
        return sectionsInLineHavingCriteriaStation.stream()
            .filter(section -> section.getDirectionOf(sectionInsertCriteriaStationId) == directionOfInsertCriteriaStationInNewSection)
            .findFirst()
            .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, CREATE_SECTION_ERROR_MESSAGE));
    }

    private boolean isFirstInsert(Section newSection, Long sectionInsertCriteriaStationId, Section sectionInLineHavingCriteriaStation) {
        return sectionInsertCriteriaStationId.equals(sectionInLineHavingCriteriaStation.getUpStationId()) && sectionInsertCriteriaStationId.equals(newSection.getDownStationId());
    }

    private boolean isLastInsert(Section newSection, Long sectionInsertCriteriaStationId, Section sectionInLineHavingCriteriaStation) {
        return sectionInsertCriteriaStationId.equals(newSection.getUpStationId()) && sectionInsertCriteriaStationId.equals(sectionInLineHavingCriteriaStation.getDownStationId());
    }

    private List<Section> getSectionsInLineHavingCriteriaStation(List<Section> sectionsHavingStationIdsOfNewSection, Long sectionInsertCriteriaStationId) {
        return sectionsHavingStationIdsOfNewSection.stream()
            .filter(sectionInLine ->
                sectionInLine.getUpStationId().equals(sectionInsertCriteriaStationId)
                    || sectionInLine.getDownStationId().equals(sectionInsertCriteriaStationId))
            .collect(Collectors.toList());
    }

    private Long getNewStationId(Section newSection, Long sectionInsertCriteriaStationId) {
        List<Long> stationIdsOfNewSection = Arrays.asList(newSection.getUpStationId(), newSection.getDownStationId());
        return stationIdsOfNewSection.stream()
            .filter(stationId -> !stationId.equals(sectionInsertCriteriaStationId))
            .findFirst()
            .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, CREATE_SECTION_ERROR_MESSAGE));
    }

    private void validateNumberOfSectionInsertCriteriaStations(List<Long> sectionInsertCriteriaStationIds) {
        if (sectionInsertCriteriaStationIds.size() != VALID_NUMBER_OF_INSERT_CRITERIA_STATION) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "추가할 구간의 한 개의 역만 기존 노선에 존재해야 합니다.");
        }
    }

    private List<Section> getSectionsHavingStationIdsOfNewSection(List<Section> allSectionsOfLine, List<Long> stationIdsOfNewSection) {
        return allSectionsOfLine.stream()
            .filter(section ->
                stationIdsOfNewSection.contains(section.getUpStationId())
                    || stationIdsOfNewSection.contains(section.getDownStationId()))
            .collect(Collectors.toList());
    }

    private List<Long> getSectionInsertCriteriaStationIds(Section newSection, Set<Long> stationIdsInSectionsHavingStationIdsOfNewSection) {
        return stationIdsInSectionsHavingStationIdsOfNewSection.stream()
            .filter(stationId ->
                stationId.equals(newSection.getUpStationId())
                    || stationId.equals(newSection.getDownStationId()))
            .collect(Collectors.toList());
    }

    private Set<Long> getStationIdsInSectionsHavingStationIdsOfNewSection(List<Section> sectionsHavingStationIdsOfNewSection) {
        Set<Long> stationIdsInSectionsHavingStationIdsOfNewSection = new HashSet<>();
        for (Section section : sectionsHavingStationIdsOfNewSection) {
            stationIdsInSectionsHavingStationIdsOfNewSection.add(section.getUpStationId());
            stationIdsInSectionsHavingStationIdsOfNewSection.add(section.getDownStationId());
        }
        return stationIdsInSectionsHavingStationIdsOfNewSection;
    }
}
