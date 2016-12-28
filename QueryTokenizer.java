import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryTokenizer 
{
    //input as read from the file
    static String INPUT_RAW = "";
    
    //Take the query String as input
    public static ArrayList<String> execute(String query) 
    {
        //create the output ArrayList
        ArrayList<String> out = new ArrayList<String>();
        
        INPUT_RAW = query;
        
        //remove all tags
        INPUT_RAW = removeTags(INPUT_RAW);
        
        //split the query at white space characteres
        String[] splitedData = INPUT_RAW.split("[-\\s]");
        
        for(String token: splitedData)
        {
            //can't have one character or less token size
            if(token.length() > 1)
            {
                //remove all white space around the token
                String temp = token.trim();
            
                //convert the token to lowercase
                temp = temp.toLowerCase();
            
                //remove parenthese and quotes
                temp = removeParenthesesAndQuotes(temp);
                
                //remove symbols
                temp = removeSymbols(temp);
                
                //remove apostrophes
                temp = removeApostrophes(temp);
                
                //remove parenthese and quotes
                temp = removeParenthesesAndQuotes(temp);
                
                //remove stop words
                if(!isStopWord(temp))
                {
                    //apply Stemming
                    temp = applyStemming(temp);
                    
                    //remove any special characters from the term
                    //only alpanumeric characters allowed
                    temp = removeSpecialCharacters(temp);
                    
                    //if the stemming hasn't changed the length
                    if(temp.length() > 1)
                    {
                        out.add(temp);
                    }
                }
            }
            //END OF IF
        }
        //END OF FOR
        return out;
    }
    public static String removeTags(String x)
    {
        //if the input doesn't have any tags
        if(!x.contains("<"))
        {
            return x;
        }
        
        String input = x;
        
        //termination condition
        boolean control = true;
        
        StringBuilder finalOut = new StringBuilder();
        while(control)
        {
            input = input.trim();
            
            //find positions of < and >
            int l1 = input.indexOf('<');
            int l2 = input.indexOf('>');
            
            //remove the tag
            input = input.substring(l2+1);
            
            //check if there are more tokens
            if(input.contains("<"))
            {
                //get the location of the next token
                int loc = input.indexOf('<');
                
                //get everthing upto the next token
                finalOut.append(" ");
                finalOut.append(input.substring(0, loc));
                
                //prepare the string for the next token
                input = input.substring(loc,input.length());
            }
            //does contain more tags
            else
            {
                //add remains to the output
                finalOut.append(input);
                control = false;
            }
        }
        return finalOut.toString();
    }
    public static String applyStemming(String x)
    {
        String out = x;
        if(out.endsWith("ies") && !(out.endsWith("eies") || out.endsWith("aies")))
        {
            out = out.replace("ies", "y");
        }
        else if(out.endsWith("es") && !(out.endsWith("aes") || out.endsWith("ees") || out.endsWith("oes")))
        {
            out = out.replace("es", "e");
        }
        else if(out.endsWith("s") && !(out.endsWith("us") || out.endsWith("ss")))
        {
            out = out.replace("s", "");
        }
        return out;
    }
    public static boolean isStopWord(String x)
    {
        if(x.equals("and")   || 
           x.equals("a")     || 
           x.equals("the")   || 
           x.equals("an")    ||
           x.equals("by")    ||
           x.equals("from")  ||
           x.equals("for")   ||
           x.equals("hence") ||
           x.equals("of")    ||
           x.equals("the")   ||
           x.equals("with")  ||
           x.equals("in")    ||
           x.equals("within")||
           x.equals("who")   ||
           x.equals("when")  ||
           x.equals("where") ||
           x.equals("why")   ||
           x.equals("how")   ||
           x.equals("whom")  ||
           x.equals("have")  ||
           x.equals("had")   ||
           x.equals("has")   ||
           x.equals("not")   ||
           x.equals("for")   ||
           x.equals("but")   ||
           x.equals("do")    ||
           x.equals("does")  ||
           x.equals("done"))  
        {
            return true;
        }
       else
        {
            return false;
        }
    }
    public static String removeApostrophes(String x)
    {
        String out = x;
        if(out.contains("'"))
        {
            out = out.replace("'", "");
        }
        return out;   
    }
    public static String removeSymbols(String x)
    {
        String out = x;
        while(out.endsWith(",")   ||
              out.endsWith(".")   ||
              out.endsWith("?")   ||
              out.endsWith(";")   ||
              out.endsWith(":")   ||
              out.endsWith("!"))
        {
            out = out.substring(0, out.length()-1);
        }
        return out;
    }
    public static String removeParenthesesAndQuotes(String x)
    {
        String out = x;
        
        //remove prentheses and quotes at the end
        while(out.startsWith("\"")  ||
              out.startsWith("\'")  ||
              out.startsWith("(")   ||
              out.startsWith("[")   ||
              out.startsWith("]")   ||
              out.startsWith(")"))
        {
            out = out.substring(1, out.length());
        }

        //remove things at the end
        while(out.endsWith("\"")   ||
              out.endsWith("\'")   ||
              out.endsWith(")")    ||
              out.endsWith("(")    ||
              out.endsWith("]")    ||
              out.endsWith("["))
        {
            out = out.substring(0, out.length()-1);
        }
        
        return out;
    }
    public static String removeSpecialCharacters(String x)
    {
        String out = "";
        for(int i = 0; i < x.length(); i++)
        {
            if(Character.isLetterOrDigit(x.charAt(i)))
            {
                out = out+x.charAt(i);
            }
        }
        
        return out;
    }
    //this is to extract a particular tag from the file
    public static String getTagData(String tag, String input)
    {
        int START = -1;
        int END = -1;
        
        //*** the beginning of the tag ***
        String PATTERN = "(<(\\s)*"+tag+"(\\s)*>)";
        Pattern checkRegex = Pattern.compile(PATTERN);
        Matcher regexMatcher = checkRegex.matcher(input);
        
        while(regexMatcher.find())
        {
            if(regexMatcher.group().length()!=0)
            {
                START = regexMatcher.end();
            }
        }
        
        //*** the ending of the tag ***
        String PATTERN2 = "<(\\s)*/{1}(\\s)*"+tag+"(\\s)*>";
        Pattern checkRegex2 = Pattern.compile(PATTERN2);
        Matcher regexMatcher2 = checkRegex2.matcher(input);
        
        while(regexMatcher2.find())
        {
            if(regexMatcher2.group().length()!=0)
            {
                END = regexMatcher2.start();
            }
        }
        return input.substring(START, END);
        
    }
    
}

