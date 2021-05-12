package wooteco.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line create(final Line line) {
        validateName(line);

        final Long id = lineDao.save(line);
        sectionDao.save(id, line.getUpStationId(), line.getDownStationId(), line.getDistance());

        return findById(id);
    }

    public void update(final Line line) {
        final Long lineId = line.getId();

        final Line old = findById(lineId);
        if (old.isDifferentName(line)) {
            validateName(line);
        }

        lineDao.update(lineId, line.getName(), line.getColor());
    }

    public void delete(final Long id) {
        final Optional<Line> optionalLine = lineDao.findById(id);

        if(optionalLine.isPresent()){
            sectionDao.deleteAllSectionInLine(id);
            lineDao.delete(id);
            return;
        }

        throw new LineException("존재하지 않는 노선입니다.");
    }

    public Line findById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new LineException("존재하지 않는 노선입니다."));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    private void validateName(final Line line) {
        if (lineDao.isExistingName(line.getName())) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }

    public List<Long> allStationIdInLine(final Long lineId) {
        if(sectionDao.stationCountInLine(lineId) == 0){
            return Collections.EMPTY_LIST;
        }
        return findBackStations(lineId, lineDao.findUpStationId(lineId));
    }

    private List<Long> findBackStations(final Long lineId, Long frontStationId){
        final List<Long> stations = new LinkedList<>();

        while(!lineDao.isDownStation(lineId, frontStationId)){
            stations.add(frontStationId);
            Section next = sectionDao.findSectionByFrontStation(lineId, frontStationId);
            frontStationId = next.back();
        }
        stations.add(frontStationId);

        return stations;
    }
}
