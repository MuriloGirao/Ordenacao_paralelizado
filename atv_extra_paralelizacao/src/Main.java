import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Main extends JFrame {
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private final int[] SIZES = {10000, 50000, 100000};
    private final int[] THREADS = {2, 4, 8};
    private final int NUM_EXECUTIONS = 3;

    public Main() {
        super("Análise Algoritmos de Oredenação");

        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Tamanho", "Tipo", "Threads", "Execução", "Tempo (ms)"}, 0);
        resultTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton startButton = new JButton("Executar Testes");
        startButton.addActionListener(e -> new Thread(this::executarTestes).start());

        add(startButton, BorderLayout.SOUTH);
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void executarTestes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("C:\\Users\\muril\\Downloads\\atv_extra_paralelizacao\\src\\resultados.csv"))) {
            writer.println("Tamanho,Tipo,Threads,Execucao,Tempo(ms)");

            for (int size : SIZES) {
                int[] baseArray = gerarArrayAleatorio(size);

                // Sequencial
                for (int exec = 1; exec <= NUM_EXECUTIONS; exec++) {
                    int[] arr = Arrays.copyOf(baseArray, baseArray.length);
                    long start = System.nanoTime();
                    SequentialBubbleSort.sort(arr);
                    long end = System.nanoTime();
                    double timeMs = (end - start) / 1_000_000.0;

                    registrarResultado(writer, size, "Sequencial", 1, exec, timeMs);
                }

                // Paralelo
                for (int threadCount : THREADS) {
                    for (int exec = 1; exec <= NUM_EXECUTIONS; exec++) {
                        int[] arr = Arrays.copyOf(baseArray, baseArray.length);
                        long start = System.nanoTime();
                        ParallelBubbleSort.sort(arr, threadCount);
                        long end = System.nanoTime();
                        double timeMs = (end - start) / 1_000_000.0;

                        registrarResultado(writer, size, "Paralelo", threadCount, exec, timeMs);
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Testes finalizados! Dados salvos em resultados.csv.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registrarResultado(PrintWriter writer, int size, String tipo, int threads, int execucao, double tempo) {
        SwingUtilities.invokeLater(() -> {
            tableModel.addRow(new Object[]{size, tipo, threads, execucao, String.format("%.2f", tempo)});
        });
        writer.printf("Bubble,%d,%s,%d,%d,%.3f%n", size, tipo, threads, execucao, tempo);
    }

    private int[] gerarArrayAleatorio(int tamanho) {
        Random rand = new Random();
        int[] arr = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            arr[i] = rand.nextInt(1_000_000);
        }
        return arr;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
