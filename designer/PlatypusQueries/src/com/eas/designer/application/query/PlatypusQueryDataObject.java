/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.client.model.StoredQueryFactory;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.store.QueryDocument2XmlDom;
import com.eas.client.model.store.XmlDom2QueryDocument;
import com.eas.client.model.store.XmlDom2QueryModel;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.query.editing.DocumentTextCompiler;
import com.eas.designer.application.query.lexer.SqlErrorAnnotation;
import com.eas.designer.application.query.lexer.SqlLanguageHierarchy;
import com.eas.designer.application.query.nodes.QueryNodeChildren;
import com.eas.designer.application.query.nodes.QueryRootNode;
import com.eas.designer.application.query.nodes.QueryRootNodePropertiesUndoRecorder;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.script.JsDoc;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.undo.UndoableEdit;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.TablesFinder;
import net.sf.jsqlparser.TablesFinder.TO_CASE;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.Annotation;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;

public class PlatypusQueryDataObject extends PlatypusDataObject {

    // reflectioned properties
    public static final String PUBLIC_PROP_NAME = "public";
    public static final String PROCEDURE_PROP_NAME = "procedure";
    public static final String MANUAL_PROP_NAME = "manual";
    public static final String READONLY_PROP_NAME = "readonly";
    public static final String CONN_PROP_NAME = "dbId";
    //
    public static final String DATAOBJECT_DOC_PROPERTY = "dataObject";
    public static final String OUTPUT_FIELDS = "outputFields";
    public static final String STATEMENT_PROP_NAME = "statement";
    public static final String STATEMENT_ERROR_PROP_NAME = "statementError";
    protected transient Entry modelEntry;
    protected transient Entry outEntry;
    protected transient Entry dialectEntry;
    // Read / Write data
    protected String sqlText;
    protected String dialectText;
    protected String dbId;
    protected boolean publicQuery;
    protected boolean procedure;
    protected boolean manual;
    protected boolean readonly;
    protected QueryModel model;
    protected List<StoredFieldMetadata> outputFieldsHints;
    // Generated data
    protected transient ModelNode<QueryEntity, QueryModel> modelNode;
    protected transient Statement statement;
    protected transient Statement commitedStatement;
    protected transient ParseException statementError;
    protected transient Annotation statementAnnotation;
    protected transient Fields outputFields;
    protected transient NbEditorDocument sqlTextDocument;
    protected transient NbEditorDocument sqlFullTextDocument;

    public PlatypusQueryDataObject(FileObject aSqlFile, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(aSqlFile, loader);
        FileObject aDialectFile = FileUtil.findBrother(aSqlFile, PlatypusFiles.DIALECT_EXTENSION);
        FileObject aModelFile = FileUtil.findBrother(aSqlFile, PlatypusFiles.MODEL_EXTENSION);
        FileObject anOutFile = FileUtil.findBrother(aSqlFile, PlatypusFiles.OUT_EXTENSION);
        modelEntry = registerEntry(aModelFile);
        dialectEntry = registerEntry(aDialectFile);
        outEntry = registerEntry(anOutFile);
        CookieSet cookies = getCookieSet();
        cookies.add(new PlatypusQuerySupport(this));
        // dirty hack
        // NetBeans marks only primary file with appropriate script engine.
        // Though, parameters aren't inserted into secondary files in data objects.
        modelEntry.getFile().setAttribute(javax.script.ScriptEngine.class.getName(), "freemarker");
        // end of dirty hack    
    }

