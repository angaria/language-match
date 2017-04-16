package com.angaria.languagematch.util;

import java.util.Set;
import java.util.TreeSet;

public class TestUtility<T>{

    public Set<T> newTreeSet(T[] t){
        Set<T> treeSet = new TreeSet<>();
        for( T local : t){
            treeSet.add(local);
        }
        return treeSet;
    }

}
