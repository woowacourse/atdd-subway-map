package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.repository.SectionRepository;
import wooteco.subway.domain.repository.StationRepository;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.utils.exception.NotFoundException;

@Transactional
@Service
public class SectionService {
    private static final String NOT_FOUND_STATION_MESSAGE = "[ERROR] %d 식별자에 해당하는 역을 찾을수 없습니다.";

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public Section create(Long lineId, LineRequest lineRequest) {

        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_STATION_MESSAGE, lineRequest.getUpStationId())));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_STATION_MESSAGE, lineRequest.getDownStationId())));

        Section section = Section.create(lineId, upStation, downStation, lineRequest.getDistance());
        return sectionRepository.save(section);
    }

}
