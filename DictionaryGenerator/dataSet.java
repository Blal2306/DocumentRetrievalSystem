import java.util.Map;
import java.util.TreeMap;

public class dataSet {
    
    //id of the document last used
    private int previousDocID = -1;
    
    //how many unique documents contain this term
    private int df = 0;
    
    //what many times have this term occurred in the collection
    private int cf = 0;
    
    //posting list for the term
    //it is going to have the docID and the tf (the number of times it 
    //occurs in a particular docID
    private Map<Integer, Integer> postingList = new TreeMap<Integer, Integer>();
    
    //accessor and mutator methods
    public void setPreviousDocID(int x)
    {
        previousDocID = x;
    }
    public int getPreviousDocID()
    {
        return previousDocID;
    }
    
    public void incrememtDF()
    {
        df++;
    }
    public int getDF()
    {
        return df;
    }
    public void incrementCF()
    {
        cf++;
    }
    public int getCF()
    {
        return cf;
    }
    public Map<Integer, Integer> getPostingList()
    {
        return postingList;
    }
    public void incrementTFPostingList(int docID)
    {
        //does the posting already has has entry
        //for the doc id
        if(postingList.containsKey(docID))
        {
            int currentTF = postingList.get(docID);
            currentTF++;
            postingList.put(docID, currentTF);
        }
        else
        {
            postingList.put(docID, 1);
        }
    }
    public int postingListSize()
    {
        return postingList.size();
    }
    
}
