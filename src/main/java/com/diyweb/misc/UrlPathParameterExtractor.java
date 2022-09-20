package com.diyweb.misc;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.ws.rs.Path;

public class UrlPathParameterExtractor {

	public static Map<String, String> processPathParameters(Class servlet, String Url){
		Map<String, String> result = new HashMap<>();
		
		WebServlet webServletAnn = (WebServlet) servlet.getAnnotation(WebServlet.class);
		Path pathAnn = (Path) servlet.getAnnotation(Path.class);
		
		String[] patterns= webServletAnn.urlPatterns();
		String path = pathAnn.value();
		
		String urlPartsStr = Url;
		for(String pattern: patterns) {
			if(Url.contains(pattern)) {				
				urlPartsStr = urlPartsStr.substring(urlPartsStr.indexOf(pattern)+pattern.length());
			}
		}
		
		String[] pathParts = path.split("/");
		pathParts = Arrays.stream(pathParts).filter(p -> !p.equals("")).toArray(String[]::new);
		
		String[] urlParts = urlPartsStr.split("/");
		urlParts = Arrays.stream(urlParts).filter(u -> !u.equals("")).toArray(String[]::new);
		
		int length = Math.min(pathParts.length, urlParts.length);
		for(int i = 0; i<length; i++) result.put(pathParts[i].substring(1, pathParts[i].length()-1), urlParts[i]);
		return result;
	}
	
	public static Map<Integer, String> checkPathParams(Map<String, String> pathParams){
		
	}
}
