package df;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.*;

public class PExecuteChen {

    public static void main(String args[]) throws FileNotFoundException, IOException {
        FileInputStream inputStream;
        Scanner sc;
        int sizeList, p; // sizelist -> tamanho da janela / p-> processo monitor
        long margin; //margem de segurança
        String trace; // nome do arquivo de traços
        String[] sArray; // para ler os dados da linha
        String line; //linha inteira

        inputStream = new FileInputStream("execplan.txt");
        sc = new Scanner(inputStream, "UTF-8");

        File arq = new File("saida.txt");
        arq.delete();
        arq.createNewFile();
        FileWriter fileWriter = new FileWriter(arq, true);
        PrintWriter printWriter = new PrintWriter(fileWriter); 

        line = sc.nextLine();
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            sArray = line.split(";");
            //<server>;<window size>;<margin>
            if (!sArray[0].equals("#")) {
                p = Integer.valueOf(sArray[0]); // process monitor
                sizeList = Integer.valueOf(sArray[1]);    // size window
                margin = Long.valueOf(sArray[2]); // safety margin
                trace = "D:\\trace.txt";                
                System.out.println(p + "|" + sizeList + "|" + margin + "|" + trace);
                //printWriter.print("------------ MARGIN: "+ margin +"\n");
                printWriter.flush();
                
                DFTeste test = new DFTeste(sizeList, margin, trace);
                test.execute();
            }
        }
        printWriter.close();
    }
}
