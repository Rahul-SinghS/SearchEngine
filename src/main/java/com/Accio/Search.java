package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String keyword = request.getParameter("keyword");
        System.out.println(keyword);
        try {
            Connection connection = DatabaseConnection.getConnection();

            //9.2 entering the search keyword and searchLink in the history table of searchaccio database
            PreparedStatement preparedStatement=connection.prepareStatement("Insert into history values(?,?);");
            preparedStatement.setString(1,keyword);
            preparedStatement.setString(2,"http://localhost:8080/SearchEngine/Search?keyword="+keyword);
            preparedStatement.executeUpdate();

            //6.6 fetching the most related pages for our keyword from the pages table in searchaccio database
            ResultSet resultSet = connection.createStatement().executeQuery("select pageTitle,pageLink,(length(lower(pageData))-length(replace(lower(pageData),"+"\""+keyword+"\""+",\"\")))/length("+"\""+keyword+"\""+") as countoccurrences from pages order by countoccurrences desc limit 30;");
            //6.8 converting the resultSet into an arrayList
            ArrayList<SearchResult> results=new ArrayList<SearchResult>();
            while(resultSet.next()){
                SearchResult searchResult=new SearchResult();
                searchResult.setPageTitle(resultSet.getString("pageTitle"));
                searchResult.setPageLink(resultSet.getString("pageLink"));
                results.add(searchResult);
            }

            request.setAttribute("results",results);//7.1 sending the results value to the search.jsp to the frontend
            request.getRequestDispatcher("/search.jsp").forward(request,response);//7.1
            //response.setContentType("text/html");//3.5
           // PrintWriter out=response.getWriter();//3,5
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }catch (ServletException servletException){
            servletException.printStackTrace();
        }
    }
}
