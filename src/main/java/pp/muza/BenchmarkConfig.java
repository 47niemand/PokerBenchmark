package pp.muza;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import pp.muza.cards.CardStack;

@Getter
@Builder(builderClassName = "Builder")
@ToString(exclude = {"debug", "verbose", "statisticFormatter"})
public final class BenchmarkConfig {

    @Default
    private final int players = TestRunner.DEFAULT_PLAYERS;
    @Default
    private final int threads = TestRunner.DEFAULT_THREADS;
    @Default
    private final boolean debug = false;
    @Default
    private final boolean verbose = false;
    @Default
    private final boolean noStats = false;
    @Default
    private final int maxIteration = TestRunner.MAX_ITERATIONS;
    private final CardStack handSetup;
    private final CardStack riverSetup;
    @Default
    private final StatisticFormatter statisticFormatter = new StatisticPrettyFormatter();
}

