package ui.classtemplate;

public class TypeInfo {

    private String name;
    private int type;
    private String objectFile;

    public TypeInfo(){
        name = "";
        type = -1;
    }

    public TypeInfo(String name, int type){
        this.name = name;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
