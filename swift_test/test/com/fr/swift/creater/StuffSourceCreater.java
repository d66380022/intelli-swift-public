package com.fr.swift.creater;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;

/**
 * This class created on 2017-11-29.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class StuffSourceCreater {


    public static DataSource createTableA() {
        return new TestDBDataSource("tableA");
    }

    public static DataSource createTableB() {
        return new TestDBDataSource("tableB");
    }

    public static DataSource createTableC() {
        return new TestDBDataSource("tableC");
    }

    public static DataSource createTableD() {
        return new TestDBDataSource("tableD");
    }

    public static DataSource createTableE() {
        return new TestDBDataSource("tableE");
    }

    public static RelationSource createRelationAB() {
        return new TestRelationSource("relationAB");
    }

    public static RelationSource createRelationBC() {
        return new TestRelationSource("relationBC");
    }

    public static RelationSource createRelationCD() {
        return new TestRelationSource("relationCD");
    }

    public static RelationSource createRelationDE() {
        return new TestRelationSource("relationDE");
    }


    public static SourcePath createPathABC() {
        return new TestPathSource("pathABC");
    }

    public static SourcePath createPathBCD() {
        return new TestPathSource("pathBCD");
    }

    public static SourcePath createPathCDE() {
        return new TestPathSource("pathCDE");
    }

    public static SourcePath createPathABCD() {
        return new TestPathSource("pathABCD");
    }

    public static SourcePath createPathBCDE() {
        return new TestPathSource("pathBCDE");

    }

    public static SourcePath createPathABCDE() {
        return new TestPathSource("pathABCDE");

    }
}
