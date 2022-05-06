package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.ClientException;

public class FakeLineDao implements LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    @Override
    public Line save(LineRequest line) {
        Line persistLine = new Line(++seq, line.getName(), line.getColor());
        lines.add(persistLine);
        return persistLine;
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Line find(Long id) {
        return lines.stream()
                .filter(line -> line.getId() == id)
                .findAny()
                .orElseThrow(() -> new ClientException("존재하지 않는 노선입니다."));
    }

    @Override
    public int update(Long id, LineRequest line) {
        int targetIndex = IntStream.range(0, lines.size())
                .filter(index -> lines.get(index).getId() == id)
                .findAny()
                .orElseThrow(() -> new ClientException("존재하지 않는 노선입니다."));
        lines.set(targetIndex, new Line(lines.get(targetIndex).getId(), line.getName(), line.getColor()));
        if (lines.get(targetIndex).getName().equals(line.getName())) {
            return 1;
        }
        return 0;
    }

    @Override
    public int delete(Long id) {
        int beforeSize = lines.size();
        lines = lines.stream()
                .filter(line -> line.getId() != id)
                .collect(Collectors.toList());
        if (lines.size() < beforeSize) {
            return 1;
        }
        return 0;
    }
}
