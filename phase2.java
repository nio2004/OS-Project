import java.util.Scanner;   
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.regex.*;  


class Buffer {
    static String buffer = new String();
    static String read_buffer(){
       return buffer;   
    }
    static void write_buffer(String input_str){
        buffer = input_str;  
    }
}

class Memory{
    static char[][] mem = new char[301][4];
    static int mem_ptr=0;

    static void read(){
        int count=0;
        for(int i=0;i<300;i++){
            if(!(String.valueOf(mem[i]).contains("/")))
            {    
                System.out.print((i)+" - ");
                for(int j=0;j<4;j++){
                    if(mem[i][j] != '\0')
                        System.out.print(mem[i][j]+"|");
                    else
                        System.out.print(" |");
                }

                if(i%10 == 9){
                    System.out.println();
                    System.out.println("----------");
                }else{
                    System.out.println();
                }
            }
        }
    }

    static void write_mem(String buffer_array){
        int count=0;
        char ch=' ';
        for (int i = 0; i < (40-buffer_array.length()); i++) {
            buffer_array = buffer_array + " ";
        }
        // System.out.println("test"+buffer_array.length());
        for(int i=mem_ptr;i<100;i++){
            if(count>buffer_array.length()-1)
                break;
            for(int j=0;j<4;j++){
                if(buffer_array.charAt(count) == '\0')
                    ch = buffer_array.charAt(count++);
                // else
                //     ch = ' ';
                mem[i][j] = ch;
            }
            mem_ptr=i;
            if(mem_ptr >= 100){
                System.out.println("memory full");
                break;
            }
        }
        
        mem_ptr = (int) ((Math.ceil((mem_ptr)/10)+1)*10);
        // System.out.println("test"+mem_ptr);
    }

    static char[] read_loc(int address){
        // System.out.println("address"+address);
        return mem[address];
    }
    static void write_loc(int address,char[] data){
        // System.out.println("test="+String.valueOf(address));
        mem[address] = data;
        mem_ptr = address+1;
    }

    static void setRead(int addr){
        mem_ptr = addr;
    }
    static void setWrite(int addr){
        String result="";
        for(int i=0;i<10; i++){
            
            result = result+String.valueOf(mem[addr+i])+"\n";
        }
        try {
                FileWriter myWriter = new FileWriter("output.txt");
                myWriter.write(result+System.lineSeparator());
                
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
    }

    static public void writeBlock(int addr) {
        String str = Buffer.read_buffer();
        int len = str.length();
        for (int i = 0; i < 40-len; i++) {
            str = str + " ";
        }
        char[] entrydata = str.toCharArray();
        
        // System.out.println("test"+entrydata.length);
        // System.out.println("test"+String.valueOf(entrydata));
        int count = 0;
        for (int i = 0; i < 10 ; i++) {
            for (int j = 0; j < 4 ; j++) {
                // System.out.print("test"+mem[(addr + i)][j]);
                // System.out.println("test"+(addr + i));
                // if(entrydata[count] == '\0')
                    mem[(addr + i)][j] = entrydata[count++];
                // else
                //     mem[(addr + i)][j] = '*';
                    
                }
        }
    }

    static String readBlock(int addr) {
        String res="";
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 4; j++) {
                res += mem[addr+i][j];
            }
        }
        return res;
    }

    static void wipe() {
        for (int i = 0; i < mem.length; i++) {
            for (int j = 0; j < 4; j++) {
                mem[i][j]='/';
            }
        }
    }
}

class PCB{
    String jobid="";
    int TLL,TTL;
    int TTC,TLC;
}

public class phase2 {
    static int IC,pcbcounter=0,pagecounter=0;
    static char[] IR= new char[4];
    static char[] Register;
    static int SI,TI,PTR,PI;
    static int LLC,TTC,ttl,tll;
    static boolean C;
    static String errormsg;
    private static PCB[] pcbArray = new PCB[20];
    static int card_type,emflag;
    static HashMap<Integer, String> map1 = new HashMap<>();

    public static void initialization() {
        SI=0;
        TI=0;
        pagecounter=0;
        map1.clear();
        System.out.println("Wiping Memory");
        Memory.wipe();
    }

