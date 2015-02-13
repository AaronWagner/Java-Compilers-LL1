import java.util.ArrayList;

public class Main
{

    public static void main(String[] args)
    {
	// write your code here
    }

   class Rule
   {
       String leftHandSide;
       ArrayList<String> rightHandSide;

       public  Rule(String inputLeftSide, ArrayList<String> inputRightSide)
       {
           leftHandSide=new String(inputLeftSide);
           for (int i=0; i<inputRightSide.size();i++  )
           {
               rightHandSide.add(inputRightSide.get(i));
           }
       }


   }

    class Language
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
            while (i<myLanguage.size())
            {
                while (j<myLanguage.size())
                {

                    j++;
                }

                i++;
            }

        }
    }

}
