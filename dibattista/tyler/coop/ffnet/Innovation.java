package dibattista.tyler.coop.ffnet;

/**
 * Created by Tyler on 09/04/2016.
 */
public class Innovation {

    public enum InnovType{
        NEWNODE, NEWLINK
    }

    public InnovType type;
    public int inNodeId, outNodeId, newNodeId;
    public double innovNum1, innovNum2, oldLinkInnov;
    public double newLinkWeight;
    public boolean recur;

    //new node constructor
    public Innovation(int inNodeId, int outNodeId, double innovNum1, double innovNum2, int newNodeId, double oldLinkInnov){
        type = InnovType.NEWNODE;
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.innovNum1 = innovNum1;
        this.innovNum2 = innovNum2;
        this.newNodeId = newNodeId;
        this.oldLinkInnov = oldLinkInnov;

        recur = false;
        newLinkWeight = 0;
    }

    //new link constructor
    public Innovation(int inNodeId, int outNodeId, double innovNum1, double weight){
        type = InnovType.NEWLINK;
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.innovNum1 = innovNum1;
        newLinkWeight = weight;

        recur = false;
        innovNum2 = 0;
        oldLinkInnov = 0;
        newNodeId = 0;
    }

    //new link with recur constructor
    public Innovation(int inNodeId, int outNodeId, double innovNum1, double weight, boolean recur){
        type = InnovType.NEWLINK;
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.innovNum1 = innovNum1;
        newLinkWeight = weight;
        this.recur = recur;

        innovNum2 = 0;
        oldLinkInnov = 0;
        newNodeId = 0;
    }

}