    public static int allocate() {
        int rand=0,valid=0;
        char[] temp = new char[4];
        

        while(valid == 0){
            rand = (int) (Math.random()*(29))+1;
            
            if(!(map1.containsKey(rand))){
                temp = Memory.read_loc(rand*10);
                for(char i : temp){
                    if(i == '/'){
                        valid = 1;
                        map1.put(rand,"valid");
                    }
                }
            }
            
        }
        
        return rand; 
    }
    
    public static void initializepagetable() {
        PTR = allocate()*10;  
        char[] ch = "    ".toCharArray();
        Memory.write_loc(PTR, ch);
        LLC=0;
        TTC=0;
    }

    private static void intializePCB(String jobid,int ttl,int tll,int ttc,int tlc) {
        PCB pcb = new PCB();
        pcb.jobid = jobid;
        pcb.TTL = ttl;
        pcb.TLL = tll;
        pcb.TTC = ttc;
        pcb.TLC = tlc;

        pcbArray[pcbcounter] = pcb;
        pcbcounter++;
    }

    public static void updatepagetable(int addr) {
        int memadr1 = allocate()*10;
        String memadr2 = String.valueOf(memadr1);
        int len = memadr2.length();
        //  System.out.println("test="+memadr2);
        // System.out.println("test="+memadr2.length());
        for (int i = 0; i < (4-len); i++) {
            memadr2 = "0"+memadr2;
        }
        // System.out.println("test="+memadr2);
        // System.out.println("test="+memadr2.length());
        Memory.write_loc(addr, memadr2.toCharArray());
        // String addr = String.valueOf(memadr1);
        // System.out.println("test"+Integer.parseInt(addr));
        // Memory.writeBlock(Integer.parseInt(addr));
        // Buffer.write_buffer("                                        ");
        Memory.writeBlock(memadr1);
        pagecounter++;
    }

    

    public static void Terminate(int em, Writer myWriter) throws IOException {
        emflag=em;
        switch(em){
            case 0: errormsg = "No error";
                    break;
            case 1: errormsg = "Out of Data";
                    break;
            case 2: errormsg = "Line Limit Exceeded";
                    break;
            case 3: errormsg = "Time Limit Exceeded";
                    break;
            case 4: errormsg = "Operation Code Error";
                    break;
            case 5: errormsg = "Operand Error";
                    break;
            case 6: errormsg = "Invalid Page Fault ";
                    break;
            default: break;
        }
        myWriter.write(errormsg+System.lineSeparator());
        System.out.println(errormsg);
    }

    static int check_card(String check_str){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("$A",2);
        map.put("$D",3);
        map.put("$E",4);
        if (check_str.length() >= 2) {
            String substr = check_str.substring(0, 2);
            if (map.containsKey(substr)) {
                return map.get(substr);
            }
        }
        return 0;
    }

    public static int addressMap(int VA) {
        int RA=0,pte;
        pte = PTR + (VA)/10;
        //if mem[pte] == **** then its a page fault
        String mem_pte = String.valueOf(Memory.read_loc(pte));
        // System.out.println("test"+String.valueOf(Memory.read_loc(pte)));
        // System.out.println("test"+(String.valueOf(Memory.read_loc(pte)).length()));
        if(!(mem_pte.contains("/"))){
            if(String.valueOf(Memory.read_loc(pte)).contains("GD")){
                Memory.read();
                System.out.println("PTR="+PTR);
                // System.out.println("PTR="+PTR);
            }
            RA = Integer.parseInt(String.valueOf(Memory.read_loc(pte)))+ VA%10;
        }else 
            RA = -1;
        // System.out.println("addr test"+pte+"-"+RA+"-"+VA);
        // if(RA == -1){
        //     Memory.read();
        //     System.out.println("PTE="+pte);
        // }
        return RA;
    }

    static int count=0;
    static void displayJOBdetails(){
        System.out.println("JOB ID - "+pcbArray[count++].jobid);
        System.out.println("IC - "+IC);
        System.out.println("IR - "+String.valueOf(IR));
        System.out.println("TTC - "+TTC);
        System.out.println("LLC - "+LLC);
    }

