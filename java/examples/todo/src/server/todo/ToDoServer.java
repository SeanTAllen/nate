package todo;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;


public class ToDoServer {
    public static void main(String[] args) throws Exception
    {
        String warPath = System.getProperty("war.path","java/examples/todo/target/todo.war");
        
        Server server = new Server(8080);
 
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(warPath);
        server.setHandler(webapp);
 
        server.start();
        server.join();
    }

}
