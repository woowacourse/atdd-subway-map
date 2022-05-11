package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundLineException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse createLine(LineRequest line) {
        Line newLine = Line.from(line);
        validateDuplicateName(newLine);

        Line createdLine = lineDao.save(newLine);

        sectionDao.save(createdLine.getId(),
                new Section(line.getUpStationId(), line.getDownStationId(), new Distance(line.getDistance())));

        return LineResponse.from(createdLine, getStationResponsesByLineId(createdLine.getId()));
    }

    private void validateDuplicateName(Line line) {
        boolean isExisting = lineDao.findByName(line.getName()).isPresent();

        if (isExisting) {
            throw new DuplicateNameException();
        }
    }

    public List<LineResponse> getAllLines() {
        return lineDao.findAll()
                .stream()
                .map(line -> LineResponse.from(line, getStationResponsesByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse getLineById(Long id) {
        return lineDao.findById(id)
                .map(line -> LineResponse.from(line, getStationResponsesByLineId(id)))
                .orElseThrow(NotFoundLineException::new);
    }

    public void update(Long id, LineUpdateRequest line) {
        Line newLine = new Line(id, line.getName(), line.getColor());
        validateExistById(id);
        lineDao.update(id, newLine);
    }

    public void delete(Long id) {
        validateExistById(id);
        lineDao.deleteById(id);
    }

    private void validateExistById(Long id) {
        boolean isExisting = lineDao.findById(id).isPresent();

        if (!isExisting) {
            throw new NotFoundLineException();
        }
    }

    // TODO: 리팩토링
    private List<StationResponse> getStationResponsesByLineId(Long lineId) {
        List<Long> stationIds = getStationIdsByLineId(lineId);

        return stationIds.stream()
                .map(stationDao::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    private List<Long> getStationIdsByLineId(Long lineId) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);

        List<Long> stationIds = new ArrayList<>();
        for (Section section : sections.getValue()) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        stationIds = stationIds.stream().distinct().collect(Collectors.toList());
        return stationIds;
    }
}
