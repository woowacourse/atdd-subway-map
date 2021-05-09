package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Transactional
    public Line save (Line line, Station upStation, Station downStation, int distance){
        if (isDuplicatedName(line)) {
            throw new IllegalArgumentException(String.format("노선 이름이 중복되었습니다. 중복된 노선 이름 : %s", line.getName()));
        }
        if (isDuplicatedColor(line)) {
            throw new IllegalArgumentException(String.format("노선 색상이 중복되었습니다. 중복된 노선 색상 : %s", line.getColor()));
        }
        Line newLine = lineDao.save(line);
        Section section = new Section(newLine.getId(), Arrays.asList(upStation, downStation), new Distance(distance));
        Section newSection = sectionDao.save(section);
        return new Line(newLine.getId(), newLine.getName(), newLine.getColor(), new Sections(new ArrayList<>(Arrays.asList(newSection))));
    }

    private boolean isDuplicatedName (Line line){
        return lineDao.checkExistName(line.getName());
    }
    private boolean isDuplicatedColor (Line line){
        return lineDao.checkExistColor(line.getColor());
    }
    public List<Line> findAll () {
        return lineDao.findAll();
    }

    public Line findById (Long id){
        Sections sections = sectionDao.findByLineId(id);
        Line line = lineDao.findById(id);
        return new Line(line.getId(), line.getName(), line.getColor(), sections);
    }

    public void update (Line line){
        ifAbsent(line);
        lineDao.update(line);
    }
    public void delete (Line line){
        ifAbsent(line);
        lineDao.delete(line);
    }
    private void ifAbsent (Line line){
        if (!lineDao.checkExistId(line.getId())) {
            throw new IllegalArgumentException("노선이 존재하지 않습니다.");
        }
    }
    public void deleteAll () {
        lineDao.deleteAll();
    }
}
