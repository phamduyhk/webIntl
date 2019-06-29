import java.util.List;
public class PersonRelAnalyzer {
	/**
     * CaboChaの係り受け解析を試す例
     */
	public static void getPhrases(Chunk headChunk, String joshi) {
		List<Chunk> dependents = headChunk.getDependents();
		for (Chunk dependent: dependents) {
        	Morpheme headMorph = dependent.getHeadMorpheme();
        	if(headMorph.getPos().equals("助詞") &&
        			headMorph.getSurface().equals(joshi))
        	{
        		String topic = "";
                for (int i = 0; i < dependent.size(); i++) {
                    topic += dependent.get(i).getSurface();
                }
        		System.out.println("助詞の「の」がついた語句:"+topic);
        	}
        	else {
        		getPhrases(dependent, joshi);
        	}
        }
	}
    public static void main(final String[] args) {
        final String text = "知ってた？隣のお客さんはたくさん柿を食べるって。"; // 解析対象のテキスト
        System.out.println("解析対象のテキスト: "+text);

        // 対象テキストを文に分割した上でCaboChaに渡し，解析結果を受け取る．
        final DepParser parser = new CaboCha();
        final List<Sentence> sentences = parser.parseText(text);
        System.out.println("係り受け解析結果:\n" + sentences);
        
        // [「食べる」という述語に係るハ格を取り出し]
        for (final Sentence sentence : sentences) { // 文を1つずつ処理するループ
            // 主辞（最後の文節）を取得
            final Chunk headChunk = sentence.getHeadChunk(); 
//            System.out.println(headChunk);
            // 主辞に係る文節のリストを返す
            final List<Chunk> dependents = headChunk.getDependents();
            for (final Chunk dependent : dependents) {
                // 文節dependentの主辞（最後の形態素）を取得
                final Morpheme headMorph = dependent.getHeadMorpheme();
                //[DEBUG]
//                System.out.println("dependent"+dependent);
//                System.out.println("dependent.getHeadMorpheme"+headMorph);
                // 助詞の「は」かどうか判定するif文
                if (headMorph.getPos().equals("助詞") && 
                        headMorph.getSurface().equals("は")) 
                {
                    // 文節dependent内の助詞以外の形態素をつなげる
                    String topic = "";
                    for (int i = 0; i < dependent.size()-1; i++) {
                        topic += dependent.get(i).getSurface();
                    }
                    System.out.println("主辞に係るハ格: "+topic);
                }
                // ヲ格
                if (headMorph.getPos().equals("助詞") && 
                        headMorph.getSurface().equals("を")) 
                {
                    // 文節dependent内の助詞以外の形態素をつなげる
                    String topic = "";
                    for (int i = 0; i < dependent.size()-1; i++) {
                        topic += dependent.get(i).getSurface();
                    }
                    System.out.println("主辞に係るヲ格: "+topic);
                }                      
            }
            getPhrases(headChunk,"の");
 
        }
    }
}