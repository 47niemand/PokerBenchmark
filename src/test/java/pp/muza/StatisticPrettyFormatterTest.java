package pp.muza;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import org.junit.Test;

import java.util.Arrays;

public class StatisticPrettyFormatterTest {

    @Test
    public void example() {
        Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_LEFT)
                .withRowLimit(7)
                .addRow("Index", "Boolean");

        for (int i = 1; i <= 20; i++) {
            tableBuilder.addRow(i, Math.random() > 0.5);
        }

        TableRow<Object> t = new TableRow<>();
        t.setColumns(Arrays.asList(111, 1.0));
        tableBuilder.addRow(t);

        System.out.println(tableBuilder.build());
    }
}