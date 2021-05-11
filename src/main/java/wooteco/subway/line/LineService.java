package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.SectionDto;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public LineResponse createLine(long upStationId, long downStationId, String lineName, String lineColor, int distance) {
        validateDuplicateName(lineName);
        Line line = lineDao.save(lineName, lineColor);
        final Station upStation = findStationById(upStationId);
        final Station downStation = findStationById(downStationId);
        final SectionDto sectionDto = sectionDao.save(line.getId(), upStation.getId(), downStation.getId(), distance);
        final Section section = generateSection(sectionDto);
        final Sections sections = new Sections(line.getId(), Arrays.asList(section));
        line.setSections(sections);
        return LineResponse.from(line);
    }

    public Section generateSection(SectionDto sectionDto) {
        final Station upStation = findStationById(sectionDto.getUpStationId());
        final Station downStation = findStationById(sectionDto.getDownStationId());
        return new Section(sectionDto.getLineId(), upStation, downStation, sectionDto.getDistance());
    }

    private void validateDuplicateName(String lineName) {
        final Optional<Line> lineWithSameName = lineDao.findByName(lineName);
        if (lineWithSameName.isPresent()) {
            throw new IllegalArgumentException("노선 이름이 중복됩니다.");
        }
    }

    private Station findStationById(long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }

    public List<LineResponse> showLines() {
        final List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            final Sections sections = findSectionsInLine(line.getId());
            line.setSections(sections);
        }
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse showLine(long lineId) {
        final Line line = findLineById(lineId);
        return LineResponse.from(line);
    }

    private Line findLineById(long lineId) {
        final Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id에 대응하는 노선이 없습니다."));
        final Sections sections = findSectionsInLine(lineId);
        line.setSections(sections);
        return line;
    }

    private Sections findSectionsInLine(long lineId) {
        final List<Section> sectionList = new ArrayList<>();
        final List<SectionDto> sectionDtoList = sectionDao.findByLineId(lineId);
        for (SectionDto sectionDto : sectionDtoList) {
            final Section section = generateSection(sectionDto);
            sectionList.add(section);
        }
        final Sections sections = new Sections(lineId, sectionList);
        return sections;
    }

    public void updateLine(long lineId, String lineName, String lineColor) {
        final Line line = findLineById(lineId);
        lineDao.update(line.getId(), lineName, lineColor);
    }

    public void deleteLine(long lineId) {
        final Line line = findLineById(lineId);
        lineDao.delete(line.getId());
    }

    public void createSection(long lineId, long upStationId, long downStationId, int distance) {
        final Line line = findLineById(lineId);
        final Station upStation = findStationById(upStationId);
        final Station downStation = findStationById(downStationId);
        line.insertSection(new Section(lineId, upStation, downStation, distance));
        sectionDao.save(lineId, upStationId, downStationId, distance);
    }

    public void deleteSection(long lineId, long stationId) {
        final Line line = findLineById(lineId);
        final Station station = findStationById(stationId);
        line.removeSection(station);
    }
}
