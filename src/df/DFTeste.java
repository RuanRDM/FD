package df;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public void execute() {
        OutputStream os;
        BufferedWriter bw;
        OutputStreamWriter osw;
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
        
        NumberFormat f = new DecimalFormat("0.000000000000000");
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
                  System.out.println("["+ id + "] => " + ts + " - " + timeout[id] + " - Margin: " + margin);
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
                }
                tPrevious[id] = ts; // último ts do id
                lin++;
            }// eof
            System.out.println("NÚMERO DE ERROS DO ID");
            for(int x=0; x<10; x++){
                if(x!=1)
                    System.out.println("["+x+ "] --> "+ nErros[x]);
            }
            System.out.println("TEMPO TOTAL DO ID");
            for(int x=0; x<10; x++){
                if(x!=1)
                    System.out.println("["+x+ "] --> Tempo final: " + tPrevious[x] + " Tempo Inicial: " + tInit[x] + " Tempo total: " + (tPrevious[x]-tInit[x]));
            }
            System.out.println("TAXA DE ERRO DO ID");
            for(int x=0; x<10; x++){
                if(x!=1)
                    System.out.println("["+x+ "] --> " + (nErros[x]/(tPrevious[x]-tInit[x])/1000000000));
            }
            System.out.println("TEMPO DE ERRO DO ID");
            for(int x=0; x<10; x++){
                if(x!=1)
                    System.out.println("["+x+ "] --> " + tErros[x]);
            }
            System.out.println("PROBABILIDADE DE ACURÁCIA");
            for(int x=0; x<10; x++){
                if(x!=1)
                    System.out.println("["+x+ "] --> " + (tErros[x]/(tPrevious[x]-tInit[x]))+"%");
            }
            
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

                    















