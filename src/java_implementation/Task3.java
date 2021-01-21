// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;
    // TODO: define necessary variables and/or data structures
    private int families;
    private int[][] familiesMatrix;
    private String testOutputString;


    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";

        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
        readProblemData();

        // TODO: implement a way of successively querying the oracle (using Task2) about various arrest numbers until you
        //  find the minimum

        for (int i = families; i > 0; i--) {
            reduceToTask2(i);
            task2Solver.solve();
            if (extractAnswerFromTask2()) {
                break;
            }
        }

        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        // TODO: read the problem input (inFilename) and store the data in the object's attributes
        BufferedReader reader =
                new BufferedReader(new FileReader(inFilename));

        String line = reader.readLine();
        String[] strLine = line.trim().split("\\s+");
        this.families = Integer.parseInt(strLine[0]);
        int relationsNo = Integer.parseInt(strLine[1]);
        this.familiesMatrix = new int[families][families];

        for(int i = 0; i < families; i++) {
            for (int j = 0; j < families; j++) {
                familiesMatrix[i][j] = 0;
            }
        }
        for (int i = 0; i < relationsNo; i++) {
            line = reader.readLine();
            strLine = line.trim().split("\\s+");
            familiesMatrix[Integer.parseInt(strLine[0]) - 1][Integer.parseInt(strLine[1]) - 1] = 1;
            familiesMatrix[Integer.parseInt(strLine[1]) - 1][Integer.parseInt(strLine[0]) - 1] = 1;
        }
    }

    public void reduceToTask2(int k) throws IOException {
        // TODO: reduce the current problem to Task2
        String task2InputString = "";
        String relationsString = "";
        int relationsNo = 0;

        for (int i = 1; i <= families; i ++) {
            for (int j = i + 1; j <= families; j++) {
                if (familiesMatrix[i - 1][j - 1] == 0) {
                    relationsNo++;
                    relationsString = relationsString + i + " " + j + "\n";
                }
            }
        }

        task2InputString = families + " " + relationsNo + " " + k + "\n" + relationsString;

        PrintWriter writer = new PrintWriter(task2InFilename, String.valueOf(StandardCharsets.UTF_8));
        writer.print(task2InputString);
        writer.close();
    }


    public boolean extractAnswerFromTask2() throws IOException {
        // TODO: extract the current problem's answer from Task2's answer
        testOutputString = "";
        BufferedReader reader =
                new BufferedReader(new FileReader(task2OutFilename));

        String line = reader.readLine();

        if (line.equals("False")) {
            return false;
        }
        line = reader.readLine();
        String[] strLine = line.trim().split("\\s+");

        List strLineArr = Arrays.asList(strLine);

        boolean firstElem = true;
        for (int i = 1; i <= families; i++) {
            if (!strLineArr.contains(String.valueOf(i))) {
                if (firstElem) {
                    testOutputString = testOutputString + i;
                    firstElem = false;
                } else {
                    testOutputString = testOutputString + " " + i;
                }
            }
        }

        return true;
    }

    @Override
    public void writeAnswer() throws IOException {
        // TODO: write the answer to the current problem (outFilename)
        PrintWriter writer = new PrintWriter(outFilename, String.valueOf(StandardCharsets.UTF_8));
        writer.print(testOutputString);
        writer.close();
    }
}