package gate.creole.oscar4;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import uk.ac.cam.ch.wwmm.oscar.Oscar;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ChemicalStructure;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.FormatType;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ResolvedNamedEntity;

@CreoleResource(name = "OSCAR4", comment = "An OSCAR4 based chemistry tagger")
public class OSCAR4 extends AbstractLanguageAnalyser {

	private static final long serialVersionUID = 8629219719149842154L;

	private static final Logger log = LoggerFactory.getLogger(OSCAR4.class);

	private Oscar oscar;

	/**
	 * Annotation set into which this PR will create new annotations.
	 */
	private String annotationSetName;

	@Optional
	@RunTime
	@CreoleParameter(comment = "The annotation set used for output annotations")
	public void setAnnotationSetName(String annotationSetName) {
		this.annotationSetName = annotationSetName;
	}

	public String getAnnotationSetName() {
		return annotationSetName;
	}

	/**
	 * Initialize this OSCAR4 based Tagger.
	 *
	 * @return this resource.
	 * @throws ResourceInstantiationException if an error occurs during init.
	 */
	public Resource init() throws ResourceInstantiationException {
		log.debug("OSCAR4 based Tagger is initializing");

		oscar = new Oscar();

		// This forces the dictionaries to be loaded, rather than
		// waiting for the first time we parse something real
		oscar.findAndResolveNamedEntities("The quick brown ethyl acetate jumps over the lazy bromine.");

		return this;
	}

	/**
	 * Execute this OSCAR4 based Tagger over the current document.
	 *
	 * @throws ExecutionException if an error occurs during processing.
	 */
	public void execute() throws ExecutionException {
		// check the interrupt flag before we start - in a long-running PR it is
		// good practice to check this flag at appropriate key points in the
		// execution, to allow the user to interrupt processing if it takes too
		// long.
		if (isInterrupted()) {
			throw new ExecutionInterruptedException("Execution of OSCAR4 has been interrupted!");
		}
		interrupted = false;

		Document doc = getDocument();
		AnnotationSet annots = doc.getAnnotations(annotationSetName);

		if (doc != null) {

			List<ResolvedNamedEntity> entities = oscar.findAndResolveNamedEntities(doc.getContent().toString());

			for (ResolvedNamedEntity entity : entities) {

				Set<String> seen = new HashSet<String>();

				for (ChemicalStructure cs : entity.getChemicalStructures(FormatType.STD_INCHI)) {
					if (!seen.add(cs.getValue()))
						continue;

					FeatureMap features = Factory.newFeatureMap();
					features.put("type", entity.getType().getDescription());
					features.put("Std_InChI", cs.getValue());
					features.put("string", entity.getSurface());

					// TODO would be safer to generate from the value we have
					// to ensure they match and nothing funny is going on
					Set<String> keys = oscar.getDictionaryRegistry().getStdInchiKeys(entity.getSurface());
					if (keys.size() > 0)
						features.put("Std_InChIKey", keys.iterator().next());

					String smiles = oscar.getDictionaryRegistry().getShortestSmiles(entity.getSurface());
					if (smiles != null)
						features.put("SMILES", smiles);

					Utils.addAnn(annots, entity.getStart(), entity.getEnd(), "ChemicalNE", features);
				}
			}
		}
	}
}
