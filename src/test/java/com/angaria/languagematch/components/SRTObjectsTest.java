package com.angaria.languagematch.components;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.wrappers.SRTObjects;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SRTObjectsTest {

    private static final File FILE_SRT_1 = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_SRT_2 = new File("src/test/resources/fileTest2.vi.srt");

    private SRTObject srtRefObject;
    private SRTObject srtTargetObject;
    private SRTObjects srtObjects;

    @Before
    public void setup(){
        srtRefObject = new SRTObject(FILE_SRT_1.getName(), "en", Sets.newHashSet(), null);
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(), null);
        srtObjects = new SRTObjects(new SRTObject[]{srtRefObject, srtTargetObject});
    }

    @Test
    public void defaultConstructor_initializesCollection(){
        srtObjects = new SRTObjects();
        assertTrue(srtObjects.getSrtObjects().isEmpty());
    }

    @Test
    public void arrayConstructor_initializesCollection(){
        srtObjects = new SRTObjects(new SRTObject[]{});
        assertTrue(srtObjects.getSrtObjects().isEmpty());
    }

    @Test
    public void arrayConstructor_setsObjects(){
        srtObjects = new SRTObjects(new SRTObject[]{srtRefObject, srtTargetObject});
        assertEquals(2, srtObjects.getSrtObjects().size());
    }

    @Test
    public void constructorWithCollection_initializesCollection(){
        srtObjects = new SRTObjects(Sets.newHashSet());
        assertTrue(srtObjects.getSrtObjects().isEmpty());
    }

    @Test
    public void constructorWithCollection_setsObjects(){
        srtObjects = new SRTObjects(Sets.newHashSet(srtRefObject, srtTargetObject));
        assertEquals(2, srtObjects.getSrtObjects().size());
    }

    @Test
    public void getReferenceSRTObject(){
        assertEquals(srtRefObject, srtObjects.getReferenceSRTObject());
    }

    @Test
    public void getSecondarySRTObject(){
        assertEquals(srtTargetObject, srtObjects.getSecondarySRTObject());
    }

    @Test
    public void firstMethod(){
        assertEquals(srtObjects.getSrtObjects().iterator().next(), srtObjects.first());
    }

    @Test
    public void sizeMethod(){
        assertEquals(srtObjects.getSrtObjects().size(), srtObjects.size());
    }
}
