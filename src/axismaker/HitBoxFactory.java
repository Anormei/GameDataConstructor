package axismaker;

import java.util.List;

public class HitBoxFactory {

	public static String createBody(String fileName, List<List<Vector2>> axes) {
		
		String code = "";
		String hitBoxCode = "";
		float bodyWidth = 0;
		float bodyHeight = 0;
		
		//HitBox
		Vector2 pos = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
		float width;
		float height;
		
		for(int i = 0; i < axes.size(); i++) {
			List<Vector2> axis = axes.get(i);
			width = 0;
			height = 0;
			pos.set(Float.MAX_VALUE, Float.MAX_VALUE);
			
			for(int j = 0; j < axis.size(); j++) {
				Vector2 point = axis.get(j);
				
				if(point.x < pos.x) {
					pos.x = point.x;
				}
				
				if(point.y < pos.y) {
					pos.y = point.y;
				}
				
				if(point.x > width) {
					width = point.x;
				}
				
				if(point.y > height) {
					height = point.y;
				}
				
				if(point.x > bodyWidth) {
					bodyWidth = point.x;
				}
				
				if(point.y > bodyHeight) {
					bodyHeight = point.y;
				}
			}
			width -= pos.x;
			height -= pos.y;
			//HitBox hitBox = new HitBox(pos, width, height, new Vector2[] {pos});
			hitBoxCode += "\tnew HitBox(new Vector2(" + pos.x + "f, " + pos.y + "f), " + width + "f, " + height + "f, \n";
			
			for(int j = 0; j < axis.size(); j++) {
				Vector2 point = axis.get(j);
				hitBoxCode += "\t\tnew Vector2(" + point.x + "f, " + point.y + "f)";
				if(j < axis.size() - 1) {
					hitBoxCode += ",\n";
				}
			}
			
			hitBoxCode += ")";
			
			if(i < axes.size() - 1){
				hitBoxCode += ",\n";
			}
			
		}
		
		code = "new Body(\"" + fileName + "\", " + bodyWidth + "f, " + bodyHeight + "f, \n" + hitBoxCode + ")";
		
		return code;
	}
}
