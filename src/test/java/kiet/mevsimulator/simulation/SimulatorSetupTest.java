package kiet.mevsimulator.simulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SimulatorSetupTest {

    @Test
    public void testCreateValidatorEntities() {
        List<Entity> entities = SimulatorSetup.createEntities(3, 5, 8, 13, 21, 34, 55);
        long whales = entities.stream().filter(e -> "Whale".equals(e.category)).count();
        assertEquals(3, whales);
        long accounts = entities.stream().filter(e -> "Account".equals(e.category)).count();
        assertEquals(1000, accounts);
    }

    @Test
    public void testCreateValidators() {
        List<Entity> entities = List.of(new Entity("Test", "Test", 64));
        List<Validator> validators = SimulatorSetup.createEntities(entities);
        assertEquals(2, validators.size());
    }
}