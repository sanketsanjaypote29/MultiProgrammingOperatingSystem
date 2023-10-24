import java.io.*;
import java.util.*;

public class Phase2 {
    static String mainMemory[][] = new String[300][4], IR[] = new String[4], R[] = new String[4];
    static String DTApart = "",EM = "";
    static int IC,DTAPointer,TI = 0,PI = 0;
    static int SI, M=0,TTL=0,JID=0,TLL=0,jobStarted = 0,TTC = 0,LLC = 0;
    static int PTR[] = new int [2];
    static boolean  C, continueExecution = false;
    
    static File outputFile = new File("out.txt");
    
    static File inputFile = new File("input.txt");

  
    static void print(Object args) {
        System.out.println(args);
    }

    static int getFrameNos(int words){
        if(10%words == 0) return words/10;
        else return (words/10)+1;
    }

    static void dispMainMemory(){
        print("\nThe main memory");
        for(int i=0;i<300;i++){
            System.out.print(i+"- ");
            for(int j = 0;j<4;j++){
                System.out.print(mainMemory[i][j]);
            }print("");
        }
    }

    static void fileDisplay(File fileName) {
        try {
            int ch;
            FileReader toDispFile = new FileReader(fileName);

            while ((ch = toDispFile.read()) != -1) {
                System.out.print((char) ch);
            }
            toDispFile.close();
        } catch (Exception ex) {
            System.out.println("Error in Reading file "+ex);
        }
    }

    static void fileWrite() {
        String temp = "";
        List<String> userProgram = new ArrayList<String>();

        Scanner userInput = new Scanner(System.in);

        while (!temp.equals("$END")) {
            String inputBuff = userInput.nextLine();
            userProgram.add(inputBuff);
            temp = inputBuff;
        }

        try {
            FileWriter fw = new FileWriter("out.txt");
            
            for(int i = 0; i<userProgram.size();i++){
                fw.append(userProgram.get(i)+"\n");
            }

            fw.close();
        } catch (Exception ex) {
            print("Error writing file "+ex);
        }
    }

    static void loadToMemory1(String data, int frames){
        int stringIndex = 0;
        int VA = 0, RA = 0, CVA = 1;


        for(int i = 0;i<frames;i++){
            try{
                for(int m = 2; m<4;m++) {
                    VA = VA*10 + Integer.parseInt(IR[m]);
                }

            } catch (NumberFormatException exception){
                if(VA == CVA){
                    VA+=10;
                }
                CVA = VA;
            }

            if(getRA(VA)<0) mapAddress(VA);

            RA = getRA(VA);

            for(int l = RA;l<RA+10;l++){
                for(int k=0;k<4;k++){
                    if(stringIndex<data.length()){
                        mainMemory[l][k] = String.valueOf(data.charAt(stringIndex));
                        stringIndex++;
                    } else {
                        k = 4000;
                        l = 20000;
                    }
                }
            } 
            
        }
    }

    static void cleanMainMemory(){
        for(int i=0;i<300;i++){
            for(int j = 0;j<4;j++){
                mainMemory[i][j] = null;
             
            }
        }
    }

    static int getRA(int VA){
        int RA = 0;
        int offset = VA%10;
        int base = VA/10;

        for(int i = PTR[0];i<PTR[1];i++){
            int pageTableBase = 0;

            for(int j = 0;j<2;j++){
                pageTableBase = pageTableBase*10+Integer.parseInt(mainMemory[i][j]);
            }

            if(base == pageTableBase){
               
                for(int j = 2;j<4;j++){
                    RA = RA*10+Integer.parseInt(mainMemory[i][j]);
                }

                RA = RA*10+offset;
                return RA;
            }

        }

        return -1;
    }

    
    static void mapAddress(int VA){
        VA = (VA/10);
        for(int j = 0;j<27;j++){ 
            try{
             
                if(mainMemory[j*10][0].equals("arg0"));

            } catch (Exception e){
                
                int RA = j;

                mainMemory[PTR[1]][0] = String.valueOf(VA/10);
                mainMemory[PTR[1]][1] = String.valueOf(VA%10);
                mainMemory[PTR[1]][2] = String.valueOf(RA/10);
                mainMemory[PTR[1]][3] = String.valueOf(RA%10);

                PTR[1]++;  
                j= 456;              
            }
        }
    }

