package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.info.StationDto;

@Service
public class StationService {
    private static final String ERROR_MESSAGE_DUPLICATE_NAME = "중복된 지하철 역 이름입니다.";
    private static final String ERROR_MESSAGE_NOT_EXISTS_ID = "존재하지 않는 지하철 역입니다.";
    private static final String ERROR_MESSAGE_ALREADY_USED = "해당 역을 지나는 노선이 있으므로 삭제가 불가합니다.";

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public StationDto save(StationDto stationDto) {
        validateNameDuplicate(stationDto);
        Station station = new Station(stationDto.getName());
        Station newStation = stationDao.save(station);
        return new StationDto(newStation.getId(), newStation.getName());
    }

    private void validateNameDuplicate(StationDto stationDto) {
        if (stationDao.existByName(stationDto.getName())) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DUPLICATE_NAME);
        }
    }

    public List<StationDto> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(it -> new StationDto(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        validateNotExists(id);
        validateAlreadyUsedInSection(id);
        stationDao.delete(id);
    }

    private void validateNotExists(Long id) {
        if (!stationDao.existById(id)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_ID);
        }
    }

    private void validateAlreadyUsedInSection(Long id) {
        if (sectionDao.existSectionUsingStation(id)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_ALREADY_USED);
        }
    }
}
