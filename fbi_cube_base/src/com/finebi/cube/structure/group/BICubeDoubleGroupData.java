package com.finebi.cube.structure.group;

import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleGroupData extends BICubeGroupData<Double> {

    public BICubeDoubleGroupData(ICubeResourceLocation superLocation) {
        super(superLocation);
    }

    @Override
    protected ICubeResourceLocation setGroupType() {
        return currentLocation.setDoubleTypeWrapper();
    }

    @Override
    protected Comparator<Double> defaultComparator() {
        return ComparatorFacotry.DOUBLE_ASC;
    }
}
