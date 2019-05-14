import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WebWorker extends Thread {

    private DefaultTableModel model;
    private String urlString;
    private int x, y;
    private Semaphore callThatEnded;
    private JLabel running, completed;
    private JProgressBar bar;

    public WebWorker(String urlString, DefaultTableModel model, int x, int y, JLabel running, JLabel completed, Semaphore callThatEnded, JProgressBar bar){
        this.urlString = urlString;
        this.model = model;
        this.x = x;
        this.y = y;
        this.callThatEnded = callThatEnded;
        this.running = running;
        this.completed = completed;
        this.bar = bar;
    }

    @Override
    public void run() {
        updateLabel(running,1);
        InputStream input = null;
        StringBuilder contents = null;
        try {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            // Set connect() to throw an IOException
            // if connection does not succeed in this many msecs.
            connection.setConnectTimeout(5000);

            connection.connect();
            input = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            char[] array = new char[1000];
            long sizeOfFileDownloaded = 0;
            int len;
            contents = new StringBuilder(1000);
            boolean ind = false;
            while ((len = reader.read(array, 0, array.length)) > 0) {
                if (isInterrupted()){
                    ind = true;
                    setModelValue("interrupted");
                    break;
                }
                contents.append(array, 0, len);
                Thread.sleep(100);
                sizeOfFileDownloaded += len * 2;
            }
            if (!ind) {
                endTime = System.currentTimeMillis();
                Date date = new Date();
                String strDateFormat = "HH:mm:ss";
                DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                String formattedDate = dateFormat.format(date);
                setModelValue("" + formattedDate + " " + (endTime - startTime) + "ms " + sizeOfFileDownloaded + " bytes");
            }
        }
        // Otherwise control jumps to a catch...
        catch (MalformedURLException ignored) {
            setModelValue("err");
        } catch (InterruptedException exception) {
            setModelValue("interrupted");
        } catch (IOException ignored) {
            setModelValue("err");
        }
        // "finally" clause, to close the input stream
        // in any case
        finally {
            try {
                if (input != null) input.close();
            } catch (IOException ignored) {
                setModelValue("err");
            }
        }

        updateLabel(running,-1);
        updateLabel(completed, 1);
        callThatEnded.release();
        synchronized (bar) {
            bar.setValue(bar.getValue() + 1);
        }
    }

    /**
     * This method sets value of model grid at str
     *
     * @param str string to which grid should be set
     * */
    private void setModelValue(String str) {
        synchronized (model){
            model.setValueAt(str, x, y);
        }
    }

    /**
     * This mwthod updates value of the specified JLabel by pl
     *
     * @param label label to be updated
     * @param pl integet by which value should be updated
     * */
    private void updateLabel(JLabel label, int pl) {
        synchronized (label) {
            String val = label.getText();
            int next = Integer.parseInt(val) + pl;
            label.setText("" + next);
        }
    }
}