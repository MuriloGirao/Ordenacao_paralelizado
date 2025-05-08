import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Main extends JFrame {
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private final int[] SIZES = {10000, 50000, 100000};
    private final int[] THREADS = {2, 4, 8};
    private final int NUM_EXECUTIONS = 1;

    public Main() {
        super("Análise de Algoritmos de Ordenação");

        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Tamanho", "Tipo", "Threads", "Tempo (ms)"}, 0);
        resultTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton startButton = new JButton("Executar Testes");
        startButton.addActionListener(e -> new Thread(this::executarTestes).start());
        buttonPanel.add(startButton);

        JButton chartButton = new JButton("Gerar Gráfico");
        chartButton.addActionListener(e -> gerarBarChart());
        buttonPanel.add(chartButton);

        add(buttonPanel, BorderLayout.SOUTH);
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void executarTestes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("resultados.csv"))) {
            writer.println("Tamanho,Tipo,Threads,Tempo(ms)");

            for (int size : SIZES) {
                int[] baseArray = gerarArrayAleatorio(size);

                // Sequencial
                int[] arr = Arrays.copyOf(baseArray, baseArray.length);
                long start = System.nanoTime();
                SequentialBubbleSort.sort(arr);
                long end = System.nanoTime();
                double timeMs = (end - start) / 1_000_000.0;
                registrarResultado(writer, size, "Sequencial", 1, timeMs);

                // Paralelo
                for (int threadCount : THREADS) {
                    arr = Arrays.copyOf(baseArray, baseArray.length);
                    start = System.nanoTime();
                    ParallelBubbleSort.sort(arr, threadCount);
                    end = System.nanoTime();
                    timeMs = (end - start) / 1_000_000.0;
                    registrarResultado(writer, size, "Paralelo", threadCount, timeMs);
                }
            }

            JOptionPane.showMessageDialog(this, "Testes finalizados! Dados salvos em resultados.csv.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registrarResultado(PrintWriter writer, int size, String tipo, int threads, double tempo) {
        SwingUtilities.invokeLater(() -> {
            tableModel.addRow(new Object[]{size, tipo, threads, String.format("%.2f", tempo)});
        });
        writer.printf("%d,%s,%d,%.3f%n", size, tipo, threads, tempo);
    }

    private int[] gerarArrayAleatorio(int tamanho) {
        Random rand = new Random();
        int[] arr = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            arr[i] = rand.nextInt(1_000_000);
        }
        return arr;
    }

    private void gerarBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            int size = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
            String tipo = tableModel.getValueAt(i, 1).toString();
            int threads = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            double tempo = Double.parseDouble(tableModel.getValueAt(i, 3).toString());

            String serie = tipo + " - " + size + " elementos";
            dataset.addValue(tempo, serie, String.valueOf(threads));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "Desempenho dos Algoritmos de Ordenação",
            "Threads",
            "Tempo (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        JFrame chartFrame = new JFrame("Desempenho dos Algoritmos");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
