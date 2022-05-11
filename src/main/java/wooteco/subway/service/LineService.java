package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Line savedLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Station upStation = stationDao.findById(lineRequest.getUpStationId());
        Station downStation = stationDao.findById(lineRequest.getDownStationId());
        Section section = new Section(savedLine.getId(), upStation.getId(), downStation.getId(),
                lineRequest.getDistance());
        sectionDao.save(section);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(),
                List.of(new StationResponse(upStation), new StationResponse(downStation)));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, stationDao.findAll()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<Section> sections = sectionDao.findAllByLineId(id);
        List<Station> stations = getStationIds(sections).stream()
                .map(stationDao::findById)
                .collect(Collectors.toList());

        return LineResponse.of(line, stations);
    }

    private List<Long> getStationIds(List<Section> sections) {
        List<Long> stationIds = new ArrayList<>();

        Long upStationId = getUpStationId(sections);
        stationIds.add(upStationId);

        while (stationIds.size() != sections.size() + 1) {
            Long downStationId = getDownStationId(sections, upStationId);
            stationIds.add(downStationId);
            upStationId = downStationId;
        }

        return stationIds;
    }

    private Long getUpStationId(List<Section> sections) {
        List<Long> upStationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        List<Long> downStationIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());

        upStationIds.removeAll(downStationIds);
        return upStationIds.get(0);
    }

    private Long getDownStationId(List<Section> sections, Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getDownStationId();
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        lineDao.updateById(id, new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
