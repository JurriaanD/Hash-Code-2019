import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/*
 * Functions to setup/fill data structures.
 * See the readme under **Insights** for the meaning of 'mates'
 */
public class Helper {
	// Returns a map <Tag, list of all slides with that tag>
	public static HashMap<String, ArrayList<Slide>> getTagSlidesMap (ArrayList<Slide> slides) {
		HashMap<String, ArrayList<Slide>> result = new HashMap<>();
		
		for (Slide slide : slides) {
			for (String tag : slide.tags) {
				if (result.containsKey(tag)) {
					result.get(tag).add(slide);
				} else {
					ArrayList<Slide> l = new ArrayList<>();
					l.add(slide);
					result.put(tag, l);
				}
			}
		}
		
		return result;
	}
	
	// Returns a map <Slide, set of mates of that slide>
	// Warning: because D and E have so few tags, you'll need a LOT of memory to get this map
	public static HashMap<Slide, HashSet<Slide>> getSlideMatesMap (ArrayList<Slide> slides) {
		HashMap<String, ArrayList<Slide>> tagMap = getTagSlidesMap(slides);
		HashMap<Slide, HashSet<Slide>> result = new HashMap<>();
		
		for (int i = 0; i < slides.size(); i++) {
			Slide slide = slides.get(i);
			HashSet<Slide> set = new HashSet<>();
			for (String tag : slide.tags) {
				for (Slide otherSlide : tagMap.get(tag)) {
					if (otherSlide != slide) {
						set.add(otherSlide);
					}
				}
			}
			result.put(slide, set);
		}		
		
		return result;
	}
	
	// Same as getSlideMatesMap() but we sort the mates.
	// The first mate in the list gives the biggest interest score, the last the smallest.
	// Calculating this might be a bad idea! Because we're sorting, this is O(n*log(n))!
	// You should only use this function if you are repeatedly looking for the mates of the same slide.
	public static HashMap<Slide, ArrayList<Slide>> getSortedSlideMatesMap(ArrayList<Slide> slides) {
		HashMap<String, ArrayList<Slide>> tagMap = getTagSlidesMap(slides);
		HashMap<Slide, ArrayList<Slide>> result = new HashMap<>();
		
		for (int i = 0; i < slides.size(); i++) {
			Slide slide = slides.get(i);
			ArrayList<Slide> list = new ArrayList<Slide>();
			for (String tag : slide.tags) {
				for (Slide other : tagMap.get(tag)) {
					if (other != slide) {
						list.add(other);
					}
				}
			}
			
			Collections.sort(list, (s1, s2) -> Integer.compare(
					Helper.getScore(s1.tags, slide.tags),
					Helper.getScore(s2.tags, slide.tags)
					));
			
			result.put(slide, list);
		}
		
		return result;
	}
	
	// Returns a map <Pic, mates of the pic>
	public static HashMap<Pic, HashSet<Pic>> getPicMatesMap (ArrayList<Pic> pics) {
		HashMap<String, HashSet<Pic>> tagMap = getTagPicsMap(pics);
		HashMap<Pic, HashSet<Pic>> result = new HashMap<>();
		
		for(Pic pic : pics) {
			HashSet<Pic> set = new HashSet<>();
			for (String tag : pic.tags) {
				for (Pic other : tagMap.get(tag)) {
					if (other != pic) {
						set.add(other);
					}
				}
			}
			result.put(pic, set);
		}		
		
		return result;
	}
	
	// Returns a map <Tag, set of all pics with that tag>
	public static HashMap<String, HashSet<Pic>> getTagPicsMap (ArrayList<Pic> pics) {
		HashMap<String, HashSet<Pic>> result = new HashMap<>();
		
		for (Pic pic : pics) {
			for (String tag : pic.tags) {
				if (result.containsKey(tag)) {
					result.get(tag).add(pic);
				} else {
					HashSet<Pic> l = new HashSet<>();
					l.add(pic);
					result.put(tag, l);
				}
			}
		}
		
		return result;
	}
	
	// Checks if every picture is only used once
	public static void checkSolutionIntegrity(ArrayList<Slide> slides) {
		HashSet<Integer> set = new HashSet<>();
		for (Slide slide : slides) {
			int sizeBefore = set.size();
			for (Pic pic : slide.pics) {
				set.add(pic.id);
			}
			if (set.size() != sizeBefore + slide.pics.size()) {
				System.out.println("Found a double!");
				System.exit(0);
			}
		}
	}
	
	// Returns the score for an entire slideshow
	public static int getSlideShowScore(ArrayList<Slide> slides) {
		int score = 0;
		for (int i = 0; i < slides.size()-1; i++) {
			score += getScore(slides.get(i).tags, slides.get(i+1).tags);
		}
		return score;
	}
	
	// Returns the score between two slides given their tags
	public static int getScore(HashSet<String> tags1, HashSet<String> tags2) {
		int intersectElements = getIntersectCount(tags1, tags2);
		
		return Math.min(intersectElements, Math.min(tags1.size() - intersectElements, tags2.size() - intersectElements));
	}
	
	// Returns the number of elements in the intersection of the two sets of tags
	public static int getIntersectCount(HashSet<String> tags1, HashSet<String> tags2) {
		int intersectElements = 0;
		
		for (String tag : tags1) {
			if (tags2.contains(tag)) {
				intersectElements++;
			}
		}
		
		return intersectElements;
	}
	
	// Merges the two given sets into a new set
	public static HashSet<String> getTagUnion(HashSet<String> tags1, HashSet<String> tags2) {
		HashSet<String> result = new HashSet<>();
		result.addAll(tags1);
		result.addAll(tags2);
		return result;
	}	
}
