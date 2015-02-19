import java.util.ArrayList;

/**
 * Created by Aaron on 2/19/2015.
 */
public class Rule
{


        String leftHandSide;
        ArrayList<Output> rightHandSideList;

        public  Rule(String inputLeftSide, ArrayList<Output> inputRightSideList)
        {
            leftHandSide=new String(inputLeftSide);
            for (int i=0; i<inputRightSideList.size();i++  )
            {
                rightHandSideList.add(inputRightSideList.get(i));
            }
        }
        public Rule()
        {
            String leftHandSide= new String();
            ArrayList<Output> rightHandSideList =new ArrayList<Output>();
        }

    public String getLeftHandSide() {
        return leftHandSide;
    }

    public ArrayList<Output> getOutputs()
    {
        return rightHandSideList;
    }
}
