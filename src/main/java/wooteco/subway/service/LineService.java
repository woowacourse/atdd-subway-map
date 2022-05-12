package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.section.SectionRequest;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse findLineInfos(Long id) {
        var sections = sectionDao.findByLineId(id);
        var stations = stationDao.findByUpStationsIdAndDownStationId(
                sections.getUpStationId(),
                sections.getDownStationId()
        );
        var line = lineDao.findById(id);

        return new LineResponse(line, stations);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll();
    }

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        var upStationId = lineRequest.getUpStationId();
        var downStationId = lineRequest.getDownStationId();

        var line = lineDao.save(lineRequest);

        sectionDao.save(line.getId(), new SectionRequest(upStationId, downStationId, lineRequest.getDistance()));

        var stations = stationDao.findByUpStationsIdAndDownStationId(upStationId, downStationId);

        return new LineResponse(line, stations);
    }

    public void updateById(Long id, String name, String color) {
        lineDao.update(id, name, color);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
