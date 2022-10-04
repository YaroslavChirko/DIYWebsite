package com.diyweb.misc;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class PathVariableCaster {
	public static <T> T castPathVariableByName(Map<String, String> pathVariables, String variableName, Class<T> variableClass) throws ParseException {
		if(pathVariables == null || variableName == null || variableClass == null) {
			throw new NullPointerException("Passed arguments were null");
		}
		
		if(pathVariables.isEmpty()) {
			throw new IllegalArgumentException("Passed path variables was empty");
		}
		
		String pathVariableValueStr = pathVariables.get(variableName);
		if(pathVariableValueStr == null || pathVariableValueStr.trim().equals("")) {
			throw new IllegalArgumentException("Path variable found was null or empty");
		}
		
		T result = null;
		//call appropriate method for specific dataType
		if(Number.class.isAssignableFrom(variableClass)) {
			
			result = (T)castNumericPathVariableByName(pathVariableValueStr, variableName, (Class<Number>)variableClass);
		}
		
		if(variableClass.isEnum()) {
			result = (T)castEnumPathVariable(pathVariableValueStr, variableName, (Class<Enum>)variableClass);
		}
		
		return result;
	}
	
	private static <T extends Number> T castNumericPathVariableByName(String pathVariableValueStr, String variableName, Class<T> variableClass) throws ParseException{
		
		Object numericPathVariableObj = null;
		if(variableClass.equals(Integer.class)) {
			numericPathVariableObj =  NumberFormat.getIntegerInstance().parse(pathVariableValueStr).intValue();
		}else {
			numericPathVariableObj = NumberFormat.getNumberInstance().parse(pathVariableValueStr);
		}
		
		if(numericPathVariableObj.getClass().equals(variableClass)) {
			try {
				T numericPathVariable = variableClass.cast(numericPathVariableObj);
				return numericPathVariable;
			}catch (NumberFormatException e) {
				throw new NumberFormatException("Number path variable was not parsed correctly: "+e.getMessage());
			}
		}
		return null;
		
	}
	
	private static <T extends Enum<T>> T castEnumPathVariable(String pathVariableValueStr, String variableName, Class<T> variableClass) {
		try {
			return Enum.valueOf(variableClass, pathVariableValueStr);
		}catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("Passed value was not of proper enum type: "+e.getMessage());
		}
		
	}
}
