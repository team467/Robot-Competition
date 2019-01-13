package frc.robot.simulator.communications;

import java.util.*;
import java.io.*;

public class CSVFile {
    public int currentRow;
    public static void main(String[] args) {
        CSVFile hi = new CSVFile();
        hi.loadFromFile("matchdata.txt");
        System.out.print(hi);
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

    public void loadFromFile(String url) {

        File resourceLocation = new File("src/main/deploy/" + url);
        try {
            InputStream in = new FileInputStream(resourceLocation);
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
        } catch (Exception e) {
            System.out.print("something went wrong with loading csv");
        }
    }
    public Object get(int row, int col){
        return data.get(row).get(col);
    }
    public Object get(int col){
        return data.get(currentRow).get(col);
    }
}