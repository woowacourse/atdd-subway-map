package wooteco.subway.domain;

public class Station {
    private static final int NAME_SIZE_LIMIT = 255;
    private static final String ERROR_MESSAGE_NAME_SIZE = "존재할 수 없는 이름입니다.";
    
    private Long id;
    private final String name;

    public Station(Long id, String name) {
        validateNameSize(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        validateNameSize(name);
        this.name = name;
    }

    private void validateNameSize(String name) {
        if (name == null || name.isBlank() || name.length() > NAME_SIZE_LIMIT) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NAME_SIZE);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

