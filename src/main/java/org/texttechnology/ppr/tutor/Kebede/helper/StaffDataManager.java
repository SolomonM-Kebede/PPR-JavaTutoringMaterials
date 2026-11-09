package org.texttechnology.ppr.tutor.Kebede.helper;

import org.json.JSONObject;
import org.texttechnology.ppr.tutor.Kebede.implementation.SalaryImpl;
import org.texttechnology.ppr.tutor.Kebede.implementation.StaffImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Wenn Sie toJson() custom method in implementation Klassen erstellt haben, können Sie
 * einfach ohne externe Library serializieren und deserializieren. Sehe die Implementation in
 * beide Salary und Staff Klassen.
 */
public class StaffDataManager {


    // local save(serialize)
    public static void saveToFile(StaffImpl staff, Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        String jsonContent = staff.toJSON().toString(2);
        Files.writeString(path, jsonContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // local read
    public static StaffImpl loadFromFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + path);
        }
        String content = Files.readString(path);
        JSONObject json = new JSONObject(content);
        return new StaffImpl(json);
    }




    // run
    public static void main(String[] args) {
        try {
            // Beispiel
            SalaryImpl grossSalary = new SalaryImpl("5500.0", "EUR");
            StaffImpl staffs = new StaffImpl("EMP-001", "Solomon", "Kebede", "Solo", grossSalary);
            System.out.println(staffs);

            // versuch mit Beispiel JSON Datei
            Path localPath = Path.of("output", "staff_001.json");
            System.out.println("\n[File] Saving to: " + localPath.toAbsolutePath());
            saveToFile(staffs, localPath);

            StaffImpl fromFile = loadFromFile(localPath);
            System.out.println("Name: " + fromFile.getName() + " | Salary: " + fromFile.getSalary());


        } catch (Exception ignored) {
        }
    }
}