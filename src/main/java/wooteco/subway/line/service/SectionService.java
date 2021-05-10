package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.SectionRepository;
import wooteco.subway.station.repository.StationRepository;

@Service
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(final LineRepository lineRepository, final StationRepository stationRepository, final SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public void lineCreateAdd(final Long lineId, final SectionRequest sectionRequest) {
        validateLineId(lineId);
        validateStations(sectionRequest);
        sectionRepository.save(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    public void add(final Long lineId, final SectionRequest sectionRequest) {
        validateAddRequest(lineId, sectionRequest);
    }

    private void validateAddRequest(final Long lineId, final SectionRequest sectionRequest) {
        validateLineId(lineId);
        validateStations(sectionRequest);
        validateSectionRequest(sectionRequest);
    }

    private void validateStations(final SectionRequest sectionRequest) {
        if (sectionRequest.getUpStationId().equals(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException("출발지와 도착지가 같을 수 없습니다.");
        }
        if (!stationRepository.isExistId(sectionRequest.getUpStationId()) || !stationRepository.isExistId(sectionRequest.getDownStationId())) {
            throw new NotFoundException("존재하지 않는 station을 구간에 등록할 수 없습니다.");
        }
    }

    //TODO : station 두개중 하나만 노선에 존재하는지 확인
    private void validateSectionRequest(final SectionRequest sectionRequest) {

    }

    private void validateLineId(final Long lineId) {
        if (!lineRepository.isExistId(lineId)) {
            throw new NotFoundException("존재하지 않는 Line id 입니다.");
        }
    }
}
