package axismaker;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import axismaker.Shape.PointInfo;



public class AxisFactory {

	private static class AxisComparator implements Comparator<PointInfo>{

		private float mid = 0;
		private float angle2 = 0;
		private Vector2 center;
		Vector2 direction = new Vector2();
		
		@Override
		public int compare(PointInfo o1, PointInfo o2) {
			// TODO Auto-generated method stub
			Vector2 vec1 = o1.vector;
			Vector2 vec2 = o2.vector;
			
			// Calculate angle between non-convex point and another point

			
			// Get angle
			direction.set(center);
			direction.sub(vec1);
			float dirAngle1 = direction.angle();
			if(angle2  > 360.0f && dirAngle1 < angle2 - 360.0f) {
				dirAngle1 += 360.0f;
				//print(String.format("dirAngle[%d] = %f", i, dirAngle));
			}
			
			direction.set(center);
			direction.sub(vec2);
			float dirAngle2 = direction.angle();
			if(angle2  > 360.0f && dirAngle2 < angle2 - 360.0f) {
				dirAngle2 += 360.0f;
				//print(String.format("dirAngle[%d] = %f", i, dirAngle));
			}
			
			// Increments the calculated angle if angle2 is above 360 but is greater than dirAngle if it was angle2 - 360.0f to gain an accurate calculation

			
			float difference1 = Math.abs(mid - dirAngle1);
			float difference2 = Math.abs(mid - dirAngle2);
			
			float difference = difference1 - difference2;
			
			if(difference == 0) {
				return 0;
			}else if(difference < 0) {
				return -1;
			}else {
				return 1;
			}
		}
		
		public void setParameters(float mid, Vector2 center, float angle2) {
			this.mid = mid;
			this.center = center;
			this.angle2 = angle2;
		}
		
	}
	
	private static final int[] filler = {74, 65, 42, 1};
	
	private static int[] p = new int[4];
	private static int[] pixels;
	private static List<Vector2> axis;
	private static Scanner scanner = new Scanner(System.in);
	
	private static int width = 0;
	private static int height = 0;
	
	private static Line2D lineCol1 = new Line2D.Float();
	private static Line2D lineCol2 = new Line2D.Float();
	
	/*
	 * Overall operation of getting each corners of a non-hollow image.
	 */
	public static List<Vector2> traceImage(String path) {
		BufferedImage raw = null;
		BufferedImage image;
		
		// 06/04/2020 full pixel image problem - the problem is that the pixels are stored in RGB format and not RGBA format
		
		try {
			raw = ImageIO.read(new File(path));
		}catch(IOException e) {
			System.out.println(e.getMessage());
		}
		


		width = raw.getWidth();
		height = raw.getHeight();
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.createGraphics().drawImage(raw, 0, 0, width, height, null);
		//print("width = " + width + ", height = " + height);
		pixels = image.getData().getPixels(0, 0, width, height, (int[]) null);
		print("" + image.getData().getPixels(0, 0, width, height, (int[]) null).length);
		autofill();
		//System.out.println(pixels.length + ", " + width * height + ", " + pixels.length / 4);
		axis = new ArrayList<>();
		axis.add(startPoint());
		print("axis.size = " + axis.size());
		Vector2 prev = new Vector2(-1.0f, -1.0f);
		
		print(String.format("Prev: x = %f, y = %f", prev.x, prev.y));
		
		Line2D line1 = null;
		Line2D line2 = new Line2D.Float(-1.0f, -1.0f, -1.0f, -1.0f);
		
		do {
			print(String.format("axis[%d]: x = %f, y = %f", axis.size() - 1, axis.get(axis.size() - 1).x, axis.get(axis.size() - 1).y));
			axis.add(findLine(axis.get(axis.size() - 1), prev));
			print("2. axis.size = " + axis.size());
			Vector2 tail = axis.get(axis.size() - 1);
			Vector2 prevTail = axis.get(axis.size() - 2);
			
			/*try {
			Thread.sleep(1000);
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			//render.drawLine((int)prevTail.x * 10, (int)prevTail.y * 10, (int)tail.x * 10, (int)tail.y * 10);
			//render.commit();
			*/
			if(line1 == null && axis.size() > 1) {
				line1 = new Line2D.Float(axis.get(0).x, axis.get(0).y, axis.get(1).x, axis.get(1).y);
			}
			
			if(axis.size() > 3) {
				Vector2 last = axis.get(axis.size() - 1);
				Vector2 prevLast = axis.get(axis.size() - 2);
				
				line2.setLine(prevLast.x, prevLast.y, last.x, last.y);
			}
			
			
		}while(!axis.get(axis.size() - 1).equals(axis.get(0)) && !line2.intersectsLine(line1));
		
		
		axis.remove(axis.size() - 1);
		print("Axis.size = " + axis.size());
		
		return axis;
	}
	
