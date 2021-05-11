package wooteco.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.SectionDao;

import java.util.LinkedList;
import java.util.List;

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
        validateName(line.getName());

        final Long id = lineDao.save(line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId());
        sectionDao.save(id, line.getUpStationId(), line.getDownStationId(), line.getDistance());

        return findById(id);
    }

    public void update(final Line line) {
        final Long id = line.getId();

        validateExisting(id);

        final String newName = line.getName();
        final String oldName = findById(id).getName();

        if (!oldName.equals(newName)) {
            validateName(newName);
        }

        lineDao.update(line.getId(), line.getName(), line.getColor());
    }

    public void delete(final Long id) {
        validateExisting(id);

        lineDao.delete(id);
    }

    public Line findById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new LineException("존재하지 않는 노선입니다."));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Long upStationId(final Long id) {
        return lineDao.findUpStationId(id);
    }

    public Long downStationId(final Long id) {
        return lineDao.findDownStationId(id);
    }

    private void validateName(final String name) {
        if (lineDao.isExistingName(name)) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }

    private void validateExisting(final Long id) {
        findById(id);
    }

    public List<Long> allStationIdInLine(final Long lineId) {
        final List<Long> stations = new LinkedList<>();

        Long stationId = lineDao.findUpStationId(lineId);
        do {
            stations.add(stationId);
            stationId = sectionDao.backStationIdOf(lineId, stationId);
        } while (sectionDao.isExistingFrontStation(lineId, stationId));

        stations.add(stationId);

        return stations;
    }
}
