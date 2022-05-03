import com.company.Ranker;
import com.company.queryProcessor;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;
import java.util.Map;

public class WebInterface extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = request.getParameter("q");

        String message = "you searched for " + name ;
        queryProcessor myq = new queryProcessor(name);
        List<String> output= myq.Run();
        StringBuilder myOutput=new StringBuilder();
        if(output!=null)
        {
            int n=output.size();
            for(String i:output)
            {
                myOutput.append("<a href='"+i+"'>"+i+"</a><br>");
            }
        }
        else
        {
            myOutput.append("<h1>not found</h1>");
        }
        response.setContentType("text/html");
        String page = "<!doctype html> <html> <body> <h1>" + message +" </h1><h2>kkkkkkkk top<h2>"+myOutput+"***************************** </body></html>";
        response.getWriter().println(page);
    }

}