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
    private static final int LINE_MIN_SIZE = 2;

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
        List<Long> stationIdsByLineId = sectionRepository.getStationIdsByLineId(lineId);

        // upStation만 Line에 존재하는 경우,
        if (stationIdsByLineId.contains(sectionRequest.getUpStationId())) {
            addBaseOnUpStation(lineId, sectionRequest);
            return;
        }
        // downStation만 Line에 존재하는 경우.
        addBaseOnDownStation(lineId, sectionRequest);
    }

    private void addBaseOnDownStation(final Long lineId, final SectionRequest sectionRequest) {
        sectionRepository.saveBaseOnDownStation(lineId, sectionRequest);
    }

    private void addBaseOnUpStation(final Long lineId, final SectionRequest sectionRequest) {
        sectionRepository.saveBaseOnUpStation(lineId, sectionRequest);
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
        if (!(stationIdsByLineId.contains(sectionRequest.getUpStationId()) || stationIdsByLineId.contains(sectionRequest.getDownStationId()))) {
            throw new NotFoundException("상행선, 하행선 둘다 현재 노선에 존재하지 않습니다.");
        }
    }

    private void validateLineId(final Long lineId) {
        if (!lineRepository.isExistId(lineId)) {
            throw new NotFoundException("존재하지 않는 Line id 입니다.");
        }
    }

    public void delete(final Long lineId, final Long stationId) {
        validateDeleteRequest(lineId, stationId);
    }

    private void validateDeleteRequest(final Long lineId, final Long stationId) {
        validateLineId(lineId);
        List<Long> stationIdsByLineId = sectionRepository.getStationIdsByLineId(lineId);
        if (!stationIdsByLineId.contains(stationId)) {
            throw new NotFoundException("해당 station id는 입력받은 Line id에 속해있지 않습니다.");
        }
        if (stationIdsByLineId.size() <= LINE_MIN_SIZE) {
            throw new IllegalArgumentException("해당 라인이 포함하고 있는 구간이 2개 이하입니다");
        }
    }
}
