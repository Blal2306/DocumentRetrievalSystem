import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class assignmentP2 {
    
    //DICTIONARY
    static Map<String, dataSet> DICTIONARY = new TreeMap<String, dataSet>();
    
    //DATA TABLE
    static Map<Integer, dataset_docTable> DOC_TABLE = new TreeMap<Integer, dataset_docTable>(); 
    
    //Doc ID counter
    static int docIDCounter = 0;
    
    public static void main(String[] args) throws IOException       
    {
        ArrayList<File> input = new ArrayList<File>();
        listf(args[0], input);
        for(File f: input)
        {
            if(!f.isHidden())
            {
                docIDCounter++;
                FileProcessor.execute(f.getPath(), docIDCounter, DICTIONARY, DOC_TABLE);
            }
        }
        
        //Debug print
        //printDictionary();
        //printDocTable();
        
        createDictionaryFile();
        createPostingFile();
        createDocTable();
        createTotalFile();
        
    }
    public static void createTotalFile()
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter("total.txt", "UTF-8");
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

        int total = 0;
        for(Map.Entry<String, dataSet> entry : DICTIONARY.entrySet())
        {
            dataSet value = entry.getValue();
            int count = value.getCF();
            total = total + count;
        }
        writer.println(total);
        
        writer.close();
    }
    public static void createDocTable()
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter("docsTable.csv", "UTF-8");
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

        for(Map.Entry<Integer, dataset_docTable> entry : DOC_TABLE.entrySet())
        {
            int docNum = entry.getKey();
            
            dataset_docTable  temp = entry.getValue();
            String headline = temp.getHeadLine();
            int docLength = temp.getDocLength();
            String snippet = temp.getSnippet();
            String path = temp.getPath();
            
            writer.println(docNum+","+headline.trim()+","+docLength+","+snippet.trim()+","+path);
        }
        
        writer.close();
    }
    public static void createPostingFile()
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter("postings.csv", "UTF-8");
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

        int offset = 0;
        for(Map.Entry<String, dataSet> entry : DICTIONARY.entrySet())
        {
            dataSet temp = entry.getValue();
            Map<Integer, Integer> postings = temp.getPostingList();
            for(Map.Entry<Integer, Integer> x: postings.entrySet())
            {
                writer.println(x.getKey()+","+x.getValue());
            }
        }
        
        writer.close();
    }
    public static void createDictionaryFile() throws UnsupportedEncodingException
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter("dictionary.csv", "UTF-8");
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

        int offset = 0;
        for(Map.Entry<String, dataSet> entry : DICTIONARY.entrySet())
        {
            String term = entry.getKey();
            dataSet value = entry.getValue();
            int cf = value.getCF();
            int df = value.getDF();
            int pos = offset;
            offset = offset+value.postingListSize();
            
            writer.println(term+","+cf+","+df+","+pos);
        }
        
        writer.close();

    }
    
    //Get a list of files in the given directory and in the subdirectories
    public static void listf(String directoryName, ArrayList<File> files) 
    {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        for (File file : fList) 
        {
            if (file.isFile()) 
            {
                files.add(file);
            } 
            else if (file.isDirectory()) 
            {
                listf(file.getPath(), files);
            }
        }
    }
    

    //print the Dictionary
    public static void printDictionary()
    {
        System.out.println("++++++++++ DICTIONARY ++++++++++\n");
        //print everything in the map
        for(Map.Entry<String, dataSet> entry : DICTIONARY.entrySet())
        {
            //get the term
            String term = entry.getKey();
            System.out.print(term);
            
            //get the dataset
            dataSet data = entry.getValue();
            
            //print cf and df
            System.out.print(", CF = "+data.getCF());
            System.out.print(", DF = "+data.getDF());
            
            //get the posting list
            Map<Integer, Integer> postings = data.getPostingList();
            
            System.out.println("\n\tPOSTINGS LIST ->");
            for(Map.Entry<Integer, Integer> e: postings.entrySet())
            {
                int docID = e.getKey();
                int tf = e.getValue();
                System.out.println("\t\tDOC ID = "+docID+", TF = "+tf);
            }
            
            System.out.println();
        }
    }
    public static void printDocTable()
    {
        System.out.println("++++++++++ DOC TABLE ++++++++++\n");
        for(Map.Entry<Integer, dataset_docTable> entry : DOC_TABLE.entrySet())
        {
            //get the doc number
            int docNum = entry.getKey();
            System.out.print(docNum);
            
            //get the dataset
            dataset_docTable data = entry.getValue();
            
            //get the headline
            String headline = data.getHeadLine();
            
            //get the document length
            int docLength = data.getDocLength();
            
            //get the snippet
            String snippet = data.getSnippet();
            
            //get doc path
            String path = data.getPath();
            
            System.out.println(", "+headline+", "+docLength+", "+snippet+", "+path);
        }
    }
}
