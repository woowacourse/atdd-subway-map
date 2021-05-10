package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.NoSuchStationException;

import java.util.*;

@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(final SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void save(final Section section) {
        if (sectionRepository.doesSectionExist(section)) {
            throw new DuplicateSectionException();
        }
        sectionRepository.save(section);
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        if (sectionRepository.doesStationNotExist(lineId, stationId)) {
            throw new NoSuchStationException();
        }
        if (sectionRepository.isUnableToDelete(lineId)) {
            throw new UnavailableSectionDeleteException();
        }

        if (sectionRepository.isEndStation(lineId, stationId)) {
            sectionRepository.deleteSection(lineId, stationId);
            return;
        }
        Section newSection = createNewSection(lineId, stationId);
        sectionRepository.save(newSection);
        sectionRepository.deleteSection(lineId, stationId);
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
        Station firstStation = getFirstStation(upAndDownStations, lineId);
        List<Station> stations = new ArrayList<>(Collections.singletonList(firstStation));

        for (int i = 0; i < upAndDownStations.size(); i++) {
            Station currentDownEndStation = stations.get(stations.size() - 1);
            Station nextDownStation = upAndDownStations.get(currentDownEndStation);
            stations.add(nextDownStation);
        }
        return stations;
    }

    private Station getFirstStation(final Map<Station, Station> upAndDownStations, final Long lineId) {
        return upAndDownStations.keySet()
                .stream()
                .filter(station -> sectionRepository.isEndStation(lineId, station.getId()))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }
}