	public static void cleanAxis(List<Vector2> axis) {

	}
	
	
	/*
	 * Splits the given axis into a a number of convex shapes
	 */
	public static List<List<Vector2>> shatterAxis(List<Vector2> a){
		
		// Encapsulate the outline axis inside the Shape.
		Shape wholeShape = new Shape(a);
		
		// Create a list for the shapes that will be split from the whole shape.
		List<Shape> shapes = new ArrayList<>();
		AxisComparator comparator = new AxisComparator();
		// Add the initial whole shape into the list.
		shapes.add(wholeShape);
		
		// Obtain all the point info and create a list dedicated to non-convex points
		List<PointInfo> ax = wholeShape.getPoints();
		List<PointInfo> nonConvex = new ArrayList<>();
		
		// Go through all the points, measure their angle and add any non-convex angles to the list
		for(int i = 0; i < ax.size(); i++) {
			
			//float angle = getAngle(ax.get(i).vector, ax.get(i - 1 < 0 ? ax.size() - 1 : i -1).vector, ax.get((i + 1) % ax.size()).vector);
			float angle = ax.get(i).angle();
			if(angle > 180.0f) {
				nonConvex.add(ax.get(i));
			}
			print(String.format("axis.get(%d) x = %f, y = %f, angle = %f", i, ax.get(i).vector.x, ax.get(i).vector.y, angle));
		}
		
		print(String.format("nonconvex.size() = %d", nonConvex.size()));

		// Buffer for calculating direction of non-convex and desired point to split at
		Vector2 direction = new Vector2();
		
		List<PointInfo> potentialCandidates = new ArrayList<>();
		// Goes through all non-convex shapes and tries to find an adjacent point
		for(int i = 0; i < nonConvex.size(); i++) {

			//Isolates a non-convex point.
			PointInfo pointInfo = nonConvex.get(i);

			// Get all points and then create a list of points dedicated to potential candidates that the point can split to.
			
			List<PointInfo> points = pointInfo.shape.getPoints();


			//*float angle = getAngle(center, head, tail);
			print("angle = " + pointInfo.angle());
			
			// Confirms if the angle is already below 180.0f (if another non-convex point has split using another non-convex point) and checks if the angle has an opposite side from another shape
			if(pointInfo.angle() < 180.0f) {
				/*PointInfo last = pointInfo.opposite;
				PointInfo current = pointInfo;

				
				while(current.opposite != last) {
					if(current.opposite.angle() > 180.0f) {
						nonConvex.add(current.opposite);
					}
					last = current;
					current = current.opposite;
				}*/
				print(String.format("FOUND CONVEX[%d]: x = %f, y = %f; angle = %f; has opposite? %b", i, pointInfo.vector.x, pointInfo.vector.y, pointInfo.angle(), pointInfo.opposite != null));
				if(pointInfo.opposite != null) {
					print(String.format("Opposite: x = %f, y = %f, angle = %f", pointInfo.opposite.vector.x, pointInfo.opposite.vector.y, pointInfo.opposite.angle()));
					if(pointInfo.opposite.opposite != null)
					print(String.format("Opposite-opposite angle", pointInfo.opposite.opposite.angle()));
				}
				
			}
			
			// Keeps processing until it splits a non-convex point into a convex point
			while(pointInfo.angle() > 180.0f) {
				
				/*try {
					System.in.read();
				}catch(Exception e) {
					
				}*/
				
				Shape shape = pointInfo.shape;
				
				// Create Vector2 buffers for the current point position, head (previous point) and tail (next point).
				Vector2 center = pointInfo.vector;
				Vector2 head = pointInfo.head().vector;
				Vector2 tail = pointInfo.tail().vector;

				
				// The value of the shaved angle on both sides until it is a convex angle.
				float offsetAngle = (pointInfo.angle() - 180.0f) / 2.0f;
				
				// Borders of the valid angles with the offset angle calculated into it.
				float angle1 = (float)Math.atan2(center.y - head.y, center.x - head.x) * Vector2.TO_DEGREES + offsetAngle;
				float angle2 = (float)Math.atan2(center.y - tail.y, center.x - tail.x) * Vector2.TO_DEGREES - offsetAngle;
				
				//Make positive
				if(angle1 < 0.0f) {
					angle1 += 360.0f;
				}
				
				if(angle2 < 0.0f) {
					angle2 += 360.0f;
				}
				
				//float mid = angle1 + (((angle2 < angle1 ? angle2 + 360.0f : angle2) - angle1) / 2.0f);
				
				// Makes sure that angle2 is always larger than angle1 (tail bigger than head).
				
				if(angle2 < angle1) {
					angle2 += 360.0f;
				}
				
				// The best angle to split at (half of the non-convex angle).
				float mid = angle1 + (angle2 - angle1) / 2.0f;
				
				print(String.format("non-convex[%d], angle1 = %f, angle2 = %f, mid = %f", i, angle1, angle2, mid));
				
				// The chosen potential candidate index
				int chosen = 0;
				
				// Difference between a particular angle and the mid angle.
				float difference = 360;
				
				
				// Finds potential candidates that are within the splitting range.
				for(int j = 0; j < points.size(); j++) {
					if(j != pointInfo.index) {
						Vector2 neighbour = points.get(j).vector;
						
						// Calculate angle between non-convex point and another point
						direction.set(center);
						direction.sub(neighbour);
						
						// Get angle
						float dirAngle = direction.angle();
						
						// Increments the calculated angle if angle2 is above 360 but is greater than dirAngle if it was angle2 - 360.0f to gain an accurate calculation
						if(angle2  > 360.0f && dirAngle < angle2 - 360.0f) {
							dirAngle += 360.0f;
							//print(String.format("dirAngle[%d] = %f", i, dirAngle));
						}
						
						//Checks if it is within the desired range then adds it to a list of potential candidates.
						if((dirAngle > angle1 && dirAngle < angle2)) {
							//print(String.format("*non-convex[%d] found candidate[%d]: x = %f, y = %f at angle = %f", i, points.get(j).index, neighbour.x, neighbour.y, dirAngle));
							potentialCandidates.add(points.get(j));
							
						}
					}
				}
				
				comparator.setParameters(mid, center, angle2);
				potentialCandidates.sort(comparator);
				
				print("potential candidates = " + potentialCandidates.size() + ", ");
				
				// Number of intersections
				int intersections = 0;
				int bestIntersection = 0; // Logging purposes only
				boolean foundChosen = false;
				
				// Go through all potential candidates and find the most with the least difference to split at.
				for(Iterator<PointInfo> iterator = potentialCandidates.iterator(); iterator.hasNext() && !foundChosen;) {
					// Calculate angle of candidate
					PointInfo candidate = iterator.next();
					direction.set(center);
					direction.sub(candidate.vector);
					float dirAngle = direction.angle();
					
					
					// Translate direction angle to angle2 values (if > 360).
					if(angle2  > 360.0f && dirAngle < angle2 - 360.0f) {
						dirAngle += 360.0f;
						//print(String.format("dirAngle[%d] = %f", i, dirAngle));
					}
					
					// Calculate intersections between non-convex and candidate
					intersections = countIntersects(pointInfo, candidate, potentialCandidates);
					
					// Accept the value if it is below the last recent difference.
					if(Math.abs(mid - dirAngle) < difference && intersections == 0) {
						
						foundChosen = true;
						chosen = candidate.index;
						//bestIntersection = intersections;
						difference = Math.abs(mid - dirAngle); // Calculate new difference
						//print(String.format("intersects[%d] = %d", i, countIntersects(center, candidate.vector, potentialCandidates)));
					}
				}
				
				if(!foundChosen || potentialCandidates.size() == 0) {
					System.out.println("Found NO candidates!");
				}
				
				// Logging purposes
				
				direction.set(center);
				direction.sub(points.get(chosen).vector);
				
				PointInfo chosenCandidate = points.get(chosen);
				
				// Logging/drawing -START-
				
				/*try {
				Thread.sleep(100);
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}*/
				
				/*
				r.drawLine((int)center.x * 10, (int)center.y * 5, (int)points.get(chosen).vector.x * 10, (int)points.get(chosen).vector.y * 5);
				r.commit();
				print(String.format("non-convex[%d]: x = %f, y = %f; chosen[%d]: x = %f, y = %f; angle = %f; intersections = %d", i, center.x, center.y, chosen, points.get(chosen).vector.x, points.get(chosen).vector.y, direction.angle(), bestIntersection));
	*/
				// Logging/drawing -END-
				
				// Split the shape into two and make the non-convex point convex.
				shapes.add(shape.splitShape(pointInfo.index, chosen));
				
				// Check the opposite side (if applicable).
				
				if(pointInfo.opposite.angle() > 180.0f) {
					nonConvex.add(pointInfo.opposite);
				}
				
				if(chosenCandidate.opposite.angle() > 180.0f) {
					nonConvex.add(chosenCandidate.opposite);
				}
				
				print(String.format("shape.size() = %d angle = %f", points.size(), pointInfo.angle()));
				
				print("altTag is null ? " + (pointInfo.opposite == null));
				print(String.format("angle = %f, altAngle = %f", pointInfo.angle(), pointInfo.opposite.angle()));

				potentialCandidates.clear();
				//print(String.format("nonConvex.get(%d), offsetAngle = %f", i, offsetAngle));
				print("-----------------------------");
				//scanner.nextLine();
			}
		}
		
		print("fin~");
		
		List<List<Vector2>> axis = new ArrayList<>();
		
		for(int i = 0; i < shapes.size(); i++) {
			axis.add(shapes.get(i).toVectors());
		}
		
		return axis;
	}
	
