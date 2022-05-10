package wooteco.subway.dao.application;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.NoSuchLineException;
import wooteco.subway.exception.NoSuchStationException;

@Service
public class LineService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(final StationDao stationDao, final LineDao lineDao,
                       final SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse createLine(final LineRequest request) {
        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(NoSuchStationException::new);

        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(NoSuchStationException::new);

        Line createdLine = lineDao.save(new Line(request.getName(), request.getColor()));

        Section createdSection = sectionDao.save(createdLine.getId(),
                new Section(upStation, downStation, request.getDistance()));
        return LineResponse.of(createdLine, createdSection);
    }

    public LineResponse findLine(final long id) {
        Line findLine = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        List<Section> sections = sectionDao.findByLineId(findLine.getId());
        return LineResponse.of(findLine, sections);
    }

    public List<LineResponse> findLines() {
        List<LineResponse> result = new ArrayList<>();
        List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            List<Section> sectionsByLine = sectionDao.findByLineId(line.getId());
            result.add(LineResponse.of(line, sectionsByLine));
        }
        return result;
    }

    public void updateLine(final Long id, final LineUpdateRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLineById(final Long id) {
        lineDao.deleteById(id);
    }

    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        // line 초기화
        Line line = lineDao.findById(lineId)
                .orElseThrow(NoSuchLineException::new);
        List<Section> sections = sectionDao.findByLineId(lineId);
        for (Section each : sections) {
            line.addSection(each);
        }

        // section 초기화
        Station upStation = stationDao.findById(sectionRequest.getUpStationId())
                .orElseThrow(NoSuchLineException::new);
        Station downStation = stationDao.findById(sectionRequest.getDownStationId())
                .orElseThrow(NoSuchLineException::new);
        Section section = sectionDao.save(lineId, new Section(upStation, downStation, sectionRequest.getDistance()));

        // section 추가
        line.addSection(section);

        // section 정보 업데이트
        sectionDao.batchUpdate(line.getSections());
    }
}
