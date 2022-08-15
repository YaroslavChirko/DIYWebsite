package com.diyweb.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.diyweb.models.Cathegory;
import com.diyweb.models.Post;
import com.diyweb.models.User;
import com.diyweb.repo.PostRepoInterface;
import com.diyweb.repo.PostRepositoryImpl;

/**
 * Home page servlet, mainly used to dispatch index.jspx
 *
 * @author erick
 */
@WebServlet(urlPatterns = "/")
public class HomeServlet extends HttpServlet {
	
	@Inject
	PostRepoInterface postRepository;
	
	
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        
    	//req.getServletContext().setAttribute("typesBean", Cathegory.values());
    	
    	RequestDispatcher dispatcher = req.getRequestDispatcher("/jsp/index.jspx");
        
        req.setAttribute("posts", postRepository.getLastTenPostsByCathegories());
        req.setAttribute("typesBean", Cathegory.values());
        dispatcher.forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        this.doGet(req, resp);
    }
}
