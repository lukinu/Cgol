package com.test.cgol;

import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {

    @Test
    public void testEvolve() throws Exception {
        Cell cell = new Cell(0, 0);
        cell.setNextState(Cell.STATE_ALIVE);
        cell.evolve();
        assertEquals(true, cell.isAlive());
    }
}