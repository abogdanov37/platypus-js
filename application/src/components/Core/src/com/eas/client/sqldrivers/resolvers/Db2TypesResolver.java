/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import java.sql.Types;
import java.util.*;

/**
 *
 * @author kl
 */
public class Db2TypesResolver extends TypesResolver {
    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<String> gisTypes = new HashSet<>();
    protected static final Set<Integer> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<Integer> jdbcTypesWithScale = new HashSet<>();

    protected static final Map<Integer, String> jdbcTypesLeftPartName = new HashMap<>();
    protected static final Map<Integer, String> jdbcTypesRightPartName = new HashMap<>();
    private static final Map<Integer, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<Integer, Integer> jdbcTypesDefaultSize = new HashMap<>();
    private static final List<Integer> characterTypesOrder = new ArrayList<>();
    private static final List<Integer> binaryTypesOrder = new ArrayList<>();

    static {
        // rdbms -> jdbc
        rdbmsTypes2JdbcTypes.put("SMALLINT", Types.SMALLINT);
        rdbmsTypes2JdbcTypes.put("INTEGER", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("INT", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("BIGINT", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("DECIMAL", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("DEC", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("NUMERIC", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("NUM", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("FLOAT", Types.REAL);
        rdbmsTypes2JdbcTypes.put("REAL", Types.REAL);
        rdbmsTypes2JdbcTypes.put("DOUBLE", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("DOUBLE PRECISION", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("DECFLOAT", Types.OTHER);//????? float !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        rdbmsTypes2JdbcTypes.put("CHAR", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("CHARACTER", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("VARCHAR", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("CHAR VARYING", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("CHARACTER VARYING", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("CHAR () FOR BIT DATA", Types.BINARY);
        rdbmsTypes2JdbcTypes.put("CHARACTER () FOR BIT DATA", Types.BINARY);
        rdbmsTypes2JdbcTypes.put("CHAR VARYING () FOR BIT DATA", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("VARCHAR () FOR BIT DATA", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("CHARACTER VARYING () FOR BIT DATA", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("LONG VARCHAR", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("LONG VARCHAR FOR BIT DATA", Types.LONGVARBINARY);
        rdbmsTypes2JdbcTypes.put("CLOB", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("CHAR LARGE OBJECT", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("CHARACTER LARGE OBJECT", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("GRAPHIC", Types.CHAR);//???varchar
        rdbmsTypes2JdbcTypes.put("VARGRAPHIC", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("LONG VARGRAPHIC", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("DBCLOB", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("NCHAR", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("NATIONAL CHAR", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("NATIONAL CHARACTER", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("NVARCHAR", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("NCHAR VARYING", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("NATIONAL CHAR VARYING", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("NATIONAL CHARACTER VARYING", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("NCLOB", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("NCHAR LARGE OBJECT", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("NATIONAL CHARACTER LARGE OBJECT", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("BLOB", Types.BLOB);
        rdbmsTypes2JdbcTypes.put("BINARY LARGE OBJECT", Types.BLOB);
        rdbmsTypes2JdbcTypes.put("DATE", Types.DATE);
        rdbmsTypes2JdbcTypes.put("TIME", Types.TIME);
        rdbmsTypes2JdbcTypes.put("TIMESTAMP", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("XML", Types.BLOB); //?? OTHER  || SQLXML || BLOB
        //??rdbmsTypes2JdbcTypes.put("DATALINK", Types.VARCHAR);



        // jdbc -> rdbms
        jdbcTypes2RdbmsTypes.put(Types.BIT, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.TINYINT, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.SMALLINT, "SMALLINT");
        jdbcTypes2RdbmsTypes.put(Types.INTEGER, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.BIGINT, "BIGINT");
        jdbcTypes2RdbmsTypes.put(Types.FLOAT, "REAL");
        jdbcTypes2RdbmsTypes.put(Types.REAL, "REAL");
        jdbcTypes2RdbmsTypes.put(Types.DOUBLE, "DOUBLE");
        jdbcTypes2RdbmsTypes.put(Types.NUMERIC, "DECIMAL");
        jdbcTypes2RdbmsTypes.put(Types.DECIMAL, "DECIMAL");
        jdbcTypes2RdbmsTypes.put(Types.CHAR, "CHAR");
        jdbcTypes2RdbmsTypes.put(Types.VARCHAR, "VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "LONG VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.DATE, "DATE");
        jdbcTypes2RdbmsTypes.put(Types.TIME, "TIME");
        jdbcTypes2RdbmsTypes.put(Types.TIMESTAMP, "TIMESTAMP");
        jdbcTypes2RdbmsTypes.put(Types.BINARY, "CHAR () FOR BIT DATA");
        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "VARCHAR () FOR BIT DATA");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "LONG VARCHAR FOR BIT DATA");
        jdbcTypes2RdbmsTypes.put(Types.BLOB, "BLOB");
        jdbcTypes2RdbmsTypes.put(Types.CLOB, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.BOOLEAN, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.NCHAR, "CHAR");
        jdbcTypes2RdbmsTypes.put(Types.NVARCHAR, "VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.LONGNVARCHAR, "LONG VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "CLOB");


        //typeName(M,D)
        jdbcTypesWithScale.add(Types.DECIMAL);// (M,D)
        //--jdbcTypesWithScale.add(Types.REAL);   // (D)
        //--jdbcTypesWithScale.add(Types.DOUBLE); // (D)


        //typeName(M)
        jdbcTypesWithSize.add(Types.DECIMAL); //(M,D)
        jdbcTypesWithSize.add(Types.CHAR);    // (M)
        jdbcTypesWithSize.add(Types.VARCHAR); // (M)

        jdbcTypesWithSize.add(Types.BINARY); // (M)
        jdbcTypesWithSize.add(Types.VARBINARY); // (M)
        jdbcTypesWithSize.add(Types.CLOB); // (M)
        jdbcTypesWithSize.add(Types.BLOB); // (M)
        
        
        // max sizes for types
        jdbcTypesMaxSize.put(Types.CHAR,254);
        jdbcTypesMaxSize.put(Types.VARCHAR,4000);
        jdbcTypesMaxSize.put(Types.BINARY,254);
        jdbcTypesMaxSize.put(Types.VARBINARY,2000);
//?? зависит от установленного размера страницы       
//??        jdbcTypesMaxSize.put(Types.VARBINARY,32762);
        
        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put(Types.CHAR,1);
        jdbcTypesDefaultSize.put(Types.VARCHAR,200);
        jdbcTypesDefaultSize.put(Types.NCHAR,1 );
        jdbcTypesDefaultSize.put(Types.NVARCHAR,200);
        jdbcTypesDefaultSize.put(Types.BINARY,1);
        jdbcTypesDefaultSize.put(Types.VARBINARY,200);
        jdbcTypesDefaultSize.put(Types.CLOB, 2147483647);
        jdbcTypesDefaultSize.put(Types.BLOB, 2147483647);

        // порядок замены символьных типов, если требуется размер больше исходного
        characterTypesOrder.add(Types.CHAR);
        characterTypesOrder.add(Types.VARCHAR);
        characterTypesOrder.add(Types.CLOB);
        
        binaryTypesOrder.add(Types.BINARY);
        binaryTypesOrder.add(Types.VARBINARY);
        binaryTypesOrder.add(Types.BLOB);
    
        // для полей, где размер задается в середине имени типа
        jdbcTypesLeftPartName.put(Types.BINARY, "CHAR");
        jdbcTypesLeftPartName.put(Types.VARBINARY, "VARCHAR");
        jdbcTypesRightPartName.put(Types.BINARY, "FOR BIT DATA");
        jdbcTypesRightPartName.put(Types.VARBINARY, "FOR BIT DATA");
    }

    @Override
    public void resolve2Application(Field aField) {
        if (isGeometryTypeName(aField.getTypeInfo().getSqlTypeName())) {
            aField.setTypeInfo(DataTypeInfo.GEOMETRY.copy());
        }else
            super.resolve2Application(aField);
    }

    @Override
    public boolean isGeometryTypeName(String aTypeName) {
        return false;
    }

    @Override
    public int getJdbcTypeByRDBMSTypename(String aTypeName) {
        String sqlTypeName = (aTypeName != null ? aTypeName.toUpperCase() : null);
        // убрать (size) из имени типа
        int leftIndex = sqlTypeName.indexOf("(");
        if (leftIndex > 0) {
            int rightIndex = sqlTypeName.indexOf(")");
            if (rightIndex > 0) {
                sqlTypeName = sqlTypeName.substring(0, leftIndex) + "() " + sqlTypeName.substring(rightIndex + 1);
            }
        }
        StringTokenizer st = new StringTokenizer(sqlTypeName, " ", false);
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String part = st.nextToken();
            if (part != null && !part.isEmpty()) {
                sb.append(part).append(" ");
            }
        }
        sqlTypeName = sb.toString().trim();

        Integer jdbcType = rdbmsTypes2JdbcTypes.get(sqlTypeName);
        if (jdbcType == null) {
            jdbcType = Types.OTHER;
            if (isGeometryTypeName(sqlTypeName)) {
                jdbcType = Types.STRUCT;
            }
        }
        return jdbcType;
    }

    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        Set<Integer> supportedTypes = new HashSet<>();
        supportedTypes.addAll(rdbmsTypes2JdbcTypes.values());
        return supportedTypes;
    }

    @Override
    public boolean isSized(Integer aSqlType) {
        return jdbcTypesWithSize.contains(aSqlType);
    }

    @Override
    public boolean isScaled(Integer aSqlType) {
        return jdbcTypesWithScale.contains(aSqlType);
    }

    public int getDefaultSize(Integer aSqlType) {
        Integer ret = jdbcTypesDefaultSize.get(aSqlType);
        return (ret != null ? ret : -1);
    }

    public String getLeftPartNameType(Integer aSqlType) {
        String partName = jdbcTypesLeftPartName.get(aSqlType);
        if (partName == null) {
            partName = jdbcTypes2RdbmsTypes.get(aSqlType);
        }
        return partName;
    }

    public String getRightPartNameType(Integer aSqlType) {
        return jdbcTypesRightPartName.get(aSqlType);
    }        

    @Override
    public Map<Integer, String> getJdbcTypes2RdbmsTypes() {
        return jdbcTypes2RdbmsTypes;
    }

    @Override
    public Map<Integer, Integer> getJdbcTypesMaxSize() {
        return jdbcTypesMaxSize;
    }

    @Override
    public Map<Integer, Integer> getJdbcTypesDefaultSize() {
        return jdbcTypesDefaultSize;
    }

    @Override
    public List<Integer> getCharacterTypesOrder() {
        return characterTypesOrder;
    }

    @Override
    public  List<Integer> getBinaryTypesOrder() {
        return binaryTypesOrder;
    }
    
}
