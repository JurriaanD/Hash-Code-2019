import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Strategies {
	/*
	 * 		SLIDE SORTING
	 * All slide sorting strategies are greedy.
	 * Ideally we would find a longest path, put that problem is NP-complete.
	 * We 'sort' the slides in place.
	 * I've added a 'sorted' attribute to the Slide and Pic classes to indicate that
	 * we've already it to the slideshow.
	 */
	public static void basicSlideSort(ArrayList<Slide> slides) {
		HashMap<String, ArrayList<Slide>> map = Helper.getTagSlidesMap(slides);
		// Take a random slide as first slide
		int startIdx = (int) (Math.random() * slides.size());
		Collections.swap(slides, 0, startIdx);
		slides.get(0).used = true;
		
		// For each position in the slideshow: 
		// look for the best slide to put next.
		// swap that slide to the first free position
		for (int i = 0; i < slides.size() - 1; i++) {
			Slide currentSlide = slides.get(i);
			
			// Others is a set of slides that share at least 1 tag
			HashSet<Slide> others = new HashSet<>();
			for (String tag : currentSlide.tags) {
				others.addAll(map.get(tag));
			}
			
			int bestScore = -1;
			Slide bestSlide = null;
			for (Slide other : others) {
				if (other == currentSlide || other.used) { continue; }
				int score = Helper.getScore(other.tags, currentSlide.tags);
				if (score > bestScore) {
					bestScore = score;
					bestSlide = other;
				}
			}
			
			// If we had no slides left that share at least 1 tag
			// Basically do nothing (swap the next slide with itself)
			int bestIdx = i+1;
			if (bestSlide != null) {				
				bestIdx = slides.indexOf(bestSlide);
				bestSlide.used = true;
			}
			Collections.swap(slides, i+1, bestIdx);
		}
	}
	
	// Same as basicSlideSort, but stores the index instead of the object of the best next slide
	// We save some slides.indexOf calls, so it should be faster
	public static void fastBasicSlideSort(ArrayList<Slide> slides) {
		HashMap<String, ArrayList<Slide>> map = Helper.getTagSlidesMap(slides);

		// Take a random slide as first slide
		int startIdx = (int) (Math.random() * slides.size());
		Collections.swap(slides, 0, startIdx);
		slides.get(0).used = true;
		
		for (int i = 1; i < slides.size() - 1; i++) {
			Slide currentSlide = slides.get(i - 1);
			int bestScore = -1;
			Slide bestSlide = null;
			
			HashSet<Slide> others = new HashSet<>();
			for (String tag: currentSlide.tags) {
				others.addAll(map.get(tag));
			}

			for (Slide other : others) {
				if (currentSlide == other || other.used) { continue; }
				int score = Helper.getScore(currentSlide.tags, other.tags);
				if (score > bestScore) {
					bestScore = score;
					bestSlide = other;
				}
			}

			int bestIdx = i;
			if (bestSlide != null) {
				bestIdx = slides.indexOf(bestSlide);
			}

			Collections.swap(slides, i, bestIdx);
			slides.get(i).used = true;
		}
	}
	
	// Only use this function for A, B and C! the mates map is too big for D and E.
	// Again, basically the same as basicSort, but we don't have to build the set of mates each time
	public static void otherFastBasicSlideSort(ArrayList<Slide> slides) {
		HashMap<Slide, HashSet<Slide>> mates = Helper.getSlideMatesMap(slides);
		
		for (int i = 1; i < slides.size() - 1; i++) {
			Slide currentSlide = slides.get(i-1);
			int bestScore = -1;
			Slide bestSlide = null;
			
			for (Slide other : mates.get(currentSlide)) {
				if (other.used) { continue; }
				int score = Helper.getScore(currentSlide.tags, other.tags);
				if (score > bestScore) {
					bestScore = score;
					bestSlide = other;
				}
			}
			
			int bestIdx = i;
			if (bestSlide != null) {
				bestIdx = slides.indexOf(bestSlide);
			}
			
			Collections.swap(slides, i, bestIdx);
			slides.get(i).used = true;
		}
	}	
	
	// Go through all slides. For each slide, look at n unused slides and find the best one.
	// Place that one at the end of the slideshow.
	public static void boundedSlideSort(ArrayList<Slide> slides) {
		final int MAX_TRIES = 2500;
		
		// Take a random slide as first slide
		int startIdx = (int) (Math.random() * slides.size());
		Collections.swap(slides, 0, startIdx);
		slides.get(0).used = true;
		
		for (int i = 0; i < slides.size(); i++) {			
			Slide currentSlide = slides.get(i);
			int bestScore = -1;
			int bestIdx = 0;
			
			// Look at MAX_TRIES slides
			for (int j = 0; j < MAX_TRIES; j++) {
				int currentIdx = i + (int) (Math.random() * (slides.size() - i));
				int score = Helper.getScore(currentSlide.tags, slides.get(currentIdx).tags);
				if (score > bestScore) {
					bestScore = score;
					bestIdx = currentIdx;
				}
			}
			
			Collections.swap(slides, i, bestIdx);
		}
	}
	

	/*
	 * 		INITIALIZATION
	 * We read the input file, do something with the vertical pics and return a list of (unsorted) slides.
	 * All of these strategies are greedy as well.
	 */
	// Just combine random vertical pictures 
	public static ArrayList<Slide> initSlidesMergeVerticalsRandom (String dataset) {
		ArrayList<Pic> pics = Main.readInput(dataset);
		ArrayList<Slide> slides = new ArrayList<>();
		ArrayList<Pic> verticals = new ArrayList<>();
		
		for (Pic pic : pics) {
			if (pic.horizontal) {
				slides.add(new Slide(pic));
			} else {
				verticals.add(pic);
			}
		}
		
		Collections.shuffle(verticals);
		
		for (int i = 0; i < verticals.size() - verticals.size() % 2; i+=2) {
			slides.add(new Slide(verticals.get(i), verticals.get(i+1)));
		}
		
		return slides;
	}
	
	// Combine vertical pictures so they all have about the same number of tags
	public static ArrayList<Slide> initSlidesMergeVerticalsTagLength (String dataset) {
		ArrayList<Pic> pics = Main.readInput(dataset);
		ArrayList<Slide> slides = new ArrayList<>();
		ArrayList<Pic> verticals = new ArrayList<>();
		
		for (Pic pic : pics) {
			if (pic.horizontal) {
				slides.add(new Slide(pic));
			} else {
				verticals.add(pic);
			}
		}
		
		Collections.sort(verticals, (p1, p2) -> Integer.compare(
				p1.tags.size(),
				p2.tags.size()
				));
		
		// Combine the pic with the least tags with pic with the most tags etc.
		int size = verticals.size();
		for (int i = 0; i < size - i; i++) {
			slides.add(new Slide(verticals.get(i), verticals.get(size - 1 - i)));
		}

		return slides;
	}
	
	// When we combine vertical pictures that share tags, those tags are kind of lost.
	// So here we try to combine the vertical pics to minimize the overlap in tags
	public static ArrayList<Slide> initSlidesMergeVerticalsMinOverlap (String dataset) {
		ArrayList<Pic> pics = Main.readInput(dataset);
		ArrayList<Slide> slides = new ArrayList<Slide>();
		ArrayList<Pic> verticals = new ArrayList<Pic>();
		
		for (Pic pic : pics) {
			if (pic.horizontal) {
				slides.add(new Slide(pic));
			} else {
				verticals.add(pic);
			}
		}
		
		for (int i = 0; i < verticals.size()-1; i+=2) {
			Pic pic = verticals.get(i);
			int smallestIntersect = Integer.MAX_VALUE;
			int smallestIntersectIdx = i+1;
			
			for (int j = i+1; j < verticals.size(); j++) {
				Pic pic2 = verticals.get(j);
				int intersectElements = Helper.getIntersectCount(pic.tags, pic2.tags);
				if (intersectElements < smallestIntersect) {
					smallestIntersect = intersectElements;
					smallestIntersectIdx = j;
				}
				// If we found a pic with 0 overlap, stop looking! Best we can get.
				if (intersectElements == 0) { break; }
			}
			
			Collections.swap(verticals, i+1, smallestIntersectIdx);
			slides.add(new Slide(verticals.get(i), verticals.get(i+1)));
		}
		
		return slides;
	}
}
