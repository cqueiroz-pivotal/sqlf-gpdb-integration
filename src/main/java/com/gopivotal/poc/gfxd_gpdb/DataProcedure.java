package com.gopivotal.poc.gfxd_gpdb;

import com.pivotal.gemfirexd.procedure.ProcedureProcessorContext;
import com.pivotal.gemfirexd.procedure.ProcedureResultProcessor;

import java.util.List;

/**
 * Created by cq on 27/3/14.
 */
public class DataProcedure implements ProcedureResultProcessor {
    @Override
    public void init(ProcedureProcessorContext procedureProcessorContext) {

    }

    @Override
    public Object[] getOutParameters() throws InterruptedException {
        return new Object[0];
    }

    @Override
    public List<Object> getNextResultRow(int i) throws InterruptedException {
        return null;
    }

    @Override
    public void close() {

    }
}
