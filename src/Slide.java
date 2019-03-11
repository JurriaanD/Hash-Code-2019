import java.util.ArrayList;
import java.util.HashSet;

public class Slide {
	public HashSet<String> tags = new HashSet<>();
	public ArrayList<Pic> pics = new ArrayList<>();
	int id;
	boolean used = false;
	
	public Slide (Pic pic1, Pic pic2) {
		tags.addAll(pic1.tags); tags.addAll(pic2.tags);
		pics.add(pic1); pics.add(pic2);
		id = pic1.id;
	}
	
	public Slide(Pic pic) {
		this.tags = pic.tags;
		this.pics.add(pic);
		id = pic.id;
	}
	
	// For the output file
	public String getOutputString() {
		String result = "";
		for (Pic pic : pics) {
			result += pic.id + " ";
		}
		return result;
	}
}
