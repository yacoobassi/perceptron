import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Perceptron extends JFrame implements MouseListener, MouseMotionListener, ChangeListener {
    private JPanel pnlCanvas;
    private JButton btnLearn;
    private JButton btnClear;
    private JLabel lblLearningRate;
    private JSlider trackLearningRate;
    private List<Sample> samples;
    private Graphics objGraphics;
    private double[] weights;
    private double learningRate;
    private JButton btnAddSample;
    private JComboBox<String> cmbSampleClass;
    private int maxIterations;
    private Random rnd; // Declare Random object


    public Perceptron() {
        samples = new ArrayList<>();
        objGraphics = null;
        weights = new double[2];
        learningRate = 0.1;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Perceptron");
        setSize(600, 470);
        setLayout(null);
        rnd = new Random(); // Initialize Random object

        pnlCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                objGraphics = g;
                objGraphics.setColor(Color.WHITE);
                objGraphics.fillRect(0, 0, pnlCanvas.getWidth(), pnlCanvas.getHeight());
                objGraphics.setColor(Color.GRAY);
                objGraphics.drawLine(0, pnlCanvas.getHeight() / 2, pnlCanvas.getWidth(), pnlCanvas.getHeight() / 2);
                objGraphics.drawLine(pnlCanvas.getWidth() / 2, 0, pnlCanvas.getWidth() / 2, pnlCanvas.getHeight());
                drawSeparationLine();
                drawSamples();
            }
        };
        pnlCanvas.setBounds(10, 10, 400, 400);
        pnlCanvas.addMouseListener(this);
        pnlCanvas.addMouseMotionListener(this);
        add(pnlCanvas);

        btnLearn = new JButton("Learn");
        btnLearn.setBounds(420, 10, 80, 30);
        btnLearn.addActionListener(e -> btnLearn_Click());
        add(btnLearn);

        btnClear = new JButton("Clear");
        btnClear.setBounds(420, 50, 80, 30);
        btnClear.addActionListener(e -> btnClear_Click());
        add(btnClear);

        lblLearningRate = new JLabel("Learning Rate: 0.5");
        lblLearningRate.setBounds(420, 90, 120, 30);
        add(lblLearningRate);

        trackLearningRate = new JSlider();
        trackLearningRate.setBounds(420, 130, 120, 30);
        trackLearningRate.setMinimum(1);
        trackLearningRate.setMaximum(1000);
        trackLearningRate.setValue(500);
        trackLearningRate.addChangeListener(this);
        add(trackLearningRate);

        btnAddSample = new JButton("Add Sample");
        btnAddSample.setBounds(420, 170, 120, 30);
        btnAddSample.addActionListener(e -> btnAddSample_Click());
        add(btnAddSample);

        cmbSampleClass = new JComboBox<>(new String[]{"Red", "Blue", "Yellow", "Green"});
        cmbSampleClass.setBounds(420, 210, 120, 30);
        add(cmbSampleClass);
        setVisible(true);
    }

    private void btnLearn_Click() {
        int iterations = 0;
        double error;
        maxIterations = 10000;
        weights[0]=0.2;
        weights[1]=0.0;


        while (iterations < maxIterations) {
            for (Sample sample : samples) {
                double X = sample.getX1() * weights[0] + sample.getX2() * weights[1] - 0.2;
                double desiredClass = sample.getSampleClass();
                double predictedClass = classify(X);
                error = desiredClass - predictedClass;

                weights[0] += 0.1 * error * sample.getX1();
                weights[1] += 0.1 * error * sample.getX2();
            }
            pnlCanvas.repaint();
            iterations++;
        }


    }

    public static double classify(double x) {
       return  x>=0?1:0;
    }

    private void drawSeparationLine() {
        objGraphics.setColor(Color.GRAY);
        objGraphics.drawLine(0, pnlCanvas.getHeight() / 2, pnlCanvas.getWidth(), pnlCanvas.getHeight() / 2);
        objGraphics.drawLine(pnlCanvas.getWidth() / 2, 0, pnlCanvas.getWidth() / 2, pnlCanvas.getHeight());

        Graphics2D g2d = (Graphics2D) objGraphics;
        g2d.setColor(Color.GREEN);

        double x1 = -10;
        double y = (0.2 / weights[1]) - (weights[0] / weights[1]) * x1;
        double shift = pnlCanvas.getHeight() / 2;
        Point p1 = new Point(0, (int) (shift - y * pnlCanvas.getHeight() / 20));

        x1 = 10;
        double y2 = (0.2 / weights[1]) - (weights[0] / weights[1]) * x1;
        Point p2 = new Point(pnlCanvas.getWidth(), (int) (shift - y2 * pnlCanvas.getHeight() / 20));

     
            objGraphics.drawLine(p1.x, p1.y, p2.x, p2.y);
        
    }

    private void drawSamples() {
        for (Sample sample : samples) {
            int x = (int) (sample.getX1() * pnlCanvas.getWidth() / 20 + pnlCanvas.getWidth() / 2);
            int y = (int) (pnlCanvas.getHeight() / 2 - sample.getX2() * pnlCanvas.getHeight() / 20);

            Color dotColor;
            if(sample.getSampleClass()==0) {
                dotColor = Color.RED;

            }
            else if(sample.getSampleClass()==0.25) {
                dotColor = Color.BLUE;

            }
            else  if(sample.getSampleClass()==0.5) {
                dotColor = Color.YELLOW;

            }
            else if(sample.getSampleClass()==0.75) {
                dotColor = Color.GREEN;

            }
            else {
            	 dotColor = Color.black;
            }

            objGraphics.setColor(dotColor);
            objGraphics.fillOval(x - 3, y - 3, 6, 6);
        }
    }

    private void btnClear_Click() {
        samples.clear();
        pnlCanvas.repaint();
    }

    private void btnAddSample_Click() {
        double x1 = ((double) rnd.nextInt(pnlCanvas.getWidth()) - pnlCanvas.getWidth() / 2) * 20 / pnlCanvas.getWidth();
        double x2 = ((double) (pnlCanvas.getHeight() / 2 - rnd.nextInt(pnlCanvas.getHeight()))) * 20 / pnlCanvas.getHeight();
        int sampleClass = cmbSampleClass.getSelectedIndex() - 1;
        Sample sample = new Sample(x1, x2, sampleClass);
        samples.add(sample);
        pnlCanvas.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            double x1 = (e.getX() - pnlCanvas.getWidth() / 2) * 20.0 / pnlCanvas.getWidth();
            double x2 = (pnlCanvas.getHeight() / 2 - e.getY()) * 20.0 / pnlCanvas.getHeight();

    

            samples.add(new Sample(x1, x2, cmbSampleClass.getSelectedIndex()*0.25));
            pnlCanvas.repaint();
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        // Not implemented
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Not implemented
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not implemented
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not implemented
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Not implemented
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Not implemented
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == trackLearningRate) {
            learningRate = (double) trackLearningRate.getValue() / 1000.0;
            lblLearningRate.setText("Learning Rate: " + learningRate);
        }
    }

    public static void main(String[] args) {
        new Perceptron();
    }
}

class Sample {
private double x1;
private double x2;
private double sampleClass;

public Sample(double x1, double x2, double sampleClass) {
    this.x1 = x1;
    this.x2 = x2;
    this.sampleClass = sampleClass;
}

public double getX1() {
    return x1;
}

public double getX2() {
    return x2;
}

public double getSampleClass() {
    return sampleClass;
}
}
