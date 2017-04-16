package com.angaria.languagematch.util;

import java.util.Set;
import java.util.TreeSet;

public class TestUtility<T>{

    public Set<T> newTreeSet(T... elements){
        Set<T> treeSet = new TreeSet<>();
        for( T t : elements){
            treeSet.add(t);
        }
        return treeSet;
    }

}
