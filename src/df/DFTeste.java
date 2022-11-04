package df;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

public class DFTeste extends Thread {

    private int sizeList = 0;
    private final long margin;
    private final String trace;
    // para armazenar os tempos dos heartbeats de cada nodo 
    private Queue<Long>[] A;
    private final long delta = 100000000; //nanoseconds = 100 miliseconds
    private long EA = 0; 

    public DFTeste(int sizeList, long margin, String trace) {
        this.sizeList = sizeList;
        this.margin = margin;
        this.trace = trace;
        this.A = new Queue[10];
        for (int j = 0; j < 10; j++) {
            this.A[j] = new LinkedList<>(); //uma lista para cada nodo
        }
    }
    public void execute() throws FileNotFoundException, IOException {
        // OutputStream os;
        // BufferedWriter bw;
        // OutputStreamWriter osw;
        FileInputStream inputStream;
        Scanner sc ;
        String[] stringArray; // para ler a linha
        long[] timeout;
        int sizeList, id = 0, lin = 1;
        long ts = 0; 
        // ts -> timestamp atual
        long[] tPrevious; // para armazenar o último tempo
        long[] tInit; // para armazenar o tempo inicial
        long[] nErros; // para armazenar o numero de erros
        long[] tErros; // para armazenar o tempo de erros

        String line;
        timeout = new long[10];
        tPrevious = new long[10];
        tInit = new long[10];
        nErros = new long[10];
        tErros = new long[10];
        double tTotal = 0;
        //Fluxo de saida de um arquivo
        //File dir = new File("D:\\");
        File arq = new File("saida.txt");
        
        FileWriter fileWriter = new FileWriter(arq, true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        
        NumberFormat f = new DecimalFormat("0.000000000000000");
        NumberFormat f2 = new DecimalFormat("0.#");
        try {
            inputStream = new FileInputStream(trace);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                
                stringArray = line.split(" ");
                id = Integer.valueOf(stringArray[0]);
                ts = Long.valueOf(stringArray[3]);
                sizeList = A[id].size();
               
                EA = (long) computeEA(sizeList, id);
                timeout[id] = EA + margin;
                  //System.out.println("["+ id + "] => " + ts + " - " + timeout[id] + " - Margin: " + margin);
                if ((ts > timeout[id]) && (!A[id].isEmpty())) {
                    /// heartbeat chegou depois da estimativa  
                    /// coloca como suspeito
                    tErros[id]+=ts-timeout[id];
                    nErros[id]++;
                } else {
                    
                }

                if (A[id].size() == this.sizeList) {
                    A[id].poll();
                }
                A[id].add(ts);
                if(tInit[id]==0){
                    tInit[id]=ts;
                    if(tTotal==0){
                        tTotal=ts;
                    }
                }
                tPrevious[id] = ts; // último ts do id
                lin++;
            }// eof
            tTotal=ts-tTotal;
            //escreve bonito
            // for(int x=0; x<10; x++){
            //     if(x!=1){
            //         printWriter.print("["+x+ "] --> Numero de erros: "+ f2.format((double)nErros[x])+"\n");
            //         printWriter.print("["+x+ "] --> Tempo total: " + f2.format((double)tPrevious[x]-(double)tInit[x])+"\n");
            //         printWriter.print("["+x+ "] --> Taxa de erro: " + f.format((double)nErros[x]/(double)tTotal*1000000000) +"\n");
            //         printWriter.print("["+x+ "] --> Tempo de erro: " + f2.format((double)tErros[x])+"\n");
            //         printWriter.print("["+x+ "] --> Probabilidade de acuracia: " + (f.format(1-((double)tErros[x]/((double)tPrevious[x]-(double)tInit[x]))))+"\n");
            //         printWriter.print("---\n");
            //     }       
            // }
            //escreve feio para planilha
            for(int x=0; x<10; x++){
                if(x!=1){
                    printWriter.print(x+";"+this.sizeList+";"+margin+";");
                    printWriter.print(f2.format((double)nErros[x])+";");
                    printWriter.print(f2.format((double)tPrevious[x]-(double)tInit[x])+";");
                    printWriter.print(f.format((double)nErros[x]/(double)tTotal*1000000000)+";");
                    printWriter.print(f2.format((double)tErros[x])+";");
                    printWriter.print((f.format(1-((double)tErros[x]/((double)tPrevious[x]-(double)tInit[x]))))+"\n");
                }       
            }
            printWriter.flush();
            printWriter.close();
            System.out.println("O arquivo saida.txt foi criado com as informações da leitura das 'traces'");
            sc.close();
            inputStream.close();
        } catch (Exception ex) {
            Logger.getLogger(DFTeste.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double computeEA(long heartbeat, int id) {
        //id of node
        //heartbeat = highest number of heartbeat sequence received
        double tot = 0, avg = 0;
        int i = 0;
        long ts;
        try {
            NumberFormat f = new DecimalFormat("0.0");
            Queue<Long> q = new LinkedList();
            q.addAll(A[id]);
            while (!q.isEmpty()) {
                ts = q.poll();
                i++;
                tot += ts - (delta * i);
            }
            if (heartbeat> 0) {
                avg = ((1 / (double) heartbeat) *
                        ((double) tot)) + (((double) heartbeat + 1) * delta);
            }
            return avg;
        } catch (Exception e) {
            System.out.println("ERRO " + e.getMessage());
            return 0;
        }
    }

}

                    