	/*
	 * Counts 
	 */
	private static int countIntersects(PointInfo tag1, PointInfo tag2, List<PointInfo> candidates) {
		int count = 0;
		
		//Get the Vectors
		Vector2 point1 = tag1.vector;
		Vector2 point2 = tag2.vector;
		Line2D main = new Line2D.Float(point1.x, point1.y, point2.x, point2.y);
		Line2D line1 = new Line2D.Float();
		Line2D line2 = new Line2D.Float();
		for(int i = 0; i < candidates.size(); i++) {
			PointInfo tag = candidates.get(i);
			Vector2 vector = tag.vector;
			Shape segment = tag.shape;
			List<PointInfo> points = segment.getPoints();
			
			PointInfo head = points.get(tag.index - 1 >= 0 ? tag.index : points.size() -1);
			PointInfo tail = points.get((tag.index + 1) % points.size());
			
			line1.setLine(vector.x, vector.y, head.vector.x, head.vector.y);
			line2.setLine(vector.x, vector.y, tail.vector.x, tail.vector.y);
			if(tag.index != tag2.index && head.index != tag2.index && tail.index != tag2.index) {
				if(main.intersectsLine(line1) || main.intersectsLine(line2)) {
					//print(String.format("point1[x:%f, y:%f] to point[x:%f, y:%f] intersects candidate[%d] at x = %f, y = %f", point1.x, point1.y, point2.x, point2.y, tag.index, vector.x, vector.y));
					count++;
				}
			}
		}
		
		//print("--------------");
		
		return count;
	}
	
