package wooteco.subway.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.SectionDto;

@Repository
public class LineRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineRepository(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Long save(Line line) {
        LineDto savedLine = lineDao.save(LineDto.from(line));
        List<SectionDto> sectionDtos = line.getSections().stream()
                .map(section -> SectionDto.of(section, savedLine.getId()))
                .collect(Collectors.toList());
        sectionDao.saveAll(sectionDtos);
        return savedLine.getId();
    }

    public Line findById(Long id) {
        LineDto lineDto = lineDao.findById(id);
        List<SectionDto> sectionDtos = sectionDao.findByLineId(id);
        ArrayList<Section> sections = new ArrayList<>();
        for (SectionDto sectionDto : sectionDtos) {
            Station up = stationDao.findById(sectionDto.getUpStationId());
            Station down = stationDao.findById(sectionDto.getDownStationId());
            sections.add(new Section(sectionDto.getId(), up, down, sectionDto.getDistance()));
        }
        return new Line(lineDto.getId(), lineDto.getName(), lineDto.getColor(), Sections.from(sections));
    }

    public List<Line> findAll() {
        return lineDao.findAll().stream()
                .map(lineDto -> findById(lineDto.getId()))
                .collect(Collectors.toList());
    }
}
