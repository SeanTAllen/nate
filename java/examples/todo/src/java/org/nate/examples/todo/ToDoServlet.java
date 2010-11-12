package org.nate.examples.todo;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

public class ToDoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Encoder HTML_ENCODER = Engine.encoders().encoderFor("HTML");
	
	private Engine LAYOUT;
	private Engine ADD;
	private Engine LIST;
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		LAYOUT = createEngineFor(config, "/WEB-INF/templates/layout.html");
		ADD = createEngineFor(config, "/WEB-INF/templates/add.html");
		LIST = createEngineFor(config, "/WEB-INF/templates/list.html");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().println(get());
	}


	@SuppressWarnings("unchecked")
	private String get() {
		Map<String, Object> todo1 = new HashMap<String, Object>();
		todo1.put(".title", "Title1");
		todo1.put("input[name=id]", singletonMap("value", "111"));
		Map<String, Object> todo2 = new HashMap<String, Object>();
		todo2.put(".title", "Title2");
		todo2.put("input[name=id]", singletonMap("value", "222"));
		List<Map<String, Object>> todoData = asList(todo1, todo2);
		Object data = singletonMap(".todo", todoData);
		Engine list = LIST.inject(singletonMap(".todolist", data)).select("content:#content");
		return LAYOUT.inject(singletonMap("#content", list)).render();
	}

	private Engine createEngineFor(ServletConfig config, String source) {
		InputStream inputStream = new BufferedInputStream(config.getServletContext().getResourceAsStream(source));
		try {
			return Engine.newWith(inputStream, HTML_ENCODER);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	

}
