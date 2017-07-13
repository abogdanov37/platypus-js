package com.eas.client.changes;

/**
 *
 * @author mg
 */
public interface ChangeVisitor {

    public void visit(Insert aChange) throws Exception;

    public void visit(Update aChange) throws Exception;

    public void visit(Delete aChange) throws Exception;

    public void visit(Command aChange) throws Exception;
    
    public void visit(CommandRequest aChange) throws Exception;
}
