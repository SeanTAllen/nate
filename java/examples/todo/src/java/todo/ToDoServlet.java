package todo;

import static java.util.Collections.singletonMap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nate.Encoder;
import org.nate.Engine;
import org.nate.Nate;

public class ToDoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Encoder HTML_ENCODER = Nate.encoders().encoderFor("XML");
	
	private Engine LAYOUT;
	private Engine ADD;
	private Engine LIST;
	
	private ToDoRepository toDoRepository = new ToDoRepository();
	
	private enum Action {GetTodos, NewTodo, AddTodo, DeleteTodo};
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		LAYOUT = createEngineFor(config, "/WEB-INF/templates/layout.html");
		ADD = createEngineFor(config, "/WEB-INF/templates/add.html").select("## #content");
		LIST = createEngineFor(config, "/WEB-INF/templates/list.html").select("## #content");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		Action action = extractAction(req);
		if (action == Action.GetTodos) {
			resp.getWriter().println(getToDos());
		} else {
			resp.getWriter().println(newToDo());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=utf-8");
		Action action = extractAction(req);
		if (action == Action.AddTodo) {
			addToDo(req);
		} else {
			removeToDo(req);
		}
		resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		resp.setHeader("Location", "/todo");
	}

	private void removeToDo(HttpServletRequest req) {
		long id = Long.valueOf(req.getParameter("id"));
		toDoRepository.delete(id);
	}

	private void addToDo(HttpServletRequest req) {
		String title = req.getParameter("title");
		toDoRepository.add(new ToDo(title));
	}

	private String newToDo() {
		return LAYOUT.inject(singletonMap("#content", ADD)).render();
	}

	private String getToDos() {
		List<ToDo> toDos = toDoRepository.all();
		List<Map<String, Object>> todoData = new ArrayList<Map<String,Object>>();
		for (ToDo toDo : toDos) {
			Map<String, Object> todoMap = new HashMap<String, Object>();
			todoMap.put(".title", toDo.getTitle());
			todoMap.put("input[name=id]", singletonMap("@@value", toDo.getId()));
			todoData.add(todoMap);
		}
		Object data = singletonMap(".todo", todoData);
		Engine list = LIST.inject(singletonMap(".todolist", data));
		return LAYOUT.inject(singletonMap("#content", list)).render();
	}


	private Action extractAction(HttpServletRequest req) {
		String requestURI = req.getRequestURI();
		if (requestURI.endsWith("new")) {
			return Action.NewTodo;
		}
		if (requestURI.endsWith("add")) {
			return Action.AddTodo;
		}
		if (requestURI.endsWith("finished")) {
			return Action.DeleteTodo;
		}
		return Action.GetTodos; 
	}

	private Engine createEngineFor(ServletConfig config, String source) {
		InputStream inputStream = new BufferedInputStream(config.getServletContext().getResourceAsStream(source));
		try {
			return Nate.newWith(inputStream, HTML_ENCODER);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	

}
