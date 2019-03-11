import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
	static HashMap<String, Integer> highscores = new HashMap<>();
	
	public static void main(String[] args) {
		highscores.put("b", 206655);
		highscores.put("c", 1754);
		highscores.put("d", 433937);
		highscores.put("e", 406526);
		
		testRun("c");
	}
	
	/*
	 * Execute the strategies once.
	 * We always write to the output file, even if we don't have a new highscore.
	 */
	public static void testRun(String dataset) {
		long current = System.currentTimeMillis();
		
		ArrayList<Slide> slides = Strategies.initSlidesMergeVerticalsMinOverlap(dataset);
		Strategies.fastBasicSlideSort(slides);
		
		long elapsed = System.currentTimeMillis() - current;
		System.out.println("Done in " + elapsed + " ms.");
		System.out.println("Score: " + Helper.getSlideShowScore(slides));
		
		writeOutput(dataset, slides);
	}
	
	/*
	 *  Executes the strategies until you stop the program.
	 *  We only write to the output file if we have found a new solution.
	 */
	public static void loopHighscore(String dataset) {		
		int highscore = highscores.get(dataset);
		
		while (true) {			
			ArrayList<Slide> slides = Strategies.initSlidesMergeVerticalsMinOverlap(dataset);
			Strategies.fastBasicSlideSort(slides);
			int score = Helper.getSlideShowScore(slides);
			
			if (score > highscore) {
				System.out.println(dataset + ": " + "NEW HIGHSCORE: " + score);
				highscore = score;
				writeOutput(dataset, slides);
			} else {
				System.out.println(dataset + ": " + score + " - " + highscore);
			}
		}
		
	}

	
	/*
	 * Loads the output file and tries to improve it a bit
	 * We look for the two slides with the lowest interest factor.
	 * We then search for a third slide so that our score increases when we swap them.
	 * WARNING: this function will get you maybe 10 more points and runs very slow
	 * Edge cases are somewhat caught, but could use some more love.
	 * Edge cases make this function very long and scary 👻
	 * This function in general is a mess and I recommend not to look at it :$
	 */
	public static void improveSolution(String dataset) {
		File inputFile = new File("./../output" + dataset + ".out");
		ArrayList<Pic> pics = readInput(dataset);
		ArrayList<Slide> slides = new ArrayList<Slide>();
		
		// Read the output file and put everything in our datastructure again
		try {
			Scanner in = new Scanner(inputFile);
			int numPics = in.nextInt();
			in.nextLine();
			
			for (int i = 0; i < numPics; i++) {
				String[] picIdsStrings = in.nextLine().split(" ");		
				if (picIdsStrings.length == 1) {
					int picId = Integer.parseInt(picIdsStrings[0]);
					slides.add(new Slide(pics.get(picId)));
				} else {					
					int[] picIds = new int[picIdsStrings.length];
					picIds[0] = Integer.parseInt(picIdsStrings[0]);
					picIds[1] = Integer.parseInt(picIdsStrings[1]);
					slides.add(new Slide(pics.get(picIds[0]), pics.get(picIds[1])));
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// blacklist is a set of slides we alreay tried swapping
		HashSet<Integer> blacklist = new HashSet<>();
		int currentScore = Helper.getSlideShowScore(slides);
		System.out.println("Starting with a score of " + currentScore);
		
		// Starts here for real
		// While not every slide is in the blacklist, keep looping
		while (blacklist.size() < slides.size()) {
			// Find the slide with the lowest interest factor that isn't on our blacklist
			int minScore = Integer.MAX_VALUE;
			int minIdx = -1;
			
			for (int i = 0; i < slides.size() - 1; i++) {
				int score = Helper.getScore(slides.get(i).tags, slides.get(i+1).tags);
				if (score < minScore && !blacklist.contains(i)) {
					minScore = score;
					minIdx = i;
				}
			}
			
			Slide minSlide = slides.get(minIdx);
			Slide minSlidePrev = minIdx > 0 ? slides.get(minIdx-1) : null;
			Slide minSlideNext = minIdx < slides.size() - 1 ? slides.get(minIdx+1) : null;
			
			// Now that we've found the worse slide, go through all slides to see if swapping would increase
			// our score. We're trying to calculate the score difference 'manually' because calculating the score
			// for the entire slideshow after swapping 2 slides would be a big waste.
			// We can get the score pretty wrong +- 2000 for edge cases
			int bestSwapIdx = minIdx;
			for (int i = 0 ; i < slides.size(); i++) {
				Slide current = slides.get(i);
				Slide currentPrev = i > 0 ? slides.get(i-1) : null;
				Slide currentNext = i < slides.size() - 1 ? slides.get(i+1) : null;
				int scoreLost = 0;
				int scoreGain = 0;
				if (minSlidePrev != null) {
					scoreLost += Helper.getScore(minSlidePrev.tags, minSlide.tags);
					scoreGain += Helper.getScore(minSlidePrev.tags, current.tags);
				}
				if (minSlideNext != null) {
					scoreLost += Helper.getScore(minSlide.tags, minSlideNext.tags);
					scoreGain += Helper.getScore(current.tags, minSlideNext.tags);
				}
				if (currentPrev != null) {
					scoreLost += Helper.getScore(currentPrev.tags, current.tags);
					scoreGain += Helper.getScore(currentPrev.tags, minSlide.tags);
				}
				if (currentNext != null) {
					scoreLost += Helper.getScore(current.tags, currentNext.tags);
					scoreGain += Helper.getScore(minSlide.tags, currentNext.tags);
				}
				int score = currentScore - scoreLost + scoreGain;
				if (score > currentScore) {
					currentScore = score;
					bestSwapIdx = i;
				}
			}
			
			if (bestSwapIdx == minIdx) {
				blacklist.add(minIdx); // Found no good swap
			} else {
				// To deal with the edge cases, we use our thrustworthy function to calculate the score of an entire slideshow
				// and compare it to the score we though we had.
				Collections.swap(slides, minIdx, bestSwapIdx);
				int realScore = Helper.getSlideShowScore(slides);
				if (currentScore != realScore) {
					Collections.swap(slides, minIdx, bestSwapIdx);
					int prevScore = Helper.getSlideShowScore(slides);
					Collections.swap(slides, minIdx, bestSwapIdx);
					if (realScore <= prevScore) {
						currentScore = realScore;
						blacklist.add(minIdx);
						break;
					} else {
						currentScore = realScore;
					}
				}
				System.out.println("NEW HIGHSCORE!!! " + currentScore);
				writeOutput(dataset, slides);
				
				// All slides involved in the swap should be reconsidered
				// You could completely clear the blacklist and use this function to brute-force
				// the problem, but it would be very, very slow
				if (blacklist.contains(bestSwapIdx)) {
					blacklist.remove(bestSwapIdx);
				}
				if (blacklist.contains(bestSwapIdx-1)) {
					blacklist.remove(bestSwapIdx-1);
				}
				if (blacklist.contains(bestSwapIdx+1)) {
					blacklist.remove(bestSwapIdx);
				}
				if (blacklist.contains(bestSwapIdx+1)) {
					blacklist.remove(bestSwapIdx);
				}
				if (blacklist.contains(minIdx)) {
					blacklist.remove(minIdx);
				}
				if (blacklist.contains(minIdx-1)) {
					blacklist.remove(minIdx-1);
				}
				if (blacklist.contains(minIdx+1)) {
					blacklist.remove(minIdx+1);
				}
			}
			
		}
		
		System.out.println("Done trying to improve!");
	}
	
	// Read the input file and load everything into the appropriate data structures
	public static ArrayList<Pic> readInput(String dataset) {
		File inputfile = new File("./../inputs/" + dataset + ".txt");
		ArrayList<Pic> result = new ArrayList<>();
		try {
			Scanner in = new Scanner(inputfile);
			
			int numPics = in.nextInt();
			
			for (int i = 0; i < numPics; i++) {
				boolean horizontal = in.next().equals("H");
				int numTags = in.nextInt();
				HashSet<String> tags = new HashSet<>();				
				for (int j = 0; j < numTags; j++) {
					tags.add(in.next());
				}
				
				result.add(new Pic(i, tags, horizontal));
			}		
			
			in.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// Write our slideshow to a output file
	public static void writeOutput(String inputFile, ArrayList<Slide> slides) {
		String filename = "./../outputs/output" + inputFile + ".txt";
		
		try {
			File outputfile = new File(filename);
			FileWriter out = new FileWriter(outputfile);
			BufferedWriter bw = new BufferedWriter(out);
			bw.write(Integer.toString(slides.size()));
			for (Slide slide: slides) {
				bw.newLine();
				bw.write(slide.getOutputString());
			}
			
			bw.close();
			out.close();
		} catch (IOException e) {e.printStackTrace();}
	}	
}