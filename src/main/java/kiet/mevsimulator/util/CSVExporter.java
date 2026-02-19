package kiet.mevsimulator.util;

import kiet.mevsimulator.simulation.Entity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {

    public static void exportEntities(List<Entity> entities, String fileName) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            // CSV Header
            writer.write("Name,Category,TotalStake,EffectiveStake,MEV_Earned,Address");
            writer.newLine();

            // Write each entity
            for (Entity e : entities) {
                writer.write(
                        e.name + "," +
                                e.category + "," +
                                e.totalStake + "," +
                                e.effectiveStake + "," +
                                e.mevEarned + "," +
                                e.blockchainAddress
                );
                writer.newLine();
            }

            writer.flush();

            System.out.println("CSV file created: " + fileName);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
