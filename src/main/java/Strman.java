package main.java;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.sun.java_cup.internal.runtime.virtual_parse_stack;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaParsingConfig;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

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
	/**
	 * 注释：先介绍函数的功能，再说明参数的范围，最后说明返回值
	 * Get the character at index.This method will take care of negative indexes.
	 * The valid value of index is between -(length-1) to (length-1)
	 * For values which don't fall under this range Optional.empty will be returned
	 * @param value input value
	 * @param index location
	 * @return an Optional String if found else empty
	 */
	public static Optional<String> at(final String value, int index){
		if (value == null || value.isEmpty()) {
			return Optional.empty();
		}
		int length = value.length();
		// index < 0代表从末尾开始计数（倒数）
		if (index < 0) {
			index  = length + index;
		}
		return (index < length && index >= 0) 
				? Optional.of(String.valueOf(value.charAt(index)))
				: Optional.empty();
	}
	
	/**
	 * Returns an array with strings between start and end
	 * @param value input
	 * @param start start
	 * @param end end
	 * @return Array containing different parts between start and end.
	 */
	public static String[] between(final String value,final String start,final String end){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
	
		String[] parts = value.split(end);
		return Arrays.stream(parts).map(subpart -> subpart.substring(subpart.indexOf(start) + start.length())).toArray(String[]::new);
	}
	
	/**
	 * Returns a String array consisting of the character in the String
	 * @param value input
	 * @return character array
	 */
	public static String[] chars(final String value) {
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return value.split("");
	}
	/**
	 * Replace consecutive whitespace characters with a single space.
	 * @param value input String
	 * @return collapsed String
	 */
	public static String collapseWhitespace(final String value){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return value.trim().replaceAll("\\s\\s", " ");
	}
	
	/**
	 * Verifies that the needle is contained in the value,The search is case sensitive
	 * @param value to search
	 * @param needle	to find
	 * @return true if found else false
	 */
	public static boolean contains(final String value,final String needle){
		return contains(value, needle,false);
	}
	/**
	 * Verifies that the needle is contained in the value.
	 * @param value	to Search
	 * @param needle	to find
	 * @param caseSensitive true or false
	 * @return true if found else false
	 */
	public static boolean contains(final String value,final String needle,final boolean caseSensitive){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		if (caseSensitive) {
			return value.contains(needle);
		}
		return value.toLowerCase().contains(needle.toLowerCase());
	}	
	/**
	 * Verifies that all needles are contained in value. The search is case insensitive
	 * @param value input String to search
	 * @param needles needles to find
	 * @return true if all needles are found else false
	 */
	public static boolean containsAll(final String value,final String[] needles){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return Arrays.stream(needles).allMatch(needle -> contains(value,needle,true));
	}
	/**
	 * Verifies that all needles are contained in value
	 * @param value	input String to search
	 * @param needles	needles to find
	 * @param caseSensitive true or false
	 * @return true if all needles are found else false
	 */
	public static boolean containsAll(final String value, final String[] needles,final boolean caseSensitive){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return Arrays.stream(needles).allMatch(needle -> contains(value, needle,caseSensitive));
	}
	
	/**
	 * Verifies that any needles are contained in value.This is case insenstive
	 * @param value input String to search
	 * @param needles 	needles to find
	 * @return	true if any needle is contained in value else false
	 */
	public static boolean containsAny(final String value, final String[] needles){
		return containsAny(value, needles,false);
	}
	
	/**
	 * Verify that one or more needles are contained in value
	 * @param value		input
	 * @param needles	needles to search
	 * @param caseSensitive	true or false
	 * @return	boolean true if any needle is found else false
	 */
	public static boolean containsAny(final String value,final String[] needles, final boolean caseSensitive){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return Arrays.stream(needles).anyMatch(needle->contains(value, needle,caseSensitive));
	}
	
	/**
	 * Count the number of times substr appears in value
	 * @param value	input 
	 * @param subStr	to Search
	 * @return	count of times substring exists
	 */
	public static long countSubstr(final String value,final String subStr){
		return countSubstr(value, subStr,false,false);
	}
	/**
	 * Count the number of times substr appears in value
	 * @param value	input
	 * @param subStr	to Search
	 * @param caseSensitive	true or false
	 * @param allowOverlapping	true of false
	 * @return	count of times substr appears
	 */
	public static long countSubstr(final String value, final String subStr,final boolean caseSensitive,boolean allowOverlapping){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return countSubstr(caseSensitive?value:value.toLowerCase(),caseSensitive?subStr:subStr.toLowerCase(),allowOverlapping,0);
	}
	/**
	 * Verifies if the value ends with search.The search is case sensitive.
	 * @param value		input string
	 * @param search	string to search
	 * @return	true of false
	 */
	public static boolean endsWith(final String value,final String search){
		return endsWith(value, search,true);
	}
	/**
	 * Verifies if value ends with search 
	 * @param value input string
	 * @param search string to search
	 * @param caseSensitive true of false
	 * @return true of false
	 */
	public static boolean endsWith(final String value,final String search,final boolean caseSensitive) {
		return endsWith(value, search, value.length(),caseSensitive);
	}
	/**
	 * Test if value end with end search
	 * @param value	input string
	 * @param search string to search
	 * @param position	position till which you want to  search
	 * @param caseSensitive true of fasle
	 * @return	true or false
	 */
	public static boolean endsWith(final String value, final String search, final int position,final boolean caseSensitive){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		if (caseSensitive) {
			return value.indexOf(search,position-search.length()) > -1;
		}
		
		return value.toLowerCase().indexOf(search.toLowerCase(),position-search.length()) > -1;
	}
	
	/**
	 * Ensures that the value begins with prefix. If it doesn't exist,it's prepended.It is case sensitive.
	 * @param value		input
	 * @param prefix	prefix
	 * @return	string with prefix if it was not present.
	 */
	public static String ensureLeft(final String value,final String prefix){
		return ensureLeft(value, prefix,true);
	}
	/**
	 * Ensures that the value begins with prefix.If it doesn't exist,it's prepended
	 * @param value		input
	 * @param prefix	prefix
	 * @param caseSensitive	true of false
	 * @return	string with prefix if it was not present.
	 */
	public static String ensureLeft(final String value, final String prefix,final boolean caseSensitive){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		if (caseSensitive) {
			return value.startsWith(prefix)?value:(prefix+value);
		}
		
		String _value = value.toLowerCase();
		String _prefix = prefix.toLowerCase();
		return _value.startsWith(_prefix)? value: prefix+value;
	}
	
	/**
	 * Decodes data with MIME base 64
	 * @param value The data to decode
	 * @return	decoded data
	 */
	public static String base64Decode(final String value){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return new String(Base64.getDecoder().decode(value));
	}
	
	/**
	 * Encodes data with MIME base 64
	 * @param value	The data to encode
	 * @return	encoded String
	 */
	public static String base64Encode(final String value){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return Base64.getEncoder().encodeToString(value.getBytes());
	}
	
	/**
	 * Convert binary unicode(16 digits)string to string chars
	 * @param value The value to decode
	 * @return	The decoded String
	 */
	public static String binDecode(final String value){
		return decode(value, 16, 2);
	}
	
	/**
	 * Convert string chars to binary unicode (16 digits)
	 * @param value
	 * @return
	 */
	public static String binEncode(final String value){
		return encode(value, 16, 2);
	}
	
	 /**
     * Convert decimal unicode (5 digits) string to string chars
     *
     * @param value The value to decode
     * @return decoded String
     */
    public static String decDecode(final String value) {
        return decode(value, 5, 10);
    }

    /**
     * Convert string chars to decimal unicode (5 digits)
     *
     * @param value The value to encode
     * @return Encoded value
     */
    public static String decEncode(final String value) {
        return encode(value, 5, 10);
    }
    /**
     * Ensures that the value ends with suffix.If it doesn't , it's appended.
     * @param value	The input String
     * @param suffix	The substr to be ensured to be right
     * @return	The string which is guarenteed to end with suffix.
     */
    public static String ensureRight(final String value, final String suffix) {
    	return ensureRight(value, suffix, true);
    }
    
    /**
     * Returns the first n chars of String
     * @param value		The input string
     * @param n		The length of chars to return 
     * @return	The first n chars
     */
    public static Optional<String> first(final String value,final int n){
    	return Optional.ofNullable(value).filter(v->!v.isEmpty()).map(v -> v.substring(0,n));
    }
    /**
     * Return the first char of String
     * @param value The input String
     * @return The first char
     */
    public static Optional<String> head(final String value){
    	return first(value, 1);
    }
    /**
     * Formats a string using parameters
     * @param value		The value to be formatted
     * @param params	Parameters to be described in the string
     * @return	The formatted string
     */
    public static String format(final String value,String... params) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	Pattern p = Pattern.compile("\\{(\\w+)\\}");
    	Matcher m = p.matcher(value);
    	String result = value;
    	while(m.find()){
    		int paramNumber = Integer.parseInt(m.group(1));
    		if (params == null || paramNumber >= params.length) {
				throw new IllegalArgumentException("parmas does not have value for " + m.group());
			}
    		result = result.replace(m.group(), params[paramNumber]);
    	}
    	return result;
    }
    
    /**
     * Convert hexadecimal unicode (4 digits) string to string chars
     * @param value The value to decode
     * @return	The decoded String
     */
    public static String hexDecode(final String value){
    	return decode(value, 4, 16);
    }
    
    /**
     * Convert string chars to hexadecimal unicode (4 digits)
     * @param value		The value to encode
     * @return	String	in hexadecimal format
     */
    public static String hexEncode(final String value) {
    	return encode(value, 4, 16);
    }
    
    public static int indexOf(final String value,final String needle,int offset, boolean caseSensitive) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (caseSensitive) {
			return value.indexOf(needle,offset);
		}
    	return value.toUpperCase().indexOf(needle.toLowerCase(),offset);
    }
    
    /**
     * Tests if two Strings are inequal
     * @param first		The first String
     * @param second	The second String
     * @return	true 	if first and second are not equal false otherwise
     */
    public static boolean unequal(final String first,final String second) {
    	return !Objects.equals(first, second);
    }
    
    /**
     * Inserts 'substr' into the 'value' at the 'index' provided
     * @param value		The input String
     * @param substr	The string to insert
     * @param index		The index to insert substr
     * @return	String with substr added
     */
    public static String insert(final String value, final String substr, final int index) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	validate(substr, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (index > value.length()) {
			throw new IllegalArgumentException();
		}
    	return append(value.substring(0,index), substr,value.substring(index));
    }
    
    /**
     * Verifies if String is lower case
     * @param value		The input String
     * @return	true 	if String is lowercase false otherwise
     */
    public static boolean isUpperCase(final String value) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	for (int i = 0;  i < value.length(); i++){
    		if (!Character.isUpperCase(value.charAt(i))) {
				return false;
			}
    	}
    	
    	return true;
    }
    
    /**
     * Return the last n chars of String
     * @param value The input String
     * @param n		Number of chars to return 
     * @return	n Last characters
     */
    public static String last(final String value,int n){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (n>value.length()) {
			throw new IllegalArgumentException();
		}
    	
    	return value.substring(value.length()-n);
    }
    
    /**
     * Returns a new string of a given length such that the beginning of the string is padded.
     * @param value		The input String
     * @param pad		The pad
     * @param length	Length of teh String we want
     * @return	Padded String
     */
    public static String leftPad(final String value, final String pad,final int length) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	validate(pad, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (value.length() > length) {
			throw new IllegalArgumentException();
		}
    	return append(repeat(pad, length - value.length()), value);
    }
    
    /**
     * Checks wheter Object is String
     * @param value		The input String
     * @return	true	if Object is a String false otherwise
     */
    public static boolean isString(final Object value) {
		if (Objects.isNull(value)) {
			throw new IllegalArgumentException("value can't be null");
		}
		return value instanceof String;
	}
    
    /**
     * This method returns the index within the calling String object of the last occurrence of the specified value,searching backwards from the offset.
     * Returns -1 if the value is not found. The search starts from the end and case sensitive.
     * @param value		The input String
     * @param needle	The search String
     * @return	Return position of the last occurence of 'needle'.
     */
    public static int lastIndexOf(final String value,final String needle){
    	return lastIndexOf(value, needle,true);
    }
    
    /**
     * This method returns the index within the calling String object of the last occurence of the specified value,searching backwards from the offset.
     *	Returns -1 if the value if not found.
     * @param value		The input String
     * @param needle	The search String
     * @param caseSensitive	whether search should be case sensitive
     * @return	Return position of the last occurence of 'needle'
     */
    public static int lastIndexOf(final String value,final String needle, final boolean caseSensitive) {
    	return lastIndexOf(value, needle,value.length(),caseSensitive);
    }
    /**
     * Returns the index within the calling String object of the last occurence of the specified value, searching backwards from the offset.
     * Returns -1 if the value is not found.
     * @param value		The input String
     * @param needle	The search String
     * @param offset	The index to start search from
     * @param caseSensitive		Whether search should be case sensitive.
     * @return	Return position of the last occurence of 'needle'
     */
    public static int lastIndexOf(final String value, final String needle, final int offset, final boolean caseSensitive) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	validate(needle, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (caseSensitive) {
			return value.lastIndexOf(needle,offset);
		}
    	return value.toLowerCase().lastIndexOf(needle.toLowerCase(),offset);
    }
    
    /**
     * Removes all spaces on left
     * @param value		The input string
     * @return	String without left border spaces
     */
    public static String leftTrim(final String value){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return value.replaceAll("^\\s+", "");
    }
    
    /**
     * Returns length of String.Delegates to java.lang.String length method.
     * @param value	The input String
     * @return	Lenght of the String
     */
    public static int length(final String value){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return value.length();
    }
    
    /**
     * Returns a new String starting with prepends
     * @param value		The input String
     * @param prepends	Strings to prepend
     * @return	The prepended String
     */
    public static String prepend(final String value,final String... prepends){
    	return prependArray(value,prepends);
    }
    
    /**
     * Return a new String starting with prepends
     * @param value		The input String
     * @param prepends	STrings to prepend
     * @return	The prepended String
     */
    public static String prependArray(final String value, final String[] prepends) {
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		if (prepends == null || prepends.length == 0) {
			return value;
		}
		
		StringJoiner joiner = new StringJoiner("");
		for(String prepend : prepends)
			joiner.add(prepend);
		
		return joiner.toString() + value;
	}
    /**
     * Remove empty Strings from string array
     * @param strings	Array of String to be cleaned
     * @return	Array of String without empty Strings
     */
    public static String[] removeEmptyStrings(String[] strings) {
    	if (Objects.isNull(strings)) {
			throw new IllegalArgumentException("Input array should not be null");
		}
    	
    	return Arrays.stream(strings).filter(str -> str!= null && !str.trim().isEmpty()).toArray(String[]::new);
    }
    
    /**
     * Returns a new String with the prefix removed,if presnet.This is case sensitive.
     * @param value		The input String
     * @param prefix	String to remove on left
     * @return	The String without prefix
     */
    public static String removeLeft(final String value,final String prefix) {
    	return removeLeft(value, prefix,true);
    }
    
    /**
     * Returns a new String with the prefix removed,if presnet.
     * @param value		The input String
     * @param prefix	String to remove on left
     * @param caseSensitive	ensure case sensitivity
     * @return	The String without prefix
     */
    public static String removeLeft(final String value, final String prefix, final boolean caseSensitive){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (caseSensitive) {
			return value.startsWith(prefix)?value.substring(prefix.length()):value;
		}
    	return value.toLowerCase().startsWith(prefix.toLowerCase())?value.substring(prefix.length()):value;
    }
    
    /**
     * Returns a repeated string given a multiplier
     * @param value		The input String
     * @param multiplier	Number of repeats
     * @return	The String repeated
     */
    public static String repeat(final String value,final int multiplier) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return Stream.generate(() -> value).limit(multiplier).collect(Collectors.joining());
    }
    /**
     * Replace all occurrences of 'search' value to 'newValue'.Uses String replace method.
     * @param value	The input 
     * @param search	The String to Search
     * @param newValue	The String to replace
     * @param caseSensitive	wether search should be case sensitive or not
     * @return	String replaced with 'newValue'
     */
    public static String replace(final String value, final String search,final String newValue,final boolean caseSensitive) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	validate(search, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (caseSensitive) {
			return value.replace(search, newValue);
		}
    	return Pattern.compile(search,Pattern.CASE_INSENSITIVE)
    			.matcher(value)
    			.replaceAll(Matcher.quoteReplacement(newValue));
    }
    
    /**
     * Reverse the input String
     * @param value	The input String
     * @return	Reversed String
     */
    public static String reverse(final String value) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return new StringBuilder(value).reverse().toString();
    }
    
    /**
     * Returns a new string of a given length such that the ending of the string is padded.
     * @param value	The input String
     * @param pad	Character to repeat	
     * @param length	Max length of String
     * @return	Right padded String
     */
    public static String rightPad(final String value,String pad,final int length) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (value.length() > length) {
			return value;
		}
    	return append(value, repeat(pad, length - value.length()));
    }
    
    /**
     * Remove all spaces on right
     * @param value	The String
     * @return	String without right boarders spaces.
     */
    public static String rightTrim(final String value){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return value.replaceAll("\\s+$", "");
    }
    /**
     * Truncate the string securely,not cutting a word in half.It always returns the last full word
     * @param value	The input string
     * @param length	Max size of the truncated string
     * @param filler	String that will be added to the end of the return string. Examplee: '...'
     * @return	The truncated String
     */
    public static String safeTruncate(final String value,final int length,final String filler) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (length == 0) {
			return "";
		}
    	if (length >= value.length()) {
			return value;
		}
    	
    	String[] words = words(value);
    	StringJoiner result = new StringJoiner(" ");
    	int spaceCount = 0;
    	for(String word:words){
    		if (result.length() + word.length() + filler.length() + spaceCount > length) {
				break;
			}else{
				result.add(word);
				spaceCount++;
			}
    	}
    	return append(result.toString(), filler);
    }
    
    /**
     * Alias to String split function.Defined only for completeness.
     * @param value	The input String
     * @param regex	The delimiting regular expression
     * @return	String Array
     */
    public static String[] split(final String value,final String regex) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return value.split(regex);
    }
    /**
     * Split a string to words
     * @param value	The input string
     * @return	words array
     */
    public static String[] words(final String value){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return value.split("\\W+");
    }
    /**
     * Truncate the unsecured form string,cutting the independent string of required position.
     * @param value		Value will be truncated unsecurely
     * @param length	Size of the returned string.
     * @param filler	Value that will be added to the end of the return string. Example: '...'
     * @return	String truncated unsafely
     */
    public static String truncate(final String value, final int length,final String filler){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	if (length == 0) {
			return "";
		}
    	if (length >= value.length()) {
			return value;
		}
    	
    	return append(value.substring(0,length-filler.length()), filler);
    }
    /**
     * Converts all HTML entities to applicable characters
     * @param encodedHtml
     * @return
     */
    public static String htmlDecode(final String encodedHtml){
    	validate(encodedHtml, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	String[] entities = encodedHtml.split("$\\W+;");
    	return Arrays.stream(entities).map(e -> HtmlEntities.decodedEntities.get(e)).collect(Collectors.joining());
    }
    /**
     * Convert all applicable characters to HTML entities
     * @param html	The HTML to encode
     * @return	The encoded data
     */
    public static String htmlEncode(final String html) {
    	validate(html, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return html
    			.chars().
    			mapToObj(c -> "\\u" + String.format("%04x", c).toUpperCase())
    			.map(HtmlEntities.encodedEntities::get)
    			.collect(Collectors.joining());
    }
    
    /**
     * It returns a string with its characters in random order.
     * @param value
     * @return
     */
    public static String shuffle(final String value){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	String[] chars = chars(value);
    	Random random = new Random();
    	for (int i = 0; i < chars.length; i++) {
			int r = random.nextInt(chars.length);
			String tmp = chars[i];
			chars[i] = chars[r];
			chars[r] = tmp;
		}
    	return Arrays.stream(chars).collect(Collectors.joining());
    }
    
    /**
     * Alias of substring method
     * @param value	The input String
     * @param begin	Start of slice
     * @param end	End of slice
     * @return	The String sliced!
     */
    public static String slice(final String value,int begin,int end){
    	validate(value, NULL_STRING_PREDICATE,NULL_STRING_MSG_SUPPLIER);
    	return value.substring(begin,end);
    }
    
    /**
     * Convert a String to a slug
     * @param value	The value to slugify
     * @return	The slugified value
     */
    public static String slugify(final String value){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	String transliterated = transliterate(value);
    	return Arrays.stream(words(transliterated.replace("&", "-and-"))).collect(Collectors.joining("-"));
    }
    
    /**
     * Remove all invalid characters
     * @param value		The input String
     * @return	String without non valid characters
     */
    public static String transliterate(final String value){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	String result = value;
    	Set<Map.Entry<String, List<String>>> entries = Ascii.ascii.entrySet();
    	for(Map.Entry<String, List<String>> entry : entries){
    		for (String ch:entry.getValue()){
    			result = result.replace(ch, entry.getKey());
    		}
    	}
    	return result;
    }
    
    public static String surround(final String value,final String prefix,final String suffix) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	String _prefix = Optional.ofNullable(prefix).orElse("");
    	return append(_prefix, value,Optional.ofNullable(suffix).orElse(_prefix));
    }
    
    /**
     * Transform to camelCase
     * @param value	The input String
     * @return	String in camelCase
     */
    public static String toCamelCase(final String value) {
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	String str = toStudlyCase(value);
    	return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
    
    /**
     * Transform to StudlyCaps
     * @param value	The input String
     * @return String in StudlyCaps
     */
    
    public static String toStudlyCase(final String value){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	String[] words = collapseWhitespace(value.trim()).split("\\s*(_|-|\\s)\\s*");
    	return Arrays.stream(words)
    			.filter(w -> !w.trim().isEmpty())
    			.map(Strman::upperFirst)
    			.collect(Collectors.joining());
    }
    /**
     * Return tail of the String
     * @param value	The input String
     * @return	String tail
     */
    public static Optional<String> tail(final String value) {
    	return Optional.ofNullable(value).filter(v->!v.isEmpty()).map(v->last(v, v.length()-1));
    }
    
    /**
     * Decamelize String
     * @param value	The input String
     * @param chr	string to use
     * @return	String decamelized.
     */
    public static String toDecamelize(final String value, final String chr) {
    	String camelCasedString = toCamelCase(value);
    	String[] words = camelCasedString.split("(?=\\p{Upper})");
    	return Arrays.stream(words)
    			.map(String::toLowerCase)
    			.collect(Collectors.joining(Optional.ofNullable(chr).orElse(" ")));
    }
    
    /**
     * Ensures that the value ends with suffix.If it doesn't,it's appended.
     * @param value	The input String
     * @param suffix	The substr to be ensured to be right
     * @param caseSensitive	true of false
     * @return	The string which is guranteed to be end with suffix
     */
    public static String ensureRight(final String value, final String suffix,boolean caseSensitive){
    	validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
    	return endsWith(value, suffix,caseSensitive)? value : append(value, suffix);
    }
    
  
    
	public static String decode(final String value, final int digits,final int radix){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return Arrays
				.stream(value.split("(?<=\\G.{" + digits +"})"))
				.map(data -> String.valueOf(Character.toChars(Integer.parseInt(data,radix))))
				.collect(Collectors.joining());
	}
	
	public static String encode(final String value, final int digits,final int radix){
		validate(value, NULL_STRING_PREDICATE, NULL_STRING_MSG_SUPPLIER);
		return value.chars().mapToObj(ch -> leftPad(Integer.toString(ch,radix),"0",digits)).collect(Collectors.joining());
	}
	
	/**
	 * Joined concatenates all the elements of the strings array into a single string.The separator string is between elements in the resulting string.
	 * @param strings	The input array to concatenate
	 * @param separator	The separtor to use
	 * @return	Concatenated String
	 * @throws IllegalArgumentException
	 */
	public static String join(final String[] strings,final String separator) throws IllegalArgumentException{
		if (strings == null) {
			throw new IllegalArgumentException("Input array 'strings' can't be null");
		}
		
		if (separator == null) {
			throw new IllegalArgumentException("separtor can'be null");
		}
		
		StringJoiner joiner = new StringJoiner(separator);
		for(String el : strings){
			joiner.add(el);
		}
		return joiner.toString();
	}
	/**
	 * Converts the first character of string to upper case and the remaining to lower case.
	 * @param input	The string to capitalize
	 * @return The capitalized string
	 * @throws IllegalArgumentException
	 */
	public static String capitalize(final String input) throws IllegalArgumentException{
		if (input == null) {
			throw new IllegalArgumentException("input can't be null");
		}
		
		if (input.length() == 0) {
			return "";
		}
		
		return head(input)
				.map(String::toUpperCase)
				.map(h -> 
						tail(input).map(t -> h + t.toLowerCase()).orElse(h)
						).get();
	}
	/**
	 * Converts the first character of string to lower case.
	 * @param input
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String lowerFirst(final String input) throws IllegalArgumentException{
		if (input == null) {
			throw new IllegalArgumentException("input can't be null");
		}
		if (input.length() == 0) {
			return "";
		}
		
		return head(input)
				.map(String::toLowerCase)
				.map(h -> tail(input).map(t -> h + t).orElse(h))
				.get();
	}
	
	/**
	 * Verfifies whether String is encloser by encloser
	 * @param input	The input String
	 * @param encloser	String which encloses input String
	 * @return		true if enclosed flase otherwise
	 */
	public static boolean isEnclosedBetween(final String input, final String encloser){
		return isEnclosedBetween(input, encloser,encloser);
	}
	
	/**
	 * Validates whether input is enclosed between leftEncloser and rightEncloser
	 * @param input		The input String
	 * @param leftEncloser	String which encloses input String at left start
	 * @param rightEncloser	String which encloses input String at right end
	 * @return	true if enclosed false otherwise
	 */
	public static boolean isEnclosedBetween(final String input,final String leftEncloser,String rightEncloser){
		if (input == null) {
			throw new IllegalArgumentException("input can't be null");
		}
		
		if (leftEncloser == null) {
			throw new IllegalArgumentException("leftEncloser can't be null");
		}
		
		if (rightEncloser == null) {
			throw new IllegalArgumentException("rightEncloser can't be null");
		}
		
		return input.startsWith(leftEncloser) && input.endsWith(rightEncloser);
	}
	
	/**
	 * Converts the first character of string to upper case.
	 * @param input		The String to convert.
	 * @return	Returns the converted string.
	 */
	public static String upperFirst(String input){
		if (input == null) {
			throw new IllegalArgumentException("input can't be null");
		}
		return head(input).map(String::toUpperCase)
				.map(h -> tail(input).map(t -> h + t).orElse(h))
				.get();
	}
	/**
	 * Removes leading whitespace from string.
	 * @param input		The string to trim.
	 * @return	Returns the trimmed string.
	 */
	public static Optional<String> trimStart(final String input) {
		return Optional.ofNullable(input).filter(v -> !v.isEmpty()).map(Strman::leftTrim);
	}
	
	/**
	 * Removing leading characters from string.
	 * @param input	The string to trim.
	 * @param chars	The characters to trim.
	 
	 * @return	Returns the trimmed string.
	 */
	public static Optional<String> trimStart(final String input, String... chars) {
		return Optional.ofNullable(input)
				.filter(v -> !v.isEmpty())
				.map(v->{
					String pattern = String.format("^[%s]+", join(chars, "\\"));
					return v.replaceAll(pattern, "");
				});
	}
	
	/**
	 * Removes trailing whitespace from string.
	 * @param input		The string to trim.
	 * @return	Returns the trimmed string.
	 */
	public static Optional<String> trimEnd(final String input) {
		return Optional.ofNullable(input)
				.filter(v->!v.isEmpty())
				.map(Strman::rightTrim);
	}
	
	/**
	 * Removes trailing characters from string.
	 * @param input		The string to trim.
	 * @param chars		The characters to trim.
	 * @return	Returns the trimmed string.
	 */
	public static Optional<String> trimEnd(final String input, String... chars) {
		return Optional.ofNullable(input)
				.filter(v->!v.isEmpty())
				.map(v->{
					String pattern = String.format("[%s]+$", join(chars, "\\"));
					return v.replaceAll(pattern, "");
				});
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
		
		int offset;
		if (!allowOverlapping) {
			offset = position + subSr.length();
		}else
			offset = position + 1;
		return countSubstr(value.substring(offset), subSr, allowOverlapping, ++count);
	}
	
}
