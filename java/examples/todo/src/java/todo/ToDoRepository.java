package todo;

import java.util.ArrayList;
import java.util.List;


// Fake repository.  Should be using a database :-)

public class ToDoRepository {

	private List<ToDo> toDos = new ArrayList<ToDo>();
	
	public synchronized void add(ToDo toDo) {
		toDos.add(toDo);
	}

	public synchronized List<ToDo> all() {
		return new ArrayList<ToDo>(toDos);
	}

	public synchronized void delete(long id) {
		for (ToDo todo : toDos) {
			if (todo.getId() == id) {
				toDos.remove(todo);
				return;
			}
		}
	}
	
	
}
