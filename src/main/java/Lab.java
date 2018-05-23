import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Lab {

    private static final int SIZE = 20;

    private double[] cipherTextDistribution, keysDistribution;
    private int[][] table;

    private double[] plainDistribution;
    private double[][] plainCipherDistribution, conditionalPlainCipherDistribution;

    public void runScenario() {
        calculatePlainDistribution();
        calculatePlainCipherDistribution();
        calculateConditionalPlainCipherDistribution();
        System.out.println(getStochasticFunction());
        System.out.println(getDeterministicFunction());
    }

    Lab(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/prob_" + fileName + ".csv"));
            cipherTextDistribution = new double[SIZE];
            keysDistribution = new double[SIZE];
            table = new int[SIZE][SIZE];

            String buffer;
            buffer = reader.readLine();
            int count = 0;
            for (String s : buffer.split(",")) {
                cipherTextDistribution[count] = Double.parseDouble(s);
                ++count;
            }
            buffer = reader.readLine();
            count = 0;
            for (String s : buffer.split(",")) {
                keysDistribution[count] = Double.parseDouble(s);
                ++count;
            }

            reader = new BufferedReader(new FileReader("data/table_" + fileName + ".csv"));
            count = 0;
            while ((buffer = reader.readLine()) != null) {
                int i = 0;
                for (String s : buffer.split(",")) {
                    table[count][i] = Integer.parseInt(s);
                    ++i;
                }
                ++count;
            }
        } catch (Exception e ) {
            System.out.println(e.getMessage());
        }
    }

    private void calculatePlainDistribution() {
        plainDistribution = new double[SIZE];
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                plainDistribution[table[i][j]] += cipherTextDistribution[j] * keysDistribution[i];
            }
        }
    }

    private void calculatePlainCipherDistribution() {
        plainCipherDistribution = new double[SIZE][SIZE];
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                int c = table[i][j];
                plainCipherDistribution[c][j] += cipherTextDistribution[j] * keysDistribution[i];
            }
        }
    }

    private void calculateConditionalPlainCipherDistribution() {
        conditionalPlainCipherDistribution = new double[SIZE][SIZE];
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                conditionalPlainCipherDistribution[i][j] = plainCipherDistribution[i][j];
            }
        }

        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                conditionalPlainCipherDistribution[i][j] /= plainDistribution[i];
            }
        }
    }

    private double getDeterministicFunction() {
        double[] temp = new double[SIZE];
        for (int i = 0; i < SIZE; ++i) {
            temp[i] = getMaxIndex(conditionalPlainCipherDistribution[i]);
        }
        double result = 0.;
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if ( ((int) temp[i]) != j) {
                    result += plainCipherDistribution[i][j];
                }
            }
        }
        return result;
    }

    private int getMaxIndex(double[] array) {
        double max = -1.;
        int index = -1;
        for (int i = 0; i < SIZE; ++i) {
            if (array[i] > max) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }
    
    private double getStochasticFunction() {
        double[][] stochastic = new double[SIZE][SIZE];
        for (int i = 0; i < SIZE; ++i) {
            ArrayList<Integer> countMax = getCountMaxIndex(conditionalPlainCipherDistribution[i]);
            double p = 1. / countMax.size();
            for (int d : countMax) {
                stochastic[i][d] = p;
            }
        }
        double result = 0.;
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                result += plainCipherDistribution[i][j] * (1 - stochastic[i][j]);
            }
        }
        return result;
    }

    private ArrayList getCountMaxIndex(double[] array) {
        double max = -1.;
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (double d : array) {
            if (d > max) {
                max = d;
            }
        }
        for (int i = 0, n = array.length; i < n; ++i) {
            if (array[i] == max) {
                result.add(i);
            }
        }
        return result;
    }

}
