package todo;

import java.util.concurrent.atomic.AtomicLong;

public class ToDo {

	private static final AtomicLong sequence = new AtomicLong();
	
	private final String title;
	private final long id;

	public ToDo(String title) {
		this.id = sequence.incrementAndGet();
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public long getId() {
		return id;
	}
}
