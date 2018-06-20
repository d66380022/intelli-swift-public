package com.fr.swift.bitmap;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.impl.BitSetImmutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.bitmap.impl.IdBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.structure.array.HeapIntArray;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author anchore
 */
public final class BitMaps {
    public static final ImmutableBitMap EMPTY_IMMUTABLE = BitSetImmutableBitMap.newInstance();

    public static MutableBitMap newRoaringMutable() {
        return RoaringMutableBitMap.newInstance();
    }

    public static MutableBitMap newBitSetMutable() {
        return BitSetMutableBitMap.newInstance();
    }

    public static ImmutableBitMap newAllShowBitMap(int rowCount) {
        return AllShowBitMap.newInstance(rowCount);
    }

    public static ImmutableBitMap newRangeBitmap(int start, int end) {
        return new RangeBitmap(start, end);
    }

    public static ImmutableBitMap newIdBitMap(int id) {
        return IdBitMap.newInstance(id);
    }

    public static ImmutableBitMap newImmutableBitMap(IntList intList) {
        final MutableBitMap bitmap = RoaringMutableBitMap.newInstance();
        new IntListRowTraversal(intList).traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                bitmap.add(row);
            }
        });
        return bitmap;
    }

    public static IntArray traversal2Array(RowTraversal traversal) {
        final IntArray array = new HeapIntArray(traversal.getCardinality());
        traversal.traversal(new TraversalAction() {
            private int index = 0;

            @Override
            public void actionPerformed(int row) {
                array.put(index++, row);
            }
        });
        return array;
    }
}
