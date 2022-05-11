package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.SectionDto;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public List<StationResponse> create(long lineId, SectionRequest sectionRequest) {
        validateDistance(lineId, sectionRequest);
        if (getSectionsByLineId(lineId).findAny().isPresent()) {
            validateStations(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        }

        updateSection(lineId, sectionRequest);

        SectionDto sectionDto = sectionDao.save(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Station upStation = getUpStation(sectionDto);
        Station downStation = getDownStation(sectionDto);

        return Stream.of(upStation, downStation)
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private Stream<SectionDto> getSectionsByLineId(long lineId) {
        return sectionDao.findAll().stream()
                .filter(sectionDto -> sectionDto.getLineId() == lineId);
    }

    private void validateDistance(long lineId, SectionRequest sectionRequest) {
        int totalDistance = getSectionsByLineId(lineId)
                .mapToInt(SectionDto::getDistance)
                .sum();
        if (totalDistance != 0 && totalDistance <= sectionRequest.getDistance()) {
            throw new IllegalArgumentException("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
        }
    }

    private void validateStations(long lineId, long upStationId, long downStationId) {
        checkBothExist(lineId, upStationId, downStationId);
        checkBothDoNotExist(lineId, upStationId, downStationId);
    }

    private void checkBothExist(long lineId, long upStationId, long downStationId) {
        int count = (int)getSectionsByLineId(lineId)
                .filter(sectionDto -> sectionDto.getUpStationId() == upStationId)
                .filter(sectionDto -> sectionDto.getDownStationId() == downStationId)
                .count();
        if (count == 1) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }

    private void checkBothDoNotExist(long lineId, long upStationId, long downStationId) {
        int upStationCount = (int)getSectionsByLineId(lineId)
                .filter(sectionDto -> sectionDto.getUpStationId() == upStationId)
                .count();

        int downStationCount = (int)getSectionsByLineId(lineId)
                .filter(sectionDto -> sectionDto.getDownStationId() == downStationId)
                .count();

        if (upStationCount == 0 && downStationCount == 0) {
            throw new IllegalArgumentException("상행역과 하행역 모두 존재하지 않습니다.");
        }
    }

    private void updateSection(long lineId, SectionRequest sectionRequest) {
        updateIfUpStation(lineId, sectionRequest);
        updateIfDownStation(lineId, sectionRequest);
    }

    private void updateIfUpStation(long lineId, SectionRequest sectionRequest) {
        getSectionsByLineId(lineId)
                .filter(sectionDto -> sectionDto.getUpStationId() == sectionRequest.getUpStationId())
                .findFirst()
                .ifPresent(sectionDto -> {
                    long id = sectionDto.getId();
                    sectionDao.updateUpStation(id, sectionRequest.getDownStationId());
                    int distance = sectionDao.findDistanceById(id).orElse(0) - sectionRequest.getDistance();
                    sectionDao.updateDistance(id, distance);
                });
    }

    private void updateIfDownStation(long lineId, SectionRequest sectionRequest) {
        getSectionsByLineId(lineId)
                .filter(sectionDto -> sectionDto.getDownStationId() == sectionRequest.getDownStationId())
                .findFirst()
                .ifPresent(sectionDto -> {
                    long id = sectionDto.getId();
                    sectionDao.updateDownStation(id, sectionRequest.getUpStationId());
                    int distance = sectionDao.findDistanceById(id).orElse(0) - sectionRequest.getDistance();
                    sectionDao.updateDistance(id, distance);
                });
    }

    private Station getUpStation(SectionDto sectionDto) {
        return stationDao.findById(sectionDto.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
    }

    private Station getDownStation(SectionDto sectionDto) {
        return stationDao.findById(sectionDto.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
    }
}
