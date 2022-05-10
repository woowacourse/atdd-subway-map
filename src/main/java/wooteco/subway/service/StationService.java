package wooteco.subway.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.RowDuplicatedException;
import wooteco.subway.exception.RowNotFoundException;

@Service
public class StationService {

    private final StationDao dao;

    public StationService(StationDao dao) {
        this.dao = dao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateDistinct(station);
        return StationResponse.from(saveOrThrow(station));
    }

    private void validateDistinct(Station station) {
        final boolean isMatch = findNameMatchingStation(station);
        if (isMatch) {
            throw new RowDuplicatedException("이미 존재하는 역 이름입니다.");
        }
    }

    private boolean findNameMatchingStation(Station station) {
        final List<Station> stations = dao.findAll();
        return stations.stream()
            .anyMatch(it -> Objects.equals(it.getName(), station.getName()));
    }

    private Station saveOrThrow(Station station) {
        final Optional<Station> savedStation = dao.save(station);
        return savedStation.orElseThrow(() -> new RowDuplicatedException("이미 존재하는 역 이름입니다."));
    }

    public List<StationResponse> findAll() {
        return dao.findAll()
            .stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
    }

    public void deleteOne(Long id) {
        final boolean isDeleted = dao.deleteById(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 역이 존재하지 않습니다.");
        }
    }
}
