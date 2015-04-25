import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    String targetFile;
    String inputString;
    String inputStringWOLComments;
    String inputStringWOBlockComments;
    String remainingInputString;
    String[] inputArray;
    String[][] inputFile;
    ArrayList<Rule> theGrammer;

    ArrayList<String> lines;
    ArrayList<Rule> theRules;
    Pattern arrow;
    Pattern or;
    Pattern nextWord;
    Matcher orMatcher;
    Matcher arrowMatcher;
    Matcher nextWordMatcher;

    ArrayList<String> terminals;
    ArrayList<String> nonterminals;



    class Rule
    {


        String leftHandSide;
        ArrayList<RuleOutput> rightHandSideList;
        boolean unreachable;
        boolean hasEmpty;
        ArrayList<String> firsts;
        ArrayList<String> follows;

        public Rule(String inputLeftSide, ArrayList<RuleOutput> inputRightSideList) {
            leftHandSide = new String(inputLeftSide);
            for (int i = 0; i < inputRightSideList.size(); i++) {
                rightHandSideList.add(inputRightSideList.get(i));
            }
            unreachable = false;
        }

        public Rule() {
            String leftHandSide = new String();
            ArrayList<RuleOutput> rightHandSideList = new ArrayList<RuleOutput>();
            unreachable = false;
        }

        public Rule(String input) {
            String leftHandSide = new String(input);
            ArrayList<RuleOutput> rightHandSideList = new ArrayList<RuleOutput>();
            unreachable = false;
        }

        public String getLeftHandSide() {
            return leftHandSide;
        }

        public ArrayList<RuleOutput> getOutputs() {
            return rightHandSideList;
        }

        public String toString() {
            String out = this.getLeftHandSide() + " -> ";
            for (RuleOutput results : rightHandSideList) {
                for (String character : results) {
                    out += (" " + character + " ");
                }
                out += " | ";
            }
            out = out.substring(0, out.length() - 2);
            return out;
        }

    }






        public void parseRules()
        {
            arrow=arrow.compile("->");
            or=or.compile("^\\|");
            nextWord=nextWord.compile("(\\S)*");
            theGrammer=new ArrayList<Rule>();

            Rule newRule;
            String lineinput;
            ArrayList<String> rightSide;
            for (int i=0; i<lines.size(); i++)
            {
                lineinput=lines.get(i);
                newRule=new Rule();

                lineinput=lineinput.trim();
                //lineinput.replace("|", " | ");  //prevents multiple word rules from being treated as one rule
                arrowMatcher=arrow.matcher(lineinput);
                arrowMatcher.find();
                newRule.leftHandSide=lineinput.substring(0, arrowMatcher.start());
                System.out.print(newRule.leftHandSide+"\n");
                lineinput=lineinput.substring(arrowMatcher.end(), lineinput.length());
                RuleOutput output= new RuleOutput();
                newRule.rightHandSideList=new ArrayList<RuleOutput>();
                newRule.rightHandSideList.add(output);
                String word;
                while(lineinput.length()>0)
                {
                    nextWordMatcher=nextWord.matcher(lineinput.trim());
                    if (nextWordMatcher.find())
                    {
                        lineinput=lineinput.trim();
                        lineinput+="       ";
                        word=nextWordMatcher.group(0);
                        int wordlength=word.length();

                        lineinput = lineinput.substring(wordlength, lineinput.length()-1).trim();
                        //word=nextWordMatcher.group(0);
                        //System.out.println("Word:"+word+"\n");
                        //System.out.println("Line:"+lineinput+"\n");
                        output.add(word);
                    }
                    orMatcher=or.matcher(lineinput);

                    if (orMatcher.find())
                    {
                        output=new RuleOutput();
                        newRule.rightHandSideList.add(output);
                        lineinput=lineinput.substring(orMatcher.end(),lineinput.length()).trim();
                    }

                }
                theGrammer.add(newRule);
            }
        }





        public void cleanGrammer()
        {
            int i, j;
            i=0; j=0;
            while( i<theGrammer.size())
            {
                if (theGrammer.get(i).leftHandSide==null)
                {
                    theGrammer.remove(i);

                    i=0;
                    j=0;
                    continue;

                }
                if (theGrammer.get(i).rightHandSideList.size()>1)
                {
                    theGrammer.remove(i);
                    continue;
                }
                while( j<theGrammer.size())
                {

                    if (theGrammer.get(i).rightHandSideList.get(j)==null)
                    {
                        theGrammer.get(i).rightHandSideList.remove(j);
                        i=0;
                        j=0;
                        continue;
                    }
                }
                i++;
            }
            for (Rule theRule: theGrammer)
            {
                theRule.leftHandSide=theRule.leftHandSide.trim();
                for (RuleOutput theOutput: theRule.rightHandSideList)
                {
                    for (String character: theOutput) {
                        character = character.trim();
                    }
                }
            }
        }




        public void removeDirectRecursion()
        {
            int i=0;
            int j=0;
            //from pg 159 of Louden's Compiler construction
            String myNonTerminal;
            ArrayList<RuleOutput> myRightHandSide;
            Boolean refactor=false;
            ArrayList<Rule> myNewRules= new ArrayList<Rule>();


            //remove direct recursion



            for (i=0; i<theGrammer.size(); i++)
            {
               // cleanGrammer();
                myNonTerminal=theGrammer.get(i).leftHandSide;
                myRightHandSide=theGrammer.get(i).rightHandSideList;
                if (myNonTerminal==null)
                {
                    theGrammer.remove(i);
                }


                System.out.print("Checking for direct recursion\n");
                j=0;
                while (j<myRightHandSide.size())
                {
                    if (myRightHandSide.get(j)==null)
                    {
                        myRightHandSide.remove(j);
                        j=0;
                       // cleanGrammer();
                        continue;

                    }
                    System.out.print("Comparing "+myNonTerminal+" and "+myRightHandSide.get(j).get(0)+"\n");

                    if (myNonTerminal.trim().equals(myRightHandSide.get(j).get(0).trim())) {
                        System.out.print("Found direct recursion in " + myNonTerminal);
                        refactor = true;
                        myNewRules = refactorRule(theGrammer.get(i));
                        theGrammer.remove(i);
                       // cleanGrammer();
                        for (Rule newRule : myNewRules)
                        {
                            theGrammer.add(newRule);

                        }

                        System.out.print(getLanguage() );
                        //cleanGrammer();
                        continue;
                    }
                    j++;
                }

            }

        }
        //removes leftside recursion from the language
        public ArrayList<Rule> refactorRule(Rule input)
        {
            System.out.print("\n Refactoring: "+input.leftHandSide+"\n");
            String leftHandSidePrime=new String(input.leftHandSide+"*");
            Rule inputPrime=new Rule(leftHandSidePrime);
            inputPrime.leftHandSide=leftHandSidePrime;
            inputPrime.rightHandSideList=new ArrayList<RuleOutput>();
            /*RuleOutput empty=new RuleOutput();
            empty.add("@");
            inputPrime.rightHandSideList.add(empty);*/

            //removes all results beginning with direct recursion to input'
            for (int i=input.rightHandSideList.size()-1; i>-1; i--)
            {
                if (input.leftHandSide.trim().equals(input.rightHandSideList.get(i).get(0).trim()))
                {
                    RuleOutput newOutput= new RuleOutput();
                    for (String word:input.rightHandSideList.get(i))
                    {

                        newOutput.add(new String(word.trim()));
                        System.out.println("Added "+word+" to "+inputPrime.leftHandSide);
                    }
                    inputPrime.rightHandSideList.add(newOutput);
                    input.rightHandSideList.remove(i);

                }

            }
            //A->@ becomes A->A'
            for (int i=0; i<input.rightHandSideList.size(); i++)
            {
                if (input.rightHandSideList.get(i).get(0).equals("@"))
                {
                    input.rightHandSideList.get(i).remove(0);
                    input.rightHandSideList.get(i).add(inputPrime.leftHandSide);
                }
            }

            //A->Bb becomes A->BbA'
            for (int i=0; i<input.rightHandSideList.size(); i++)
            {
                input.rightHandSideList.get(i).add(inputPrime.leftHandSide);
            }
            if (input.rightHandSideList.isEmpty())
            {
                input.unreachable=true;
            }
            else {input.unreachable=false;}
            /*
            for (int i=0; i<inputPrime.rightHandSideList.size(); i++)
            {
                inputPrime.rightHandSideList.get(i).remove(0);  // inputPrime->inputB to inputB
                inputPrime.rightHandSideList.get(i).add(inputPrime.leftHandSide);
            }
            */
            RuleOutput empty=new RuleOutput();
            empty.add("@");
            inputPrime.rightHandSideList.add(empty);
            ArrayList<Rule> output= new ArrayList<Rule>();
            output.add(input);
            output.add(inputPrime);
            return output;
        }


        public void findFirsts()
        {
            terminals=new ArrayList<String>();
            nonterminals=new ArrayList<String>();

            //all nonterminals have a production rule
            for (int i=0; i<theGrammer.size(); i++)
            {
                if (!theGrammer.get(i).unreachable)
                {
                    nonterminals.add(theGrammer.get(i).leftHandSide);

                }
            }
            //terminals are not on the left hadn side of a rule
            for (int i=0; i<theGrammer.size(); i++)
            {
                for (int j=0; j<theGrammer.get(i).rightHandSideList.size(); j++)
                {
                    for (int k=0; k<theGrammer.get(i).rightHandSideList.get(j).size();k++)
                    {
                        String possibleTerminal=theGrammer.get(i).rightHandSideList.get(j).get(k);
                        if (!nonterminals.contains(possibleTerminal)&&possibleTerminal!="@")
                        {
                            terminals.add(possibleTerminal);
                        }
                    }
                }
            }

        }

        public String getLanguage()
        {
            String out=new String();
            for (Rule rule: theGrammer)
            {
                out+="\n"+rule.toString()+"\n";
            }
            return out;
        }
        public void findFollows()
        {

        }

    public void Stemming ()
    {
        boolean changeMade=false;
        while (!changeMade)
        {
            changeMade=false;
            for (Rule aRule : theGrammer)
            {
                for (RuleOutput anOutput: aRule.rightHandSideList)
                {
                    for (RuleOutput otherOutput: aRule.rightHandSideList)
                    {
                        if (anOutput.get(0).equals(otherOutput.get(0))&&(anOutput!=otherOutput))
                        {
                            String violatingOutput=new String(anOutput.get(0));
                            stem(aRule, violatingOutput);
                            changeMade=true;
                            break;
                        }
                        if(changeMade) {break;}
                    }
                    if(changeMade) {break;}
                }
                if(changeMade) {break;}
            }

        }
    }
    public void stem(Rule input, String violatingFirstOutput)
    {
        System.out.println("\n Stemming: "+input.leftHandSide+"\n");
        int num=0;
        String newRulename= new String("stemmed-"+input.leftHandSide);
        System.out.println("\nNew Rulename:" + newRulename + "\n");
        //check to see if this name is allready used
        boolean uniqueName=true;
        while (!uniqueName)
        {
            uniqueName=true;
            for (Rule anyRule: theGrammer)
            {
                if (anyRule.leftHandSide.trim().equals(newRulename.trim()))
                {
                    newRulename= new String("stemmed-"+Integer.toString(num)+"-"+input.leftHandSide);
                    uniqueName=false;
                    System.out.print("\nThis name is taken: "+newRulename+"\n");
                }

            }
        }
        System.out.print("\n Found a name for new rule: "+newRulename+"\n");
        Rule stemmedRule = new Rule(newRulename);
        stemmedRule.leftHandSide=newRulename;
        ArrayList<RuleOutput> stemmedRightHandSide = new ArrayList<RuleOutput>();
        // look for all the first output with the first same character
        for (int i=input.rightHandSideList.size()-1; i>-1; i--) //RuleOutput possiblyOffendingRuleOutput: input.rightHandSideList)
        {
            RuleOutput possiblyOffendingRuleOutput=input.rightHandSideList.get(i);
            if (violatingFirstOutput.equals(input.rightHandSideList.get(i).get(0).trim()))
            {
                RuleOutput addingToStemmed=new RuleOutput();

                for (int j=0; j<possiblyOffendingRuleOutput.size(); j++)
                {
                    String newCharacter=new String(possiblyOffendingRuleOutput.get(j));
                    if (j!=0)  //don't re-add the reson you are stemming
                    {
                        addingToStemmed.add(newCharacter);

                        //System.out.
                    }
                    else if (possiblyOffendingRuleOutput.size()==1&&!stemmedRule.hasEmpty)
                    {
                        addingToStemmed.add("@");
                        stemmedRule.hasEmpty=true;
                    }



                    //clone them
                }
                stemmedRightHandSide.add(addingToStemmed);
                input.rightHandSideList.remove(i); //this only happens in the first if
                //remove the offending rule output
            }
        }
        stemmedRule.rightHandSideList=stemmedRightHandSide;
        for (RuleOutput anOutput: stemmedRule.rightHandSideList)
        {
            if (anOutput.isEmpty()|anOutput==null)
            {
                anOutput = new RuleOutput();
                anOutput.add("@");
            }
        }
        RuleOutput stemmedOption = new RuleOutput();
        stemmedOption.add(violatingFirstOutput);
        stemmedOption.add(stemmedRule.leftHandSide);
        input.rightHandSideList.add(stemmedOption);
        theGrammer.add(stemmedRule);
        //add a stemeed expression back to expression

    }

    public static void main(String[] args) {
        //MyFileLoader myFileloader= new MyFileLoader(args[0]);
        Main myMain = new Main();
        myMain.targetFile=args[0];
        myMain.loadFile();


        myMain.parseRules();
        System.out.print(myMain.getLanguage());
        myMain.removeDirectRecursion();
        System.out.print(myMain.getLanguage());

        myMain.Stemming();
        System.out.print(myMain.getLanguage());

    }

    public void loadFile() {
        //theGrammer = new Language();
        //theParser = new Parser();
        try

        {
            FileInputStream inputStream = new FileInputStream(targetFile);
            DataInputStream dataInput = new DataInputStream(inputStream);
            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(dataInput));
            String tempinputString;
            inputString = "";

            ArrayList<String> inputList = new ArrayList<String>();

            while ((tempinputString = (inputBuffer.readLine())) != null) {
                //tempinputString = tempinputString.trim();
                if ((tempinputString.length() != 0)) {
                    inputList.add(tempinputString + "\n");
                }
                inputString += tempinputString + "\n";
                //System.out.print("\n" + inputString + "\n");
            }
            lines=inputList;
            inputArray = (String[]) inputList.toArray(new String[inputList.size()]);
            inputFile = new String[inputArray.length][];
            for (int i = 0; i < inputArray.length; i++) {
                inputFile[i] = inputArray[i].split("(\\s+)"); // this needs to be "(\\s+) (not a float word or number)"
            }
            //inputString=new String (tempinputString);
            //int pants=0;
        }
        catch (Exception e)

        {
            // Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }


    public class RuleOutput extends ArrayList<String> {

    }




}
