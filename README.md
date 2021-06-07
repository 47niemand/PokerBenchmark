PokerBenchmark
==============

The program calculates the winning probabilities for Texas holdem poker game in specific situations.

# Build

This application uses Gradle to build. Use the following command: 
```
gradlew build
```
The resulting PokerBenchmark-1.0.jar file will be placed to the ./build/libs folder

# Run

use java to execute the application:
```
java -jar PokerBenchmark-1.0.jar
```

usage: Poker Benchmark
```
 -d,--debug              use debug output
 -g,--go                 run
 -h,--hand <arg>         define hand cards
 -i,--iterations <arg>   define number of iterations
 -n,--nostats            don't collect statistics
 -p,--players <arg>      define number of players
 -r,--river <arg>        define river cards
 -s,--simple             use simple formatter
 -t,--threads <arg>      define number of CPU threads
 -v,--verbose            use verbose output
```
  
# Game setup

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

java -jar build/libs/PokerBenchmark-1.0.jar -g -i 2000000 -t 4 -p 7 -h AS KC JD -r 2C TD AD KD
```
2000000 iterations performed in 4861 milliseconds 
Results statistics:
| % HIGHCARD | % ONEPAIR | % TWOPAIRS | % THREEOFAKIND | % STRAIGHT | % FLUSH | % FULLHOUSE | % QUADS | % STRAIGHTFLUSH |
| ----------:| ---------:| ----------:| --------------:| ----------:| -------:| -----------:| -------:| ---------------:|
|       0.00 |      0.00 |      28.56 |           3.79 |      12.68 |   41.59 |        9.85 |    0.07 |            3.46 |
Players statistics:
River setup [2C, 10D, AD, KD]
| Player  | Hand  | % Wins | % HIGHCARD | % ONEPAIR | % TWOPAIRS | % THREEOFAKIND | % STRAIGHT | % FLUSH | % FULLHOUSE | % QUADS | % STRAIGHTFLUSH |
| ------- | ----- | ------:| ----------:| ---------:| ----------:| --------------:| ----------:| -------:| -----------:| -------:| ---------------:|
| Player0 | AS KC |  36.43 |       0.00 |      0.00 |      76.68 |           0.00 |       0.00 |    0.00 |       23.32 |    0.00 |            0.00 |
| Player1 | JD    |  34.85 |       0.00 |      0.00 |       0.00 |           1.01 |      17.79 |   71.28 |        0.00 |    0.00 |            9.93 |
| Player2 |       |   5.75 |       0.00 |      0.00 |       2.16 |          12.01 |      22.60 |   58.28 |        4.72 |    0.22 |            0.00 |
| Player3 |       |   5.75 |       0.00 |      0.00 |       2.23 |          12.04 |      22.53 |   58.23 |        4.75 |    0.23 |            0.00 |
| Player4 |       |   5.71 |       0.00 |      0.00 |       2.17 |          11.92 |      22.61 |   58.30 |        4.77 |    0.24 |            0.00 |
| Player5 |       |   5.77 |       0.00 |      0.00 |       2.18 |          12.04 |      22.51 |   58.29 |        4.75 |    0.22 |            0.00 |
| Player6 |       |   5.74 |       0.00 |      0.00 |       2.17 |          11.85 |      22.64 |   58.46 |        4.65 |    0.23 |            0.00 |
```

Verbose output example:
```
ITERATION 499 [PokerCalc{player=2,result=FULLHOUSE, hand="KH JC AS AC JS JD 9D", combination="AS AC JS JD JC", score=1809}]
ITERATION 500 [PokerCalc{player=0,result=THREEOFAKIND, hand="AD 4H AS AC KS 6H 10C", combination="AS AD AC", score=1128}]
```
