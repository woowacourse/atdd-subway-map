package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

}