    static void read(){
        SI = 0;

        IR[3] = "0";
        int memoryAddress = 0;
        for(int i =2;i<4;i++) memoryAddress = memoryAddress*10 + Integer.parseInt(String.valueOf(IR[i])); 
        M = memoryAddress;
        if(DTApart.length() == 0){
            terminate(1);
        }

        String dataCardHolder[] = DTApart.split("#");
        if(DTAPointer<dataCardHolder.length){
            loadToMemory1(dataCardHolder[DTAPointer], 1);
            DTAPointer++;
        }

    }

    static void write(){
        int memoryAddress = 0;

        SI = 0;

        for(int i =2;i<4;i++) memoryAddress = memoryAddress*10 + Integer.parseInt(String.valueOf(IR[i]));

        memoryAddress = getRA(memoryAddress);

        try{
            if(LLC >= TLL){
                terminate(2);
                continueExecution = false;
            } else {
                String toBeWritten = "";
                FileWriter wf = new FileWriter(outputFile,true);
                PrintWriter printWF = new PrintWriter(wf);
                inputFile.setWritable(true);
                for (int i = memoryAddress; i < memoryAddress+10; i++) {
                    for (int j = 0; j < 4 && mainMemory[i][j] != null; j++) {
                        toBeWritten = toBeWritten.concat(mainMemory[i][j]);
                    }
                } printWF.append("\n");
                printWF.write(toBeWritten);
                LLC++;
                printWF.close();
                wf.close();
            }

        } catch (Exception ex){
            print("Write() cannot write into output file:");
            ex.printStackTrace();
        }
    }

