package frc.robot.simulator.communications;

import java.util.*;

import org.apache.logging.log4j.Logger;

import frc.robot.logging.RobotLogManager;

import java.io.*;

public class CSVFile {
    
  private static final Logger LOGGER = RobotLogManager.getMainLogger(CSVFile.class.getName());
    public int currentRow;
    public static void main(String[] args) {
        CSVFile file = new CSVFile();
        file.loadFromFile("src/main/deploy/test.txt");
        boolean success = file.toString().equals("abcd, !@#$%^&*()_+-=\n?:\";\', 1234567890");
        System.out.println(success);
    }

    public List<List<Object>> data = new ArrayList<>();

    public CSVFile() {

    }

    public void addRow(int afterRow) {
        data.add(afterRow, new ArrayList<Object>());
    }

    public void addRow() {
        data.add(new ArrayList<Object>());
    }

    public void pushVar(Object o, int row, int col) {
        data.get(row).add(col, o);
    }

    public void pushVar(Object o, int row) {
        data.get(row).add(o);
    }

    public String toString() {
        String out = "";
        int col = 0;
        for (List<Object> row : data) {
            for (Object o : row) {
                if (col != 0)
                    out += ",";
                out += o.toString();
                col++;
            }
            col = 0;
            out += "\n";
        }
        out = out.stripTrailing();
        return out;
    }

    public void clear() {
        data.clear();
    }

    public void loadFromString(String s) {
        Scanner rows = new Scanner(s);
        while (rows.hasNextLine()) {
            String row = rows.nextLine();
            if (row.startsWith("!"))continue;
            String[] values = row.split(",");
            addRow();
            for (String value : values) {
                pushVar(value, data.size() - 1);
            }
        }
        rows.close();
    }

    public boolean loadFromFile(String url) {

        File resourceLocation = new File(url);
        LOGGER.error("helloworld");
        try {
            FileInputStream in = new FileInputStream(resourceLocation);
            Scanner scanner = new Scanner(in);
            String s = "";
            for (String row; scanner.hasNextLine();) {
                row = scanner.nextLine();
                s += row;
                if (scanner.hasNextLine())
                    s += "\n";
            }
            scanner.close();
            loadFromString(s);
            return true;
        } catch (Exception e) {
            System.out.print("something went wrong with loading csv");
            return false;
        }
    }
    public Object get(int row, int col){
        return data.get(row).get(col);
    }
    public Object get(int col){
        return data.get(Math.min(currentRow, data.size()-1)).get(col);
    }
}