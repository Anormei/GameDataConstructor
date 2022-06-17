package axismaker;

import java.util.ArrayList;
import java.util.List;

public class Shape {

	private List<PointInfo> points = new ArrayList<>();
	private boolean greaterFirst;
	private Vector2 angle = new Vector2();
	
	public class PointInfo {
		public Shape shape;
		public int index;
		public Vector2 vector;
		public PointInfo opposite;
		
		public PointInfo(Vector2 vector, int i) {
			this.shape = Shape.this;
			this.index = i;
			this.vector = vector;
		}
		
		public void reAdjust(Shape shape) {
			this.shape = shape;
			this.index = shape.getPoints().size();
			shape.addPoint(this);
		}
		
		public float angle() {
			float angle1 = (float)Math.atan2(vector.y - head().vector.y, vector.x - head().vector.x);
			float angle2 = (float)Math.atan2(vector.y - tail().vector.y, vector.x - tail().vector.x);
			float angle = 360 - ((angle1 - angle2) * Vector2.TO_DEGREES);
			if(angle > 360) {
				angle -= 360;
			}
			
			return angle;
		}
		
		public PointInfo head() {
			return greaterFirst ? shape.points.get((index + 1) % shape.points.size()) : shape.points.get(index - 1 < 0 ? shape.points.size() - 1 : index - 1);
		}
		
		public PointInfo tail() {
			return greaterFirst ? shape.points.get(index - 1 < 0 ? shape.points.size() - 1 : index - 1) : shape.points.get((index + 1) % shape.points.size());
		}
	}
	
	public Shape() {
		
	}
	
	public Shape(List<Vector2> axis) {
		float sum1 = angle.set(axis.get(0)).sub(axis.get(1)).angle();
		float sum2 = angle.set(axis.get(0)).sub(axis.get(axis.size() - 1)).angle();
		greaterFirst = sum1 < sum2;
		//System.out.println("GreaterFirst ? " + greaterFirst + ", angle(0 to 1) = " + sum1 + ", angle(0 to size - 1) = " + sum2);
		for(int i = 0; i < axis.size(); i++) {
			Vector2 vector = new Vector2(axis.get(i));
			points.add(new PointInfo(vector, i));
		}
	}
	
	public void addPoint(PointInfo tag) {
		points.add(tag);
	}
	
	public List<PointInfo> getPoints(){
		return points;
	}
	
	public List<Vector2> toVectors(){
		List<Vector2> list = new ArrayList<>();
		for(int i = 0; i < points.size(); i++) {
			list.add(points.get(i).vector);
		}
		
		return list;
	}
	
	public Vector2 getVector(int i) {
		return points.get(i).vector;
	}
	
	public void updateIndexes() {
		for(int i = 0; i < points.size(); i++) {
			PointInfo point = points.get(i);
			point.index = i;
		}
	}
	
	public void setGreaterFirst(boolean b) {
		greaterFirst = b;
	}
	
	public Shape splitShape(int i1, int i2) {
		List<PointInfo> bin = new ArrayList<>();
		int right = (i2 > i1 ? i2 : i2 + points.size()) - i1;
		int left = i1 - (i2 < i1 ? i2 : i2 + points.size());
		
		int start = right < left ? i1 : i2;
		int end = right < left ? i2 : i1;
		//System.out.println(String.format("i1 = %d, i2 = %d, right = %d, left = %d, start = %d, end = %d", i1, i2, right, left, start, end));
		
		Shape shape = new Shape();
		shape.setGreaterFirst(greaterFirst);
		int i = start;
		while(i != end + 1){
			if(i >= points.size()) {
				//System.out.println(String.format("i = %d, points.size() = %d", i, points.size()));
				i = 0;
				//System.out.println(String.format("after-i = %d, points.size() = %d", i, points.size()));
			}
			PointInfo point = points.get(i);
			
			if(i == start || i == end) {
				PointInfo opposite = new PointInfo(point.vector, shape.getPoints().size());
				opposite.shape = shape;
				if(i == i1) {
					point.opposite = opposite;
					//tag.alt = point;
				}
				
				if(i == i2) {
					point.opposite = opposite;
				}

				shape.addPoint(opposite);
			}else {
				point.reAdjust(shape);
				bin.add(point);
			}
			
			if(i != end + 1) {
				i++;
			}
		}
		
		points.removeAll(bin);
		updateIndexes();
		return shape;
	}
	
}
