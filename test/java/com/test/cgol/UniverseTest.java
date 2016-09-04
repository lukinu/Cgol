package com.test.cgol;

import com.test.cgol.utils.Clock;

import org.junit.Test;

import static org.junit.Assert.*;

public class UniverseTest {

    @Test
    public void testGetInstance() throws Exception {
        Universe universe = Universe.getInstance();
        assertEquals(true, (universe instanceof Universe));
    }

    @Test
    public void testInit() throws Exception {
        int cellNum = 10;
        Universe universe = Universe.getInstance();
        universe.init(cellNum);
        assertEquals(cellNum * cellNum, universe.getAllCells().size());
        for (Object obj : universe.getAllCells()) {
            assertEquals(true, obj instanceof Cell);
        }
    }

    @Test
    public void testGetCellByXY() throws Exception {
        int cellNum = 2;
        Universe universe = Universe.getInstance();
        universe.init(cellNum);
        Cell cell = universe.getCellByXY(1, 1);
        assertEquals(1, cell.getX());
        assertEquals(1, cell.getY());
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void testEvolve() throws Exception {
        Universe universe = Universe.getInstance();
        for (Cell cell : universe.getAllCells()) {
            assertEquals(false, cell.isAlive());
            cell.setNextState(Cell.STATE_ALIVE);
        }
        universe.evolve();
        for (Cell cell : universe.getAllCells()) {
            assertEquals(true, cell.isAlive());
        }
    }

    @Test
    public void testGetClock() throws Exception {
        Universe universe = Universe.getInstance();
        assertEquals(true, universe.getClock() instanceof Clock);
    }

    @Test
    public void testClear() throws Exception {
        Universe universe = Universe.getInstance();
        universe.clear();
        assertEquals(true, universe.getAllCells().size() == 0);
    }
}