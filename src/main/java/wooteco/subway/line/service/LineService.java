package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Lines;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.repository.LineDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.util.Collections;
import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(final LineDao lineDao, final SectionService sectionService, final StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public List<LineResponse> getLines() {
        Lines lines = lineDao.findAll();
        for (Line line : lines.toList()) {
            Sections sections = sectionService.findAll(line.getId());
            line.setSections(sections);
        }
        return LineResponse.toDtos(lines);
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        Line line = new Line(lineRequest.getColor(), lineRequest.getName());

        validateDuplicateName(line);
        Line newLine = lineDao.save(line);

        Section section = sectionService.save(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        Sections sections = getSections(section);

        return LineResponse.toDto(new Line(newLine.getId(), newLine.getColor(), newLine.getName(), sections));
    }

    private void validateDuplicateName(final Line line) {
        Lines lines = lineDao.findAll();
        if (lines.doesNameExist(line.getName())) {
            throw new DuplicateLineNameException();
        }
    }

    private Sections getSections(final Section section) {
        Station upStation = stationService.findById(section.getUpStationId());
        Station downStation = stationService.findById(section.getDownStationId());

        return new Sections(
                Collections.singletonList(
                        new Section(section.getId(), section.getLineId(), upStation, downStation, section.getDistance())
                )
        );
    }

    public LineResponse getLine(final Long id) {
        Line line = lineDao.findById(id).orElseThrow(NoSuchLineException::new);
        Sections sections = sectionService.findAll(id);
        line.setSections(sections);

        return LineResponse.toDto(line);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        Line line = new Line(id, lineRequest.getColor(), lineRequest.getName());
        validateId(id);
        lineDao.update(line);
    }

    @Transactional
    public void deleteById(final Long id) {
        validateId(id);
        lineDao.deleteById(id);
    }

    private void validateId(final Long id) {
        Lines lines = lineDao.findAll();
        if (lines.doesIdNotExist(id)) {
            throw new NoSuchLineException();
        }
    }
}
