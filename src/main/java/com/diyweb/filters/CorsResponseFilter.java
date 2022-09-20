package com.diyweb.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/")
public class CorsResponseFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse resp = (HttpServletResponse)response;
		
		resp.addHeader("Access-Control-Allow-Origin", "http://localhost:8090");
		resp.addHeader("Access-Control-Allow-Credentials", "true");
		resp.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        resp.addHeader("Access-Control-Allow-Headers", "CSRF-Token, X-Requested-By, Content-Type, Accept");
        
        chain.doFilter(request, response);
	}

}
