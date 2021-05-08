package wooteco.subway.service.section;

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
    private final SectionDao sectionDao;

    public SectionCreateService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionCreateResponseDto createSection(Long lineId, SectionCreateRequestDto sectionCreateRequestDto) {
        Section newSection = new Section(lineId,
            sectionCreateRequestDto.getUpStationId(), sectionCreateRequestDto.getDownStationId(), sectionCreateRequestDto.getDistance());

        List<Section> allSectionsOfLine = sectionDao.findAllByLineId(lineId);

        Set<Long> stationIdsOfRequest = new HashSet<>();
        stationIdsOfRequest.add(newSection.getUpStationId());
        stationIdsOfRequest.add(newSection.getDownStationId());

        List<Section> sectionsHavingStationIdsOfRequest = allSectionsOfLine.stream()
            .filter(section ->
                stationIdsOfRequest.contains(section.getUpStationId())
                    || stationIdsOfRequest.contains(section.getDownStationId()))
            .collect(Collectors.toList());

        Set<Long> stationIdsOfSectionHavingStationsOfRequest = new HashSet<>();
        for (Section section : sectionsHavingStationIdsOfRequest) {
            stationIdsOfSectionHavingStationsOfRequest.add(section.getUpStationId());
            stationIdsOfSectionHavingStationsOfRequest.add(section.getDownStationId());
        }

        List<Long> standardSingleIdInList = stationIdsOfSectionHavingStationsOfRequest.stream()
            .filter(stationId ->
                stationId.equals(newSection.getUpStationId())
                    || stationId.equals(newSection.getDownStationId()))
            .collect(Collectors.toList());

        if (standardSingleIdInList.size() != 1) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "추가할 구간의 한 개의 역만 기존 노선에 존재해야 합니다.");
        }

        Long standardStationId = standardSingleIdInList.get(0);
        Long newStationId = stationIdsOfRequest.stream()
            .filter(stationId -> !stationId.equals(standardStationId))
            .findFirst()
            .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "구간 추가 에러"));

        List<Section> sectionsInLineHavingNewStation = sectionsHavingStationIdsOfRequest.stream()
            .filter(section ->
                section.getUpStationId().equals(standardStationId)
                    || section.getDownStationId().equals(standardStationId))
            .collect(Collectors.toList());

        if (sectionsInLineHavingNewStation.size() == 1) {
            Section sectionInLineHavingStandardStation = sectionsInLineHavingNewStation.get(0);
            if ((standardStationId.equals(newSection.getUpStationId()) && standardStationId.equals(sectionInLineHavingStandardStation.getDownStationId()))
                || (standardStationId.equals(sectionInLineHavingStandardStation.getUpStationId()) && standardStationId.equals(newSection.getDownStationId()))) {
                Section savedSection = sectionDao.save(newSection);
                return new SectionCreateResponseDto(savedSection);
            }
        }

        Direction directionOfStandardStationInNewSection = newSection.getDirectionOf(standardStationId);

        Section sectionToBeSplit = sectionsInLineHavingNewStation.stream()
            .filter(section -> section.getDirectionOf(standardStationId) == directionOfStandardStationInNewSection)
            .findFirst()
            .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "구간 추가 에러"));

        if (newSection.getDistance() >= sectionToBeSplit.getDistance()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "추가할 구간의 길이가 너무 큽니다.");
        }

        Direction reverseDirectionOfStandardStation = directionOfStandardStationInNewSection.getReversed();

        Section splitSection = null;
        int splitDistance = sectionToBeSplit.getDistance() - newSection.getDistance();
        if (reverseDirectionOfStandardStation == Direction.UP) {
            splitSection = new Section(lineId, sectionToBeSplit.getUpStationId(), newStationId, splitDistance);
        }
        if (reverseDirectionOfStandardStation == Direction.DOWN) {
            splitSection = new Section(lineId, newStationId, sectionToBeSplit.getDownStationId(), splitDistance);
        }
        sectionDao.deleteById(Objects.requireNonNull(sectionToBeSplit).getId());
        sectionDao.save(splitSection);
        Section savedNewSection = sectionDao.save(newSection);
        return new SectionCreateResponseDto(savedNewSection);
    }
}