    static void terminate(int code){
        
        try{
            FileWriter wf = new FileWriter(outputFile,true);
            PrintWriter writer = new PrintWriter(wf);

            String IRS = "";
            try {
                for(int i=0;i<4;i++) IRS = IRS.concat(IR[i]);
            } catch (Exception e) {
            }

            continueExecution = false;

            if(code == 0){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n NO ERROR\n"+
     
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            if(code == 1){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n OUT OF DATA ERROR\n"+
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            if(code == 2){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  LINE LIMIT EXCEED ERROR\n"+
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            if(code == 3){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  TIME LIMIT EXCEED ERROR\n"+
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            if(code == 4){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  OPCODE ERROR\n"+
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }
            if(code == 5){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  OPERAND ERROR\n"+
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            if(code == 6){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  INVALID PAGE FAULT\n"+
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            if(code == 7){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  TIME LIMIT EXCEED AND OPCODE ERROR\n"+
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            if(code == 8){
                EM = "";
                EM = EM.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  TIME LIMIT EXCEED AND OPERAND ERROR\n"+
                "IC\t: "+IC+"\nIR\t: "+IRS+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }


            outputFile.setWritable(true);
            writer.append(EM+"\n\n");
            writer.close();
        } catch (Exception ex) {
            print("Not able to terminate");
            ex.printStackTrace();
        }
    }

    static void MOS(){
        if(TI == 0){
            if(SI == 1){
                read();
            } else if (SI == 2){
                write();
            } else if (SI == 3){
                terminate(0);
            } else if (PI == 1){
                terminate(4);
            } else if (PI == 2){
                terminate(5);
            } else if (PI == 3){
                terminate(6);
            }
        } else if(TI == 2){
            if(SI == 1){
                terminate(3);
            } else if (SI == 2){
                write();
                terminate(3);
            } else if (SI == 3){
                terminate(0);
            } else if (PI == 1){
                terminate(7);
            } else if (PI == 2){
                terminate(8);
            } else if (PI == 3){
                terminate(3);
            }
        }

    }

    static void executeUserProgram(){
        while(continueExecution){
            if(TTC >= TTL){
                if(TI == 2){
                    SI = 1;
                    MOS();
                    continue;
                }
                TI = 2;
            }
            for(int i = 0; i<4;i++) IR[i] = mainMemory[IC][i]; 
            IC++;

            String instruction = "";
            int memoryAddress = 0;
            for (int i = 0; i < 2 && IR[i] != null; i++) instruction = instruction.concat(IR[i]); 
            try {
                for (int i = 2; i < 4 && IR[i] != null; i++) memoryAddress = (memoryAddress*10) + Integer.parseInt(IR[i]);
            } catch (Exception e) {
                PI = 2;
                MOS();
                continueExecution = false;
                continue;
            }                
            boolean pageFault = false;
            if(getRA(memoryAddress) >= 0){
                memoryAddress = getRA(memoryAddress);
            } else {
                pageFault = true;
            }
            if(TTC<TTL) TTC++;
            if(instruction.equals("LR")){
                if(!pageFault){
                    try{
                        if(mainMemory[memoryAddress][0].equals("arg0"));
                        
                        for(int i = 0; i<4;i++) {
                            R[i] = mainMemory[memoryAddress][i];
                        } 
                    } catch (Exception exception){
                        PI = 3;
                        MOS();
                        continueExecution = false;
                    }
                } else {
                    PI = 3;
                    MOS();
                    continueExecution = false;
                }
                
            } else if(instruction.equals("SR")){
                String strToBeLoaded = "";
                try {
                    for(int i = 0; i<4;i++)  strToBeLoaded = strToBeLoaded.concat(R[i]);
                } catch (Exception e) {
                }
                
                loadToMemory1(strToBeLoaded, 1);

            } else if (instruction.equals("CR")){
                C = false;
                if(!pageFault) {
                    try{
                        if(mainMemory[memoryAddress][0].equals("arg0"));
                        for(int i = 0; i<4;i++){
                            if(R[i] == mainMemory[memoryAddress][i]) C = true;
                            else {
                                C = false;
                                break;
                            }
                        }
                    } catch (Exception exception){
                        PI = 3;
                        MOS();
                        continueExecution = false;
                    }
                } else {
                    PI = 3;
                    MOS();
                    continueExecution = false;
                }
            } else if (instruction.equals("BT")){
                if(C) IC = memoryAddress;
            } else if (instruction.equals("GD")){
                SI = 1; 
                MOS();
            } else if (instruction.equals("PD")) {
                if(!pageFault){
                    SI = 2; 
                    MOS();
                } else {
                    PI = 3;
                    MOS();
                    continueExecution = false;
                    continue;
                }
                
            } else if (instruction.matches("H")){
            
                SI = 3; 
                MOS();
                continueExecution = false;
            } else {
                PI = 1;
                MOS();
                continueExecution = false;
            }
        }
    }
    static void startExecution(){
        IC = 0;
        executeUserProgram();
    }
    static void load(){
        try{
            String loadBuffer = "";
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader reader = new BufferedReader(fileReader);
            loadBuffer = reader.readLine();
            while(loadBuffer != null){
               
                if(loadBuffer.substring(0, 4).equals("$AMJ")){
                    TTL = 0;
                    TLL = 0;
                    JID = 0;
                    M = 0;

                    TTC = 0;
                    LLC = 0;

                    SI = 0;
                    PI = 0;
                    TI = 0;
                    cleanMainMemory();

                    IR[0] = null;
                    IR[2] = null;

                    for(int i = 270,j = 27;i<273;i++,j++){
                        mainMemory[i][0] = String.valueOf(j/10);
                        mainMemory[i][1] = String.valueOf(j%10);
                        mainMemory[i][2] = String.valueOf(j/10);
                        mainMemory[i][3] = String.valueOf(j%10);
                    }
                    PTR[0] = 270;
                    PTR[1] = 273;
                    
                    for(int i=4;i<8;i++) JID = JID*10 + Integer.parseInt(String.valueOf(loadBuffer.charAt(i)));
                    for(int i=8;i<12;i++) TTL = TTL*10 + Integer.parseInt(String.valueOf(loadBuffer.charAt(i)));
                    for(int i=12;i<16;i++) TLL = TLL*10 + Integer.parseInt(String.valueOf(loadBuffer.charAt(i)));

                    loadBuffer = reader.readLine();
                    String controlsToBeLoaded = "";
                    while(true){
                        controlsToBeLoaded = controlsToBeLoaded.concat(loadBuffer);
                        loadBuffer = reader.readLine();
                        if (loadBuffer.length() >= 4){
                            if(loadBuffer.substring(0, 4).equals("$DTA")) break;
                        }
                    } 
                    
                    loadToMemory1(controlsToBeLoaded, getFrameNos(TTL));
            
                    DTApart = ""; 
                    DTAPointer = 0; 

                    while(true){
                        loadBuffer = reader.readLine();
                        if(loadBuffer.length()>=4){
                            if(loadBuffer.substring(0, 4).equals("$END")) break;
                        }
                        DTApart = DTApart.concat(loadBuffer+"#");
                    }
                    continueExecution = true;
                    startExecution();

                }
                loadBuffer = reader.readLine();
            }

            reader.close();
        } catch (Exception exception) {
            print("load(): cannot read the input file:");
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {

        
        fileDisplay(inputFile);
        load();
        fileDisplay(outputFile);
       
    }
}