package com.diyweb.misc;

import jakarta.servlet.http.HttpSession;

public class SessionAttributeRetriever {
	public static <T> T getAttributeByName(HttpSession session, String attributeName, Class<T> attributeClass) {
		Object attributeObj = session.getAttribute(attributeName);
		if(attributeObj == null) {
			throw new IllegalArgumentException("Attribute for provided name wasn\'t found");
		}
		
		T attribute = null;
		if(attributeObj.getClass().equals(attributeClass)) {
			attribute = (T)attributeObj;
		}
		
		return attribute;
	}
}
