package dataconstructor;

import java.util.HashMap;
import java.util.InputMismatchException;

public class FieldData {

    public static final String NO_NAME = "<NO NAME>";
    public static final String NO_TYPE = "<NO TYPE>";
    private static final HashMap<Integer, ValueIdentifier> valueIdentifierHashMap = new HashMap<>();

    static{
        valueIdentifierHashMap.put(DataStructure.NO_TYPE, new ValueIdentifier(NO_TYPE){
            @Override
            public boolean typeMatch(Object object){
                return false;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_BYTE, new ValueIdentifier("Byte"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof Byte;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_SHORT, new ValueIdentifier("Short"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof Short;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_INT, new ValueIdentifier("Integer"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof Integer;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_LONG, new ValueIdentifier("Long"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof Long;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_FLOAT, new ValueIdentifier("Float"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof Float;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_DOUBLE, new ValueIdentifier("Double"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof Double;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_BOOLEAN, new ValueIdentifier("Boolean"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof Boolean;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_CHAR, new ValueIdentifier("Character"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof Character;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_STRING, new ValueIdentifier("String"){
            @Override
            public boolean typeMatch(Object object){
                return object instanceof String;
            }
        });

        valueIdentifierHashMap.put(DataStructure.TYPE_OBJECT, new ValueIdentifier("Object"){
            @Override
            public boolean typeMatch(Object object){
                return true;
            }
        });
    }


    private String name;
    private int type;
    private Object value;

    public FieldData(){
        name = "";
        type = -1;
    }

    public FieldData(String name){
        this.name = name;
    }

    public FieldData(String name, int type){
        this(name);
        this.type = type;
    }

    public FieldData(String name, int type, Object value){
        this(name, type);
    if(!valueIdentifierHashMap.get(type).typeMatch(value) && value != null){
            throw new InputMismatchException("Wrong value type");
        }
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name.equals(NO_NAME)){
            this.name = "";
        }
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getTypeToString(){
        return valueIdentifierHashMap.get(type).toString();
    }

    public void setType(int type) {
        this.type = type;
        value = null;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if(!valueIdentifierHashMap.get(type).typeMatch(value) && value != null){
            throw new InputMismatchException("Wrong value type");
        }
        this.value = value;
    }

    @Override
    public String toString(){

        if(name.equals("")){
            return NO_NAME;
        }

        return name;
    }
}
