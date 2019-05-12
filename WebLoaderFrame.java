import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WebLoaderFrame extends JPanel {

    private JLabel running, completed, elapsed;
    private JPanel panel;
    private DefaultTableModel model;
    private JTable table;
    private static JProgressBar progressBar;
    private JButton oneThreadB, multyThreadB, stop;
    private JTextField numberField;
    private int completedNumber, runNumber, startTIme, endTime;
    private ArrayList<String> list;
    private Thread[] threads;

    private static final int MAX_THREADS = 4;
    private static final int FIELD_SIZE = 5;
    private static final int PANE_WIDTH = 600;
    private static final int PANE_HEIGHT = 400;
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 700;
    private static final String FILE_NAME = "links.txt";

    public WebLoaderFrame(){
        runNumber = startTIme = endTime = completedNumber = 0;
        running = new JLabel("Running: 0");
        completed = new JLabel("Completed: 0");
        elapsed = new JLabel("Elapsed: 0");
        oneThreadB = new JButton("Single Thread Fetch");
        multyThreadB = new JButton("Concurent Fetch");
        stop = new JButton("Stop");
        numberField = new JTextField(FIELD_SIZE);
        panel = new JPanel();
        model = new DefaultTableModel(new String[] {"url", "status"}, 0);
        table = new JTable(model);
        progressBar = new JProgressBar();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(PANE_WIDTH, PANE_HEIGHT));
        panel.add(scrollPane);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(panel);
        add(oneThreadB);
        add(Box.createRigidArea(new Dimension(0,5)));
        add(multyThreadB);
        add(Box.createRigidArea(new Dimension(0,5)));
        add(numberField);
        add(Box.createRigidArea(new Dimension(0,5)));
        add(running);
        add(Box.createRigidArea(new Dimension(0,5)));
        add(completed);
        add(Box.createRigidArea(new Dimension(0,5)));
        add(elapsed);
        add(Box.createRigidArea(new Dimension(0,5)));
        add(progressBar);
        add(Box.createRigidArea(new Dimension(0,5)));
        add(stop);
        stop.setEnabled(false);

        addLinks();
        addListeners();
    }

    /**
     * This method adds listeners of the buttons
     * */
    private void addListeners() {
        oneThreadB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oneThreadB.setEnabled(false);
                multyThreadB.setEnabled(false);
                stop.setEnabled(true);
                makeThreads(1);
            }
        });

        multyThreadB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = numberField.getText();
                if (!check(s)){
                    return;
                }
                oneThreadB.setEnabled(false);
                multyThreadB.setEnabled(false);
                stop.setEnabled(true);
                makeThreads(Integer.parseInt("" + s.charAt(0)));
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oneThreadB.setEnabled(true);
                multyThreadB.setEnabled(true);
                stop.setEnabled(false);
            }
        });
    }

    /**
     * This method checks if the numvberFiled text is correct
     *
     * @param s string to be checked if it is integer less than or equal to MAX_THREADS
     * */
    private boolean check(String s) {
        return !(s.length() != 1
                || s.length() == 1 && !Character.isDigit(s.charAt(0))
                || s.length() == 1 && Integer.parseInt("" + s.charAt(0)) > MAX_THREADS);
    }

    private static final int QUEUE_SIZE = 10;

    /**
     * This method creates and runs worker threads
     * */
    private void makeThreads(int n) {
        threads = new Thread[n];
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        for (int i=0; i<n; ++i){
            threads[i] = new WebWorker(queue);
            threads[i].start();
        }
    }

    /**
     * This method reads links from file with FILE_NAME
     * */
    private void addLinks() {
        list = new ArrayList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(FILE_NAME));
            while (true){
                String s = reader.readLine();
                if (s == null || s.equals("")){
                    break;
                }
                list.add(s);
                model.addRow(new String[] {s});
            }
            progressBar.setMaximum(list.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is to create GUI
     * */
    private static void createAndShowGUI() {
        WebLoaderFrame mainPanel = new WebLoaderFrame();
        JFrame mainFrame = new JFrame();
        mainFrame.setLayout(new FlowLayout());
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}