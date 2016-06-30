package dibattista.tyler.coop.ffnet;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

/**
 * Created by Tyler on 21/06/2016.
 */
public class Visualization extends JFrame {

    private static final int NEURON_SIZE = 20;
    private static final int NEURON_DISTANCE = 25;
    private static final int NEURON_OUT_DISTANCE = 50;
    private final int outputStart;

    Organism organism;
    List<VizNeuron> vizInputNeurons, vizOutputNeurons, vizHiddenNeurons;
    List<Neuron> inputNeurons, outputNeurons, hiddenNeurons;
    int rows, cols;

    public Visualization(Population pop, int rowsNum, int colsNum){
        organism = pop.organisms.get(0);
        rows = rowsNum;
        cols = colsNum;
        outputStart = 1500 - rows*NEURON_OUT_DISTANCE;

        inputNeurons = organism.genome.inputNeurons;
        outputNeurons = organism.genome.outputNeurons;
        hiddenNeurons = organism.genome.hiddenNeurons;

        vizInputNeurons = new ArrayList<VizNeuron>();
        vizOutputNeurons = new ArrayList<VizNeuron>();
        vizHiddenNeurons = new ArrayList<VizNeuron>();
        initialize();

        setSize(1500,900);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setVisible(true);
    }

    private void initialize(){
        int i = 0;
        VizNeuron vn;
        for(Neuron n : organism.genome.outputNeurons){
            vn = new VizNeuron(n, outputStart + (i % rows) * NEURON_OUT_DISTANCE, (i / rows) * NEURON_OUT_DISTANCE);
            addConnections(vn);
            vizOutputNeurons.add(vn);
            i++;
        }


        /*int i = 0;
        for(Neuron n : organism.genome.inputNeurons){
            vizInputNeurons.add(new VizNeuron(n, (i % rows) * NEURON_DISTANCE, (i / rows) * NEURON_DISTANCE + 190));
            i++;
        }

        i = 0;
        for(Neuron n : organism.genome.outputNeurons){
            vizOutputNeurons.add(new VizNeuron(n, outputStart + (i % rows) * NEURON_OUT_DISTANCE, (i / rows) * NEURON_OUT_DISTANCE));
            i++;
        }

        i = 0;
        for(Neuron n : organism.genome.hiddenNeurons){
            vizHiddenNeurons.add(new VizNeuron(n, 450, i * NEURON_DISTANCE));
            i++;
        }*/
    }

    private void addConnections(VizNeuron vn){
        VizNeuron vnHidden, vnInput;
        int h = 0;
        outerLoop:
        for(Connection c : vn.neuron.connections){
            if(c.in.type == Neuron.NeuronTypes.INPUT){
                vnInput = new VizNeuron(c.in, (c.in.getId() % rows) * NEURON_DISTANCE, (c.in.getId() / rows) * NEURON_DISTANCE + 190);
                vn.inputs.add(new VizConnection(c, vnInput, vn));
                vizInputNeurons.add(vnInput);
            }else{
                for(VizNeuron vnIter : vizHiddenNeurons){
                    if(vnIter.neuron == c.in){
                        continue outerLoop;
                    }
                }
                vnHidden = new VizNeuron(c.in, 450, h * NEURON_DISTANCE);
                vn.inputs.add(new VizConnection(c, vnHidden, vn));
                vizHiddenNeurons.add(vnHidden);
                addConnections(vnHidden);
                h++;
            }
        }
    }

    public void setOrganism(Organism o){
        organism = o;
        vizInputNeurons.clear();
        vizOutputNeurons.clear();
        vizHiddenNeurons.clear();
        getGraphics().clearRect(0, 0, getWidth(), getHeight());
        initialize();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        for(VizNeuron vn : vizInputNeurons){
            g.drawOval(vn.xPos, vn.yPos, NEURON_SIZE, NEURON_SIZE);
        }

        for(VizNeuron vn : vizHiddenNeurons){
            g.setColor(Color.BLACK);
            g.drawOval(vn.xPos, vn.yPos, NEURON_SIZE, NEURON_SIZE);

            for(VizConnection vc : vn.inputs){ //loop through inputs variable to draw all connections, maybe need a VizConnection class to store weight for black/red
                if(vc.in.neuron.type == Neuron.NeuronTypes.INPUT){
                    if(vc.weight < 0){
                        g.setColor(Color.RED);
                    }else{
                        g.setColor(Color.BLACK);
                    }
                    g.drawLine(vizInputNeurons.get(vc.in.neuron.getId()).xPos + (NEURON_SIZE / 2), vizInputNeurons.get(vc.in.neuron.getId()).yPos + (NEURON_SIZE / 2), vn.xPos + (NEURON_SIZE / 2), vn.yPos + (NEURON_SIZE / 2));
                }else{
                    if(vc.weight < 0){
                        g.setColor(Color.RED);
                    }else{
                        g.setColor(Color.BLACK);
                    }
                    g.drawLine(vc.in.xPos, vc.in.yPos, vn.xPos, vn.yPos);
                }
            }
        }

        for(VizNeuron vn : vizOutputNeurons){
            g.setColor(Color.BLACK);
            g.drawOval(vn.xPos, vn.yPos, NEURON_SIZE, NEURON_SIZE);

            for(VizConnection vc : vn.inputs){ //loop through inputs variable to draw all connections, maybe need a VizConnection class to store weight for black/red
                if(vc.in.neuron.type == Neuron.NeuronTypes.INPUT){
                    if(vc.weight < 0){
                        g.setColor(Color.RED);
                    }else{
                        g.setColor(Color.BLACK);
                    }
                    g.drawLine(vizInputNeurons.get(vc.in.neuron.getId()).xPos + (NEURON_SIZE / 2), vizInputNeurons.get(vc.in.neuron.getId()).yPos + (NEURON_SIZE / 2), vn.xPos + (NEURON_SIZE / 2), vn.yPos + (NEURON_SIZE / 2));
                }else{
                    if(vc.weight < 0){
                        g.setColor(Color.RED);
                    }else{
                        g.setColor(Color.BLACK);
                    }
                    g.drawLine(vc.in.xPos, vc.in.yPos, vn.xPos, vn.yPos);
                }
            }
        }
    }

    public class VizNeuron{

        Neuron neuron;
        int xPos, yPos;
        List<VizConnection> inputs;

        VizNeuron(Neuron n, int x, int y){
            neuron = n;
            xPos = x;
            yPos = y;
            inputs = new ArrayList<VizConnection>();
        }

    }

    public class VizConnection{

        VizNeuron in, out;
        double weight;

        VizConnection(Connection c, VizNeuron input, VizNeuron output){
            in = input;
            out = output;
            weight = c.weight;
        }

    }
}