	/*
	 * Finds a straight line of an outline
	 */
	private static Vector2 findLine(Vector2 point, Vector2 prev) {
		int counter = 0; //Length of the valid line
		int[] pixel = new int[4]; //Data of pixel
		float[] rotationMatrix = new float[] {
				0, 1,
				1, 0
		};
		
		float[] targetRotationMatrix = new float[] {
				0, 1,
				1, 0
		};
		
		int largestCounter = 0;
		int largestAngle = 0;
		Vector2 nextPoint = new Vector2();
		
		//Cursor is current position from the starting position.
		Vector2 cursor = new Vector2();
		
		float rad;
		float cos;
		float sin;
		
		boolean valid;

		print(String.format("Prev: x = %f, y = %f", prev.x, prev.y));
		
		//Goes through each degree as an integer in increments of 1.
		for(int i = 0; i != 360; i++) { 
			//Makes sure angle is <= 360
			if(i > 360) {
				i %= 360;
			}
			
			//Set the cursor to the start of the line.
			cursor.set(point);
			
			//Get first pixel (for first condition in loop to be true).
			getPixel((int)cursor.x, (int)cursor.y, pixel);
			//print(String.format("Angle = %d", i));
			
			//Get cos, sin for rotation.
			rad = ((float)i) * Vector2.TO_RADIANS;
			cos = (float)Math.cos(rad);
			sin = (float)Math.sin(rad);
			
			//Rotation matrix for rotating the extending line.
	        rotationMatrix[0] = cos;
	        rotationMatrix[2] = sin;
	        
	        //Count sides (for first condition in loop to be true).
	        int sides = countSides((int)Math.round(cursor.x), Math.round((int)cursor.y));
	        
	        valid = true;
			counter = 0;
			
			//Measures the length of the 'valid' line - true as long as the next position in the line exists a pixel.
			while(valid) {
				//Set cursor to next position of the rotated line.
				cursor.x = rotationMatrix[0] * counter + point.x;
				cursor.y = rotationMatrix[2] * counter + point.y;
				//print(String.format("cursor.x = %f cursor.y = %f, prev.x = %f, prev.y = %f counter = %d",cursor.x, cursor.y, prev.x, prev.y, counter));
				//Checks if the next position is within the boundaries of an image and does not equal the previous position of the starting position.
				if(Math.round(cursor.x) >= 0 && (Math.round(cursor.x) < width) 
						&& Math.round(cursor.y) >= 0 && Math.round(cursor.y) < height
						&& !(Math.round(cursor.x) == prev.x && Math.round(cursor.y) == prev.y)){
					//print(String.format("1. cursor.x = %f cursor.y = %f",cursor.x, cursor.y));
					getPixel(Math.round(cursor.x), Math.round(cursor.y), pixel); //Get the next pixel data
					//print(String.format("2. cursor.x = %d cursor.y = %d", Math.round(cursor.x), Math.round(cursor.y)));
					sides = countSides(Math.round(cursor.x), Math.round(cursor.y));
					
					//print("Sides = " + sides);
					/*
					 * Checks if a pixel exists (alpha > 0) and that the pixel is part of an outline.
					 */
					if(pixel[3] > 0 && sides > 0) {
						counter++; //Increase the length of the line
					}else {
						valid = false; //Otherwise end the length of the line.
					}
				}else {
					valid = false; //End length of line.
				}
			}
			
			if(straight(i)) {
				counter *= 2;
			}
			
			//Checks if the length of the line is bigger than the last biggest line.
			//was > before
			if(counter > largestCounter && !axisCollision(cursor, rotationMatrix)) {
				//Update largest line.
				largestCounter = counter;
				largestAngle = i;
				//Get the rotation of the long line.
				targetRotationMatrix[0] = rotationMatrix[0];
				targetRotationMatrix[2] = rotationMatrix[2];
				print(String.format("Angle = %d, largestCounter = %d", i, largestCounter));
				print(String.format("cursor.x = %d cursor.y = %d AT CHANGING COUNTER", Math.round(cursor.x), Math.round(cursor.y)));
			}
		}
		
		//print(String.format("prev.x = %f, prev.y = %f", (targetRotationMatrix[0] * (largestCounter-2) + point.x), (targetRotationMatrix[2] * (largestCounter-2) + point.y)));
		//print(String.format("next.x = %f, next.y = %f", (targetRotationMatrix[0] * (largestCounter-1) + point.x), (targetRotationMatrix[2] * (largestCounter-1) + point.y)));
		
		
		/*
		 * Once largest line found (finishing touches) -
		 * 1. Gets the previous position of the pixel (one pixel before the end of the line) and set it as the next previous.
		 * 2. Set the ending point of the valid pixel line - one pixel before then actual end (last position is invalid pixel).
		 * 3. Makes sure the calculation to get the previous pixel is not the same as the end pixel (and gets the true previous pixel).
		 */
		
		if(straight(largestAngle)){
			largestCounter /= 2;
		}
		
		prev.set(Math.round(targetRotationMatrix[0] * (largestCounter-2) + point.x), Math.round(targetRotationMatrix[2] * (largestCounter-2) + point.y));
		nextPoint.set(Math.round(targetRotationMatrix[0] * (largestCounter-1) + point.x), Math.round(targetRotationMatrix[2] * (largestCounter-1) + point.y));
		int i = 0;
		while(prev.x == nextPoint.x && prev.y == nextPoint.y) {
			prev.set(Math.round(targetRotationMatrix[0] * (largestCounter-2 - i) + point.x), Math.round(targetRotationMatrix[2] * (largestCounter-2 - i) + point.y));
			i++;
		}
		
		print(String.format("Point Found at: x = %f, y = %f, prev: x = %f, y = %f", nextPoint.x, nextPoint.y, prev.x, prev.y));
		
		return nextPoint;
	}
	
