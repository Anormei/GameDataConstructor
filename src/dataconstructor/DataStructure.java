package dataconstructor;

import java.io.Serializable;

public class DataStructure implements Serializable {

    private static final long serialVersionUID = 2L;

    public static final int NO_TYPE = -1;
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_SHORT = 1;
    public static final int TYPE_INT = 2; //*
    public static final int TYPE_LONG = 3;
    public static final int TYPE_FLOAT = 4; //*
    public static final int TYPE_DOUBLE = 5;
    public static final int TYPE_BOOLEAN = 6; //*
    public static final int TYPE_CHAR = 7; //*
    public static final int TYPE_STRING = 8; //*
    public static final int TYPE_OBJECT = 9; //*

    private int numFields;
    public String className;
    public int[] types;
    public String[] names;
    public Object[] values;

    public DataStructure(int numFields){
        this.numFields = numFields;
        values = new Object[numFields];
        types = new int[numFields];
        names = new String[numFields];
    }

    public DataStructure(String[] names, int[] types, Object[] values){
        this.numFields = values.length;
        this.names = names;
        this.types = types;
        this.values = values;
    }

    public int size(){
        return numFields;
    }

    public void setData(int index, String name, int type, Object value){
        types[index] = type;
        names[index] = name;
        values[index] = value;
    }
}
