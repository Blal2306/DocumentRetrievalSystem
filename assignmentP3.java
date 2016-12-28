import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class assignmentP3 
{
    static Map<Integer, docTable_data> docsTable = new TreeMap<Integer, docTable_data>();
    static Map<String, dictionary_data> dictionary = new TreeMap<String, dictionary_data>();
    static ArrayList<String> postings_raw = new ArrayList<String>();
    
    //colletion frequency
    static int collFrequency = 0;
    
    static final double w = 0.1;
    
    public static void main(String[] args) 
    {
        //initialize everything
        buildDocTable();
        buildDictionary();
        readCF();
        
        System.out.print("ENTER QUERY: ");
        Scanner in = new Scanner(System.in);
        String query = in.nextLine();
        System.out.println(query);
        
        while(!query.equals("EXIT"))
        {
        
            //process the query
            ArrayList<String> terms = QueryTokenizer.execute(query);
        
            //final list of documents that contain atleat one of term
            ArrayList<Integer> docs = getAllDocs(terms);
        
            //this will have the final ranks and the docIDs
            ArrayList<element> ranks = new ArrayList<element>();
        
            //calculate all the ranks
            for(int d: docs)
            {
                double com_rank = 0;
                docTable_data docData = docsTable.get(d);
            
                for(String t: terms)
                {
                    if(dictionary.containsKey(t))
                    {
                        //get the data for the term
                        dictionary_data val = dictionary.get(t);
                        double temp = (1-w)*(val.getFromPostings(d)/(double) docData.getDocLength()) + (w*(val.getCF()/(double)collFrequency));
                        com_rank = com_rank+(Math.log(temp)/(double) Math.log(2));
                    }
                }
            
                //insert the rank and the docID into the final ranks table
                ranks.add(new element(com_rank, d));
            }
        
            //sort all the ranks
            ranks.sort(new rankComparator());
        
            if(ranks.isEmpty())
            {
                System.out.println("NO RESULTS");
            }
            
            //displayResults(ranks);
            writeResults(ranks, query);
        
            //re-read the query
            System.out.print("ENTER QUERY: ");
            query = in.nextLine();
            System.out.println(query);
        }
        
        
    }
    public static void writeResults(ArrayList<element> ranks, String q)
    {
        int i = 0;
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter("result.txt", "UTF-8");
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

        writer.println("QUERY : "+q);
        if(ranks.isEmpty())
        {
            writer.println("NO RESULTS");
        }
        for(element e: ranks)
        {
            //want to display only five results
            if(i == 5)
                break;
            
            docTable_data result = docsTable.get(e.getDoc());
            writer.println(result.getHeadline());
            writer.println(result.getDocPath());
            writer.println("Computed probablity: "+e.getRank());
            writer.println(result.getSnippet());
            writer.println();
            i++;
        }
        
        writer.close();
    }
    public static void displayResults(ArrayList<element> ranks)
    {
        int i = 0;
        for(element e: ranks)
        {
            //want to display only five results
            if(i == 5)
                break;
            
            docTable_data result = docsTable.get(e.getDoc());
            System.out.println(result.getHeadline());
            System.out.println(result.getDocPath());
            System.out.println("Computed probablity: "+e.getRank());
            System.out.println(result.getSnippet()+"\n");
            i++;
        }
    }
    //read the collection frequency from the dictionary
    public static void readCF()
    {
        String fileName = "total.txt";
        String line = null;
        try 
        {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            collFrequency = Integer.parseInt(line);
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
    }
    //get list of all the documents that contain at least one of the query terms
    public static ArrayList<Integer> getAllDocs(ArrayList<String> terms)
    {
        ArrayList<Integer> docs = new ArrayList<Integer>();
        
        for(String t: terms)
        {
            if(dictionary.containsKey(t))
            {
                dictionary_data temp = dictionary.get(t);
                Map<Integer, Integer> temp_postings = temp.getPostingsList();
                for(Map.Entry<Integer, Integer> e: temp_postings.entrySet())
                {
                    int docId = e.getKey();
                    if(!docs.contains(docId))
                    {
                        docs.add(docId);
                    }
                }
            }
        }
        return docs;
    }
    public static void printDictionary()
    {
        System.out.println("*** DICTIONARY ***");
        for(Map.Entry<String, dictionary_data> entry : dictionary.entrySet())
        {
            String term = entry.getKey();
            dictionary_data value = entry.getValue();
            int cf = value.getCF();
            int df = value.getDF();
            int offset = value.getOffset();
            Map<Integer, Integer> post = value.getPostingsList();
            
            System.out.print(term+", ");
            System.out.print(cf+", ");
            System.out.print(df+", ");
            System.out.println(offset);
            
            for(Map.Entry<Integer, Integer> entry2: post.entrySet())
            {
                System.out.println("\t"+entry2.getKey()+", "+entry2.getValue());
            }  
        }
        System.out.println();
    }
    public static void buildDictionary()
    {
        getPostings();
        String fileName = "dictionary.csv";
        String line = null;
        try 
        {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) 
            {
                line = line.trim();
                //add a space at the end of the line
                
                String[] temp = line.split(",");
                String term = temp[0].trim();
                int cf = Integer.parseInt(temp[1]);
                int df = Integer.parseInt(temp[2]);
                int offset = Integer.parseInt(temp[3]);
                
                dictionary_data data_container = new dictionary_data(cf, df, offset);
                
                //insert the posting lists
                for(int i = offset; i < offset+df; i++)
                {
                    String p_raw = postings_raw.get(i);
                    String[] temp2 = p_raw.split(",");
                    int docID = Integer.parseInt(temp2[0]);
                    int tf = Integer.parseInt(temp2[1]);
                    data_container.insertToPostings(docID, tf);
                }
                
                //put everything into the dictioanry
                dictionary.put(term, data_container);
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
    }
    public static void getPostings()
    {
        String fileName = "postings.csv";
        String line = null;
        try 
        {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) 
            {
                line = line.trim();
                //add a space at the end of the line
                
                postings_raw.add(line);
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
    }
    public static void printDocTable()
    {
        System.out.println("*** DOC TABLE ***");
        for(Map.Entry<Integer, docTable_data> entry : docsTable.entrySet())
        {
            int key = entry.getKey();
            docTable_data value = entry.getValue();
            
            System.out.print(key+", ");
            System.out.print(value.getHeadline()+", ");
            System.out.print(value.getDocLength()+", ");
            System.out.print(value.getSnippet()+", ");
            System.out.println(value.getDocPath());
        }
        System.out.println();
    }
    public static void buildDocTable()
    {
        String fileName = "docsTable.csv";
        String line = null;
        try 
        {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) 
            {
                line = line.trim();
                //add a space at the end of the line
                
                String[] temp = line.split(",");
                int key = Integer.parseInt(temp[0]);
                String headline = temp[1];
                int docLength = Integer.parseInt(temp[2]);
                String snippet = temp[3];
                String docPath = temp[4];
                
                docTable_data data = new docTable_data(headline.trim(), docLength, snippet.trim(), docPath.trim());
                docsTable.put(key, data);
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

    }
    
}
//*** Comparator for ranking ****//
class rankComparator implements Comparator<element>
{
    public int compare(element x, element y)
    {
        if(x.getRank() > y.getRank())
        {
            return -1;
        }
        else if(x.getRank() < y.getRank())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
//*** element for the ranking dataset ***//
class element
{
    private double rank;
    private int doc;
    public element(double rank, int doc)
    {
        this.rank = rank;
        this.doc = doc;
    }
    public void setRank(double x)
    {
        rank = x;
    }
    public void setDoc(int x)
    {
        doc = x;
    }
    public double getRank()
    {
        return rank;
    }
    public int getDoc()
    {
        return doc;
    }
}
