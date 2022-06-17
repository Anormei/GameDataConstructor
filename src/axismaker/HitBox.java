package axismaker;

public class HitBox {

    public int count;

    Vector2[] vertices;
    Vector2[] axis;

    Vector2 p1 = new Vector2();
    Vector2 p2 = new Vector2();

    Vector2 pos;

    float width;
    float height;

    public HitBox() {

    }

    public HitBox(Vector2 pos, float width, float height, Vector2... vertices){
        this();
        this.vertices = vertices;
        axis = new Vector2[vertices.length];
    }

}