	private static boolean axisCollision(Vector2 next, float[] matrix) {
		if(axis.size() <= 2) {
			return false;
		}
		Vector2 last = axis.get(axis.size() - 1);
		
		lineCol1.setLine(matrix[0] * 1 + last.x, matrix[2] * 1 + last.y, next.x, next.y);
		
		for(int i = 2; i < axis.size(); i++) {
			Vector2 point1 = axis.get(i);
			Vector2 point2 = axis.get(i - 1);
			lineCol2.setLine(point2.x, point2.y, point1.x, point1.y);
			
			if(lineCol1.intersectsLine(lineCol2)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean straight(int angle) {
		return angle == 90 || angle == 180 || angle == 270 || angle == 360;
	}
	
	private static Vector2 startPoint() {
		int[] pixel = new int[4];
		int sides;
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(getPixel(x, y, pixel)[3] != 0) {
					sides = countSides(x, y);
					if(sides > 0) {
						return new Vector2(x, y);
						
					}
				}
			}
		}
		
		return null;
	}
	
	private static void autofill() {
		boolean start = false;
		int column = 0;
		
		for(int i = 0; i < pixels.length; i+=4) {
			if(start && getY(i) == column) {
				if(pixels[i + 3] == 0) {
					pixels[i] = filler[0];
					pixels[i + 1] = filler[1];
					pixels[i + 2] = filler[2];
					pixels[i + 3] = filler[3];
					
					if(!existsPixel(getX(i), getY(i) - 1) || getX(i) - 1 < 0 || getX(i) + 1 >= width || isFalling(getX(i), getY(i)) || getY(i) - 1 < 0){
						reverse(getX(i), getY(i));
						start = false;
					}
				}else {
					start = false;
				}
			}
			
			if(pixels[i + 3] > 0) {
				start = true;
				column = getY(i);
			}
		}
	}
	
	private static boolean isFalling(int x, int y) {
		int i = y + 1;
		
		while(i < height) {

			getPixel(x, i, p);

			if(p[3] > 0) {
				//System.out.println(i);
				return false;
			}
			
			i++;
		}
		//System.out.println("yes");
		return true;
	}
	
	private static void reverse(int x, int y) {
		int pos = (width * y + x) * 4;
		
		pixels[pos] = 0;
		pixels[pos + 1] = 0;
		pixels[pos + 2] = 0;
		pixels[pos + 3] = 0;
		
		if(isFiller(x, y - 1))
			reverse(x, y - 1);
		
		if(isFiller(x, y + 1))
			reverse(x, y + 1);
		
		if(isFiller(x - 1, y)) 
			reverse(x - 1, y);
		
		if(isFiller(x + 1, y))
			reverse(x + 1, y);
		
	}
	
	private static boolean isFiller(int x, int y) {

		boolean pixelExists = false;
		int[] pixel = new int[4];
		if(existsPixel(x, y)) {
			getPixel(x, y, pixel);
			pixelExists = true;
		}
		return pixelExists && pixel[0] == filler[0] && pixel[1] == filler[1] && pixel[2] == filler[2] && pixel[3] == filler[3];
	}
	
	private static int getX(int pos) {
		return (pos / 4) % width;
	}
	
	private static int getY(int pos) {
		return (pos / 4) / width;
	}
	
	private static boolean existsPixel(int x, int y) {
		int pos = (width * y + x) * 4;
		return x >= 0 && x < width && y >= 0 && y < height && getPixel(x, y, null)[3] > 0;
	}
	
	private static boolean hasSkippedOutline(Vector2 pos) {
		
		int yMin = (int)Math.round((pos.y - 1 >= 0 ? pos.y - 1 : pos.y));
		int yMax = (int)Math.round(pos.y + 1 < height ? pos.y + 1 : pos.y);
		
		int xMin = (int)Math.round((pos.x - 1 >= 0 ? pos.x - 1 : pos.x));
		int xMax = (int)Math.round(pos.x + 1 < height ? pos.x + 1 : pos.x);
		
		for(int y = yMin; yMax < y + 1; y++) {
			for(int x = xMin; x < xMax; x++) {
				
			}
		}
		
		return true;
	}
	
	private static int countSides(int x0, int y0) {
		int count = 0;
		int pixel[] = new int[4];
		
		
		if(getPixel(x0, y0, pixel)[3] == 0) {
			//print("empty");
			return 0;
		}
		
		for(int y1 = y0 - 1; y1 <= y0 + 1; y1++) {
			for(int x1 = x0 - 1; x1 <= x0 + 1; x1++) {
				//print("x1 = " + x1 + ", y1 = " + y1);
				if(y1 < 0 || y1 > height - 1 || x1 < 0 || x1 > width - 1) {
					count++;
				}else if(getPixel(x1, y1, pixel)[3] == 0){
					count++;
				}
			}
		}
		
		if(count == 1) {
			count = 0;
			for(int y1 = y0 - 1; y1 <= y0 + 1; y1++) {
				if(y1 < 0 || y1 > height - 1) {
					count++;
				}else if(getPixel(x0, y1, pixel)[3] == 0) {
					count++;
				}
			}
			
			for(int x1 = x0 - 1; x1 <= x0 + 1; x1++) {
				if(x1 < 0 || x1 >width - 1) {
					count++;
				}else if(getPixel(x1, y0, pixel)[3] == 0){
					count++;
				}
			}
		}
		
		return count;
	}
	
	
	private static void print(String args) {
			//System.out.println(args);
	}
	
	private static int[] getPixel(int x, int y, int[] pixel) {
		int pos = (width * y + x) * 4;

		
		if(pixel == null) {
			return new int[] {pixels[pos], pixels[pos + 1], pixels[pos + 2], pixels[pos + 3]};
		}else {
			pixel[0] = pixels[pos];
			pixel[1] = pixels[pos + 1];
			pixel[2] = pixels[pos+ 2];
			pixel[3] = pixels[pos + 3];
		}
		
		return pixel;
	}
}
