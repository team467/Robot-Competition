package frc.robot.simulator.communications;
import frc.robot.*;

import java.util.*;

import org.apache.logging.log4j.Logger;

import frc.robot.logging.RobotLogManager;

import java.io.*;
import java.net.*;

public class CSVFile {
    
  private static final Logger LOGGER = RobotLogManager.getMainLogger(CSVFile.class.getName());
    public int currentRow;
    public int lastRowThatWasCreated;
    public static void main(String[] args) {
        CSVFile file = new CSVFile();
        file.addRow();
        file.pushVar("hi");
        file.writeToFile("data.txt");
    }

    public List<List<Object>> data = new ArrayList<>();

    public CSVFile() {

    }

    public void addRow(int afterRow) {
        data.add(afterRow, new ArrayList<Object>());
        lastRowThatWasCreated=data.size()-1;
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
    public void pushVar(Object o) {
        data.get(lastRowThatWasCreated).add(o);
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

    public String loadFromFile(String url) {
        File root = new File(getClass().getResource("./").getPath());
        String path = root.getAbsolutePath();
        path = path.substring(0, path.length()-44);
        File resourceLocation = new File(path+"/"+url);
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
        } catch (Exception e) {
            System.out.print("something went wrong with loading csv");
        }
        return resourceLocation.getAbsolutePath();
    }
    public String writeToFile(String url) {
        File root = new File(getClass().getResource("./").getPath());
        String path = root.getAbsolutePath();
        path = path.substring(0, path.length()-44);
        File resourceLocation = new File(path+"/"+url);
        try {
            FileOutputStream out = new FileOutputStream(resourceLocation);
            out.write(toString().getBytes());
        } catch (Exception e) {
            System.out.print("something went wrong with loading csv");
        }
        return resourceLocation.getAbsolutePath();
    }
    public Object get(int row, int col){
        return data.get(row).get(col);
    }
    public Object get(int col){
        return data.get(Math.min(currentRow, data.size()-1)).get(col);
    }
}