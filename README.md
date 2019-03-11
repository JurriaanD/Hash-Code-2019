# Hash-Code-2019
## Problem statement
The problem statement is included as _problem statement.pdf_.

## Insights
Some things I found out about the datasets:
* B has 80k pictures and ~840k unique tags
* D has 90k pictures and 220 unique tags
* E has 80k pictures and 500 unique tags
* B: the interest factor between any two pictures is either 0 or 3 (-> at most 80,000 * 3 = 240,000 points)
* B only has horizontal pictures
* E only has vertical pictures
* There is no tag that occurs only once in any dataset

A key idea used to speed up things for B and C is that if you have a slide and you are looking for the next one, you should only consider slides that have at least 1 tag in common. If they don't, then the interest factor will always be 0. The pictures that share a tag are called _mates_ in the code.

## Code Layout
There are 3 main files: Main, Strategies and Helper.
* Helper: contains functions to generate data structures, calculate the score between two slides, the score of an entire slideshow, ...
* Strategies: contains functions to a) merge vertical pictures into slides and b) 'sort'/arrange the slides
* Main: contains the functions the read the input, write the output, to 'solve' once and finally a function that solves in an infinite loop (nice to run overnight). 

## Score
In the extended round, we got a total score of 1,048,874.
* A - Example: 2
* B - Lovely landscapes: 206,655
* C - Memorable moments: 1,754
* D - Pet pictures: 433,937
* E - Shiny selfies: 406,526

## Running the code
Just run Main.java, no external libraries required.

## Improvements
* Most of the heavy lifting would greatly benefit from being multithreaded
* Feel free to open an issue/pull request with your ideas!

## Zip
You can use _./zip_ to easily generate a zip file of the source code. Just a nice tool to have during the contest :)
This is a simple bash script, so it should work on UNIX (Linux and Mac). For Windows, you'll want to install [The Windows Subsystem for Linux](https://www.howtogeek.com/249966/how-to-install-and-use-the-linux-bash-shell-on-windows-10/).

