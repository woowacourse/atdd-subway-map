package wooteco.subway.service;

import java.util.List;
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
        validateDistinct(stationRequest.getName());
        Station station = new Station(stationRequest.getName());
        final Station savedStation = dao.save(station);
        return StationResponse.from(savedStation);
    }

    private void validateDistinct(String name) {
        Optional<Station> station = dao.findByName(name);
        if (station.isPresent()) {
            throw new RowDuplicatedException("이미 존재하는 역 이름입니다.");
        }
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
