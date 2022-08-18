package com.diyweb.filters;

import java.io.IOException;
import java.util.Enumeration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Path;

//@WebFilter(urlPatterns = "/verify/*")
//@Path("/{pathIdentifier}/{pathEmail}")
public class VerificationFilter implements Filter {
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		String userIdentifier = (String)req.getAttribute("userIdentifier");
		String userEmail = (String)req.getAttribute("userEmail");
		
		System.out.println("Serv path: "+((HttpServletRequest)req).getServletPath());
		System.out.println("Cont path: "+((HttpServletRequest)req).getServletPath());
		System.out.println("Path info: "+((HttpServletRequest)req).getPathInfo());
		System.out.println("Path trans info: "+((HttpServletRequest)req).getPathTranslated());
	}

}
