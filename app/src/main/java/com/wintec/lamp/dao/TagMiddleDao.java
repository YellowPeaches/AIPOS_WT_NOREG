package com.wintec.lamp.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wintec.lamp.dao.entity.TagMiddle;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TAG_MIDDLE".
*/
public class TagMiddleDao extends AbstractDao<TagMiddle, Long> {

    public static final String TABLENAME = "TAG_MIDDLE";

    /**
     * Properties of entity TagMiddle.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Id = new Property(1, Integer.class, "id", false, "ID");
        public final static Property Templateid = new Property(2, Integer.class, "templateid", false, "TEMPLATEID");
        public final static Property Abscissa = new Property(3, Integer.class, "abscissa", false, "ABSCISSA");
        public final static Property Ordinate = new Property(4, Integer.class, "ordinate", false, "ORDINATE");
        public final static Property FontSize = new Property(5, Integer.class, "fontSize", false, "FONT_SIZE");
        public final static Property FontFormat = new Property(6, String.class, "fontFormat", false, "FONT_FORMAT");
        public final static Property Overstriking = new Property(7, Integer.class, "overstriking", false, "OVERSTRIKING");
        public final static Property Underline = new Property(8, Integer.class, "underline", false, "UNDERLINE");
        public final static Property Italic = new Property(9, Integer.class, "italic", false, "ITALIC");
        public final static Property TagName = new Property(10, String.class, "tagName", false, "TAG_NAME");
        public final static Property Length = new Property(11, Integer.class, "length", false, "LENGTH");
        public final static Property Breadth = new Property(12, Integer.class, "breadth", false, "BREADTH");
        public final static Property CodeSystem = new Property(13, Integer.class, "codeSystem", false, "CODE_SYSTEM");
        public final static Property ComponentType = new Property(14, Integer.class, "componentType", false, "COMPONENT_TYPE");
        public final static Property DivId = new Property(15, String.class, "divId", false, "DIV_ID");
        public final static Property TemplateId = new Property(16, Integer.class, "templateId", false, "TEMPLATE_ID");
        public final static Property TenantId = new Property(17, Integer.class, "tenantId", false, "TENANT_ID");
        public final static Property BranchId = new Property(18, Integer.class, "branchId", false, "BRANCH_ID");
        public final static Property Name = new Property(19, String.class, "name", false, "NAME");
        public final static Property Lengths = new Property(20, Integer.class, "lengths", false, "LENGTHS");
        public final static Property Breadths = new Property(21, Integer.class, "breadths", false, "BREADTHS");
        public final static Property Unit = new Property(22, String.class, "unit", false, "UNIT");
        public final static Property DateFormat = new Property(23, String.class, "dateFormat", false, "DATE_FORMAT");
        public final static Property Units = new Property(24, String.class, "units", false, "UNITS");
        public final static Property Bz1 = new Property(25, String.class, "bz1", false, "BZ1");
        public final static Property Bz2 = new Property(26, String.class, "bz2", false, "BZ2");
        public final static Property LabelNo = new Property(27, Integer.class, "labelNo", false, "LABEL_NO");
    }


    public TagMiddleDao(DaoConfig config) {
        super(config);
    }
    
    public TagMiddleDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TAG_MIDDLE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"ID\" INTEGER," + // 1: id
                "\"TEMPLATEID\" INTEGER," + // 2: templateid
                "\"ABSCISSA\" INTEGER," + // 3: abscissa
                "\"ORDINATE\" INTEGER," + // 4: ordinate
                "\"FONT_SIZE\" INTEGER," + // 5: fontSize
                "\"FONT_FORMAT\" TEXT," + // 6: fontFormat
                "\"OVERSTRIKING\" INTEGER," + // 7: overstriking
                "\"UNDERLINE\" INTEGER," + // 8: underline
                "\"ITALIC\" INTEGER," + // 9: italic
                "\"TAG_NAME\" TEXT," + // 10: tagName
                "\"LENGTH\" INTEGER," + // 11: length
                "\"BREADTH\" INTEGER," + // 12: breadth
                "\"CODE_SYSTEM\" INTEGER," + // 13: codeSystem
                "\"COMPONENT_TYPE\" INTEGER," + // 14: componentType
                "\"DIV_ID\" TEXT," + // 15: divId
                "\"TEMPLATE_ID\" INTEGER," + // 16: templateId
                "\"TENANT_ID\" INTEGER," + // 17: tenantId
                "\"BRANCH_ID\" INTEGER," + // 18: branchId
                "\"NAME\" TEXT," + // 19: name
                "\"LENGTHS\" INTEGER," + // 20: lengths
                "\"BREADTHS\" INTEGER," + // 21: breadths
                "\"UNIT\" TEXT," + // 22: unit
                "\"DATE_FORMAT\" TEXT," + // 23: dateFormat
                "\"UNITS\" TEXT," + // 24: units
                "\"BZ1\" TEXT," + // 25: bz1
                "\"BZ2\" TEXT," + // 26: bz2
                "\"LABEL_NO\" INTEGER);"); // 27: labelNo
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TAG_MIDDLE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TagMiddle entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        Integer id = entity.getId();
        if (id != null) {
            stmt.bindLong(2, id);
        }
 
        Integer templateid = entity.getTemplateid();
        if (templateid != null) {
            stmt.bindLong(3, templateid);
        }
 
        Integer abscissa = entity.getAbscissa();
        if (abscissa != null) {
            stmt.bindLong(4, abscissa);
        }
 
        Integer ordinate = entity.getOrdinate();
        if (ordinate != null) {
            stmt.bindLong(5, ordinate);
        }
 
        Integer fontSize = entity.getFontSize();
        if (fontSize != null) {
            stmt.bindLong(6, fontSize);
        }
 
        String fontFormat = entity.getFontFormat();
        if (fontFormat != null) {
            stmt.bindString(7, fontFormat);
        }
 
        Integer overstriking = entity.getOverstriking();
        if (overstriking != null) {
            stmt.bindLong(8, overstriking);
        }
 
        Integer underline = entity.getUnderline();
        if (underline != null) {
            stmt.bindLong(9, underline);
        }
 
        Integer italic = entity.getItalic();
        if (italic != null) {
            stmt.bindLong(10, italic);
        }
 
        String tagName = entity.getTagName();
        if (tagName != null) {
            stmt.bindString(11, tagName);
        }
 
        Integer length = entity.getLength();
        if (length != null) {
            stmt.bindLong(12, length);
        }
 
        Integer breadth = entity.getBreadth();
        if (breadth != null) {
            stmt.bindLong(13, breadth);
        }
 
        Integer codeSystem = entity.getCodeSystem();
        if (codeSystem != null) {
            stmt.bindLong(14, codeSystem);
        }
 
        Integer componentType = entity.getComponentType();
        if (componentType != null) {
            stmt.bindLong(15, componentType);
        }
 
        String divId = entity.getDivId();
        if (divId != null) {
            stmt.bindString(16, divId);
        }
 
        Integer templateId = entity.getTemplateId();
        if (templateId != null) {
            stmt.bindLong(17, templateId);
        }
 
        Integer tenantId = entity.getTenantId();
        if (tenantId != null) {
            stmt.bindLong(18, tenantId);
        }
 
        Integer branchId = entity.getBranchId();
        if (branchId != null) {
            stmt.bindLong(19, branchId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(20, name);
        }
 
        Integer lengths = entity.getLengths();
        if (lengths != null) {
            stmt.bindLong(21, lengths);
        }
 
        Integer breadths = entity.getBreadths();
        if (breadths != null) {
            stmt.bindLong(22, breadths);
        }
 
        String unit = entity.getUnit();
        if (unit != null) {
            stmt.bindString(23, unit);
        }
 
        String dateFormat = entity.getDateFormat();
        if (dateFormat != null) {
            stmt.bindString(24, dateFormat);
        }
 
        String units = entity.getUnits();
        if (units != null) {
            stmt.bindString(25, units);
        }
 
        String bz1 = entity.getBz1();
        if (bz1 != null) {
            stmt.bindString(26, bz1);
        }
 
        String bz2 = entity.getBz2();
        if (bz2 != null) {
            stmt.bindString(27, bz2);
        }
 
        Integer labelNo = entity.getLabelNo();
        if (labelNo != null) {
            stmt.bindLong(28, labelNo);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TagMiddle entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        Integer id = entity.getId();
        if (id != null) {
            stmt.bindLong(2, id);
        }
 
        Integer templateid = entity.getTemplateid();
        if (templateid != null) {
            stmt.bindLong(3, templateid);
        }
 
        Integer abscissa = entity.getAbscissa();
        if (abscissa != null) {
            stmt.bindLong(4, abscissa);
        }
 
        Integer ordinate = entity.getOrdinate();
        if (ordinate != null) {
            stmt.bindLong(5, ordinate);
        }
 
        Integer fontSize = entity.getFontSize();
        if (fontSize != null) {
            stmt.bindLong(6, fontSize);
        }
 
        String fontFormat = entity.getFontFormat();
        if (fontFormat != null) {
            stmt.bindString(7, fontFormat);
        }
 
        Integer overstriking = entity.getOverstriking();
        if (overstriking != null) {
            stmt.bindLong(8, overstriking);
        }
 
        Integer underline = entity.getUnderline();
        if (underline != null) {
            stmt.bindLong(9, underline);
        }
 
        Integer italic = entity.getItalic();
        if (italic != null) {
            stmt.bindLong(10, italic);
        }
 
        String tagName = entity.getTagName();
        if (tagName != null) {
            stmt.bindString(11, tagName);
        }
 
        Integer length = entity.getLength();
        if (length != null) {
            stmt.bindLong(12, length);
        }
 
        Integer breadth = entity.getBreadth();
        if (breadth != null) {
            stmt.bindLong(13, breadth);
        }
 
        Integer codeSystem = entity.getCodeSystem();
        if (codeSystem != null) {
            stmt.bindLong(14, codeSystem);
        }
 
        Integer componentType = entity.getComponentType();
        if (componentType != null) {
            stmt.bindLong(15, componentType);
        }
 
        String divId = entity.getDivId();
        if (divId != null) {
            stmt.bindString(16, divId);
        }
 
        Integer templateId = entity.getTemplateId();
        if (templateId != null) {
            stmt.bindLong(17, templateId);
        }
 
        Integer tenantId = entity.getTenantId();
        if (tenantId != null) {
            stmt.bindLong(18, tenantId);
        }
 
        Integer branchId = entity.getBranchId();
        if (branchId != null) {
            stmt.bindLong(19, branchId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(20, name);
        }
 
        Integer lengths = entity.getLengths();
        if (lengths != null) {
            stmt.bindLong(21, lengths);
        }
 
        Integer breadths = entity.getBreadths();
        if (breadths != null) {
            stmt.bindLong(22, breadths);
        }
 
        String unit = entity.getUnit();
        if (unit != null) {
            stmt.bindString(23, unit);
        }
 
        String dateFormat = entity.getDateFormat();
        if (dateFormat != null) {
            stmt.bindString(24, dateFormat);
        }
 
        String units = entity.getUnits();
        if (units != null) {
            stmt.bindString(25, units);
        }
 
        String bz1 = entity.getBz1();
        if (bz1 != null) {
            stmt.bindString(26, bz1);
        }
 
        String bz2 = entity.getBz2();
        if (bz2 != null) {
            stmt.bindString(27, bz2);
        }
 
        Integer labelNo = entity.getLabelNo();
        if (labelNo != null) {
            stmt.bindLong(28, labelNo);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TagMiddle readEntity(Cursor cursor, int offset) {
        TagMiddle entity = new TagMiddle( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // id
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // templateid
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // abscissa
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // ordinate
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // fontSize
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // fontFormat
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // overstriking
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // underline
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // italic
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // tagName
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // length
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12), // breadth
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // codeSystem
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // componentType
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // divId
            cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16), // templateId
            cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17), // tenantId
            cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18), // branchId
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // name
            cursor.isNull(offset + 20) ? null : cursor.getInt(offset + 20), // lengths
            cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21), // breadths
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // unit
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // dateFormat
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // units
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // bz1
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // bz2
            cursor.isNull(offset + 27) ? null : cursor.getInt(offset + 27) // labelNo
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TagMiddle entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setTemplateid(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setAbscissa(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setOrdinate(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setFontSize(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setFontFormat(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setOverstriking(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setUnderline(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setItalic(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setTagName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setLength(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setBreadth(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
        entity.setCodeSystem(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setComponentType(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setDivId(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setTemplateId(cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16));
        entity.setTenantId(cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17));
        entity.setBranchId(cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18));
        entity.setName(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setLengths(cursor.isNull(offset + 20) ? null : cursor.getInt(offset + 20));
        entity.setBreadths(cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21));
        entity.setUnit(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setDateFormat(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setUnits(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setBz1(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setBz2(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setLabelNo(cursor.isNull(offset + 27) ? null : cursor.getInt(offset + 27));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TagMiddle entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TagMiddle entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TagMiddle entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
