package wooteco.subway.station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.DuplicatedStationException;
import wooteco.subway.exception.station.StationNotFoundException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationDao stationDao;

    public Station save(Station station) {
        if(stationDao.findStationByName(station.getName()).isPresent()) {
            throw new DuplicatedStationException();
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteStation(Long id) {
        stationDao.delete(id);
    }

    public Station findStation(Long id) {
        return stationDao.findStationById(id).orElseThrow(StationNotFoundException::new);
    }
}
