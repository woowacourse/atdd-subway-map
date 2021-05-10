package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.exception.SectionDistanceException;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.repository.StationRepository;

@Service
public class SectionService {

    private static final int MIN_DISTANCE = 0;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;


    public SectionService(SectionRepository sectionRepository,
        StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void createSection(Section newSection) {
        validateDistance(newSection);
        validateStations(newSection);
        sectionRepository.save(newSection);
    }

    private void validateDistance(Section section) {
        if (section.getDistance() <= MIN_DISTANCE) {
            throw new SectionDistanceException();
        }
    }

    private void validateStations(Section section) {
        validateStation(section.getUpStationId());
        validateStation(section.getDownStationId());
    }

    private void validateStation(Long stationId) {
        if (!stationRepository.isExist(stationId)) {
            throw new NotExistStationException();
        }
    }

}
