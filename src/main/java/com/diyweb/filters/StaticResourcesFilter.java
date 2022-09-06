package com.diyweb.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/static/*")
public class StaticResourcesFilter implements Filter {
	
	private RequestDispatcher defaultDispatcher;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.defaultDispatcher = filterConfig.getServletContext().getNamedDispatcher("default");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		//this way we forward to static resources, this is needed to avoid home servlet redirect
		defaultDispatcher.forward(req, resp);
	}

}
