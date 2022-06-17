package axismaker;


public class Vector2 {
    public static final float
            TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
    public static final float TO_DEGREES = (1 / (float) Math.PI) * 180.0f;
    public float x, y;

    public Vector2(){

    }

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 v){
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2 copy(){
        return new Vector2(x, y);
    }

    public Vector2 set(float x, float y){
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 set(Vector2 v){
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    public Vector2 add(float x, float y){
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 add(Vector2 v){
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public Vector2 sub(float x, float y){
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 sub(Vector2 v){
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public Vector2 mul(float scalar){
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Vector2 div(float scalar){
        if(scalar != 0){
            this.x /= scalar;
            this.y /= scalar;
        }
        return this;
    }

    public float length(){
        return (float)Math.sqrt(x * x + y * y);
    }

    public Vector2 nor(){
        float len = length();
        if(len != 0){
            this.x /= len;
            this.y /= len;
        }
        return this;
    }

    public float dot(Vector2 v){
        return x * v.x + y * v.y;
    }

    public float angle(){
        float angle = (float) Math.atan2(y, x) * TO_DEGREES;
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    public Vector2 rotate(float angle){
        float rad = angle * TO_RADIANS;
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        return this;
    }

    public float dist(Vector2 v){
        float distX = this.x - v.x;
        float distY = this.y - v.y;
        return (float) Math.sqrt(distX * distX + distY * distY);
    }

    public float dist(float x, float y){
        float distX = this.x - x;
        float distY = this.y - y;
        return (float) Math.sqrt(distX * distX + distY * distY);
    }
    
    @Override
    public boolean equals(Object o) {
    	Vector2 v = (Vector2) o;
    	return Float.compare(this.x, v.x) == 0 && Float.compare(this.y, v.y) == 0;
    }

}
