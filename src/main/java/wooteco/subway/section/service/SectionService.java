package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.NoSuchStationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(final SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void save(final Section section) {
        if (sectionRepository.isInitialSave(section)) {
            sectionRepository.save(section);
            return;
        }
        validateSection(section);
        addSection(section);
    }

    private void validateSection(final Section section) {
        if (bothStationsExist(section)) {
            throw new DuplicateSectionException();
        }
        if (bothStationsDoNotExist(section)) {
            throw new NoSuchStationException();
        }
    }

    private void addSection(final Section section) {
        if (isNotEndStationSave(section)) {
            Section originalSection = sectionRepository.getExistingSectionByBaseStation(section);
            Section modifiedSection = getModifiedSection(section, originalSection);

            sectionRepository.updateSection(modifiedSection);
        }
        sectionRepository.save(section);
    }

    private Section getModifiedSection(final Section section, final Section originalSection) {
        validateSectionDistance(section, originalSection);
        int newSectionDistance = originalSection.getDistanceGap(section);

        if (section.hasSameUpStation(originalSection)) {
            return new Section(
                    originalSection.getId(),
                    section.getLineId(),
                    section.getDownStationId(),
                    originalSection.getDownStationId(),
                    newSectionDistance);
        }
        return new Section(
                originalSection.getId(),
                section.getLineId(),
                originalSection.getUpStationId(),
                section.getUpStationId(),
                newSectionDistance);
    }

    private void validateSectionDistance(final Section section, final Section originalSection) {
        if (originalSection.isShorterOrEqualTo(section)) {
            throw new IllegalSectionDistanceException();
        }
    }

    private boolean bothStationsExist(final Section section) {
        return sectionRepository.isStationExist(section.getLineId(), section.getUpStationId()) &&
                sectionRepository.isStationExist(section.getLineId(), section.getDownStationId());
    }

    private boolean bothStationsDoNotExist(final Section section) {
        return !sectionRepository.isStationExist(section.getLineId(), section.getUpStationId()) &&
                !sectionRepository.isStationExist(section.getLineId(), section.getDownStationId());
    }

    private boolean isNotEndStationSave(final Section section) {
        return !((sectionRepository.isEndStation(section.getLineId(), section.getDownStationId()) &&
                sectionRepository.isExistInUpStation(section.getLineId(), section.getDownStationId())) ||
                (sectionRepository.isEndStation(section.getLineId(), section.getUpStationId()) &&
                        sectionRepository.isExistInDownStation(section.getLineId(), section.getUpStationId())));
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        validateStationExistence(lineId, stationId);
        validateSectionCount(lineId);

        if (!sectionRepository.isEndStation(lineId, stationId)) {
            Section newSection = createNewSection(lineId, stationId);
            sectionRepository.save(newSection);
        }
        sectionRepository.deleteRelevantSections(lineId, stationId);
    }

    private void validateSectionCount(final Long lineId) {
        if (sectionRepository.isUnableToDelete(lineId)) {
            throw new UnavailableSectionDeleteException();
        }
    }

    private void validateStationExistence(final Long lineId, final Long stationId) {
        if (!sectionRepository.isStationExist(lineId, stationId)) {
            throw new NoSuchStationException();
        }
    }

    private Section createNewSection(final Long lineId, final Long stationId) {
        long newUpStationId = sectionRepository.getNewUpStationId(lineId, stationId);
        long newDownStationId = sectionRepository.getNewDownStationId(lineId, stationId);
        int newDistance = sectionRepository.getNewSectionDistance(lineId, stationId);

        return new Section(lineId, newUpStationId, newDownStationId, newDistance);
    }

    public List<Station> getAllStations(final Long lineId) {
        Map<Station, Station> upAndDownStations = sectionRepository.getAllUpAndDownStations(lineId);
        return getOrderedStations(upAndDownStations, lineId);
    }

    private List<Station> getOrderedStations(final Map<Station, Station> upAndDownStations, final Long lineId) {
        Station firstStation = getFirstStation(upAndDownStations);
        List<Station> stations = new ArrayList<>(Collections.singletonList(firstStation));

        for (int i = 0; i < upAndDownStations.size(); i++) {
            Station currentDownEndStation = stations.get(stations.size() - 1);
            Station nextDownStation = upAndDownStations.get(currentDownEndStation);
            stations.add(nextDownStation);
        }
        return stations;
    }

    private Station getFirstStation(final Map<Station, Station> upAndDownStations) {
        return upAndDownStations.keySet()
                .stream()
                .filter(station -> !upAndDownStations.containsValue(station))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }
}
