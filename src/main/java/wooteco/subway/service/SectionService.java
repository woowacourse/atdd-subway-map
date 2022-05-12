package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEntity;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.SectionDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(SectionDto sectionDto) {
        sectionDao.save(sectionDto.toEntity());
    }

    public void addSectionInLine(SectionDto sectionDto) {
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(sectionDto.getLineId());
        List<Section> convertedSections = convertSectionEntitiesToSections(sortSectionEntities(sectionEntities));
        Sections sections = new Sections(convertedSections);
        Section newSection = new Section(getStationById(sectionDto.getUpStationId()),
            getStationById(sectionDto.getDownStationId()),
            sectionDto.getDistance());

        sections.add(newSection);
        List<Section> sectionsToUpdate = sections.getDifference(convertedSections);
        sectionsToUpdate.remove(newSection);

        updateModifiedSections(sectionDto.getLineId(), sectionsToUpdate);

        sectionDao.save(sectionDto.toEntity());
    }

    private List<Section> convertSectionEntitiesToSections(List<SectionEntity> sectionEntities) {
        return sectionEntities.stream()
            .map(sectionEntity -> {
                Station upDestination = getStationById(sectionEntity.getUpStationId());
                Station downDestination = getStationById(sectionEntity.getDownStationId());
                return new Section(sectionEntity.getId(), upDestination, downDestination, sectionEntity.getDistance());
            })
            .collect(Collectors.toList());
    }

    private List<SectionEntity> sortSectionEntities(List<SectionEntity> sectionEntities) {
        if (sectionEntities.size() <= 1) {
            return sectionEntities;
        }
        Long upDestinationId = findUpDestinationId(sectionEntities);
        Map<Long, SectionEntity> sections = sectionEntities.stream()
            .collect(Collectors.toMap(SectionEntity::getUpStationId, sectionEntity -> sectionEntity, (a, b) -> b));

        List<SectionEntity> sortedSectionEntities = new ArrayList<>();

        Long key = upDestinationId;
        sortedSectionEntities.add(sections.get(key));

        for (int i = 0; i < sections.size() - 1; i++) {
            key = sections.get(key).getDownStationId();
            sortedSectionEntities.add(sections.get(key));
        }

        return sortedSectionEntities;
    }

    private void updateModifiedSections(Long lineId, List<Section> modifiedSections) {
        for (Section section : modifiedSections) {
            sectionDao.update(
                new SectionEntity(section.getId(), lineId, section.getUpStation().getId(),
                    section.getDownStation().getId(), section.getDistance()));
        }
    }

    private Station getStationById(Long stationId) {
        return new Station(stationId, stationService.findById(stationId).getName());
    }

    public List<Station> findStationsByLineId(Long lineId) {
        List<SectionEntity> sections = sectionDao.findByLineId(lineId);
        Long upDestinationId = findUpDestinationId(sections);
        Map<Long, Long> stationIds = sections.stream()
            .collect(Collectors.toMap(
                SectionEntity::getUpStationId,
                SectionEntity::getDownStationId,
                (a, b) -> b)
            );

        List<Station> sectionEntities = new ArrayList<>();
        sectionEntities.add(stationService.findById(upDestinationId));

        Long key = upDestinationId;
        for (int i = 0; i < stationIds.size(); i++) {
            key = stationIds.get(key);
            sectionEntities.add(stationService.findById(key));
        }

        return sectionEntities;
    }

    private Long findUpDestinationId(List<SectionEntity> sections) {
        List<Long> upStations = collectValuesBy(SectionEntity::getUpStationId, sections);
        List<Long> downStations = collectValuesBy(SectionEntity::getDownStationId, sections);

        upStations.removeAll(downStations);
        if (upStations.size() != 1) {
            throw new IllegalStateException("저장된 구간들 값이 올바르지 않습니다.");
        }
        return upStations.get(0);
    }

    private <T, E> List<T> collectValuesBy(Function<E, T> mapper, List<E> collection) {
        return collection.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }

    public void deleteStation(Long lineId, Long stationId) {
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(lineId);
        List<Section> originalSections = convertSectionEntitiesToSections(sortSectionEntities(sectionEntities));
        Sections sections = new Sections(originalSections);
        sections.delete(getStationById(stationId));
        List<Section> modifiedSections = new ArrayList<>(sections.getValues());

        List<Long> originalSectionIds = collectValuesBy(Section::getId, originalSections);
        List<Long> modifiedSectionIds = collectValuesBy(Section::getId, modifiedSections);

        originalSectionIds.removeAll(modifiedSectionIds);
        Long removedId = originalSectionIds.get(0);
        sectionDao.deleteById(removedId);

        modifiedSections.removeAll(originalSections);
        updateModifiedSections(lineId, modifiedSections);
    }
}
