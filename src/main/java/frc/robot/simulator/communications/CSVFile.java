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
    public List<List<Object>> data = new ArrayList<>();

    public static void main(String[] args) {
        CSVFile file = new CSVFile();
        for (int i = 0; i < 2000; i++) {
            file.addRow();
            file.pushVar(i);
            file.pushVar(i);
            file.pushVar("basement");
        }
        file.writeToFile("run.txt");
    }

    public CSVFile() {

    }

    public void addRow(int afterRow) {
        data.add(afterRow, new ArrayList<Object>());
        lastRowThatWasCreated = data.size() - 1;
    }

    public void addRow() {
        data.add(new ArrayList<Object>());
        lastRowThatWasCreated = data.size() - 1;
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
            if (row.startsWith("!"))
                continue;
            String[] values = row.split(",");
            addRow();
            for (String value : values) {
                pushVar(value, data.size() - 1);
            }
        }
        rows.close();
    }

    public String loadFromFile(String url) {
        File root = new File(url);
        if (!url.startsWith("C:")) {
            root = new File(getClass().getResource("./").getPath());
            while (!root.getAbsolutePath().endsWith("Robot2019-Competition")) {
                root = root.getParentFile();
            }
        }
        String path = root.getAbsolutePath();
        File resourceLocation = new File(path + "/" + url);
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

    public String loadFromFile(File url) {
        try {
            FileInputStream in = new FileInputStream(url);
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
        return url.getAbsolutePath();
    }

    public String writeToFile(String url) {
        File root = new File(url);
        if (!url.startsWith("C:")) {
            root = new File(getClass().getResource("./").getPath());
            while (!root.getAbsolutePath().endsWith("Robot2019-Competition")) {
                root = root.getParentFile();
            }
        }
        String path = root.getAbsolutePath();
        File resourceLocation = new File(path + "/" + url);
        try {
            FileOutputStream out = new FileOutputStream(resourceLocation);
            out.write(toString().getBytes());
            out.close();
        } catch (Exception e) {
            System.out.print("something went wrong with loading csv");
        }
        return resourceLocation.getAbsolutePath();
    }
    public String writeToFile(File url) {
        try {
            FileOutputStream out = new FileOutputStream(url);
            out.write(toString().getBytes());
            out.close();
        } catch (Exception e) {
            System.out.print("something went wrong with loading csv");
        }
        return url.getAbsolutePath();
    }


    public Object get(int row, int col) {
        return data.get(row).get(col);
    }

    public Object get(int col) {
        return data.get(Math.min(currentRow, data.size() - 1)).get(col);
    }
}