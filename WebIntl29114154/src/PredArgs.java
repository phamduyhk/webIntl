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
			if(item.getPred()!=null) {
				if(item.getPred().getBaseform().length()>0) {
					if(item.getArgs().size()<=0) {
						str+=item.getPred().getBaseform()+"\n";
					}
					else {
						str+=item.getPred().getBaseform()+item.getArgs()+"\n";
					}
				}
			}
		}
		if(str.length()==0)
			return null;
		return str.substring(0, str.length()-1);
	}
}
