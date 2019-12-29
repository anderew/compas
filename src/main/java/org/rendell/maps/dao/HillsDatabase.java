package org.rendell.maps.dao;

import java.io.File;

/**
 * Super crude database - to be replaced by a true GIS, but in the mean time...
 */
public class HillsDatabase {

    private final File localCopyOfHillsCsv;

    public HillsDatabase(String localCopyOfHillsCsvName) {
        this.localCopyOfHillsCsv = new File(localCopyOfHillsCsvName);
    }

    public void initialise() {



    }

}
