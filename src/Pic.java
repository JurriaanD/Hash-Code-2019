import java.util.HashSet;

public class Pic {
	public HashSet<String> tags;
	public boolean horizontal;
	public int id;
	
	public Pic (int id, HashSet<String> tags, boolean horizontal) {
		this.tags = tags;
		this.horizontal = horizontal;
		this.id  = id;
	}
}
