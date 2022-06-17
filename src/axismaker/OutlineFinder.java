package axismaker;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

public class OutlineFinder {
	
	
	private static class CursorInfo{
		
		private int pos;
		private int start;
		private int end;
		private int inc;
		
		void setPos(int pos, int maxLength) {
			this.pos = pos - inc;
			start = pos - inc;
			end = pos + inc;
			
			if(start < 0 || start >= maxLength) {
				start += inc;
			}
			
			if(end < 0 || end >= maxLength) {
				end -= inc;
			}
		}
		
		void increment() {
			pos += inc;
		}

		boolean equal() {
			return pos == end;
		}
		
		CursorInfo realign() {
			pos = start;
			return this;
		}
		
		void reverse() {
			inc = -inc;
		}
	}
	
	private static int[] pixels;
	
	//private static boolean[] area = new boolean[9];
	private static CursorInfo cursorX = new CursorInfo();
	private static CursorInfo cursorY = new CursorInfo();
	private static boolean invertDirection = true;
	
	private static int width = 0;
	private static int height = 0;
	
	private OutlineFinder(String path) {
		
	}
	
	public static Vector2[][] traceImage(String path) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(new File(path));
		}catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
		width = image.getWidth();
		height = image.getHeight();
		pixels = image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), (int[])null);
		return null; //remove
	}
	
	private static List<Vector2> getPoints() {

		List<Vector2> points = new ArrayList<>();
		Vector2[][] coords = new Vector2[3][3];
		Vector2 curr = findAPoint();
		Vector2 prev = new Vector2(curr);

		
		if(curr == null) {
			return null;
		}
		
		points.add(curr);
		
		cursorX.inc = 1;
		cursorY.inc = -1;
		invertDirection = true;
		
		/*for(Iterator<Vector2> iterator = points.iterator(); iterator.hasNext();) {
			Vector2 point = iterator.next();
			scanPixel((int)point.x, (int)point.y, coords);
			for(int i = 0; i < coords.size(); i++) {
				Vector2 coord = coords.get(i);
				if(countSides((int)coord.x, (int)coord.y) > 0 && !(coord.x == prev.x && coord.y == prev.y)) {
					
					
				}
			}
		}*/
		
		while(points.size() <= 1 && !points.get(0).equals(points.get(points.size() - 1))) {
			prev = curr;
			
			curr = nextPixel((int)prev.x, (int)prev.y);
		}
	
		return null; //remove
	}
		
	
	private static Vector2 findAPoint() {
		int[] pixel = new int[4];
		int sides;
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				getPixel(x, y, pixel);
				if(pixel[3] != 0) {
					sides = countSides(x, y);
					if(sides == 1 || sides >= 4) {
						return new Vector2(x, y);
						
					}
				}
			}
		}
		
		return null;
	}
	
	private static Vector2 nextPixel(int x0, int y0) {
		int[] pixel = new int[4];
		cursorX.setPos(x0, width);
		cursorY.setPos(y0, height);
		
		for(CursorInfo cursor1 = invertDirection ? cursorX : cursorY; !cursor1.equal(); cursor1.increment()) {
			for(CursorInfo cursor2 = (invertDirection ? cursorY : cursorX).realign(); !cursor2.equal(); cursor2.increment()) {
				if((cursorX.pos != x0 && cursorY.pos != y0) && countSides(cursorX.pos, cursorY.pos) > 1) {
					
					Vector2 newPos = new Vector2(cursorX.pos, cursorY.pos);
					
					if(cursorX.pos != x0) {
						for(cursorY.realign();!cursorY.equal(); cursorY.increment()) {
							getPixel(cursorY.pos, (int)newPos.x, pixel);
							
							if(pixel[3] == 0) {
								cursorX.inc = (int)newPos.x - x0;
								cursorY.inc = y0 - (int)newPos.y;
								invertDirection = true;
								
								return new Vector2(cursorX.pos, cursorY.pos);
							}
							
						}

					}
					
					if(cursorY.pos != y0) {
						for(cursorX.realign(); !cursorX.equal(); cursorX.increment()) {
							getPixel(cursorX.pos, (int)newPos.y, pixel);
							
							if(pixel[3] == 0) {
								cursorY.inc = (int)newPos.y - y0;
								cursorX.inc = x0 - (int)newPos.x;
								invertDirection = false;
								
								return new Vector2(cursorX.pos, cursorY.pos);
							}
						
						}
					}
						

				}
			}
		}
		
		return null;
	}
	
	private static int[] getPixel(int x, int y, int[] pixel) {
		int pos = x * y;
		pixel[0] = pixels[pos];
		pixel[1] = pixels[pos + 1];
		pixel[2] = pixels[pos+ 2];
		pixel[3] = pixels[pos + 3];
		return new int[0];
	}
	
	/*private static void scanPixel(int x0, int y0, Vector2[][] coords) {
		int[] pixel = new int[4];
		
		for(int y1 = y0 - 1; y1 < y0 + 1; y1++) {
			for(int x1 = x0 - 1; x1 < x0 + 1; x1++) {
				if((y1 >= 0 && y1 < pixels.getHeight() && x1 >= 0 && x1 < pixels.getWidth()) && (x1 != x0 && y1 != y0)) {
					pixels.getPixel(x1, y1, pixel);
					if(pixel[3] > 0) {
						coords[y1 - y0][x1 - x0] = new Vector2(x1, y1);
					}else {
						coords[y1 - y0][x1 - x0] = null;
					}
				}
			}
		}		
	}*/
	
	private static int countSides(int x0, int y0) {
		int count = 0;
		int[] pixel = new int[4];
		getPixel(x0, y0, pixel);
		
		if(pixel[3] == 0) {
			return 0;
		}
		
		for(int y1 = y0 - 1; y1 < y0 + 1; y1++) {
			for(int x1 = x0 - 1; x1 < x0 + 1; x1++) {
				if(y1 < 0 || y1 > height || x1 < 0 || x1 >= width) {
					count++;
				}else if(getPixel(x1, y1, (int[]) null)[3] == 0){
					count++;
				}
			}
		}
		
		return count;
	}
	
}
