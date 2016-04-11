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

    //new node constructor
    public Innovation(int inNodeId, int outNodeId, double innovNum1, double innovNum2, int newNodeId, double oldLinkInnov){
        type = InnovType.NEWNODE;
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.innovNum1 = innovNum1;
        this.innovNum2 = innovNum2;
        this.newNodeId = newNodeId;
        this.oldLinkInnov = oldLinkInnov;
    }

    //new link constructor
    public Innovation(int inNodeId, int outNodeId){

    }

}
