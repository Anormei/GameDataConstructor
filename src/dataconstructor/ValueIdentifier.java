package dataconstructor;

public abstract class ValueIdentifier {

    private String name;

    public ValueIdentifier(String name){
        this.name = name;
    }

    public abstract boolean typeMatch(Object object);

    @Override
    public String toString(){
        return name;
    }
}
