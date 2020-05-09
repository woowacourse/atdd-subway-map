package wooteco.subway.admin.dto;

public class Request<T> {
	private T content;

	public Request() {
	}

	public Request(T content) {
		this.content = content;
	}

	public T getContent() {
		return content;
	}
}
