package org.drools.jpdl.xml;

import org.drools.xml.DefaultSemanticModule;
import org.drools.xml.SemanticModule;
import org.drools.xml.processes.ActionNodeHandler;
import org.drools.xml.processes.CompositeNodeHandler;
import org.drools.xml.processes.ConnectionHandler;
import org.drools.xml.processes.ConstraintHandler;
import org.drools.xml.processes.EndNodeHandler;
import org.drools.xml.processes.GlobalHandler;
import org.drools.xml.processes.ImportHandler;
import org.drools.xml.processes.InPortHandler;
import org.drools.xml.processes.JoinNodeHandler;
import org.drools.xml.processes.MappingHandler;
import org.drools.xml.processes.MilestoneNodeHandler;
import org.drools.xml.processes.OutPortHandler;
import org.drools.xml.processes.ParameterHandler;
import org.drools.xml.processes.ProcessHandler;
import org.drools.xml.processes.RuleSetNodeHandler;
import org.drools.xml.processes.SplitNodeHandler;
import org.drools.xml.processes.SubProcessNodeHandler;
import org.drools.xml.processes.TimerNodeHandler;
import org.drools.xml.processes.TypeHandler;
import org.drools.xml.processes.ValueHandler;
import org.drools.xml.processes.VariableHandler;
import org.drools.xml.processes.WorkHandler;
import org.drools.xml.processes.WorkItemNodeHandler;

public class JpdlProcessSemanticModule extends DefaultSemanticModule implements SemanticModule {    
    public JpdlProcessSemanticModule() {
        super ( "http://drools.org/drools-4.0/process" );

        addHandler( "process",
                           new ProcessHandler() );
        addHandler( "start",
                           new StartStateHandler() );
        addHandler( "end",
                           new EndNodeHandler() );
        addHandler( "action",
                           new ActionNodeHandler() );
        addHandler( "ruleSet",
                           new RuleSetNodeHandler() );
        addHandler( "subProcess",
                           new SubProcessNodeHandler() );
        addHandler( "workItem",
                           new WorkItemNodeHandler() );
        addHandler( "split",
                           new SplitNodeHandler() );
        addHandler( "join",
                           new JoinNodeHandler() );
        addHandler( "milestone",
                           new MilestoneNodeHandler() );
        addHandler( "timer",
                           new TimerNodeHandler() );
        addHandler( "composite",
                           new CompositeNodeHandler() );
        addHandler( "connection",
                           new ConnectionHandler() );
        addHandler( "import",
                           new ImportHandler() );
        addHandler( "global",
                           new GlobalHandler() );        
        addHandler( "variable",
                           new VariableHandler() );        
        addHandler( "type",
                           new TypeHandler() );        
        addHandler( "value",
                           new ValueHandler() );        
        addHandler( "work",
                           new WorkHandler() );        
        addHandler( "parameter",
                           new ParameterHandler() );        
        addHandler( "mapping",
                           new MappingHandler() );        
        addHandler( "constraint",
                           new ConstraintHandler() );        
        addHandler( "in-port",
                           new InPortHandler() );        
        addHandler( "out-port",
                           new OutPortHandler() );        
    }
}