    static void startExecution(Scanner scanner,Writer myWriter) throws IOException{
        String pre_IR,operand_data_addr;
        IC=0;
        //intitialize the units place digit of PTR to 0 
        
        //for gd and sr page fault is valid
        //for rest all it is invalid
        while(SI != 3 ){

            // if(addressMap(IC)== -1){
            //     System.out.println("test"+IC);
            //     System.out.println(addressMap(IC));
            //     Memory.read();
            // }
            // System.out.println("IC="+IC);
            pre_IR = String.valueOf(Memory.read_loc(addressMap(IC)));
            
            //normalising the preIR to length 4
            // System.out.println("test"+pre_IR.length());
            //System.out.println("test"+String.valueOf(pre_IR));
            for (int i = 0; i < 4-pre_IR.length(); i++) {
                pre_IR = "0"+pre_IR;
            }
            IR = pre_IR.toCharArray();
            // System.out.println("test"+IR.length);
            // System.out.println("test="+String.valueOf(IR));
            // Memory.read();
            String opcode=String.valueOf(IR[0])+String.valueOf(IR[1]);
            String operand=String.valueOf(IR[2])+String.valueOf(IR[3]);
            //checking operand error and page fault
            if(!Pattern.matches("[0-9][0-9]", operand))
            {
                if(IR[0] == 'H'){
                    TTC++;
                    SI=3;
                    System.out.println();
                    System.out.println();
                    Terminate(0,myWriter);
                    break;
                }
                PI = 2;
                if(TI == 2)
                    Terminate(3,myWriter);
                IC++;
                Terminate(5,myWriter);
                return;
            }else{
                IC++;
                int RA = addressMap(Integer.parseInt(operand));
                // operand_data_addr = String.valueOf(addressMap(Integer.parseInt(operand)));
                // System.out.println("operand="+operand);
                // System.out.println("opcode="+opcode);
                // System.out.println("operand="+operand);
                if(RA == -1){
                    PI = 3;
                    
                    if(TI == 2){
                        Terminate(3,myWriter);
                        return;
                    }else{
                        
                        if(opcode.equals("GD") || opcode.equals("SR")){ 
                           
                            // Memory.read();
                            // System.out.println("========================");
                            Buffer.write_buffer("                                        ");
                            updatepagetable((PTR+Integer.parseInt(operand)/10));
                            // Memory.read();
                            // IC--;
                        }else{
                            Terminate(6,myWriter);
                            break;
                        }
                    }
                }
                if(addressMap(Integer.parseInt(operand)) == -1){
                    System.out.println("test"+IC);
                    System.out.println("test="+addressMap(IC));
                     System.out.println("opcode="+opcode);
                         System.out.println("operand="+operand);
                    // Memory.read();
                }
                operand_data_addr = String.valueOf(addressMap(Integer.parseInt(operand)));
                
                // System.out.println("test="+opcode);
                // System.out.println("test="+operand_data_addr);
                switch(opcode){
                    case "LR": TTC++;
                                Register=Memory.read_loc(Integer.parseInt(operand_data_addr));
                                // System.out.println("reg="+String.valueOf(Register));
                        break;
                    case "SR": TTC++;
                                // System.out.println("reg="+Register.length);
                                
                                Memory.write_loc(Integer.parseInt(operand_data_addr), Register);
                                // System.out.println("reg="+String.valueOf(Register));
                                // Register="   ".toCharArray();
                                break;
                    case "CR": if(Register == Memory.read_loc(Integer.parseInt(operand_data_addr))){
                                    C = true;
                                }else{
                                    C = false;
                                }
                        break;
                    case "BT": TTC++;
                                if(C == true){
                                    IC = Integer.parseInt(operand_data_addr);
                                }
                        break;
                    case "GD": 
                                SI=1;
                                if(TI == 0){
                                //System.out.println("test1");
                                // scanner.nextLine();
                                String data = scanner.nextLine();
                                
                                
                                if(data.length()>=2){
                                    
                                    if(data.contains("$E"))
                                    {
                                        Terminate(1,myWriter);
                                        Buffer.write_buffer(data);
                                        return;
                                    }
                                }
                                TTC+=1;
                                Buffer.write_buffer(data);
                                // Memory.setRead(Integer.parseInt(operand_data_addr));
                                // System.out.println("test=="+Buffer.read_buffer());
                                // System.out.println("test"+Integer.parseInt(operand_data_addr));
                                Memory.writeBlock(Integer.parseInt(operand_data_addr));
                                
                                }else if(TI == 2){
                                    Terminate(3,myWriter);
                                    return;
                                }
                                
                        break;
                    case "PD": TTC++;
                                if(TI == 2){
                                    Terminate(3,myWriter);
                                    return;
                                }
                                LLC++;
                                SI=2; 
                                if(LLC > tll){
                                    Terminate(2,myWriter);
                                    LLC--;
                                    return;
                                }
                                
                                // System.out.println("==========");
                                // Memory.setWrite(Integer.parseInt(operand_data_addr));
                                
                                // System.out.println("operand addr="+operand_data_addr);
                                // Memory.read();
                                myWriter.write(Memory.readBlock(Integer.parseInt(operand_data_addr))+System.lineSeparator());
                                System.out.println(Memory.readBlock(Integer.parseInt(operand_data_addr)));
                                // System.out.println("==========");
                                
                        break;
                    case "H ": TTC++;
                                SI=3;
                                System.out.println();
                                System.out.println();
                                IC++;
                                Terminate(0,myWriter);
                                
                        break;
                    default:   PI=1;
                                if(TI == 2)
                                    Terminate(3,myWriter); 
                                Terminate(4,myWriter);
                                return;
                                
                    
                }
            }
            if(TTC > ttl) {
                TI =2;
                TTC--;
                Terminate(3,myWriter);
                break;
            }
            // Memory.read();
            // System.out.println("test-"+SI+"test-"+PI);
        }
        
    }
	public static void main(String arg[]) throws IOException
	{   String job =" ";
         FileWriter myWriter = new FileWriter("outputphase2.txt");
        
         try {
			Scanner scanner = new Scanner(new File("file2.txt"));
            Buffer.write_buffer(scanner.nextLine());
                
			while (scanner.hasNextLine()) {
                card_type = check_card(Buffer.read_buffer());
                // System.out.println("test"+card_type);
                switch(card_type){
                    case 2: initialization();
                            int count=0;
                            char[] amjline = Buffer.read_buffer().toCharArray();
                            job = String.valueOf(amjline[4])+String.valueOf(amjline[5])+String.valueOf(amjline[6])+String.valueOf(amjline[7]);
                            ttl= Integer.parseInt(String.valueOf(amjline[8])+String.valueOf(amjline[9])+String.valueOf(amjline[10])+String.valueOf(amjline[11]));
                            tll = Integer.parseInt(String.valueOf(amjline[12])+String.valueOf(amjline[13])+String.valueOf(amjline[14])+String.valueOf(amjline[15]));
                            // System.out.println("test"+job);
                            intializePCB(job, ttl,tll,0,0);
                            initializepagetable();

                            while(card_type == 2){
                                Buffer.write_buffer(scanner.nextLine());
                                if(check_card(Buffer.read_buffer()) != 0 && check_card(Buffer.read_buffer()) != 2){
                                    card_type = check_card(Buffer.read_buffer());
                                }else{
                                    updatepagetable(PTR+count);
                                    // Memory.writeBlock(Integer.parseInt(String.valueOf(Memory.read_loc(PTR+count))));
                                
                                    count++;
                                    // Memory.write_mem(Buffer.read_buffer());
                                }
                            }
                            // System.out.println("card_type="+card_type);
                            break;
                    case 3: //while(card_type == 3){
                            //     Buffer.write_buffer(scanner.nextLine());
                            //     if(check_card(Buffer.read_buffer()) != 0 && check_card(Buffer.read_buffer()) != 3){
                            //         card_type = check_card(Buffer.read_buffer());
                            //         break;
                            //     }else{
                            //         Memory.write_mem(Buffer.read_buffer());
                            //     }
                            // }
                            // IC=0;
                            System.out.println("Executing... ");
                            
                            startExecution(scanner,myWriter);
                            if(emflag != 1)
                                Buffer.write_buffer(scanner.nextLine());
                            System.out.println("Done Executing... ");
                                                      
                            break;
                    case 4: Buffer.write_buffer(scanner.nextLine());
                            // Terminate(1);
                            // System.out.println("Printing the Memory"); 
                            displayJOBdetails();
                            myWriter.write("=============NEXT PROGRAM================"+System.lineSeparator());
                            System.out.println("=============NEXT PROGRAM================");
                            // Memory.read();
                            initialization();
                            break;

                    default: Buffer.write_buffer(scanner.nextLine());
                            // card_type = check_card(scanner.nextLine());
                            // System.out.println("card_type="+card_type);
                            break;
                }
			}
            displayJOBdetails();
            // Memory.read();
            System.out.println("Done with writing in memory....");
			scanner.close();
            myWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
}
