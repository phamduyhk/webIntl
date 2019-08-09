import java.util.*;
	public class PredArgStruct {
		//述語変数
		Morpheme pred;
		//項
		Map<String,String> args = new HashMap<String,String>();
		
		public Morpheme getPred() {
			return this.pred;
		}
		public Map<String,String> getArgs(){
			return this.args;
		}
		public void setPred(Morpheme pred) {
			this.pred = pred;
		}
		public void addArgs(String key,String value) {
			args.put(key, value);
		}
}
