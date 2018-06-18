package insights;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.personality_insights.v3.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.ProfileOptions;

public class Insights_lib {

	private PersonalityInsights service;
	private List<Double> bigPersonality = new ArrayList<Double>();
	private List<List<Double>> bigList = new ArrayList<List<Double>>();

	public Insights_lib() {
		service = new PersonalityInsights("2016-10-19");
		service.setUsernameAndPassword("2381ade7-3d6a-4e20-940a-87fdec5e63a5", "2jpBRC3ud3oI");
	}

	public void getProfile(String text) {
		ProfileOptions options = new ProfileOptions.Builder().text(text).build();
		Profile profile = service.profile(options).execute();
		System.out.println(profile);
		this.getJson(profile.toString(),text);
	}

	public void getJson(String json,String text) {

		JsonNode node = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			node = mapper.readTree(json.toString());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		//大きいカテゴリーだけ取得
		for(JsonNode jn : node.get("personality")){
			bigPersonality.add(jn.get("percentile").asDouble());
		}
		//小さいカテゴリだけ取得
		for(JsonNode jn : node.get("personality")){
			//適当なListを作って入れる
			List<Double> test = new ArrayList<Double>();
			for(JsonNode jnn : jn.get("children")){
				test.add(jnn.get("percentile").asDouble());
			}
			bigList.add(test);
		}
		MySQL mysql = new MySQL();
		mysql.insertScreens(bigPersonality, bigList, text);
	}
}
