import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JCounter extends JPanel {
    private JButton start, stop;
    private JLabel label;

    /* the main counter thread*/
    private Thread counter;
    private JTextField field;

    /* JCounter id */
    private final int id;

    private static final int MAX_VALUE = 100000000;
    private static final int PERIOD = 10000;

    private static final int TEXT_FIELD_SIZE = 15;

    public JCounter(int id){
        this.id = id;

        start = new JButton("Start");
        stop = new JButton("Stop");
        field = new JTextField(TEXT_FIELD_SIZE);
        label = new JLabel("0");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new TitledBorder("Counter " + this.id));

        add(field);
        add(label);
        add(start);
        add(stop);
        add(Box.createRigidArea(new Dimension(0,40)));

        /* add start button ActionListener */
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter == null){
                    createNewThread();
                } else {
                    counter.interrupt();
                    try {
                        counter.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    createNewThread();
                }
            }
        });

        /* add stop button ActionListener */
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter != null){
                    counter.interrupt();
                    try {
                        counter.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    counter = null;
                }
            }
        });
    }

    /**
     * This method creates new runnable anonymous thread and starts it
     * */
    private void createNewThread() {
        counter = new Thread(new Runnable() {
            @Override
            public void run() {
                label.setText("0");
                String s = field.getText();
                int to = -1;
                if (check(s)){
                    to = Integer.parseInt(s);
                }
                for (int i=0; i<=(to == -1 ?MAX_VALUE:to); ++i){
                    if (i % PERIOD == 0){
                        label.setText(String.valueOf(i));
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    if (counter.isInterrupted()){
                        break;
                    }
                }
            }
        });
        counter.start();
    }

    /**
     * This method checks if the string is in integer format
     *
     * @param s string to be checked
     * @return true if string is integer and val(s) > 0 && val(s) < 10^9, false otherwise
     * */
    private boolean check(String s) {
        for (int i=0; i<s.length(); ++ i){
            if (!Character.isDigit(s.charAt(i))){
                return false;
            }
        }
        return s.length() < 10 && s.length() > 0;
    }

    private static final int APPLICATION_WIDTH = 200;
    private static final int APPLICATION_HEIGHT = 650;
    private static final int COUNTER_NUMBER = 4;


    /**
     * This method creates GUI of the JCounter
     * */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("JCount");
        frame.setLayout(new FlowLayout());
        frame.setSize(APPLICATION_WIDTH,APPLICATION_HEIGHT);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel table = new JPanel();
        table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
        frame.add(table);

        for (int i=1; i<=COUNTER_NUMBER; ++i){
            JCounter count = new JCounter(i);
            table.add(count);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