    @Override
    protected Node createNodeDelegate() {
        Node node = super.createNodeDelegate();
        if (node instanceof AbstractNode) {
            ((AbstractNode) node).setIconBaseWithExtension(PlatypusQueryDataObject.class.getPackage().getName().replace('.', '/') + "/query.png");
        }
        return node;
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    @Override
    protected void dispose() {
        getLookup().lookup(PlatypusQuerySupport.class).closeAllViews();
        super.dispose();
    }

    @Override
    protected void clientChanged() {
        if (model != null) {
            try {
                DbClient client = getClient();
                model.setClient(client);
                refreshOutputFields();
                if (client != null) {
                    PlatypusQuerySupport support = getLookup().lookup(PlatypusQuerySupport.class);
                    UndoableEdit edit = support.complementSqlTextEdit(null);
                    support.getModelUndo().addEdit(edit);
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    protected void readQuery() throws Exception {
        sqlText = getPrimaryFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        if (dialectEntry.getFile() != getPrimaryFile()) {
            dialectText = dialectEntry.getFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        }
        Document modelDoc = Source2XmlDom.transform(modelEntry.getFile().asText(PlatypusUtils.COMMON_ENCODING_NAME));
        model = XmlDom2QueryModel.transform(getClient(), modelDoc);
        dbId = model.getDbId();
        publicQuery = PlatypusFilesSupport.getAnnotationValue(sqlText, PlatypusFiles.PUBLIC_ANNOTATION_NAME) != null;
        procedure = PlatypusFilesSupport.getAnnotationValue(sqlText, PlatypusFiles.PROCEDURE_ANNOTATION_NAME) != null;
        manual = PlatypusFilesSupport.getAnnotationValue(sqlText, PlatypusFiles.MANUAL_ANNOTATION_NAME) != null;
        readonly = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.READONLY_TAG) != null;

        //TODO set output fields in query document
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            statement = parserManager.parse(new StringReader(sqlText));
            statementError = null;
            commitedStatement = statement;
        } catch (Exception ex) {
            statement = commitedStatement = null;
            if ((ex instanceof JSQLParserException) && ((JSQLParserException) ex).getCause() instanceof ParseException) {
                statementError = (ParseException) ex.getCause();
            }
        }
        UndoRedo.Manager undoReciever = getLookup().lookup(PlatypusQuerySupport.class).getModelUndo();
        //addParametersListener();
        EditorKit editorKit = CloneableEditorSupport.getEditorKit(SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);

        sqlTextDocument = (NbEditorDocument) editorKit.createDefaultDocument();
        sqlTextDocument.putProperty(NbEditorDocument.MIME_TYPE_PROP, SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);
        sqlTextDocument.putProperty(DATAOBJECT_DOC_PROPERTY, this);
        sqlTextDocument.insertString(0, sqlText, null);
        sqlTextDocument.addUndoableEditListener(undoReciever);

        sqlFullTextDocument = (NbEditorDocument) editorKit.createDefaultDocument();
        sqlFullTextDocument.putProperty(NbEditorDocument.MIME_TYPE_PROP, SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);
        sqlFullTextDocument.putProperty(DATAOBJECT_DOC_PROPERTY, this);
        sqlFullTextDocument.insertString(0, dialectText, null);
        sqlFullTextDocument.addUndoableEditListener(undoReciever);

        sqlTextDocument.addDocumentListener(new DocumentTextCompiler(this));

        String hintsContent = outEntry.getFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        if (hintsContent != null && !hintsContent.isEmpty()) {
            Document fieldsHintsDoc = Source2XmlDom.transform(hintsContent);
            if (fieldsHintsDoc != null) {
                outputFieldsHints = XmlDom2QueryDocument.parseFieldsHintsTag(fieldsHintsDoc.getDocumentElement());
            } else {
                outputFieldsHints = new ArrayList<>();
            }
        } else {
            outputFieldsHints = new ArrayList<>();
        }
        refreshOutputFields();
        modelNode = new QueryRootNode(new QueryNodeChildren(this, model, undoReciever, getLookup()), this);
        modelNode.addPropertyChangeListener(new QueryRootNodePropertiesUndoRecorder(undoReciever));
    }

    protected void checkQueryRead() throws Exception {
        if (model == null || sqlText == null) {
            readQuery();
        }
    }

    public QueryModel getModel() throws Exception {
        checkQueryRead();
        return model;
    }

    public UndoRedo.Manager getUndoRedoManager() {
        return getLookup().lookup(PlatypusQuerySupport.class).getModelUndo();
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String aValue) throws Exception {
        String oldValue = dbId;
        if (aValue != null && aValue.isEmpty()) {
            aValue = null;
        }
        dbId = aValue;
        if (model != null) {
            model.setDbId(aValue);
        }
        firePropertyChange(CONN_PROP_NAME, oldValue, aValue);
        if ((oldValue == null && aValue != null) || (oldValue != null && !oldValue.equals(aValue))) {
            refreshOutputFields();
        }
    }

    public boolean isPublic() {
        return publicQuery;
    }

    public void setPublic(boolean aValue) {
        boolean oldValue = publicQuery;
        publicQuery = aValue;
        if (oldValue != publicQuery) {
            firePropertyChange(PUBLIC_PROP_NAME, oldValue, aValue);
            try {
                String content = sqlTextDocument.getText(0, sqlTextDocument.getLength());
                String newContent = PlatypusFilesSupport.replaceAnnotationValue(content, PlatypusFiles.PUBLIC_ANNOTATION_NAME, publicQuery ? "" : null);
                sqlTextDocument.replace(0, sqlTextDocument.getLength(), newContent, null);
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    public boolean isProcedure() {
        return procedure;
    }

    public void setProcedure(boolean aValue) {
        boolean oldValue = procedure;
        procedure = aValue;
        if (oldValue != procedure) {
            procedureChanged(oldValue, aValue);
            try {
                String content = sqlTextDocument.getText(0, sqlTextDocument.getLength());
                String newContent = PlatypusFilesSupport.replaceAnnotationValue(content, PlatypusFiles.PROCEDURE_ANNOTATION_NAME, procedure ? "" : null);
                sqlTextDocument.replace(0, sqlTextDocument.getLength(), newContent, null);
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    public void procedureChanged(boolean aOldValue, boolean aNewValue) {
        firePropertyChange(PROCEDURE_PROP_NAME, aOldValue, aNewValue);
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean aValue) {
        boolean oldValue = manual;
        manual = aValue;
        if (oldValue != manual) {
            manualChanged(oldValue, aValue);
            try {
                String content = sqlTextDocument.getText(0, sqlTextDocument.getLength());
                String newContent = PlatypusFilesSupport.replaceAnnotationValue(content, PlatypusFiles.MANUAL_ANNOTATION_NAME, manual ? "" : null);
                sqlTextDocument.replace(0, sqlTextDocument.getLength(), newContent, null);
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    public void manualChanged(boolean aOldValue, boolean aNewValue) {
        firePropertyChange(MANUAL_PROP_NAME, aOldValue, aNewValue);
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean aValue) {
        boolean oldValue = readonly;
        readonly = aValue;
        if (oldValue != readonly) {
            firePropertyChange(READONLY_PROP_NAME, oldValue, aValue);
            try {
                String content = sqlTextDocument.getText(0, sqlTextDocument.getLength());
                String newContent = PlatypusFilesSupport.replaceAnnotationValue(content, JsDoc.Tag.READONLY_TAG, readonly ? "" : null);
                sqlTextDocument.replace(0, sqlTextDocument.getLength(), newContent, null);
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    public ModelNode<QueryEntity, QueryModel> getModelNode() throws Exception {
        checkQueryRead();
        return modelNode;
    }

    public Statement getStatement() throws Exception {
        checkQueryRead();
        return statement;
    }

    public void setStatement(Statement aValue) {
        Statement oldValue = statement;
        statement = aValue;
        firePropertyChange(STATEMENT_PROP_NAME, oldValue, statement);
    }

    public ParseException getStatementError() {
        return statementError;
    }

    public void setStatementError(ParseException aValue) throws BadLocationException {
        if (statementError != aValue) {
            ParseException oldValue = statementError;
            statementError = aValue;
            firePropertyChange(STATEMENT_ERROR_PROP_NAME, oldValue, statementError);
            setStatementErrorAnnotation(statementError);
        }
    }

    protected void setStatementErrorAnnotation(ParseException aValue) throws BadLocationException {
        if (statementAnnotation != null) {
            try {
                sqlTextDocument.removeAnnotation(statementAnnotation);
            } catch (IllegalStateException ex) {
                // no op
                ex = null;
            }
            statementAnnotation = null;
        }
        if (aValue != null && sqlTextDocument.getLength() != 0) {
            statementAnnotation = new SqlErrorAnnotation(aValue);
            int start = 0;
            int end = 0;
            if (aValue.currentToken != null) {
                start = NbDocument.findLineOffset(sqlTextDocument, aValue.currentToken.beginLine > 0 ? aValue.currentToken.beginLine - 1 : 0) + aValue.currentToken.beginColumn > 0 ? aValue.currentToken.beginColumn - 1 : 0;
                end = NbDocument.findLineOffset(sqlTextDocument, aValue.currentToken.endLine > 0 ? aValue.currentToken.endLine - 1 : 0) + aValue.currentToken.endColumn > 0 ? aValue.currentToken.endColumn : 0;
            }
            sqlTextDocument.addAnnotation(sqlTextDocument.createPosition(start), end - start, statementAnnotation);
        }
    }

    public Fields getOutputFields() throws Exception {
        checkQueryRead();
        return outputFields;
    }

    public List<StoredFieldMetadata> getOutputFieldsHints() throws Exception {
        checkQueryRead();
        return outputFieldsHints;
    }

    public javax.swing.text.Document getSqlTextDocument() throws Exception {
        checkQueryRead();
        return sqlTextDocument;
    }

    public javax.swing.text.Document getSqlFullTextDocument() throws Exception {
        checkQueryRead();
        return sqlFullTextDocument;
    }

    @Override
    protected void validateModel() throws Exception {
        if (getModel() != null) {
            getModel().validate();
        }
    }

    public void shrink() {
        modelNode = null;
        statement = null;
        commitedStatement = null;
        model = null;
        if (sqlTextDocument != null) {
            sqlTextDocument.putProperty(DATAOBJECT_DOC_PROPERTY, null);
        }
        sqlTextDocument = null;
        if (sqlFullTextDocument != null) {
            sqlFullTextDocument.putProperty(DATAOBJECT_DOC_PROPERTY, null);
        }
        sqlFullTextDocument = null;
        outputFields = null;
    }

    public void saveQuery() throws Exception {
        sqlText = sqlTextDocument.getText(0, sqlTextDocument.getLength());
        dialectText = sqlFullTextDocument.getText(0, sqlFullTextDocument.getLength());
        write2File(getPrimaryFile(), sqlText);
        FileObject dialectFileO = dialectEntry.getFile();
        if (dialectFileO == getPrimaryFile()) {
            String path = getPrimaryFile().getPath();
            File dialectFile = new File(path.substring(0, path.length() - getPrimaryFile().getExt().length()) + PlatypusFiles.DIALECT_EXTENSION);
            if (dialectFile.createNewFile()) {
                dialectFileO = FileUtil.toFileObject(dialectFile);
                dialectEntry = registerEntry(dialectFileO);
            }
        }
        if (getPrimaryFile() != dialectFileO) {
            write2File(dialectFileO, dialectText);
        }
        Document modelDocument = model.toXML();
        write2File(modelEntry.getFile(), XmlDom2String.transform(modelDocument));
        Document outHintsDocument = QueryDocument2XmlDom.transformOutHints(outputFieldsHints, outputFields);
        write2File(outEntry.getFile(), XmlDom2String.transform(outHintsDocument));
        if (getClient() != null) {
            getClient().appEntityChanged(IndexerQuery.file2AppElementId(getPrimaryFile()));
        }
    }

    private void write2File(FileObject aFile, String aContent) throws Exception {
        try (OutputStream out = aFile.getOutputStream()) {
            byte[] data = aContent.getBytes(PlatypusUtils.COMMON_ENCODING_NAME);
            out.write(data);
            out.flush();
        }
    }

    public Statement getCommitedStatement() {
        return commitedStatement;
    }

    public void commitStatement() throws Exception {
        validateStatement();
        commitedStatement = statement;
        sqlText = sqlTextDocument.getText(0, sqlTextDocument.getLength());
        boolean oldPublicQuery = publicQuery;
        publicQuery = PlatypusFilesSupport.getAnnotationValue(sqlText, PlatypusFiles.PUBLIC_ANNOTATION_NAME) != null;
        if (oldPublicQuery != publicQuery) {
            firePropertyChange(PUBLIC_PROP_NAME, oldPublicQuery, publicQuery);
        }
        boolean oldProcedure = procedure;
        procedure = PlatypusFilesSupport.getAnnotationValue(sqlText, PlatypusFiles.PROCEDURE_ANNOTATION_NAME) != null;
        if (oldProcedure != procedure) {
            firePropertyChange(PROCEDURE_PROP_NAME, oldProcedure, procedure);
        }
        boolean oldManual = manual;
        manual = PlatypusFilesSupport.getAnnotationValue(sqlText, PlatypusFiles.MANUAL_ANNOTATION_NAME) != null;
        if (oldManual != manual) {
            firePropertyChange(MANUAL_PROP_NAME, oldManual, manual);
        }
        boolean oldReadonly = readonly;
        readonly = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.READONLY_TAG) != null;
        if (oldReadonly != readonly) {
            firePropertyChange(READONLY_PROP_NAME, oldReadonly, readonly);
        }
        refreshOutputFields();
    }

    public Set<String> achieveSchemas() throws Exception {
        Set<String> schemas = new HashSet<>();
        DbClient client = getClient();
        if (client != null) {
            DbMetadataCache mdCache = client.getDbMetadataCache(dbId);
            SqlDriver driver = mdCache.getConnectionDriver();
            String sql4Schemas = driver.getSql4SchemasEnumeration();
            SqlCompiledQuery schemasQuery = new SqlCompiledQuery(client, dbId, sql4Schemas);
            Rowset schemasRowset = schemasQuery.executeQuery();
            int schemaColIndex = schemasRowset.getFields().find(ClientConstants.JDBCCOLS_TABLE_SCHEM);
            schemasRowset.beforeFirst();
            while (schemasRowset.next()) {
                String schemaName = schemasRowset.getString(schemaColIndex);
                schemas.add(schemaName);
            }
        }
        return schemas;
    }

    public Map<String, Fields> achieveTables(String aSchema) throws Exception {
        Map<String, Fields> tables = new HashMap<>();
        DbClient client = getClient();
        if (client != null) {
            DbMetadataCache mdCache = client.getDbMetadataCache(dbId);
            if (aSchema != null && aSchema.equalsIgnoreCase(mdCache.getConnectionSchema())) {
                aSchema = null;
            }
            if (aSchema != null) {
                mdCache.fillTablesCacheBySchema(aSchema, true);
            }
            SqlDriver driver = mdCache.getConnectionDriver();
            String sql4Tables = driver.getSql4TablesEnumeration(aSchema != null ? aSchema : mdCache.getConnectionSchema());
            SqlCompiledQuery tablesQuery = new SqlCompiledQuery(client, dbId, sql4Tables);
            Rowset tablesRowset = tablesQuery.executeQuery();
            //int schemaColIndex = tablesRowset.getFields().find(ClientConstants.JDBCCOLS_TABLE_SCHEM);
            int tableColIndex = tablesRowset.getFields().find(ClientConstants.JDBCCOLS_TABLE_NAME);
            tablesRowset.beforeFirst();
            while (tablesRowset.next()) {
                String cachedTableName = (aSchema != null ? aSchema + "." : "") + tablesRowset.getString(tableColIndex);
                Fields fields = mdCache.getTableMetadata(cachedTableName);
                tables.put(cachedTableName/*.toLowerCase()*/, fields);
            }
        }
        return tables;
    }

    private void validateStatement() throws Exception {
        DbClient client = getClient();
        if (client != null) {
            DbMetadataCache mdCache = client.getDbMetadataCache(dbId);
            Map<String, Table> tables = TablesFinder.getTablesMap(TO_CASE.LOWER, statement, true);
            for (Table table : tables.values()) {
                String schema = table.getSchemaName();
                if (schema != null && schema.equalsIgnoreCase(mdCache.getConnectionSchema())) {
                    schema = null;
                }
                String cachedTableName = (schema != null ? schema + "." : "") + table.getName();
                if (!mdCache.containsTableMetadata(cachedTableName) && !existsAppQuery(cachedTableName)) {
                    throw new AbsentTableParseException(NbBundle.getMessage(PlatypusQueryDataObject.class, "absentTable", cachedTableName));
                }
            }
        }
    }

    /**
     * Refreshs output fields from sql text document.
     */
    public void refreshOutputFields() {
        Fields oldValue = outputFields;
        DbClient client = getClient();
        String s = null;
        try {
            s = sqlTextDocument.getText(0, sqlTextDocument.getLength());
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().log(ex.getMessage());
        }
        if (statementError == null && client != null && s != null && !s.isEmpty()) {
            try {
                StoredQueryFactory factory = new StoredQueryFactory(client, true);
                SqlQuery outQuery = new SqlQuery(client, dbId, s);
                outQuery.setEntityId(String.valueOf(IDGenerator.genID()));
                factory.putTableFieldsMetadata(outQuery);
                outputFields = outQuery.getFields();
            } catch (Exception ex) {
                ErrorManager.getDefault().log(ex.getMessage());
                StatusDisplayer.getDefault().setStatusText(ex.getMessage()); // NOI18N                    
                outputFields = null;
            }
        } else {
            outputFields = null;
        }
        if ((oldValue == null && outputFields != null) || (oldValue != null && !oldValue.equals(outputFields))) {
            outputFieldsChanged(oldValue, outputFields);
        }
    }

    public void outputFieldsChanged(Fields aOldValue, Fields aNewValue) {
        firePropertyChange(OUTPUT_FIELDS, aOldValue, aNewValue);
    }

    public boolean existsAppQuery(String aTablyName) {
        PlatypusProject project = getProject();
        if (project != null) {
            try {
                ApplicationElement appElement = project.getAppCache().get(aTablyName);
                if (appElement == null && StoredQueryFactory.SUBQUERY_LINK_PATTERN.matcher(aTablyName.toLowerCase()).matches()) {
                    appElement = project.getAppCache().get(aTablyName.substring(1));
                }
                return appElement != null && appElement.getType() == ClientConstants.ET_QUERY;
            } catch (Exception ex) {
                return false;
            }
        } else {
            return false;
        }
    }
}
