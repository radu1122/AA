// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

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
    private final ArrayList<RelationEntity> relations = new ArrayList<>();
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

        for (int i = 0; i < relationsNo; i++) {
            line = reader.readLine();
            strLine = line.trim().split("\\s+");
            relations.add(new RelationEntity(Integer.parseInt(strLine[0]), Integer.parseInt(strLine[1])));
        }
    }

    public void reduceToTask2(int k) {
        // TODO: reduce the current problem to Task2

    }

    private boolean hasEdge(int fam1, int fam2) {
        RelationEntity relation = new RelationEntity(fam1, fam2);
        if (relations.contains(relation)) {
            return true;
        }
        return false;
    }

    public boolean extractAnswerFromTask2() throws IOException {
        // TODO: extract the current problem's answer from Task2's answer
        testOutputString = "";
        BufferedReader reader =
                new BufferedReader(new FileReader(oracleOutFilename));

        String line = reader.readLine();

        if (line.equals("False")) {
            return false;
        }
        line = reader.readLine();
        String[] strLine = line.trim().split("\\s+");

        ArrayList<String> strLineArr = (ArrayList<String>) Arrays.asList(strLine);

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
