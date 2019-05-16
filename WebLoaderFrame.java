import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class WebLoaderFrame extends JPanel {

    private JLabel running, completed, elapsed;
    private JPanel panel;
    private DefaultTableModel model;
    private JTable table;
    private JProgressBar progressBar;
    private JButton oneThreadB, multyThreadB, stop;
    private JTextField numberField;
    private ArrayList<String> list;
    private static Thread listenerThread = null;
    private static Semaphore listenerLock;

    private static final int MAX_THREADS = 4;
    private static final int FIELD_SIZE = 15;
    private static final int PANE_WIDTH = 600;
    private static final int PANE_HEIGHT = 400;
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 700;
    private static final String FILE_NAME = "links.txt";

    public WebLoaderFrame(){
        running = new JLabel("0");
        completed = new JLabel("0");
        elapsed = new JLabel("0");
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
        JPanel pOne = new JPanel(new FlowLayout());
        pOne.add(oneThreadB);
        add(pOne);
        add(Box.createRigidArea(new Dimension(0,3)));
        JPanel pMulty = new JPanel(new FlowLayout());
        pMulty.add(multyThreadB);
        add(pMulty);
        add(Box.createRigidArea(new Dimension(0,3)));
        JPanel p = new JPanel(new FlowLayout());
        p.add(numberField);
        add(p);
        add(Box.createRigidArea(new Dimension(0,3)));
        addLabel("Running: ", running);
        add(Box.createRigidArea(new Dimension(0,3)));
        addLabel("Completed: ", completed);
        add(Box.createRigidArea(new Dimension(0,3)));
        addLabel("Elapsed: ", elapsed);
        add(Box.createRigidArea(new Dimension(0,3)));
        add(progressBar);
        add(Box.createRigidArea(new Dimension(0,3)));
        JPanel pStop = new JPanel(new FlowLayout());
        pStop.add(stop);
        add(pStop);
        stop.setEnabled(false);

        addLinks();
        addListeners();

        listenerLock = new Semaphore(1);
    }

    /**
     * This method adds labels so as to be in the middle and also
     * number and text to be separated
     *
     * @param s string to which label to be followed
     * @param label JLabel which should ne added next to JLabel
     * */
    private void addLabel(String s, JLabel label) {
        JPanel helperPanel = new JPanel();
        helperPanel.setLayout(new FlowLayout());
        helperPanel.add(new JLabel(s));
        helperPanel.add(label);
        add(helperPanel);
    }

    /**
     * This method adds listeners of the buttons
     * */
    private void addListeners() {
        oneThreadB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    listenerLock.acquire();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                listenerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        running.setText("0");
                        completed.setText("0");
                        elapsed.setText("0");
                        oneThreadB.setEnabled(false);
                        multyThreadB.setEnabled(false);
                        long startTime = System.currentTimeMillis();
                        for (int i=0; i<list.size(); ++i){
                            model.setValueAt("", i, 1);
                        }
                        stop.setEnabled(true);
                        makeThreads(1);
                        long endTime = System.currentTimeMillis();
                        elapsed.setText("" + ((double)(endTime - startTime) / 1000) + " seconds");
                        oneThreadB.setEnabled(true);
                        multyThreadB.setEnabled(true);
                        stop.setEnabled(false);
                        progressBar.setValue(0);
                        listenerLock.release();
                    }
                });
                listenerThread.start();
            }
        });

        multyThreadB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    listenerLock.acquire();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                listenerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        running.setText("0");
                        completed.setText("0");
                        elapsed.setText("0");
                        oneThreadB.setEnabled(false);
                        multyThreadB.setEnabled(false);
                        long startTime = System.currentTimeMillis();
                        String s = numberField.getText();
                        if (!check(s)) {
                            oneThreadB.setEnabled(true);
                            multyThreadB.setEnabled(true);
                            listenerLock.release();
                            return;
                        }
                        stop.setEnabled(true);
                        for (int i = 0; i < list.size(); ++i) {
                            model.setValueAt("", i, 1);
                        }
                        makeThreads(Integer.parseInt("" + s.charAt(0)));
                        long endTime = System.currentTimeMillis();
                        elapsed.setText("" + ((double) (endTime - startTime) / 1000.0) + " seconds");
                        progressBar.setValue(0);
                        oneThreadB.setEnabled(true);
                        multyThreadB.setEnabled(true);
                        stop.setEnabled(false);
                        listenerLock.release();
                    }
                });
                listenerThread.start();
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenerThread.interrupt();
                try {
                    listenerThread.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
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

    private static final int QUEUE_SIZE = 4;

    /**
     * This method creates and runs worker threads
     *
     * @param n the maximum number of thread which  could be running simultaneously
     * */
    private void makeThreads(int n) {
        Semaphore lock = new Semaphore(n);
        int size = list.size();
        WebWorker[] workers = new WebWorker[size];
        int pos = 0;
        boolean ind = false;
        for (pos=0; pos<size; pos++){
            workers[pos] = new WebWorker(list.get(pos), model, pos, 1, running, completed, lock, progressBar);
            if (listenerThread.isInterrupted()){
                ind = true;
                break;
            }
            try {
                lock.acquire();
            } catch (InterruptedException e) {
                ind = true;
                break;
            }
            workers[pos].start();
        }
        if (!ind) {
            for (int i = 0; i < pos; ++i) {
                try {
                    workers[i].join();
                } catch (InterruptedException e) {
                    ind = true;
                    break;
                }
            }
        }
        if (ind){
            for (int i=0; i<pos; i++){
                workers[i].interrupt();
            }
            for (int i=0; i<pos; ++i){
                try {
                    workers[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
        JFrame mainFrame = new JFrame("WebLoader");
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