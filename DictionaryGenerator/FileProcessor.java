import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessor 
{
    //input as read from the file
    static String INPUT_RAW = "";
    
    //backup for the docID to extract Tags
    static String INPUT_FOR_DOCTABLE = "";
    
    static Map<String, Integer> data = new TreeMap<String, Integer>();
    
    //this will take FILE LOCATION, DOC ID, dictionary and docTable
    public static void execute(String filePath, 
                               int docID, 
                               Map<String, dataSet> dictionary,
                               Map<Integer, dataset_docTable> docTable) 
    {
        //this will store the contents of the file location in the string
        //INPUT_RAW
        readFile(filePath);
        
        //save the raw input for the docTable
        INPUT_FOR_DOCTABLE = INPUT_RAW;
        
        //remove all tags
        INPUT_RAW = removeTags(INPUT_RAW);
        
        //split the input string
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
                        // +++++ ALL PROCESSING IS DONE HERE +++++
                        
                        //check if the term is already in the dictionary
                        if(dictionary.containsKey(temp))
                        {
                            //get the dataset of the term
                            dataSet temp2 = dictionary.get(temp);
                            
                            //increment the cf (collection frequency)
                            temp2.incrementCF();
                            
                            //check if need to update df (document frequency)
                            if(temp2.getPreviousDocID() != docID)
                            {
                                //this term occured in a new document
                                temp2.incrememtDF();
                                temp2.setPreviousDocID(docID);
                            }
                            
                            //update the posting list
                            temp2.incrementTFPostingList(docID);
                            
                            //put everything pack into the dictionary
                            dictionary.put(temp, temp2);
                        }
                        //the term is not in the dictionary
                        else
                        {
                            //make a new dataSet
                            dataSet newData = new dataSet();
                            
                            newData.setPreviousDocID(docID);
                            newData.incrementCF();
                            newData.incrememtDF();
                            newData.incrementTFPostingList(docID);
                            
                            //add the dataset into the dictionary
                            dictionary.put(temp, newData);
                        }
                        
                        //++++++++++++++++++++++++++++++++++++++++
                        
                        //also build the data file, used to build the doc
                        //table
                        if(data.containsKey(temp))
                        {
                            int count = data.get(temp);
                            count++;
                            data.put(temp, count);
                        }
                        else
                        {
                            data.put(temp, 1);
                        }
                    }
                }
                
            }
            //END OF IF
        }
        //END OF FOR
        
        //+++++++ BUILD THE DOC TABLE FOR THE CURRENT DOCUMENT ++++
        //get the headline
        String headline = getTagData("HEADLINE",INPUT_FOR_DOCTABLE);
        
        //remove all tags from the headline
        headline = removeTags(headline);
        
        //replace all commans in the headline with space
        headline = headline.replaceAll(",", " ");
        
        //get the headline
        int docLength = getDocLength();
        
        //get the snippet
        String snippet = getTagData("TEXT",INPUT_FOR_DOCTABLE);
        String finalSnippet = "";
        
        //keep only first 40 words
        String[] tempSnippet = snippet.split(" ");
        
        //if the length is less than 40, keep the orignial snippet
        if(tempSnippet.length < 40 )
        {
            finalSnippet = snippet;
        }
        else
        {
            int count = 0;
            int i = 0;
            while(count < 40 && i < tempSnippet.length)
            {
                if(tempSnippet[i].length() > 0)
                {
                    finalSnippet = finalSnippet+tempSnippet[i]+" ";
                    count++;
                }
                i++;
            }
        }
        
        //remove tags from the final snippet
        finalSnippet = removeTags(finalSnippet);
        
        //replace all commas with the space  in the token
        finalSnippet = finalSnippet.replaceAll(", ", " ");
        
        //what is the path
        String path = filePath;
        
        //put everthing into the docTable
        //create a dataset
        dataset_docTable table = new dataset_docTable();
        table.setHeadLine(headline);
        table.setDocLength(docLength);
        table.setSnippet(finalSnippet);
        table.setPath(path);
        
        //insert the dataset in to the doctable
        docTable.put(docID, table);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        
        //remove everything from the data set
        data.clear();
        
    }
    //FILE READING
    public static void readFile(String in) 
    {
        String fileName = in;
        String line = null;
        StringBuilder br = new StringBuilder();
        try 
        {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) 
            {
                line = line.trim();
                //add a space at the end of the line
                line = line + " ";
                br.append(line);
            }   
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) 
        {
            System.out.println("Couldn't open the file ...");             
        }
        catch(IOException ex) 
        {
            System.out.println("Couldn't read the file ...");
        }
        
        //output everything
        INPUT_RAW = br.toString();
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
    public static void printData()
    {
        System.out.println("TERM\t\tCOUNT");
        System.out.println("****\t\t*****");
        
        //print everything in the map
        for(Map.Entry<String, Integer> entry : data.entrySet())
        {
            String term = entry.getKey();
            int count = entry.getValue();
            
            if(term.length() > 6)
            {
                System.out.print(term.substring(0, 6)+"*");
            }
            else
            {
                System.out.print(term);
            }
            System.out.println("\t\t"+count);
        }
    }
    //create the output file
    public static void writeFile(String name)
    {
    
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(name, "UTF-8");
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File could't be created ...");
            System.exit(0);
        }
        catch(UnsupportedEncodingException e)
        {
            System.out.println("Unsupported Encoding ...");
            System.exit(0);
        }

        for(Map.Entry<String, Integer> entry : data.entrySet())
        {
            String term = entry.getKey();
            int count = entry.getValue();
            
            writer.println(term+" "+count);
        }
        
        writer.close();
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
    public static int getDocLength()
    {
        int out = 0;
        for(Map.Entry<String, Integer> entry : data.entrySet())
        {
            int count = entry.getValue();
            out = out +count;
        }
        return out;
    }
    
}
