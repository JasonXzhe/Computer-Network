public class fnode {
		String	filename = null;
		String	checksum = null;
		boolean flag = true;
		
	public fnode(String name, String chm) {
		filename = name;
		checksum = chm;
	}
	
	public fnode(String name, String chm, boolean f) {
		filename = name;
		checksum = chm;
		flag = f;
	}
	
	
	public fnode() {}
}
