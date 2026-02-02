package simulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SimulatorSetupTest {

    @Test
    public void testCreateEntities() {
        List<Entity> entities = SimulatorSetup.createEntities();

        // Check total entities: 3+5+8+13+21+34+55+1000 = 1139
        assertEquals(1139, entities.size());

        // Check categories
        long whales = entities.stream().filter(e -> "Whale".equals(e.category)).count();
        assertEquals(3, whales);

        long sharks = entities.stream().filter(e -> "Shark".equals(e.category)).count();
        assertEquals(5, sharks);

        long accounts = entities.stream().filter(e -> "Account".equals(e.category)).count();
        assertEquals(1000, accounts);
    }

    @Test
    public void testCreateValidators() {
        List<Entity> entities = List.of(new Entity("Test", "Test", 64));
        List<Validator> validators = SimulatorSetup.createValidators(entities);

        assertEquals(2, validators.size()); // 64 / 32 = 2
    }
}