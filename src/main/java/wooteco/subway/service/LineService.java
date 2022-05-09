package wooteco.subway.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NoSuchLineException;
import wooteco.subway.exception.station.NoSuchStationException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse create(final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Line savedLine = lineDao.insert(line)
                .orElseThrow(DuplicateLineException::new);

        final Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(NoSuchStationException::new);
        final Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(NoSuchStationException::new);

        final Section section = new Section(
                savedLine.getId(),
                upStation.getId(),
                downStation.getId(),
                request.getDistance());
        sectionDao.insert(section);

        return LineResponse.of(savedLine, List.of(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> {
                    final List<Station> stations = stationDao.findAllByLineId(line.getId());
                    return LineResponse.of(line, stations);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long id) {
        final Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        final List<Section> sections = sectionDao.findAllByLineId(line.getId());
        final Long upEndStationId = findUpEndStationId(sections);
        return LineResponse.of(line, findAllStation(sections, upEndStationId));
    }

    private Long findUpEndStationId(List<Section> s) {
        final List<Section> sections = new ArrayList<>(s);
        final Map<Long, Integer> countByStationId = new HashMap<>();
        for (Section section : sections) {
            countByStationId.put(section.getUpStationId(), countByStationId.getOrDefault(section.getUpStationId(), 0) + 1);
            countByStationId.put(section.getDownStationId(), countByStationId.getOrDefault(section.getDownStationId(), 0) + 1);
        }
        Long result = null;
        for (Entry<Long, Integer> entry : countByStationId.entrySet()) {
            if (entry.getValue().equals(1)) {
                for (Section section : sections) {
                    if (!section.contains(entry.getKey())) {
                        continue;
                    }
                    if (section.getUpStationId().equals(entry.getKey())) {
                        result = entry.getKey();
                        break;
                    }
                }
            }
        }
        return result;
    }

    private List<Station> findAllStation(final List<Section> sections, final Long firstStationId) {
        final List<Station> stations = new ArrayList<>();

        stations.add(stationDao.findById(firstStationId).orElseThrow(NoSuchStationException::new));

        Long targetStationId = null;
        for (Section section : sections) {
            if (section.getUpStationId().equals(firstStationId)) {
                targetStationId = section.getDownStationId();
                stations.add(stationDao.findById(section.getDownStationId()).orElseThrow(NoSuchStationException::new));
                break;
            }
        }
        while (stations.size() != sections.size() + 1) {
            for (Section targetSection : sections) {
                if (targetSection.getUpStationId().equals(targetStationId)) {
                    stations.add(stationDao.findById(targetSection.getDownStationId()).orElseThrow(NoSuchStationException::new));
                    targetStationId = targetSection.getDownStationId();
                }
            }
        }

        return stations;
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        line.updateName(request.getName());
        line.updateColor(request.getColor());
        lineDao.updateById(id, line)
                .orElseThrow(DuplicateLineException::new);
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
