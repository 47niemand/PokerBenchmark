PokerBenchmark
==============

The program calculates the winning probabilities for Texas holdem poker game in specific situations.

Build
-----

This application uses Gradle to build.

On Linux/macOS:

```bash
./gradlew build
```

On Windows:

```bash
.\gradlew.bat build
```

The resulting `PokerBenchmark-1.0.jar` file will be placed in the `./build/libs/` folder.

Run
---

Use Java to execute the application:

```bash
java -jar ./build/libs/PokerBenchmark-1.0.jar
```

usage: Poker Benchmark

```no-highlight
 -d,--debug              use debug output
 -g,--go                 run
 -h,--hand <arg>         define hand cards
 -i,--iterations <arg>   define number of iterations
 -n,--no_stats           don't collect statistics
 -p,--players <arg>      define number of players
 -r,--river <arg>        define river cards
 -s,--simple             use simple formatter
 -t,--threads <arg>      define number of CPU threads
 -v,--verbose            use verbose output
```
  
Game setup
----------

The deck is shuffled before each iteration. The river cards distribute in the order given by the parameter --river <arg> or from the top of the deck.
Then, cards are dealt to the players in the order of the specified parameters --players <arg> or from the top of the deck.

13 distinct values indicated by one or more characters. Allowed values: A or 1, 2, 3, 4, 5, 6, 7, 8 ,9, 10 or T, J, Q, K.

Suits coded by C, D, S, H characters corresponding Clubs ♣, Diamonds ♦, Spades ♠, Hearts ♥.

Example:

    Ace of Spades = 1S or AS
    Ten of Diamonds = 10D or TD

A few cards can be listed by separating them with a delimiter

    1H,2H,3H,4H,5H,6H,7H,8H,9H,10H
    AD 5C 10S 2H 4S 3H 2C

Execution example:

```bash
java -jar build/libs/PokerBenchmark-1.0.jar -g -i 2000000 -t 4 -p 7 -h AS KC JD -r 2C TD AD KD
```

```no-highlight
BenchmarkConfig(players=7, threads=8, noStats=false, maxIteration=2000000, handSetup=AS KC JD, riverSetup=2C 10D AD KD)                                            
2000000 iterations performed in 3435 milliseconds
Results statistics:
| % HIGHCARD | % ONEPAIR | % TWOPAIRS | % THREEOFAKIND | % STRAIGHT | % FLUSH | % FULLHOUSE | % QUADS | % STRAIGHTFLUSH |
| ----------:| ---------:| ----------:| --------------:| ----------:| -------:| -----------:| -------:| ---------------:|
|       0.00 |      0.00 |      26.97 |           3.84 |      16.91 |   38.43 |        9.58 |    0.06 |            4.21 |
Players statistics:
River setup [2C, 10D, AD, KD]
| Player  | Hand  | % Wins | % HIGHCARD | % ONEPAIR | % TWOPAIRS | % THREEOFAKIND | % STRAIGHT | % FLUSH | % FULLHOUSE | % QUADS | % STRAIGHTFLUSH |
| ------- | ----- | ------:| ----------:| ---------:| ----------:| --------------:| ----------:| -------:| -----------:| -------:| ---------------:|
| Player0 | AS KC |  34.64 |       0.00 |      0.00 |      76.17 |           0.00 |       0.00 |    0.00 |       23.83 |    0.00 |            0.00 |
| Player1 | JD    |  36.09 |       0.00 |      0.00 |       0.00 |           0.93 |      20.55 |   66.85 |        0.00 |    0.00 |           11.67 |
| Player2 |       |   5.86 |       0.00 |      0.00 |       1.99 |          11.95 |      32.31 |   48.91 |        4.59 |    0.24 |            0.00 |
| Player3 |       |   5.87 |       0.00 |      0.00 |       2.00 |          11.98 |      32.33 |   49.06 |        4.44 |    0.20 |            0.00 |
| Player4 |       |   5.86 |       0.00 |      0.00 |       1.94 |          11.88 |      32.67 |   48.76 |        4.53 |    0.22 |            0.00 |
| Player5 |       |   5.86 |       0.00 |      0.00 |       2.00 |          12.05 |      32.52 |   48.68 |        4.54 |    0.21 |            0.00 |
| Player6 |       |   5.81 |       0.00 |      0.00 |       2.06 |          11.95 |      32.29 |   48.96 |        4.51 |    0.23 |            0.00 |
```

Verbose output example:

```no-highlight
ITERATION 499 [PokerCalc{player=1,result=STRAIGHT, hand="JD QH 2C 10D AD KD 5C", combination="AD KD QH JD 10D", score=4505625}]
ITERATION 500 [PokerCalc{player=0,result=TWOPAIRS, hand="AS KC 2C 10D AD KD 7H", combination="AS AD KD KC", score=3033000}]
```
