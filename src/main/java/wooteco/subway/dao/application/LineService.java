package wooteco.subway.dao.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequestV2;
import wooteco.subway.dto.LineResponseV2;
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
    public LineResponseV2 createLine(final LineRequestV2 request) {
        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(NoSuchStationException::new);

        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(NoSuchStationException::new);

        Line createdLine = lineDao.save(new Line(request.getName(), request.getColor()));

        Section createdSection = sectionDao.save(createdLine.getId(),
                new Section(upStation, downStation, request.getDistance()));
        return LineResponseV2.of(createdLine, createdSection);
    }

    public LineResponseV2 findLine(final long id) {
        Line findLine = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        List<Section> sections = sectionDao.findByLineId(findLine.getId());
        return LineResponseV2.of(findLine, sections);
    }
}
