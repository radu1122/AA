// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {
    // TODO: define necessary variables and/or data structures
    private int families;
    private int familyDimension;
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
        this.familyDimension = Integer.parseInt(strLine[2]);

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

        String oracleAskString = "";

        for (int i = 1; i <= families; i++) {
            for (int j = 1; j <= familyDimension; j++) {
                clauses.put("" + i + "-" + j, i);
            }
        }

        Set<String> keys = clauses.keySet();

        ArrayList<String> listKeys = new ArrayList<>(keys);

        // step 1 commented after I did some refactoring
//        for (int i = 1; i <= familyDimension; i++) {
//            for (int f = 1; f <= families; f++) {
//                int elem = listKeys.indexOf("" + f + "-" + i) + 1;
//                oracleAskString = oracleAskString + elem + " ";
//            }
//            clausesNo++;
//            oracleAskString = oracleAskString + "0\n";
//        }

        // step 2
        for (int i = 1; i <= families; i++) {
            for (int j = i + 1; j <= families; j++) {
                if (!hasEdge(i, j)) {
                    for (int i1 = 1; i1 <= familyDimension; i1++) {
                        for (int j1 = 1; j1 <= familyDimension; j1++) {
                            int elem1 = listKeys.indexOf("" + i + "-" + i1) + 1;
                            int elem2 = listKeys.indexOf("" + j + "-" + j1) + 1;

                            clausesNo++;
                            oracleAskString = oracleAskString + "-" + elem1 + " -" + elem2 + " 0\n";
                        }
                    }
                }
            }
        }

        //step 3
        for (int dim = 1; dim <= familyDimension; dim++) {
            String stringClause = "";
            for (int i = 1; i <= families; i++) {

                int elem = listKeys.indexOf("" + i + "-" + dim) + 1;
                stringClause = stringClause + elem + " ";

                for (int j = i + 1; j <= families; j++) {
                    int elem1 = listKeys.indexOf("" + i + "-" + dim) + 1;
                    int elem2 = listKeys.indexOf("" + j + "-" + dim) + 1;

                    clausesNo++;
                    oracleAskString = oracleAskString + "-" + elem1 + " -" + elem2 + " 0\n";
                }
            }
            clausesNo++;
            oracleAskString = oracleAskString + stringClause + "0\n";
        }

        for (int i = 1; i <= families; i++) {
            for (int dim = 1; dim <= familyDimension; dim++) {
                for (int dim1 = dim + 1; dim1 <= familyDimension; dim1++) {
                    int elem1 = listKeys.indexOf("" + i + "-" + dim) + 1;
                    int elem2 = listKeys.indexOf("" + i + "-" + dim1) + 1;

                    clausesNo++;
                    oracleAskString = oracleAskString + "-" + elem1 + " -" + elem2 + " 0\n";
                }
            }
        }

        String oracleHeader = "p cnf " + (families * familyDimension) + " " + clausesNo + "\n";

        oracleAskString = oracleHeader + oracleAskString;

        PrintWriter writer = new PrintWriter(oracleInFilename, String.valueOf(StandardCharsets.UTF_8));
        writer.print(oracleAskString);
        writer.close();
    }

    private boolean hasEdge(int fam1, int fam2) {
//        for(RelationEntity element: relations) {
//            if (element.getFam1() == fam1 && element.getFam2() == fam2) {
//                return true;
//            }
//        }
        RelationEntity relation = new RelationEntity(fam1, fam2);
        if (relations.contains(relation)) {
            return true;
        }
        return false;
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
                String elemFam = String.valueOf(clauses.get(elemKey));
                if (firstElem) {
                    testOutputString = testOutputString + elemFam;
                    firstElem = false;
                } else {
                    testOutputString = testOutputString + " " + elemFam;
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
}
