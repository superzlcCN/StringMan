package main.java;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Strman {
	private static final Predicate<String> NULL_STRING_PREDICATE = str->str == null;
	private static final Supplier<String> NULL_STRING_MSG_SUPPLIER = () -> "'value' should be not null";
	
	private Strman(){
		
	}
	
	public static String append(final String value,final String... appends){
		return appendArray(value, appends);
	}
	
	/**
	 * Append an array of String to value
	 * @param value initial String
	 * @param appends an arrays of strings to append
	 * @return full String
	 */
	public static String appendArray(final String value,final String[] appends){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		if (appends == null || appends.length == 0) {
			return value;
		}
		StringJoiner joiner = new StringJoiner("");
		for (int i = 0; i < appends.length; i++) {
			joiner.add(appends[i]);
		}
		return value + joiner.toString();
	}
	
	public static Optional<String> at(final String value, int index){
		if (value == null || value.isEmpty()) {
			return Optional.empty();
		}
		int length = value.length();
		if (index < 0) {
			index  = length + index;
		}
		return (index < length && index >= 0) 
				? Optional.of(String.valueOf(value.charAt(index)))
				: Optional.empty();
	}
	private static void validate(String value, Predicate<String> predicate,final Supplier<String> supplier){
		if (predicate.test(value)) {
			throw new IllegalArgumentException(supplier.get());
		}
	}
	
	private static long countSubstr(String value,String subSr,boolean allowOverlapping, long count){
		int position = value.indexOf(subSr);
		if (position == -1) {
			return count;
		}
		
		int offset
		if (!allowOverlapping) {
			offset += position + subSr.length();
		}else
			offset = position + 1;
		return countSubstr(value.substring(offset), subSr, allowOverlapping, ++count);
	}
	
}
