// Copyright 2020
// Author: Matei Simtinică

import javax.swing.text.html.parser.Entity;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */

class RelationEntity {
    private int fam1;
    private int fam2;
    public RelationEntity(int fam1, int fam2) {
        this.fam1 = fam1;
        this.fam2 = fam2;
    }

    public int getFam1() {
        return fam1;
    }

    public void setFam1(int fam1) {
        this.fam1 = fam1;
    }

    public int getFam2() {
        return fam2;
    }

    public void setFam2(int fam2) {
        this.fam2 = fam2;
    }

    @Override
    public String toString() {
        return "RelationEntity{" +
                "fam1=" + fam1 +
                ", fam2=" + fam2 +
                '}';
    }
}

public class Task1 extends Task {
    // TODO: define necessary variables and/or data structures
    private int families;
    private int spies;
    private final ArrayList<RelationEntity> relations = new ArrayList<>();
    private final LinkedHashMap<String, Integer> clauses = new LinkedHashMap<>();
    private String testOutputString;

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
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
        this.spies = Integer.parseInt(strLine[2]);

        for (int i = 0; i < relationsNo; i++) {
            line = reader.readLine();
            strLine = line.trim().split("\\s+");
            relations.add(new RelationEntity(Integer.parseInt(strLine[0]), Integer.parseInt(strLine[1])));
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        // TODO: transform the current problem into a SAT problem and write it (oracleInFilename) in a format
        //  understood by the oracle
        int clausesNo = 0;
        clausesNo = clausesNo + relations.size() * spies;
        clausesNo = clausesNo + families;
        clausesNo = clausesNo + families * ((spies * (spies - 1)) / 2);
        String oracleAskString = "p cnf " + (families * spies) + " " + clausesNo + "\n";

        for (int i = 1; i <= families; i++) {
            for (int j = 1; j <= spies; j++) {
                clauses.put("" + i + "-" + j, j);
            }
        }

        Set<String> keys = clauses.keySet();

        ArrayList<String> listKeys = new ArrayList<>(keys);

        for (RelationEntity element : relations) {
            for (int i = 1; i <= spies; i++) {
                int elem1 = listKeys.indexOf("" + element.getFam1() + "-" + i) + 1;
                int elem2 = listKeys.indexOf("" + element.getFam2() + "-" + i) + 1;

                oracleAskString = oracleAskString + "-" + elem1 + " -" + elem2 + " 0\n";
            }
        }

        for (int f = 1; f <= families; f++) {
            for (int i = 1; i <= spies; i++) {
                int elem = listKeys.indexOf("" + f + "-" + i) + 1;
                oracleAskString = oracleAskString + elem + " ";
            }
            oracleAskString = oracleAskString + "0\n";
        }

        for (int f = 1; f <= families; f++) {
            for (int i = 1; i <= spies; i++) {
                for (int j = i + 1; j <= spies; j++) {
                    int elem1 = listKeys.indexOf("" + f + "-" + i) + 1;
                    int elem2 = listKeys.indexOf("" + f + "-" + j) + 1;

                    oracleAskString = oracleAskString + "-" + elem1 + " -" + elem2 + " 0\n";
                }
            }
        }

            PrintWriter writer = new PrintWriter(oracleInFilename, String.valueOf(StandardCharsets.UTF_8));
        writer.print(oracleAskString);
        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // TODO: extract the current problem's answer from the answer given by the oracle (oracleOutFilename)
        testOutputString = "";
        BufferedReader reader =
                new BufferedReader(new FileReader(oracleOutFilename));

        String line = reader.readLine();

        if (line.equals("False")) {
            testOutputString = "False";
            return;
        }

        testOutputString = "True\n";

        line = reader.readLine();
        int n = Integer.parseInt(line);

        line = reader.readLine();
        String[] strLine = line.trim().split("\\s+");

        boolean firstElem = true;
        Set<String> keys = clauses.keySet();

        ArrayList<String> listKeys = new ArrayList<>(keys);

        for (int i = 0; i < n; i++) {
            int activeElem = Integer.parseInt(strLine[i]);
            if (activeElem > 0) {
                String elemKey = listKeys.get(activeElem - 1);
                String elemSpy = String.valueOf(clauses.get(elemKey));
                if (firstElem) {
                    testOutputString = testOutputString + elemSpy;
                    firstElem = false;
                } else {
                    testOutputString = testOutputString + " " + elemSpy;
                }
            }
        }



    }

    @Override
    public void writeAnswer() throws IOException {
        // TODO: write the answer to the current problem (outFilename)
        PrintWriter writer = new PrintWriter(outFilename, String.valueOf(StandardCharsets.UTF_8));
        writer.print(testOutputString);
        writer.close();
    }

    public int getFamilies() {
        return families;
    }

    public void setFamilies(int families) {
        this.families = families;
    }

    public int getSpies() {
        return spies;
    }

    public void setSpies(int spies) {
        this.spies = spies;
    }

    public ArrayList<RelationEntity> getRelations() {
        return relations;
    }
}
