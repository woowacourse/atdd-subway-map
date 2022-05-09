package wooteco.subway.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Sections {

    private final LinkedList<Section> value;

    public Sections(final LinkedList<Section> value) {
        validateSize(value);
        this.value = value;
    }

    private void validateSize(final LinkedList<Section> value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 최소 한 개의 구간이 있어야 객체를 생성할 수 있습니다.");
        }
    }

    public Sections(Section first) {
        this.value = new LinkedList<>();
        value.add(first);
    }

    public List<Section> getValue() {
        return Collections.unmodifiableList(value);
    }

    public void add(final Long id, final Section section) {
        value.add(id.intValue() - 1, section);
    }

    public int getSize() {
        return value.size();
    }
}
