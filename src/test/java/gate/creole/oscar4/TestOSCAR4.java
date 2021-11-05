package gate.creole.oscar4;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import gate.Document;
import gate.Factory;
import gate.LanguageAnalyser;
import gate.test.GATEPluginTests;
import gate.util.GateException;


/**
 * Using this class automatically prepares GATE and the plugin for testing.
 * 
 * This class automatically initializes GATE and loads the plugin. 
 * Any method in this class with the "@Test" annotation will then get
 * run with the plugin already properly loaded.
 * 
 */
public class TestOSCAR4 extends GATEPluginTests {

  @Test
  public void testOSCAR4() throws GateException {
    LanguageAnalyser pr = (LanguageAnalyser)Factory.createResource("gate.creole.oscar4.OSCAR4");
    try {
      Document doc = Factory.newDocument("The quick brown ethyl acetate jumps over the lazy bromine.");

      pr.setDocument(doc);
      pr.execute();

      assertEquals(2, doc.getAnnotations().size());

    } finally {
      Factory.deleteResource(pr);
    }
  }
}
