package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.service.dto.LineWithStationsDto;
import wooteco.subway.service.dto.SimpleStation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IntegratedSubwayService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public IntegratedSubwayService(LineDao lineDao, SectionDao sectionDao,
                                   StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineWithStationsDto findAllInfoByLineId(Long id) {
        if (!lineDao.isExistById(id)) {
            throw new LineNotFoundException();
        }
        final Line line = lineDao.findById(id);
        final Sections sections = new Sections(sectionDao.findAllByLineId(id));
        final Set<SimpleStation> stations = sections.toSet();
        final List<StationResponse> stationResponses = makeStationResponse(stations);
        return new LineWithStationsDto(line, sortByStationId(stationResponses));
    }

    private List<StationResponse> sortByStationId(List<StationResponse> stationResponses) {
        return stationResponses.stream()
                .sorted(Comparator.comparing(StationResponse::getId))
                .collect(Collectors.toList());
    }

    private List<StationResponse> makeStationResponse(Set<SimpleStation> stations) {
        final List<StationResponse> stationResponses = new ArrayList<>();
        for (SimpleStation station : stations) {
            if (!stationDao.isExistById(station.getId())) {
                throw new StationNotFoundException();
            }
            stationResponses.add(new StationResponse(stationDao.findById(station.getId())));
        }
        return stationResponses;
    }
}
