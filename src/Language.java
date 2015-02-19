import java.util.ArrayList;

/**
 * Created by Aaron on 2/19/2015.
 */
public class Language
{

        ArrayList<Rule> myLanguage;

        public Language()
        {}

        public void addRule()
        {

        }

        public void removeRecursion()
        {
            int i=0;
            int j=0;
            //from pg 159 of Louden's Compiler construction
            String myNonTerminal;
            ArrayList<Output> myRightHandSide;
            Boolean refactor=false;
            Rule myNewRule= new Rule();
            //remove direct recursion
            for (i=0; i<myLanguage.size(); i++)
            {

                myNonTerminal=myLanguage.get(i).getLeftHandSide();
                myRightHandSide=myLanguage.get(i).getOutputs();
                myNewRule.leftHandSide=new String(myNonTerminal+"'");

                for (j=0; j<myRightHandSide.size(); j++)
                {
                    if (myNonTerminal.equals(myRightHandSide.get(j).characters.get(0)))
                    {
                        refactor=true;


                    }
                }
            }

        }


}
