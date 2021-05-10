package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.SectionRepository;
import wooteco.subway.station.repository.StationRepository;

import java.util.List;

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
        validateSectionRequest(lineId, sectionRequest);
    }

    private void validateStations(final SectionRequest sectionRequest) {
        if (sectionRequest.getUpStationId().equals(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException("출발지와 도착지가 같을 수 없습니다.");
        }
        if (!stationRepository.isExistId(sectionRequest.getUpStationId()) || !stationRepository.isExistId(sectionRequest.getDownStationId())) {
            throw new NotFoundException("존재하지 않는 station을 구간에 등록할 수 없습니다.");
        }
    }

    private void validateSectionRequest(final Long lineId, final SectionRequest sectionRequest) {
        List<Long> stationIdsByLineId = sectionRepository.getStationIdsByLineId(lineId);
        if (stationIdsByLineId.contains(sectionRequest.getUpStationId()) && stationIdsByLineId.contains(sectionRequest.getDownStationId())) {
            throw new DuplicateException("이미 노선에 등록되어있는 구간입니다.");
        }
        if (!(stationIdsByLineId.contains(sectionRequest.getUpStationId()) || !stationIdsByLineId.contains(sectionRequest.getDownStationId()))) {
            throw new NotFoundException("상행선, 하행선 둘다 현재 노선에 존재하지 않습니다.");
        }
    }

    private void validateLineId(final Long lineId) {
        if (!lineRepository.isExistId(lineId)) {
            throw new NotFoundException("존재하지 않는 Line id 입니다.");
        }
    }
}
