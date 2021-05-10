package wooteco.subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineRequest;
import wooteco.subway.line.LineService;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final LineService lineService;
    private final StationService stationService;

    @Autowired
    public SectionService(SectionDao sectionDao, LineService lineService, StationService stationService) {
        this.sectionDao = sectionDao;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public Section createSection(long lineId, LineRequest lineRequest) {
        Section section = new Section(lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());

        try {
            sectionDao.save(section);
        } catch (DataAccessException e) {
            throw new IllegalInputException();
        }

        return section;
    }
}
