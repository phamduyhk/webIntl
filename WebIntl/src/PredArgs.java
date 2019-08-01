import java.util.ArrayList;
public class PredArgs extends ArrayList<PredArgStruct>{
	PredArgs(){
		super();
	}
	public void addPredArgSruct(PredArgStruct item) {
		this.add(item);
	}
	public String getSurface() {
		String str = "";
		for(PredArgStruct item : this) {
			str+=item.getPred().getBaseform()+item.getArgs()+"\n";
		}
		return str.substring(0, str.length()-1);
	}
}
