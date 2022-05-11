package wooteco.subway.service;

import java.util.List;
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

    public List<StationResponse> create(long id, SectionRequest sectionRequest) {
        validateDistance(id, sectionRequest);
        SectionDto sectionDto = sectionDao.save(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Station upStation = getStation(sectionDto);
        Station downStation = getStation(sectionDto);

        return Stream.of(upStation, downStation)
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private void validateDistance(long id, SectionRequest sectionRequest) {
        int distance = sectionDao.findDistance(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId())
                .orElse(0);
        checkDistance(sectionRequest, distance);
    }

    private void checkDistance(SectionRequest sectionRequest, int distance) {
        if (distance != 0 && distance <= sectionRequest.getDistance()) {
            throw new IllegalArgumentException("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
        }
    }

    private Station getStation(SectionDto sectionDto) {
        return stationDao.findById(sectionDto.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
    }
}
