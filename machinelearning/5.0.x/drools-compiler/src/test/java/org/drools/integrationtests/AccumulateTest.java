package org.drools.integrationtests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.FactHandle;
import org.drools.OuterClass;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.RuntimeDroolsException;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

public class AccumulateTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        return loadRuleBase( reader,
                             new PackageBuilderConfiguration() );
    }

    private RuleBase loadRuleBase(final Reader reader,
                                  final PackageBuilderConfiguration conf) throws IOException,
                                                                         DroolsParserException,
                                                                         Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            System.out.println( parser.getErrors() );
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder( conf );
        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        // test rulebase serialization
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        return ruleBase;
    }

    public void testAccumulateModify() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateModify.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 24,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 31,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void testAccumulate() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Accumulate.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Person( "Bob",
                               "stilton",
                               20 ) );
        wm.insert( new Person( "Mark",
                               "provolone" ) );
        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );

        wm.fireAllRules();

        Assert.assertEquals( new Integer( 165 ),
                             results.get( 0 ) );
        Assert.assertEquals( new Integer( 10 ),
                             results.get( 1 ) );
        Assert.assertEquals( new Integer( 150 ),
                             results.get( 2 ) );
        Assert.assertEquals( new Integer( 10 ),
                             results.get( 3 ) );
        Assert.assertEquals( new Integer( 210 ),
                             results.get( 4 ) );
    }

    public void testMVELAccumulate() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateMVEL.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Person( "Bob",
                               "stilton",
                               20 ) );
        wm.insert( new Person( "Mark",
                               "provolone" ) );
        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );

        wm.fireAllRules();

        Assert.assertEquals( new Integer( 165 ),
                             results.get( 0 ) );
        Assert.assertEquals( new Integer( 10 ),
                             results.get( 1 ) );
        Assert.assertEquals( new Integer( 150 ),
                             results.get( 2 ) );
        Assert.assertEquals( new Integer( 10 ),
                             results.get( 3 ) );
        Assert.assertEquals( new Integer( 210 ),
                             results.get( 4 ) );
    }

    public void testAccumulateModifyMVEL() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateModifyMVEL.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 24,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 31,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void testAccumulateReverseModify() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateReverseModify.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 24,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        cheese[3].setPrice( 20 );
        wm.update( cheeseHandles[3],
                   cheese[3] );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 36,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void testAccumulateReverseModify2() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateReverseModify2.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 24,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        cheese[3].setPrice( 20 );
        wm.update( cheeseHandles[3],
                   cheese[3] );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 36,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void testAccumulateReverseModifyMVEL() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateReverseModifyMVEL.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 24,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 31,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void testAccumulateReverseModifyMVEL2() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateReverseModifyMVEL2.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 24,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 31,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void testAccumulateWithFromChaining() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateWithFromChaining.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         8 ), new Cheese( "stilton",
                                                                          10 ), new Cheese( "stilton",
                                                                                            9 ), new Cheese( "brie",
                                                                                                             4 ), new Cheese( "brie",
                                                                                                                              1 ), new Cheese( "provolone",
                                                                                                                                               8 )};

        Cheesery cheesery = new Cheesery();

        for ( int i = 0; i < cheese.length; i++ ) {
            cheesery.addCheese( cheese[i] );
        }

        FactHandle cheeseryHandle = wm.insert( cheesery );

        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // one fire, as per rule constraints
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 3,
                             ((List) results.get( results.size() - 1 )).size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setType( "brie" );
        wm.update( cheeseryHandle,
                   cheesery );
        wm.fireAllRules();

        // no fire
        Assert.assertEquals( 1,
                             results.size() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 3,
                             ((List) results.get( results.size() - 1 )).size() );

        // ---------------- 4th scenario
        cheesery.getCheeses().remove( cheese[3] );
        wm.update( cheeseryHandle,
                   cheesery );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void testMVELAccumulate2WM() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateMVEL.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm1 = ruleBase.newStatefulSession();
        final List results1 = new ArrayList();

        wm1.setGlobal( "results",
                       results1 );

        final WorkingMemory wm2 = ruleBase.newStatefulSession();
        final List results2 = new ArrayList();

        wm2.setGlobal( "results",
                       results2 );

        wm1.insert( new Person( "Bob",
                                "stilton",
                                20 ) );
        wm1.insert( new Person( "Mark",
                                "provolone" ) );

        wm2.insert( new Person( "Bob",
                                "stilton",
                                20 ) );
        wm2.insert( new Person( "Mark",
                                "provolone" ) );

        wm1.insert( new Cheese( "stilton",
                                10 ) );
        wm1.insert( new Cheese( "brie",
                                5 ) );
        wm2.insert( new Cheese( "stilton",
                                10 ) );
        wm1.insert( new Cheese( "provolone",
                                150 ) );
        wm2.insert( new Cheese( "brie",
                                5 ) );
        wm2.insert( new Cheese( "provolone",
                                150 ) );
        wm1.fireAllRules();

        wm2.fireAllRules();

        Assert.assertEquals( new Integer( 165 ),
                             results1.get( 0 ) );
        Assert.assertEquals( new Integer( 10 ),
                             results1.get( 1 ) );
        Assert.assertEquals( new Integer( 150 ),
                             results1.get( 2 ) );
        Assert.assertEquals( new Integer( 10 ),
                             results1.get( 3 ) );
        Assert.assertEquals( new Integer( 210 ),
                             results1.get( 4 ) );

        Assert.assertEquals( new Integer( 165 ),
                             results2.get( 0 ) );
        Assert.assertEquals( new Integer( 10 ),
                             results2.get( 1 ) );
        Assert.assertEquals( new Integer( 150 ),
                             results2.get( 2 ) );
        Assert.assertEquals( new Integer( 10 ),
                             results2.get( 3 ) );
        Assert.assertEquals( new Integer( 210 ),
                             results2.get( 4 ) );
    }

    public void testAccumulateInnerClass() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateInnerClass.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new OuterClass.InnerClass( 10 ) );
        wm.insert( new OuterClass.InnerClass( 5 ) );

        wm.fireAllRules();

        Assert.assertEquals( new Integer( 15 ),
                             results.get( 0 ) );
    }

    public void testAccumulateReturningNull() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateReturningNull.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        try {
            wm.insert( new Cheese( "stilton",
                                   10 ) );

            fail( "Should have raised an exception because accumulate is returning null" );
        } catch ( RuntimeDroolsException rde ) {
            // success, working fine
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should have raised a DroolsRuntimeException instead of " + e );
        }

    }

    public void testAccumulateReturningNullMVEL() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateReturningNullMVEL.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        try {
            wm.insert( new Cheese( "stilton",
                                   10 ) );

            fail( "Should have raised an exception because accumulate is returning null" );
        } catch ( RuntimeDroolsException rde ) {
            // success, working fine
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should have raised a DroolsRuntimeException instead of " + e );
        }

    }

    public void testAccumulateSumJava() throws Exception {
        execTestAccumulateSum( "test_AccumulateSum.drl" );
    }

    public void testAccumulateSumMVEL() throws Exception {
        execTestAccumulateSum( "test_AccumulateSumMVEL.drl" );
    }

    public void testAccumulateMultiPatternWithFunctionJava() throws Exception {
        execTestAccumulateSum( "test_AccumulateMultiPatternFunctionJava.drl" );
    }

    public void testAccumulateMultiPatternWithFunctionMVEL() throws Exception {
        execTestAccumulateSum( "test_AccumulateMultiPatternFunctionMVEL.drl" );
    }

    public void testAccumulateCountJava() throws Exception {
        execTestAccumulateCount( "test_AccumulateCount.drl" );
    }

    public void testAccumulateCountMVEL() throws Exception {
        execTestAccumulateCount( "test_AccumulateCountMVEL.drl" );
    }

    public void testAccumulateAverageJava() throws Exception {
        execTestAccumulateAverage( "test_AccumulateAverage.drl" );
    }

    public void testAccumulateAverageMVEL() throws Exception {
        execTestAccumulateAverage( "test_AccumulateAverageMVEL.drl" );
    }

    public void testAccumulateMinJava() throws Exception {
        execTestAccumulateMin( "test_AccumulateMin.drl" );
    }

    public void testAccumulateMinMVEL() throws Exception {
        execTestAccumulateMin( "test_AccumulateMinMVEL.drl" );
    }

    public void testAccumulateMaxJava() throws Exception {
        execTestAccumulateMax( "test_AccumulateMax.drl" );
    }

    public void testAccumulateMaxMVEL() throws Exception {
        execTestAccumulateMax( "test_AccumulateMaxMVEL.drl" );
    }

    public void testAccumulateMultiPatternJava() throws Exception {
        execTestAccumulateReverseModifyMultiPattern( "test_AccumulateMultiPattern.drl" );
    }

    public void testAccumulateMultiPatternMVEL() throws Exception {
        execTestAccumulateReverseModifyMultiPattern( "test_AccumulateMultiPatternMVEL.drl" );
    }

    public void execTestAccumulateSum(String fileName) throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( fileName ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        StatefulSession session = ruleBase.newStatefulSession();
        DataSet data = new DataSet();
        data.results = new ArrayList<Object>();

        session.setGlobal( "results",
                           data.results );

        data.cheese = new Cheese[]{new Cheese( "stilton",
                                               8,
                                               0 ), new Cheese( "stilton",
                                                                10,
                                                                1 ), new Cheese( "stilton",
                                                                                 9,
                                                                                 2 ), new Cheese( "brie",
                                                                                                  11,
                                                                                                  3 ), new Cheese( "brie",
                                                                                                                   4,
                                                                                                                   4 ), new Cheese( "provolone",
                                                                                                                                    8,
                                                                                                                                    5 )};
        data.bob = new Person( "Bob",
                               "stilton" );

        data.cheeseHandles = new FactHandle[data.cheese.length];
        for ( int i = 0; i < data.cheese.length; i++ ) {
            data.cheeseHandles[i] = session.insert( data.cheese[i] );
        }
        data.bobHandle = session.insert( data.bob );

        // ---------------- 1st scenario
        session.fireAllRules();
        Assert.assertEquals( 1,
                             data.results.size() );
        Assert.assertEquals( 27,
                             ((Number) data.results.get( data.results.size() - 1 )).intValue() );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        updateReferences( session,
                          data );

        // ---------------- 2nd scenario
        final int index = 1;
        data.cheese[index].setPrice( 3 );
        session.update( data.cheeseHandles[index],
                        data.cheese[index] );
        session.fireAllRules();

        Assert.assertEquals( 2,
                             data.results.size() );
        Assert.assertEquals( 20,
                             ((Number) data.results.get( data.results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        data.bob.setLikes( "brie" );
        session.update( data.bobHandle,
                        data.bob );
        session.fireAllRules();

        Assert.assertEquals( 3,
                             data.results.size() );
        Assert.assertEquals( 15,
                             ((Number) data.results.get( data.results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        session.retract( data.cheeseHandles[3] );
        session.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 3,
                             data.results.size() );

    }

    private void updateReferences(final StatefulSession session,
                                  final DataSet data ) {
        data.results = (List<?>) session.getGlobal( "results" );
        for ( Iterator< ? > it = session.iterateObjects(); it.hasNext(); ) {
            Object next = (Object) it.next();
            if ( next instanceof Cheese ) {
                Cheese c = (Cheese) next;
                data.cheese[c.getOldPrice()] = c;
                data.cheeseHandles[c.getOldPrice()] = session.getFactHandle( c );
                assertNotNull( data.cheeseHandles[c.getOldPrice()] );
            } else if( next instanceof Person ) {
                Person p = (Person) next;
                data.bob = p;
                data.bobHandle = session.getFactHandle( data.bob );
            }
        }
    }

    public void execTestAccumulateCount(String fileName) throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( fileName ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         8 ), new Cheese( "stilton",
                                                                          10 ), new Cheese( "stilton",
                                                                                            9 ), new Cheese( "brie",
                                                                                                             4 ), new Cheese( "brie",
                                                                                                                              1 ), new Cheese( "provolone",
                                                                                                                                               8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 3,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 3 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 3,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 3,
                             results.size() );
        Assert.assertEquals( 2,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 3,
                             results.size() );

    }

    public void execTestAccumulateAverage(String fileName) throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( fileName ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            11 ), new Cheese( "brie",
                                                                                                              15 ), new Cheese( "brie",
                                                                                                                                17 ), new Cheese( "provolone",
                                                                                                                                                  8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 10,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 16,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.retract( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void execTestAccumulateMin(String fileName) throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( fileName ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         8 ), new Cheese( "stilton",
                                                                          10 ), new Cheese( "stilton",
                                                                                            9 ), new Cheese( "brie",
                                                                                                             4 ), new Cheese( "brie",
                                                                                                                              1 ), new Cheese( "provolone",
                                                                                                                                               8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 3 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 3,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 1,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.retract( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void execTestAccumulateMax(String fileName) throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( fileName ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         4 ), new Cheese( "stilton",
                                                                          2 ), new Cheese( "stilton",
                                                                                           3 ), new Cheese( "brie",
                                                                                                            15 ), new Cheese( "brie",
                                                                                                                              17 ), new Cheese( "provolone",
                                                                                                                                                8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 9,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 17,
                             ((Number) results.get( results.size() - 1 )).intValue() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.retract( cheeseHandles[4] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void execTestAccumulateReverseModifyMultiPattern(String fileName) throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( fileName ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );
        final Person mark = new Person( "Mark",
                                        "provolone" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );
        final FactHandle markHandle = wm.insert( mark );

        // ---------------- 1st scenario
        wm.fireAllRules();
        // no fire, as per rule constraints
        Assert.assertEquals( 0,
                             results.size() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        // 1 fire
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 32,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        // 2 fires
        Assert.assertEquals( 2,
                             results.size() );
        Assert.assertEquals( 39,
                             ((Cheesery) results.get( results.size() - 1 )).getTotalAmount() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( 2,
                             results.size() );

    }

    public void testAccumulateWithPreviouslyBoundVariables() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulatePreviousBinds.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );
        wm.insert( new Cheese( "brie",
                               20 ) );

        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( new Integer( 45 ),
                      results.get( 0 ) );
    }

    public void testAccumulateGlobals() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_AccumulateGlobals.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );
        wm.setGlobal( "globalValue",
                      new Integer( 50 ) );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );
        wm.insert( new Cheese( "brie",
                               20 ) );

        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( new Integer( 100 ),
                      results.get( 0 ) );
    }

    public static class DataSet {
        public Cheese[]     cheese;
        public FactHandle[] cheeseHandles;
        public Person       bob;
        public FactHandle   bobHandle;
        public List< ? >    results;
    }
}
