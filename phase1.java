import java.util.Scanner;   
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


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
    static char[][] mem = new char[100][4];
    static int mem_ptr=0;

    static void read(){
        int count=0;
        for(int i=0;i<mem_ptr;i++){
            System.out.print((count++)+" - ");
            for(int j=0;j<4;j++){
                System.out.print(mem[i][j]+"|");
            }
            if(i%10 == 9){
                System.out.println();
                System.out.println("----------");
            }else{
                System.out.println();
            }
            
        }
    }

    static void write_mem(String buffer_array){
        int count=0;
        for(int i=mem_ptr;i<100;i++){
            if(count>buffer_array.length()-1)
                break;
            for(int j=0;j<4;j++){
                char ch = buffer_array.charAt(count++);
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
        mem[address] = data;
        mem_ptr = address+1;
    }

    static void setRead(int addr){
        mem_ptr = addr;
    }
    static void setWrite(int addr){
        String result="";
        for(int i=0;i<10; i++){
            if(String.valueOf(mem[addr+i]) != " "){
                System.out.println("test"+String.valueOf(mem[addr+i]));
                result = result+String.valueOf(mem[addr+i])+"\n";
            }
        }
        try {
            FileWriter myWriter = new FileWriter("output.txt",true);
            if(result != ""){
                myWriter.write(result);
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

public class phase1 {
    static int IC;
    static char[] IR;
    static char[] Register;
    static int SI;
    static boolean C;

    static int check_card(String check_str){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("$A",2);
        map.put("$D",3);
        map.put("$E",4);
        String substr = check_str.substring(0,2);
        if(map.containsKey(substr)){
            return map.get(substr);
        }
        return 0;
    }

    static void startExecution(){
        while(SI != 3){
            
            IR = Memory.read_loc(IC);
            IC++;
            String check1=String.valueOf(IR[0])+String.valueOf(IR[1]);
            String check2=String.valueOf(IR[2])+String.valueOf(IR[3]);
            switch(check1){
                case "LR": Register=Memory.read_loc(Integer.parseInt(check2));
                    break;
                case "SR": Memory.write_loc(Integer.parseInt(check2), Register);
                    break;
                case "CR": if(Register == Memory.read_loc(Integer.parseInt(check2))){
                                C = true;
                            }else{
                                C = false;
                            }
                    break;
                case "BT": if(C == true){
                                IC = Integer.parseInt(check2);
                            }
                    break;
                case "GD": SI=1;
                            //System.out.println("test1");
                            Memory.setRead(Integer.parseInt(check2));
                    break;
                case "PD": SI=2; 
                            System.out.println("==========");
                            Memory.setWrite(Integer.parseInt(check2));
                            System.out.println("==========");
                    break;
                case "H ": SI=3;
                            System.out.println();
                            System.out.println();
                    break;
                default: break;
            }
        }
    }
	public static void main(String[] args)
	{
        try {
			Scanner scanner = new Scanner(new File("file1.txt"));
            Buffer.write_buffer(scanner.nextLine());
            //run loop till the end of file
			while (scanner.hasNextLine()) {
                int card_type = check_card(Buffer.read_buffer());
                //System.out.println("test"+card_type);
                switch(card_type){
                    case 2: 
                            while(card_type == 2){
                                Buffer.write_buffer(scanner.nextLine());
                                if(check_card(Buffer.read_buffer()) != 0 && check_card(Buffer.read_buffer()) != 2){
                                    card_type = check_card(Buffer.read_buffer());
                                }else{
                                    Memory.write_mem(Buffer.read_buffer());
                                }
                            }
                            // System.out.println("card_type="+card_type);
                            break;
                    case 3: while(card_type == 3){
                                Buffer.write_buffer(scanner.nextLine());
                                if(check_card(Buffer.read_buffer()) != 0 && check_card(Buffer.read_buffer()) != 3){
                                    card_type = check_card(Buffer.read_buffer());
                                    break;
                                }else{
                                    Memory.write_mem(Buffer.read_buffer());
                                }
                            }
                            IC=0;
                            System.out.println("Executing... ");
                            startExecution();
                            System.out.println("Done Executing... ");
                            break;
                    case 4: scanner.nextLine();
                            System.out.println("Printing the Memory");
                            //Memory.read(); 
                            System.out.println("NEXT PROGRAM");
                            break;

                    default: break;
                }
                System.out.println("card_type"+card_type);
			}
            System.out.println("Done with writing in memory....");
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
}
